package tw.com.ispan.eeit.controller.store;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.service.store.OpenHourService;

@RestController
@RequestMapping("/api/stores/{storeId}/hours")
public class OpenHourController {

    @Autowired
    private OpenHourService openHourService;

    /**
     * 取得餐廳的營業時間設定
     */
    @GetMapping
    public ResponseEntity<List<OpenHourBean>> getOpenHours(@PathVariable Integer storeId) {
        try {
            List<OpenHourBean> openHours = openHourService.getOpenHoursByStore(storeId);
            return ResponseEntity.ok(openHours);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 取得餐廳特定星期的營業時間設定
     */
    @GetMapping("/{day}")
    public ResponseEntity<OpenHourBean> getOpenHourByDay(
            @PathVariable Integer storeId,
            @PathVariable DayOfWeek day) {
        try {
            OpenHourBean openHour = openHourService.getOpenHourByStoreAndDay(storeId, day);
            return ResponseEntity.ok(openHour);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 設定餐廳的營業時間
     */
    @PostMapping
    public ResponseEntity<OpenHourBean> setOpenHour(
            @PathVariable Integer storeId,
            @RequestParam DayOfWeek day,
            @RequestParam String openTime,
            @RequestParam String closeTime,
            @RequestParam(defaultValue = "true") boolean isOpen) {
        try {
            OpenHourBean openHour = openHourService.setOpenHour(storeId, day, openTime, closeTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(openHour);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 更新營業時間設定
     */
    @PutMapping("/{openHourId}")
    public ResponseEntity<OpenHourBean> updateOpenHour(
            @PathVariable Integer storeId,
            @PathVariable Integer openHourId,
            @RequestParam(required = false) String openTime,
            @RequestParam(required = false) String closeTime,
            @RequestParam(required = false) Boolean isOpen) {
        try {
            OpenHourBean openHour = openHourService.updateOpenHour(openHourId, openTime, closeTime);
            return ResponseEntity.ok(openHour);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 刪除營業時間設定
     */
    @DeleteMapping("/{openHourId}")
    public ResponseEntity<Void> deleteOpenHour(
            @PathVariable Integer storeId,
            @PathVariable Integer openHourId) {
        try {
            openHourService.deleteOpenHour(openHourId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 設定預設營業時間
     */
    @PostMapping("/default")
    public ResponseEntity<String> setDefaultOpenHours(@PathVariable Integer storeId) {
        try {
            openHourService.setDefaultOpenHours(storeId);
            return ResponseEntity.ok("成功設定預設營業時間");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("設定預設營業時間時發生錯誤: " + e.getMessage());
        }
    }

    /**
     * 檢查餐廳在指定時間是否營業
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isStoreOpen(
            @PathVariable Integer storeId,
            @RequestParam DayOfWeek day,
            @RequestParam String time) {
        try {
            java.time.LocalTime localTime = java.time.LocalTime.parse(time);
            boolean isOpen = openHourService.isStoreOpen(storeId, day, localTime);
            return ResponseEntity.ok(isOpen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 檢查餐廳在指定日期和時間是否營業
     */
    @GetMapping("/check/{date}")
    public ResponseEntity<Boolean> isStoreOpenByDate(
            @PathVariable Integer storeId,
            @PathVariable String date,
            @RequestParam String time) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            java.time.LocalTime localTime = java.time.LocalTime.parse(time);
            boolean isOpen = openHourService.isStoreOpen(storeId, localDate, localTime);
            return ResponseEntity.ok(isOpen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ========== 特殊營業時間相關 API ==========

    /**
     * 取得餐廳的特殊營業時間設定
     */
    @GetMapping("/special")
    public ResponseEntity<List<SpecialHoursBean>> getSpecialHours(@PathVariable Integer storeId) {
        try {
            List<SpecialHoursBean> specialHours = openHourService.getSpecialHoursByStore(storeId);
            return ResponseEntity.ok(specialHours);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 設定特殊營業時間
     */
    @PostMapping("/special")
    public ResponseEntity<SpecialHoursBean> setSpecialHours(
            @PathVariable Integer storeId,
            @RequestParam String date,
            @RequestParam(required = false) String openTime,
            @RequestParam(required = false) String closeTime,
            @RequestParam(required = false) Boolean isClose) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            SpecialHoursBean specialHours = openHourService.setSpecialHours(storeId, localDate, openTime, closeTime,
                    isClose);
            return ResponseEntity.status(HttpStatus.CREATED).body(specialHours);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 刪除特殊營業時間設定
     */
    @DeleteMapping("/special/{specialHoursId}")
    public ResponseEntity<Void> deleteSpecialHours(
            @PathVariable Integer storeId,
            @PathVariable Integer specialHoursId) {
        try {
            openHourService.deleteSpecialHours(specialHoursId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
