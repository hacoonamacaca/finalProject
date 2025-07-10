package tw.com.ispan.eeit.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit.model.dto.order.OrderDTO;
import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;
import tw.com.ispan.eeit.model.entity.promotion.PromotionBean;
import tw.com.ispan.eeit.repository.UserRepository;
import tw.com.ispan.eeit.repository.order.OrderDetailRepository;
import tw.com.ispan.eeit.repository.order.OrderRepository;
import tw.com.ispan.eeit.repository.promotion.PromotionRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PromotionRepository promotionRepository;

    // 創建訂單 (包含其明細)
    public Optional<OrderBean> createOrder(OrderDTO orderDTO) {
        // 1. 將 OrderDTO 轉換為 OrderBean
        // 在創建操作中，通常我們會創建一個新的 Bean，並將 DTO 的數據填充進去。
        // DTO 中的 ID 在這裡通常是 null，因為是資料庫自動生成的。
        OrderBean newOrderBean = new OrderBean();

        // 複製 DTO 中的基本屬性到 Bean
        // newOrderBean.setId(orderDTO.getId()); // 創建時 ID 通常由資料庫生成，不需從 DTO 複製
        
        newOrderBean.setTotal(orderDTO.getTotal());
        newOrderBean.setStatus(orderDTO.getStatus() == null || orderDTO.getStatus().isEmpty() ? "PENDING" : orderDTO.getStatus()); // 設定預設狀態
        newOrderBean.setCreateTime(LocalDateTime.now()); // 由 Service 層設定創建時間
        newOrderBean.setContent(orderDTO.getContent());
        newOrderBean.setPickupTime(orderDTO.getPickupTime());
        
     // --- 處理 Promotion 關聯 ---
        if (orderDTO.getPromotionId() != null) {
            promotionRepository.findById(orderDTO.getPromotionId())
                               .ifPresentOrElse(
                                   newOrderBean::setPromotion, // 如果找到 PromotionBean，設定給訂單
                                   () -> {
                                       System.out.println("Promotion with ID " + orderDTO.getPromotionId() + " not found for order creation.");
                                       // 這裡可以選擇拋出異常，或者讓 Promotion 為 null，取決於業務規則
                                       // throw new RuntimeException("Associated Promotion not found.");
                                   }
                               );
        } else {
            newOrderBean.setPromotion(null); // 如果 DTO 中沒有提供 promotionId，設為 null
        }

        // 處理 User 關聯 (與之前相同)
        if (orderDTO.getUser() != null && orderDTO.getUser().getId() != null) {
            userRepository.findById(orderDTO.getUser().getId())
                          .ifPresentOrElse(
                              newOrderBean::setUser,
                              () -> System.out.println("User with ID " + orderDTO.getUser().getId() + " not found for order creation.")
                          );
        }
        // 處理關聯物件的設置 (User, Store, Comment)
        // 對於創建操作，我們需要確保設置的關聯物件是資料庫中存在的實體，
        // 而不是創建新的 UserBean 或 StoreBean 實例。

        // 設定 User 關聯
        if (orderDTO.getUser() != null && orderDTO.getUser().getId() != null) {
            userRepository.findById(orderDTO.getUser().getId())
                          .ifPresentOrElse(
                              newOrderBean::setUser, // 找到 User，就設定
                              () -> {
                                  // 可以記錄日誌或拋出異常，表示關聯的 User 不存在
                                  System.out.println("User with ID " + orderDTO.getUser().getId() + " not found for order creation.");
                                  // throw new RuntimeException("Associated User not found."); // 拋出異常，回滾事務
                              }
                          );
        }
        // 如果 DTO 中沒有提供 User 資訊，newOrderBean.setUser() 會保持為 null

        // 設定 Store 關聯
        if (orderDTO.getStore() != null && orderDTO.getStore().getId() != null) {
            storeRepository.findById(orderDTO.getStore().getId())
                           .ifPresentOrElse(
                               newOrderBean::setStore, // 找到 Store，就設定
                               () -> {
                                   System.out.println("Store with ID " + orderDTO.getStore().getId() + " not found for order creation.");
                                   // throw new RuntimeException("Associated Store not found.");
                               }
                           );
        }

        // 設定 Comment 關聯
        // 通常在創建訂單時不會立即有評論，評論是在訂單完成後才生成的。
        // 如果你的業務邏輯允許在創建時綁定評論（例如，預先填寫的評論），
        // 且 DTO 中有 CommentResponseDTO，則處理方式與 Update 類似。
        // 這裡暫時假設創建訂單時不設置 Comment。
        // 如果需要，可以根據 DTO 中的 comment.getId() 去查詢 CommentBean 並設定。
        if (orderDTO.getComment() != null && orderDTO.getComment().getId() != null) {
             CommentBean commentBean = new CommentBean(); // 這裡創建一個新的 Bean，只設定 ID
             commentBean.setId(orderDTO.getComment().getId());
             // 更好的做法是從資料庫查詢 CommentBean，確保其存在且有效
             // this.commentRepository.findById(orderDTO.getComment().getId()).ifPresent(newOrderBean::setComment);
             newOrderBean.setComment(commentBean);
        }


        // 2. 保存 OrderBean 到資料庫，以獲取生成的 ID
        OrderBean savedOrder = orderRepository.save(newOrderBean);

        // 3. 處理 OrderDetails 列表
        if (orderDTO.getOrderDetails() != null && !orderDTO.getOrderDetails().isEmpty()) {
            List<OrderDetailBean> orderDetails = orderDTO.getOrderDetails().stream()
                .map(detailDTO -> {
                    OrderDetailBean detailBean = detailDTO.toBean(); // 將 OrderDetailDTO 轉換為 OrderDetailBean
                    detailBean.setOrder(savedOrder); // !!! 重要：設定 OrderDetailBean 指向新保存的 OrderBean
                    return detailBean;
                })
                .collect(Collectors.toList());

            // 批量保存訂單明細
            orderDetailRepository.saveAll(orderDetails);

            // 重新載入訂單以確保 orderDetails 列表被正確填充
            // 或者，如果你的 JPA 配置了 OneToMany 的 FetchType.LAZY 且你希望立即返回明細，
            // 且之前的 saveAll 並沒有自動更新 savedOrder 的關聯集合，
            // 你可以重新查詢或手動添加到 savedOrder 的集合中。
            // 這裡直接將保存的明細列表設定回 savedOrder，前提是關係被正確管理
            savedOrder.setOrderDetails(orderDetails);
            // 也可以選擇重新從資料庫載入
            // savedOrder = orderRepository.findById(savedOrder.getId()).orElse(savedOrder);
        }

        // 4. 返回保存後的 OrderBean，包裝在 Optional 中
        return Optional.of(savedOrder);
    }


    // 根據 ID 查找訂單
    public Optional<OrderBean> findOrderById(Integer id) {
        Optional<OrderBean> orderOptional = orderRepository.findById(id);
        // orderOptional.ifPresent(order -> {
        // order.setOrderDetails(orderDetailRepository.findByOrder_Id(order.getId()));
        // });
        return orderOptional;
    }

    // 查找所有訂單
    public List<OrderBean> findAllOrders() {
        return orderRepository.findAll();
    }

    // 更新訂單
    public Optional<OrderBean> updateOrder(Integer id, OrderDTO orderDTO) {
        Optional<OrderBean> existingOrderOptional = orderRepository.findById(id);

        if (existingOrderOptional.isEmpty()) {
            return Optional.empty(); // 找不到訂單，返回空 Optional
        }

        OrderBean existingOrderBean = existingOrderOptional.get();
        if (orderDTO.getPromotionId() != null) {
            // 從資料庫中查找 PromotionBean
            promotionRepository.findById(orderDTO.getPromotionId())
                               .ifPresentOrElse(
                                   existingOrderBean::setPromotion,
                                   () -> {
                                       // 如果找不到對應的 Promotion，這裡可以選擇：
                                       // 1. 拋出一個業務異常，因為設置了一個不存在的 Promotion ID
                                       // throw new YourBusinessException("Promotion with ID " + orderDTO.getPromotionId() + " not found.");
                                       // 2. 記錄日誌，並將 promotion 設為 null
                                       System.out.println("Promotion with ID " + orderDTO.getPromotionId() + " not found for order update. Setting promotion to null.");
                                       existingOrderBean.setPromotion(null);
                                   }
                               );
        } else {
            // 如果 DTO 中沒有提供 Promotion ID，則將 promotion 設為 null
            existingOrderBean.setPromotion(null);
        }
        // 複製 OrderDTO 的資料到 existingOrderBean
        existingOrderBean.setTotal(orderDTO.getTotal());
        existingOrderBean.setStatus(orderDTO.getStatus());
        existingOrderBean.setContent(orderDTO.getContent());
        existingOrderBean.setPickupTime(orderDTO.getPickupTime());

        // 更新 User 關聯
        if (orderDTO.getUser() != null && orderDTO.getUser().getId() != null) {
            userRepository.findById(orderDTO.getUser().getId())
                          .ifPresentOrElse(
                              existingOrderBean::setUser,
                              () -> System.out.println("User with ID " + orderDTO.getUser().getId() + " not found for order update.") // 或使用 logger
                          );
        } else {
             existingOrderBean.setUser(null);
        }

        // 更新 Store 關聯
        if (orderDTO.getStore() != null && orderDTO.getStore().getId() != null) {
            storeRepository.findById(orderDTO.getStore().getId())
                           .ifPresentOrElse(
                               existingOrderBean::setStore,
                               () -> System.out.println("Store with ID " + orderDTO.getStore().getId() + " not found for order update.") // 或使用 logger
                           );
        } else {
            existingOrderBean.setStore(null);
        }

        // 更新 Comment 關聯
        if (orderDTO.getComment() != null && orderDTO.getComment().getId() != null) {
            CommentBean commentBean = new CommentBean(); // 這裡僅設定 ID，若需更新內容需從資料庫查詢
            commentBean.setId(orderDTO.getComment().getId());
            existingOrderBean.setComment(commentBean);
        } else {
            existingOrderBean.setComment(null);
        }
        // 處理 OrderDetails 列表的更新
        if (existingOrderBean.getOrderDetails() != null) {
            existingOrderBean.getOrderDetails().clear(); // 清除舊的明細
        }
        if (orderDTO.getOrderDetails() != null && !orderDTO.getOrderDetails().isEmpty()) {
            List<OrderDetailBean> newOrderDetails = orderDTO.getOrderDetails().stream()
                .map(detailDTO -> {
                    OrderDetailBean detailBean = detailDTO.toBean();
                    detailBean.setOrder(existingOrderBean); // 設定父訂單關聯
                    return detailBean;
                })
                .collect(Collectors.toList());

            if (existingOrderBean.getOrderDetails() == null) {
                existingOrderBean.setOrderDetails(newOrderDetails);
            } else {
                existingOrderBean.getOrderDetails().addAll(newOrderDetails);
            }
        }
        // 保存更新後的 OrderBean
        return Optional.of(orderRepository.save(existingOrderBean));
    }

    // 刪除訂單
    public boolean deleteOrder(Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    
    public List<OrderDTO> findOrdersByUserId(Integer userId) {
        if (userId != null) {
            // orderRepository.findByUser_Id(userId);
            List<OrderBean> orderBeans = orderRepository.findByUser_Id(userId);
            return orderBeans.stream()
                    .map(OrderDTO::fromEntity)
                    .collect(Collectors.toList());
            // .map(orderBean -> OrderDto.fromEntity(orderBean))
            // 最終，map 操作會產生一個包含 orderDto1、orderDto2、orderDto3 的新 Stream。
            // 最後的 .collect(Collectors.toList()) 則會將這個 Stream 中的所有 OrderDto 物件收集起來，
            // 形成一個 List<OrderDto>。
        }
        return null;
    }

    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderDTO> findOrdersUser_IdAndStatus(Integer userId, String status) {
    	if (userId != null) {
            // orderRepository.findByUser_Id(userId);
            List<OrderBean> orderBeans = orderRepository.findByUser_IdAndStatus(userId,status);
            return orderBeans.stream()
                    .map(OrderDTO::fromEntity)
                    .collect(Collectors.toList());
            // .map(orderBean -> OrderDto.fromEntity(orderBean))
            // 最終，map 操作會產生一個包含 orderDto1、orderDto2、orderDto3 的新 Stream。
            // 最後的 .collect(Collectors.toList()) 則會將這個 Stream 中的所有 OrderDto 物件收集起來，
            // 形成一個 List<OrderDto>。
        }
        return null;
    }

    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderDTO> findOrdersByUser_IdAndStatusNot(Integer userId, String status) {
        if (userId != null && !status.isEmpty() && status.length() > 0) {
        	List<OrderBean> orderBeans =orderRepository.findByUser_IdAndStatusNot(userId, status);
            return orderBeans.stream()
                    .map(OrderDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return null;
    }

}