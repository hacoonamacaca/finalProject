package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.service.reservation.ReservationService;

@RestController
@RequestMapping("/api/stores/{storeId}/tables")
public class TableController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 取得餐廳的所有桌位
     */
    @GetMapping
    public ResponseEntity<List<TableBean>> getStoreTables(@PathVariable Integer storeId) {
        try {
            List<TableBean> tables = reservationService.getStoreTables(storeId);
            return ResponseEntity.ok(tables);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 取得餐廳的可用桌位
     */
    @GetMapping("/available")
    public ResponseEntity<List<TableBean>> getAvailableTables(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(defaultValue = "120") int duration,
            @RequestParam(defaultValue = "1") int minSeats) {
        try {
            List<TableBean> tables = reservationService.findAvailableTables(storeId, startTime, duration, minSeats);
            return ResponseEntity.ok(tables);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 新增桌位
     */
    @PostMapping
    public ResponseEntity<TableBean> addTable(
            @PathVariable Integer storeId,
            @RequestParam Integer seats,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam(defaultValue = "true") Boolean status) {
        try {
            TableBean table = reservationService.addTable(storeId, seats, quantity, status);
            return ResponseEntity.status(HttpStatus.CREATED).body(table);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新桌位
     */
    @PutMapping("/{tableId}")
    public ResponseEntity<TableBean> updateTable(
            @PathVariable Integer storeId,
            @PathVariable Integer tableId,
            @RequestParam(required = false) Integer seats,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Boolean status) {
        try {
            TableBean table = reservationService.updateTable(tableId, seats, quantity, status);
            return ResponseEntity.ok(table);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 刪除桌位
     */
    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deleteTable(
            @PathVariable Integer storeId,
            @PathVariable Integer tableId) {
        try {
            reservationService.deleteTable(tableId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 取得桌位詳情
     */
    @GetMapping("/{tableId}")
    public ResponseEntity<TableBean> getTableById(
            @PathVariable Integer storeId,
            @PathVariable Integer tableId) {
        try {
            TableBean table = reservationService.getTableById(tableId);
            return ResponseEntity.ok(table);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 檢查桌位是否可用
     */
    @GetMapping("/{tableId}/availability")
    public ResponseEntity<Boolean> checkTableAvailability(
            @PathVariable Integer storeId,
            @PathVariable Integer tableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(defaultValue = "120") int duration) {
        try {
            boolean isAvailable = reservationService.isTableAvailable(tableId, startTime, duration);
            return ResponseEntity.ok(isAvailable);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}