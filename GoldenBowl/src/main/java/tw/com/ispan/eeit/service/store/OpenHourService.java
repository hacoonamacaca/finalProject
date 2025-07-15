package tw.com.ispan.eeit.service.store;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.dto.store.OpenHourDTO;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;
import tw.com.ispan.eeit.repository.store.SpecialHoursRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.util.DatetimeConvert;

@Service
@Transactional
public class OpenHourService {

	@Autowired
	private OpenHourRepository openHourRepo;

	@Autowired
	private StoreRepository storeRepo;

	@Autowired
	private SpecialHoursRepository specialHoursRepo;

	/**
	 * 新增一個營業時間設定。
	 * 將 DTO 轉換為 Bean，並處理 StoreBean 關聯，然後儲存。
	 * @param openHourDTO 包含營業時間資訊的 DTO 物件。
	 * @return 儲存成功後轉換回的 OpenHourDTO 物件，如果商店不存在則返回 null。
	 */
	public OpenHourBean create(OpenHourDTO openHourDTO) {
		if (openHourDTO == null || openHourDTO.getStoreId() == null) {
			System.err.println("OpenHourDTO 或 StoreId 為空，無法新增。");
			return null;
		}

		// 1. 根據 storeId 查詢 StoreBean
		Optional<StoreBean> storeOptional = storeRepo.findById(openHourDTO.getStoreId());
		if (storeOptional.isEmpty()) {
			System.err.println("找不到對應的商店，Store ID: " + openHourDTO.getStoreId());
			return null; // 或者拋出一個自定義異常
		}
		StoreBean storeBean = storeOptional.get();

		// 2. 將 OpenHourDTO 轉換為 OpenHourBean
		// 注意：OpenHourDTO.toBean 方法中，我們只會設置一個帶有 ID 的 StoreBean "空殼"，
		// 這裡需要將查詢到的完整 StoreBean 設置進去。
		OpenHourBean openHourBean = OpenHourDTO.toBean(openHourDTO);
		openHourBean.setStore(storeBean); // 設定正確的 StoreBean 關聯

		// 3. 儲存 OpenHourBean
		OpenHourBean savedBean = openHourRepo.save(openHourBean);

		// 4. 將儲存後的 Bean 轉換回 DTO 並返回
		return openHourRepo.save(openHourBean);
	}
	
	//查詢所有營業時間
	@Transactional(readOnly = true)
	public List<OpenHourDTO> findAll() {
		List<OpenHourBean> openHourBeans = openHourRepo.findAll();
		return openHourBeans.stream()
				.map(OpenHourDTO::toDto)
				.collect(Collectors.toList());
	}
	//查詢店家所有營業時間
	@Transactional(readOnly = true)
	public List<OpenHourDTO> findByStoreId(Integer storeId) {
		if (storeId == null) {
			return List.of(); // 返回空列表
		}
		// 假設 OpenHourRepository 有 findByStore_Id 方法 (根據 JPA 命名規範)
		List<OpenHourBean> openHourBeans = openHourRepo.findByStoreId(storeId);
		return openHourBeans.stream()
				.map(OpenHourDTO::toDto)
				.collect(Collectors.toList());
	}
	
