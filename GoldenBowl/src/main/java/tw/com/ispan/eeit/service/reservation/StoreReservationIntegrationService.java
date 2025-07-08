package tw.com.ispan.eeit.service.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.dto.reservation.StoreReservationDTO;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;

@Service
public class StoreReservationIntegrationService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ReservationService reservationService;

    /**
     * 獲取餐廳完整資訊（包含訂位相關資訊）
     */
    public Map<String, Object> getStoreWithReservationInfo(Integer storeId) {
        try {
            Optional<StoreBean> storeOpt = storeRepository.findById(storeId);
            if (storeOpt.isEmpty()) {
                return Map.of("success", false, "message", "餐廳不存在");
            }

            StoreBean store = storeOpt.get();
            StoreReservationDTO dto = StoreReservationDTO.fromStore(store);

            // 獲取桌位資訊
            List<TableBean> tables = tableRepository.findByStoreId(storeId);
            dto.setTableInfo(tables);

            return Map.of("success", true, "store", dto);
        } catch (Exception e) {
            return Map.of("success", false, "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 檢查餐廳在指定時間的可用性
     */
    public Map<String, Object> checkStoreAvailability(
            Integer storeId,
            LocalDate date,
            LocalTime time,
            Integer guests) {
        try {
            // 檢查餐廳是否存在且營業
            Optional<StoreBean> storeOpt = storeRepository.findById(storeId);
            if (storeOpt.isEmpty()) {
                return Map.of("success", false, "message", "餐廳不存在");
            }

            StoreBean store = storeOpt.get();
            if (!store.getIsOpen()) {
                return Map.of("success", false, "message", "餐廳目前不營業");
            }

            // 檢查可用性
            boolean available = reservationService.checkAvailability(storeId, date, time, guests);

            return Map.of(
                    "success", true,
                    "available", available,
                    "message", available ? "有可用桌位" : "無可用桌位",
                    "storeName", store.getName());
        } catch (Exception e) {
            return Map.of("success", false, "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 創建訂位（整合版本）
     */
    public Map<String, Object> createReservationWithStoreValidation(
            Integer userId,
            Integer storeId,
            LocalDate reservedDate,
            LocalTime reservedTime,
            Integer guests,
            Integer duration,
            String content) {
        try {
            // 驗證餐廳
            Optional<StoreBean> storeOpt = storeRepository.findById(storeId);
            if (storeOpt.isEmpty()) {
                return Map.of("success", false, "message", "餐廳不存在");
            }

            StoreBean store = storeOpt.get();
            if (!store.getIsOpen()) {
                return Map.of("success", false, "message", "餐廳目前不營業");
            }

            // 檢查可用性
            boolean available = reservationService.checkAvailability(storeId, reservedDate, reservedTime, guests);
            if (!available) {
                return Map.of("success", false, "message", "該時段無可用桌位");
            }

            // 創建訂位
            ReservationBean reservation = reservationService.createReservation(
                    userId, storeId, reservedDate, reservedTime, guests, duration, content);

            return Map.of(
                    "success", true,
                    "reservationId", reservation.getId(),
                    "storeName", store.getName(),
                    "message", "訂位成功");
        } catch (Exception e) {
            return Map.of("success", false, "message", "訂位失敗: " + e.getMessage());
        }
    }

    /**
     * 獲取餐廳的訂位統計
     */
    public Map<String, Object> getStoreReservationStats(Integer storeId) {
        try {
            Optional<StoreBean> storeOpt = storeRepository.findById(storeId);
            if (storeOpt.isEmpty()) {
                return Map.of("success", false, "message", "餐廳不存在");
            }

            // 獲取今日訂位
            List<ReservationBean> todayReservations = reservationService.getStoreReservations(storeId);

            // 計算統計資訊
            long totalReservations = todayReservations.size();
            long pendingReservations = todayReservations.stream()
                    .filter(r -> r.getStatus().name().equals("PENDING"))
                    .count();
            long confirmedReservations = todayReservations.stream()
                    .filter(r -> r.getStatus().name().equals("CONFIRMED"))
                    .count();

            return Map.of(
                    "success", true,
                    "storeName", storeOpt.get().getName(),
                    "totalReservations", totalReservations,
                    "pendingReservations", pendingReservations,
                    "confirmedReservations", confirmedReservations);
        } catch (Exception e) {
            return Map.of("success", false, "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 搜尋餐廳（支援多種條件）
     */
    public Map<String, Object> searchStores(String keyword, String category, Boolean isOpen) {
        try {
            List<StoreBean> stores;

            if (keyword != null && !keyword.trim().isEmpty()) {
                // 按名稱搜尋
                stores = storeRepository.findByNameContainingIgnoreCase(keyword.trim());
            } else if (category != null && !category.trim().isEmpty()) {
                // 按類別搜尋
                stores = storeRepository.findByCategoriesName(category.trim());
            } else {
                // 獲取所有餐廳
                stores = storeRepository.findAll();
            }

            // 過濾營業狀態
            if (isOpen != null) {
                stores = stores.stream()
                        .filter(store -> store.getIsOpen().equals(isOpen))
                        .toList();
            }

            return Map.of("success", true, "stores", stores);
        } catch (Exception e) {
            return Map.of("success", false, "message", "搜尋失敗: " + e.getMessage());
        }
    }
}