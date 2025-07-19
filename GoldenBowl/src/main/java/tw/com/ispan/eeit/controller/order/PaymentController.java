package tw.com.ispan.eeit.controller.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.eeit.controller.ECPayController;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;
import tw.com.ispan.eeit.model.entity.order.PaymentBean;
import tw.com.ispan.eeit.repository.order.PaymentRepository;
import tw.com.ispan.eeit.service.order.PaymentService;
import tw.com.ispan.eeit.util.CheckMacValueUtil;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ECPayController ecpayController; // 注入 ECPay 控制器

    @Autowired
    private PaymentRepository paymentRepository;

    // 建立付款表單的 HTML 內容
    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody Map<String, Object> payload) {
        System.out.println("Payload received: " + payload);
        // 從請求中獲取 PAYLOAD
        try {
            // 解析參數
            Integer orderId = Integer.parseInt(payload.get("orderId").toString());
            String description = payload.get("description") != null
                    ? payload.get("description").toString()
                    : "無描述";
            Integer amount = Integer.parseInt(payload.get("amount").toString());

            String foodNameList = payload.get("foodNameList").toString();

            // System.out.println(foodItem) ; // 這裡有問題，後面需要處理
            // System.out.println("最終 description： " + description);

            // 建立付款記錄
            PaymentBean payment = paymentService.createPaymentRecord(orderId, amount, description);

            // 準備 ECPay 的 payload，透過 ECPayController 處理
            Map<String, Object> ecpayPayload = Map.of(
                    "amount", payment.getTotal(),
                    "description", "訂單 #" + payment.getOrder().getId(),
                    "ItemName", foodNameList,
                    "merchantTradeNo", payment.getTransactionId());
            String form = ecpayController.createOrder(ecpayPayload);
            // 將 payload 傳遞給 ecpayController.createOrder

            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(form);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("付款建立失敗：" + e.getMessage());
        }
    }

    // 處理 callback（ReturnURL 回傳）
    @PostMapping("/notify")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> payload) {
        try {
            boolean verified = CheckMacValueUtil.verify(payload, "5294y06JbISpM5x9", "v77hoKGq4kWxNNIS");
            if (!verified)
                return ResponseEntity.ok("0|Fail");

            String merchantTradeNo = payload.get("MerchantTradeNo");
            paymentService.processEcpayCallback(merchantTradeNo, payload);

            return ResponseEntity.ok("1|OK");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("0|Exception");
        }
    }
}
