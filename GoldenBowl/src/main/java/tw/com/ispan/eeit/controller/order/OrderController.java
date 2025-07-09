package tw.com.ispan.eeit.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.order.OrderDTO;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.service.order.OrderService;

@RestController
@RequestMapping("/api/orders") // 建議使用 /api/ 作為 RESTful API 前綴
public class OrderController {

    private final OrderDetailController orderDetailController;

    @Autowired
    private OrderService orderService;

    OrderController(OrderDetailController orderDetailController) {
        this.orderDetailController = orderDetailController;
    }

    // 創建新訂單
    @PostMapping
    public ResponseEntity<OrderBean> createOrder(@RequestBody OrderBean orders) {
    	System.out.println(orders.getTotal());
//    	JSONObject obj = new JSONObject(body);
//    	OrderBean order = new OrderBean();
    	System.out.println(orders.getTotal());
    	OrderBean order = new OrderBean();
        OrderBean createdOrder = orderService.createOrder(order);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // 根據 ID 獲取單一訂單
    @GetMapping("/{id}")
    public ResponseEntity<OrderBean> getOrderById(@PathVariable Integer id) {
        return orderService.findOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 獲取所有訂單
    @GetMapping
    public ResponseEntity<List<OrderBean>> getAllOrders() {
        List<OrderBean> orders = orderService.findAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    // 根據用戶 ID 獲取訂單列表 (示例)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderDTO> orders = orderService.findOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    // 更新現有訂單
    @PutMapping("/{id}")
    public ResponseEntity<OrderBean> updateOrder(@PathVariable Integer id, @RequestBody OrderBean order) {
        return orderService.updateOrder(id, order)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 刪除訂單
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        if (orderService.deleteOrder(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
}