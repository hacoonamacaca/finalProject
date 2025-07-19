package tw.com.ispan.eeit.service.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.order.PaymentBean;
import tw.com.ispan.eeit.repository.order.OrderRepository;
import tw.com.ispan.eeit.repository.order.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    // 建立付款記錄
    public PaymentBean createPaymentRecord(Integer orderId, Integer amount, String description) {
        PaymentBean payment = new PaymentBean();
        OrderBean order = new OrderBean();
        order.setId(orderId);
        payment.setOrder(order);
        payment.setTotal(amount);
        payment.setMethod("ECPay");
        payment.setIsPaid(false);
        payment.setTransactionId(generateTradeNo(orderId));
        Date now = new java.util.Date();
        payment.setPaidTime(now);
        // 這裡可以將 description 存入資料庫

        return paymentRepository.save(payment);
    }

    // 處理綠界回調並更新 DB
    public void processEcpayCallback(String merchantTradeNo, Map<String, String> callbackData) {
        PaymentBean payment = paymentRepository.findByTransactionId(merchantTradeNo);

        if (payment != null) {
            payment.setIsPaid(true);
            payment.setTransactionId(callbackData.get("TradeNo")); // 更新綠界回調的交易號

            // 更新付款時間
            Date now = new java.util.Date();
            System.out.println("更新 paidTime: " + now);
            payment.setPaidTime(now);
            paymentRepository.save(payment);

            // 可加入 rtnMsg、paymentType 等欄位，如 PaymentBean 有新增
            paymentRepository.save(payment);
        } else {
            System.err.println("找不到對應的付款記錄，MerchantTradeNo: " + merchantTradeNo);
            // 這裡可以加入錯誤處理，例如記錄日誌或發送通知
        }
    }

    // 建立唯一的交易號
    private String generateTradeNo(Integer orderId) {
        return "ORDER" + orderId + "_" + System.currentTimeMillis();
    }

    // 日期解析工具
    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}