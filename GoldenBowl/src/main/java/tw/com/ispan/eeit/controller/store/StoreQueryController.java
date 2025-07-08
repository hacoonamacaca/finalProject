package tw.com.ispan.eeit.controller.store;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;
import tw.com.ispan.eeit.service.reservation.ReservationService;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;

@RestController
@RequestMapping("/api/store/query")
@CrossOrigin(origins = "*")
public class StoreQueryController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ReservationService reservationService;

    /**
     * 查詢所有餐廳
     */
    @GetMapping("/all")
    public Map<String, Object> getAllStores() {
        try {
            List<StoreBean> stores = storeRepository.findAll();
            return Map.of(
                    "success", true,
                    "stores", stores);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 根據 ID 查詢餐廳
     */
    @GetMapping("/{storeId}")
    public Map<String, Object> getStoreById(@PathVariable Integer storeId) {
        try {
            Optional<StoreBean> store = storeRepository.findById(storeId);
            if (store.isPresent()) {
                return Map.of(
                        "success", true,
                        "store", store.get());
            } else {
                return Map.of(
                        "success", false,
                        "message", "餐廳不存在");
            }
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢餐廳的桌位資訊
     */
    @GetMapping("/{storeId}/tables")
    public Map<String, Object> getStoreTables(@PathVariable Integer storeId) {
        try {
            List<TableBean> tables = tableRepository.findByStoreId(storeId);
            return Map.of(
                    "success", true,
                    "tables", tables);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢餐廳的可用桌位
     */
    @GetMapping("/{storeId}/available-tables")
    public Map<String, Object> getAvailableTables(
            @PathVariable Integer storeId,
            @RequestParam Integer minSeats) {
        try {
            List<TableBean> tables = reservationService.getAvailableTables(storeId, minSeats);
            return Map.of(
                    "success", true,
                    "tables", tables);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢營業中的餐廳
     */
    @GetMapping("/open")
    public Map<String, Object> getOpenStores() {
        try {
            List<StoreBean> stores = storeRepository.findByIsOpenTrue();
            return Map.of(
                    "success", true,
                    "stores", stores);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 根據類別查詢餐廳
     */
    @GetMapping("/category/{categoryName}")
    public Map<String, Object> getStoresByCategory(@PathVariable String categoryName) {
        try {
            List<StoreBean> stores = storeRepository.findByCategoriesName(categoryName);
            return Map.of(
                    "success", true,
                    "stores", stores);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢餐廳的訂位記錄
     */
    @GetMapping("/{storeId}/reservations")
    public Map<String, Object> getStoreReservations(@PathVariable Integer storeId) {
        try {
            List<ReservationBean> reservations = reservationService.getStoreReservations(storeId);
            return Map.of(
                    "success", true,
                    "reservations", reservations);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }
}