package tw.com.ispan.eeit.service.store;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

@Service
@Transactional
public class OpenHourService {

	@Autowired
	private OpenHourRepository openHourRepo;

	@Autowired
	private StoreRepository storeRepo;

	/**
	 * 為餐廳設定營業時間
	 */
	public OpenHourBean setOpenHour(Integer storeId, DayOfWeek day, String openTime, String closeTime, boolean isOpen,
			Integer timeIntervalMinutes) {
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
			openHour.setDay(day);
		}

		// 設定營業時間
		if (openTime != null) {
			openHour.setOpenTime(java.time.LocalTime.parse(openTime));
		}
		if (closeTime != null) {
			openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
		}
		openHour.setIsOpen(isOpen);
		if (timeIntervalMinutes != null) {
			openHour.setTimeIntervalMinutes(timeIntervalMinutes);
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
			setOpenHour(storeId, day, "11:00", "22:00", true, 30);
		}

		// 週六週日：10:00-23:00
		for (DayOfWeek day : List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
			setOpenHour(storeId, day, "10:00", "23:00", true, 30);
		}
	}

	/**
	 * 更新營業時間
	 */
	public OpenHourBean updateOpenHour(Integer openHourId, String openTime, String closeTime, boolean isOpen,
			Integer timeIntervalMinutes) {
		OpenHourBean openHour = openHourRepo.findById(openHourId)
				.orElseThrow(() -> new ResourceNotFoundException("Open hour not found"));

		if (openTime != null) {
			openHour.setOpenTime(java.time.LocalTime.parse(openTime));
		}
		if (closeTime != null) {
			openHour.setCloseTime(java.time.LocalTime.parse(closeTime));
		}
		openHour.setIsOpen(isOpen);
		if (timeIntervalMinutes != null) {
			openHour.setTimeIntervalMinutes(timeIntervalMinutes);
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
	 * 檢查餐廳在指定時間是否營業
	 */
	public boolean isStoreOpen(Integer storeId, DayOfWeek day, java.time.LocalTime time) {
		try {
			OpenHourBean openHour = getOpenHourByStoreAndDay(storeId, day);

			if (!openHour.getIsOpen()) {
				return false;
			}

			return time.isAfter(openHour.getOpenTime()) && time.isBefore(openHour.getCloseTime());
		} catch (ResourceNotFoundException e) {
			// 如果沒有設定，使用預設邏輯
			return isDefaultOpenTime(day, time);
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
}
