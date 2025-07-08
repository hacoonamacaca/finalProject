package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.eeit.service.reservation.StoreReservationIntegrationService;

@RestController
@RequestMapping("/api/integration")
@CrossOrigin(origins = "*")
public class StoreReservationController {

    @Autowired
    private StoreReservationIntegrationService integrationService;

    /**
     * 獲取餐廳完整資訊
     */
    @GetMapping("/store/{storeId}")
    public Map<String, Object> getStoreInfo(@PathVariable Integer storeId) {
        return integrationService.getStoreWithReservationInfo(storeId);
    }

    /**
     * 檢查餐廳可用性
     */
    @GetMapping("/store/{storeId}/availability")
    public Map<String, Object> checkAvailability(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer guests) {
        return integrationService.checkStoreAvailability(storeId, date, time, guests);
    }

    /**
     * 創建訂位（整合版本）
     */
    @PostMapping("/reservation/create")
    public Map<String, Object> createReservation(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = Integer.parseInt(request.get("userId").toString());
            Integer storeId = Integer.parseInt(request.get("storeId").toString());
            LocalDate reservedDate = LocalDate.parse(request.get("reservedDate").toString());
            LocalTime reservedTime = LocalTime.parse(request.get("reservedTime").toString());
            Integer guests = Integer.parseInt(request.get("guests").toString());
            Integer duration = Integer.parseInt(request.get("duration").toString());
            String content = (String) request.get("content");

            return integrationService.createReservationWithStoreValidation(
                    userId, storeId, reservedDate, reservedTime, guests, duration, content);
        } catch (Exception e) {
            return Map.of("success", false, "message", "參數錯誤: " + e.getMessage());
        }
    }

    /**
     * 獲取餐廳訂位統計
     */
    @GetMapping("/store/{storeId}/stats")
    public Map<String, Object> getStoreStats(@PathVariable Integer storeId) {
        return integrationService.getStoreReservationStats(storeId);
    }

    /**
     * 搜尋餐廳
     */
    @GetMapping("/search")
    public Map<String, Object> searchStores(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isOpen) {
        return integrationService.searchStores(keyword, category, isOpen);
    }
}