	/**
	 * 更新一個現有的營業時間設定。
	 * @param openHourDTO 包含更新資訊的 DTO 物件。
	 * @return 更新成功後轉換回的 OpenHourDTO 物件，如果找不到或商店不存在則返回 null。
	 */
	public OpenHourDTO update(OpenHourDTO openHourDTO) {
		if (openHourDTO == null || openHourDTO.getId() == null) {
			System.err.println("OpenHourDTO 或 ID 為空，無法更新。");
			return null;
		}

		// 1. 檢查要更新的 Bean 是否存在
		Optional<OpenHourBean> existingOpenHourOptional = openHourRepo.findById(openHourDTO.getId());
		if (existingOpenHourOptional.isEmpty()) {
			System.err.println("找不到要更新的營業時間設定，ID: " + openHourDTO.getId());
			return null;
		}
		OpenHourBean existingBean = existingOpenHourOptional.get();

		// 2. 處理 StoreBean 關聯 (如果 DTO 中提供了新的 storeId)
		if (openHourDTO.getStoreId() != null && !openHourDTO.getStoreId().equals(existingBean.getStore().getId())) {
			Optional<StoreBean> newStoreOptional = storeRepo.findById(openHourDTO.getStoreId());
			if (newStoreOptional.isEmpty()) {
				System.err.println("更新時找不到新的商店，Store ID: " + openHourDTO.getStoreId());
				return null; // 或者拋出自定義異常
			}
			existingBean.setStore(newStoreOptional.get());
		}

		// 3. 更新 Bean 的其他屬性
		// 這裡手動更新屬性，或者可以再次呼叫 OpenHourDTO.toBean(openHourDTO) 並複製屬性，
		// 但對於更新操作，通常建議手動複製以避免意外覆蓋。
		existingBean.setDayOfWeek(openHourDTO.getDayOfWeek());
		
		// 處理 LocalTime 的更新，確保即使為 null 也能正確設定
		if (openHourDTO.getOpenTime() != null) {
		    existingBean.setOpenTime(openHourDTO.getOpenTime());
		} else if (openHourDTO.getOpenTimeStr() != null && !openHourDTO.getOpenTimeStr().isEmpty()) {
		    existingBean.setOpenTime(DatetimeConvert.parseLocalTime(openHourDTO.getOpenTimeStr(), "HH:mm"));
		} else {
		    existingBean.setOpenTime(null);
		}

		if (openHourDTO.getCloseTime() != null) {
		    existingBean.setCloseTime(openHourDTO.getCloseTime());
		} else if (openHourDTO.getCloseTimeStr() != null && !openHourDTO.getCloseTimeStr().isEmpty()) {
		    existingBean.setCloseTime(DatetimeConvert.parseLocalTime(openHourDTO.getCloseTimeStr(), "HH:mm"));
		} else {
		    existingBean.setCloseTime(null);
		}


		// 4. 儲存更新後的 Bean
		OpenHourBean updatedBean = openHourRepo.save(existingBean);

		// 5. 將更新後的 Bean 轉換回 DTO 並返回
		return OpenHourDTO.toDto(updatedBean);
	}
	
	/**
	 * 根據 ID 刪除一個營業時間設定。
	 * @param id 要刪除的營業時間的 ID。
	 * @return 如果刪除成功則返回 true，否則返回 false。
	 */
	public boolean delete(Integer id) {
		if (id == null) {
			System.err.println("刪除 ID 為空。");
			return false;
		}
		if (openHourRepo.existsById(id)) {
			openHourRepo.deleteById(id);
			return true;
		}
		System.err.println("找不到要刪除的營業時間設定，ID: " + id);
		return false;
	}
	
	//---------------------------------------------------------
	/**
	 * 為餐廳設定營業時間
	 */
	public OpenHourBean setOpenHour(Integer storeId, DayOfWeek day, String openTime, String closeTime) {
		return setOpenHour(storeId, day, openTime, closeTime, true);
	}
	
	
	
