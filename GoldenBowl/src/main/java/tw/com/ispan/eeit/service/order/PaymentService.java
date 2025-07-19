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

    // �إߥI�ڰO��
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
        // �A�]�i�H�� description �s�J�Ƶ����

        return paymentRepository.save(payment);
    }

    // �B�z��ɦ^�Ǩç�s DB
    public void processEcpayCallback(String merchantTradeNo, Map<String, String> callbackData) {
    	PaymentBean payment = paymentRepository.findByTransactionId(merchantTradeNo);


        if (payment != null) {
            payment.setIsPaid(true);
            payment.setTransactionId(callbackData.get("TradeNo")); // �g�J���y�^�ǥ���s��

            // ��������A����U�ɶ�
            Date now = new java.util.Date();
            System.out.println("�g�J paidTime: " + now);
            payment.setPaidTime(now);
            paymentRepository.save(payment);
            
            // �i�[�J rtnMsg�BpaymentType �����A�Y PaymentBean ���X�R
            paymentRepository.save(payment);
        } else {
            System.err.println("�䤣��������I�ڰO���AMerchantTradeNo: " + merchantTradeNo);
            // �o�̥i�H�[�J���~�B�z�A�Ҧp�O����x�εo�eĵ��
        }
    }

    // �إ߰ߤ@����s��
    private String generateTradeNo(Integer orderId) {
        return "ORDER" + orderId + "_" + System.currentTimeMillis();
    }

    // �ɶ��ഫ�u��
    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
}