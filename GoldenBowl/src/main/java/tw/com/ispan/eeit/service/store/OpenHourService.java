package tw.com.ispan.eeit.service.store;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;
import tw.com.ispan.eeit.repository.store.SpecialHoursRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

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
	 * 為餐廳設定營業時間
	 */
	public OpenHourBean setOpenHour(Integer storeId, DayOfWeek day, String openTime, String closeTime) {
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

		// 設定營業時間
		if (openTime != null) {
			openHour.setOpenTime(java.time.LocalTime.parse(openTime));
		}
		if (closeTime != null) {
			openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
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
	public OpenHourBean updateOpenHour(Integer openHourId, String openTime, String closeTime) {
		OpenHourBean openHour = openHourRepo.findById(openHourId)
				.orElseThrow(() -> new ResourceNotFoundException("Open hour not found"));

		if (openTime != null) {
			openHour.setOpenTime(java.time.LocalTime.parse(openTime));
		}
		if (closeTime != null) {
			openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
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
			specialHours.setStoreId(storeId);
			specialHours.setDate(date);
		}

		// 設定特殊營業時間
		if (isClose != null) {
			specialHours.setIsClose(isClose);
		}
		if (openTime != null) {
			specialHours.setOpenTime(java.time.LocalTime.parse(openTime));
		}
		if (closeTime != null) {
			specialHours.setCloseTime(java.time.LocalTime.parse(closeTime));
		}

		return specialHoursRepo.save(specialHours);
	}

	/**
	 * 取得餐廳的特殊營業時間設定
	 */
	public List<SpecialHoursBean> getSpecialHoursByStore(Integer storeId) {
		return specialHoursRepo.findByStoreId(storeId);
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
}