	/**
	 * 為餐廳設定營業時間 (包含是否營業選項)
	 */
	public OpenHourBean setOpenHour(Integer storeId, DayOfWeek day, String openTime, String closeTime, boolean isOpen) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));

		// 檢查是否已存在設定
		Optional<OpenHourBean> existingOpenHour = openHourRepo.findByStoreAndDay(store, day);

		OpenHourBean openHour;
		if (existingOpenHour.isPresent()) {
			openHour = existingOpenHour.get();
		} else {
			openHour = new OpenHourBean();
			openHour.setStore(store);
			openHour.setDayOfWeek(day);
		}

		if (!isOpen) {
			// 如果設定為不營業，將時間設為NULL
			openHour.setOpenTime(null);
			openHour.setCloseTime(null);
		} else {
			// 設定營業時間
			if (openTime != null && !openTime.trim().isEmpty()) {
				openHour.setOpenTime(java.time.LocalTime.parse(openTime));
			}
			if (closeTime != null && !closeTime.trim().isEmpty()) {
				openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
			}
		}

		return openHourRepo.save(openHour);
	}

	/**
	 * 取得餐廳的營業時間設定
	 */
	public List<OpenHourBean> getOpenHoursByStore(Integer storeId) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));
		return openHourRepo.findByStore(store);
	}

	/**
	 * 取得餐廳的營業時間設定 (DTO 版本，只返回核心資料)
	 */
	public List<OpenHourDTO> getOpenHoursDTOByStore(Integer storeId) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));
 
		List<OpenHourBean> openHours = openHourRepo.findByStoreOrderByDayAsc(store);
		return openHours.stream()
				.map(oh -> new OpenHourDTO(
						oh.getId(),
						storeId,
						oh.getDayOfWeek(),
						oh.getOpenTime(),
						oh.getCloseTime()))
				.collect(java.util.stream.Collectors.toList());
	}

	/**
	 * 取得餐廳特定星期的營業時間設定
	 */
	public OpenHourBean getOpenHourByStoreAndDay(Integer storeId, DayOfWeek day) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));

		return openHourRepo.findByStoreAndDay(store, day)
				.orElseThrow(
						() -> new ResourceNotFoundException("Open hour not found for store " + storeId + " on " + day));
	}

	/**
	 * 為餐廳設定預設營業時間（週一到週日）
	 */
	public void setDefaultOpenHours(Integer storeId) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));

		// 週一到週五：11:00-22:00
		for (DayOfWeek day : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
				DayOfWeek.FRIDAY)) {
			setOpenHour(storeId, day, "11:00", "22:00");
		}

		// 週六週日：10:00-23:00
		for (DayOfWeek day : List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
			setOpenHour(storeId, day, "10:00", "23:00");
		}
	}

	/**
	 * 更新營業時間
	 */
	public OpenHourBean updateOpenHour(Integer openHourId, String openTime, String closeTime, Boolean isOpen) {
		OpenHourBean openHour = openHourRepo.findById(openHourId)
				.orElseThrow(() -> new ResourceNotFoundException("Open hour not found"));

		if (isOpen != null) {
			if (!isOpen) {
				// 設定為公休日
				openHour.setOpenTime(null);
				openHour.setCloseTime(null);
			} else {
				// 設定營業時間
				if (openTime != null && !openTime.trim().isEmpty()) {
					openHour.setOpenTime(java.time.LocalTime.parse(openTime));
				}
				if (closeTime != null && !closeTime.trim().isEmpty()) {
					openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
				}
			}
		} else {
			// 只更新時間，不改變營業狀態
			if (openTime != null && !openTime.trim().isEmpty()) {
				openHour.setOpenTime(java.time.LocalTime.parse(openTime));
			}
			if (closeTime != null && !closeTime.trim().isEmpty()) {
				openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
			}
		}

		return openHourRepo.save(openHour);
	}

	/**
	 * 刪除營業時間設定
	 */
	public void deleteOpenHour(Integer openHourId) {
		if (!openHourRepo.existsById(openHourId)) {
			throw new ResourceNotFoundException("Open hour not found");
		}
		openHourRepo.deleteById(openHourId);
	}

	/**
	 * 檢查餐廳在指定時間是否營業（包含特殊營業時間判定）
	 */
	public boolean isStoreOpen(Integer storeId, DayOfWeek day, java.time.LocalTime time) {
		return isStoreOpen(storeId, LocalDate.now(), time);
	}

	/**
	 * 檢查餐廳在指定日期和時間是否營業（包含特殊營業時間判定）
	 */
	public boolean isStoreOpen(Integer storeId, LocalDate date, java.time.LocalTime time) {
		// 1. 首先檢查是否有特殊營業時間設定
		Optional<SpecialHoursBean> specialHours = specialHoursRepo.findByStoreIdAndDate(storeId, date);

		if (specialHours.isPresent()) {
			SpecialHoursBean special = specialHours.get();

			// 如果設定為休息一天
			if (special.getIsClose() != null && special.getIsClose()) {
				return false;
			}

			// 如果有設定特殊營業時間，使用特殊營業時間
			if (special.getOpenTime() != null && special.getCloseTime() != null) {
				return time.isAfter(special.getOpenTime()) && time.isBefore(special.getCloseTime());
			}
		}

		// 2. 如果沒有特殊設定，使用一般營業時間
		try {
			OpenHourBean openHour = getOpenHourByStoreAndDay(storeId, date.getDayOfWeek());

			// 檢查是否為公休日（open_time和close_time都為NULL）
			if (openHour.getOpenTime() == null || openHour.getCloseTime() == null) {
				return false;
			}

			return time.isAfter(openHour.getOpenTime()) && time.isBefore(openHour.getCloseTime());
		} catch (ResourceNotFoundException e) {
			// 如果沒有設定，使用預設邏輯
			return isDefaultOpenTime(date.getDayOfWeek(), time);
		}
	}

	/**
	 * 預設營業時間檢查
	 */
	private boolean isDefaultOpenTime(DayOfWeek day, java.time.LocalTime time) {
		switch (day) {
			case MONDAY:
			case TUESDAY:
			case WEDNESDAY:
			case THURSDAY:
			case FRIDAY:
				return time.isAfter(java.time.LocalTime.of(11, 0)) && time.isBefore(java.time.LocalTime.of(22, 0));
			case SATURDAY:
			case SUNDAY:
				return time.isAfter(java.time.LocalTime.of(10, 0)) && time.isBefore(java.time.LocalTime.of(23, 0));
			default:
				return false;
		}
	}

	/**
	 * 檢查某天是否為公休日
	 */
	public boolean isClosedDay(Integer storeId, DayOfWeek day) {
		try {
			OpenHourBean openHour = getOpenHourByStoreAndDay(storeId, day);
			return openHour.getOpenTime() == null || openHour.getCloseTime() == null;
		} catch (ResourceNotFoundException e) {
			// 如果沒有設定，預設為營業
			return false;
		}
	}

	/**
	 * 檢查某天是否為公休日（使用日期）
	 */
	public boolean isClosedDay(Integer storeId, LocalDate date) {
		return isClosedDay(storeId, date.getDayOfWeek());
	}

	/**
	 * 設定特殊營業時間
	 */
	public SpecialHoursBean setSpecialHours(Integer storeId, LocalDate date, String openTime, String closeTime,
			Boolean isClose) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));

		// 檢查是否已存在設定
		Optional<SpecialHoursBean> existingSpecial = specialHoursRepo.findByStoreIdAndDate(storeId, date);

		SpecialHoursBean specialHours;
		if (existingSpecial.isPresent()) {
			specialHours = existingSpecial.get();
		} else {
			specialHours = new SpecialHoursBean();
			specialHours.setStore(existingSpecial.get().getStore());
			specialHours.setDate(date);
		}

		// 設定特殊營業時間
		if (isClose != null) {
			specialHours.setIsClose(isClose);
		}
		if (openTime != null && !openTime.trim().isEmpty()) {
			specialHours.setOpenTime(java.time.LocalTime.parse(openTime));
		}
		if (closeTime != null && !closeTime.trim().isEmpty()) {
			specialHours.setCloseTime(java.time.LocalTime.parse(closeTime));
		}

		return specialHoursRepo.save(specialHours);
	}



	/**
	 * 刪除特殊營業時間設定
	 */
	public void deleteSpecialHours(Integer specialHoursId) {
		if (!specialHoursRepo.existsById(specialHoursId)) {
			throw new ResourceNotFoundException("Special hours not found");
		}
		specialHoursRepo.deleteById(specialHoursId);
	}

	/**
	 * 取得餐廳的完整營業時間資訊（包括公休日）
	 */
	public List<OpenHourDTO> getCompleteOpenHoursByStore(Integer storeId) {
		StoreBean store = storeRepo.findById(storeId)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found"));

		// 取得現有的營業時間設定
		List<OpenHourBean> existingOpenHours = openHourRepo.findByStore(store);

		// 建立完整的營業時間列表（週一到週日）
		List<OpenHourDTO> completeOpenHours = new java.util.ArrayList<>();

		for (DayOfWeek day : DayOfWeek.values()) {
			// 尋找現有的設定
			Optional<OpenHourBean> existing = existingOpenHours.stream()
					.filter(oh -> oh.getDayOfWeek() == day)
					.findFirst();

			if (existing.isPresent()) {
				// 使用現有設定
				OpenHourBean oh = existing.get();
				completeOpenHours.add(new OpenHourDTO(
						oh.getId(),
						storeId,
						oh.getDayOfWeek(),
						oh.getOpenTime(),
						oh.getCloseTime()));
			} else {
				// 建立預設設定（營業）
				OpenHourDTO defaultHour = new OpenHourDTO(
						null,
						storeId,
						day,
						java.time.LocalTime.of(11, 0),
						java.time.LocalTime.of(22, 0));
				completeOpenHours.add(defaultHour);
			}
		}

		return completeOpenHours;
	}
}
