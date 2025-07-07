package tw.com.ispan.eeit.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.model.dto.food.FoodDto;
import tw.com.ispan.eeit.model.dto.order.OrderDetailDto;
import tw.com.ispan.eeit.model.dto.order.OrderDto;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;
import tw.com.ispan.eeit.repository.order.OrderDetailRepository;
import tw.com.ispan.eeit.repository.order.OrderRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // 創建訂單 (包含其明細)
    public OrderBean createOrder(OrderBean order) {
        order.setCreateTime(LocalDateTime.now());
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }

        OrderBean savedOrder = orderRepository.save(order);

        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            for (OrderDetailBean detail : order.getOrderDetails()) {
                detail.setOrder(savedOrder);
                orderDetailRepository.save(detail);
            }
            // 重新加載訂單以確保 orderDetails 列表被正確填充 (如果需要即時返回)
            // 注意: 這裡的 findByOrder_Id 是對 OrderDetailRepository 的調用，它是正確的
            savedOrder.setOrderDetails(orderDetailRepository.findByOrder_Id(savedOrder.getId()));
        }

        return savedOrder;
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
    public Optional<OrderBean> updateOrder(Integer id, OrderBean updatedOrder) {
        return orderRepository.findById(id).map(order -> {
            order.setUser(updatedOrder.getUser());
            order.setStore(updatedOrder.getStore());
            order.setPromotion(updatedOrder.getPromotion());
            order.setTotal(updatedOrder.getTotal());
            order.setStatus(updatedOrder.getStatus());
            order.setContent(updatedOrder.getContent());
            order.setPickupTime(updatedOrder.getPickupTime());
            return orderRepository.save(order);
        });
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
    @Transactional(readOnly = true) // 確保在一個讀取事務中
    public List<OrderDto> findOrdersByUserId(Integer userId) {
    	if(userId!=null) {
//    		 orderRepository.findByUser_Id(userId);
    		List<OrderBean> orderBeans = 
    				 orderRepository.findByUser_Id(userId);
    		return orderBeans.stream()
	               .map(this::convertToOrderDto)
	               .collect(Collectors.toList());
    	}
    	return null;
    }
    
    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderBean> findOrdersUser_IdAndStatus(Integer userId,String status) {
    	if(userId!=null&& !status.isEmpty() && status.length()>0) {

    		return orderRepository.findByUser_IdAndStatus(userId,status);
    	}
    	return null;
    }
    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderBean> findOrdersByUser_IdAndStatusNot(Integer userId,String status) {
        if(userId!=null&& !status.isEmpty() && status.length()>0) {
        	return orderRepository.findByUser_IdAndStatusNot(userId,status);
        }
        return null;
    }
    
    private OrderDto convertToOrderDto(OrderBean orderBean) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderBean.getId());
        orderDto.setTotal(orderBean.getTotal());
        orderDto.setStatus(orderBean.getStatus());
        orderDto.setCreateTime(orderBean.getCreateTime());
        orderDto.setContent(orderBean.getContent());
        orderDto.setPickupTime(orderBean.getPickupTime());

        // 複製 User 資訊
        // 檢查關聯是否已初始化且不為 null，避免 LazyInitializationException
        if (orderBean.getUser() != null && 
        		org.hibernate.Hibernate.isInitialized(orderBean.getUser())) {
            orderDto.setUserId(orderBean.getUser().getId());
            // 如果 OrderDto 包含嵌套的 UserDto，可以這樣做：
            // OrderDto.OrderUserDto userDto = new OrderDto.OrderUserDto();
            // userDto.setId(orderBean.getUser().getId());
            // userDto.setName(orderBean.getUser().getName());
            // orderDto.setUser(userDto);
        }

        // 複製 Comment 資訊
        // 檢查關聯是否已初始化且不為 null
        if (orderBean.getComment() != null && org.hibernate.Hibernate.isInitialized(orderBean.getComment())) {
            OrderDto.OrderCommentDto commentDto = new OrderDto.OrderCommentDto();
            commentDto.setId(orderBean.getComment().getId());
            commentDto.setContent(orderBean.getComment().getContent());
            commentDto.setScore(orderBean.getComment().getScore());
            // ... 複製其他評論屬性
            orderDto.setComment(commentDto);
        }

        // 複製 OrderDetails 及其子關聯 (Food, LikedFoods)
        // 檢查集合是否已初始化且不為 null
//     	isInitialized在觸發初始化之前，安全地檢查一個物件或集合是否已經初始化，從而避免 LazyInitializationException
        if (orderBean.getOrderDetails() != null && org.hibernate.Hibernate.isInitialized(orderBean.getOrderDetails())) {
            List<OrderDetailDto> orderDetailDtos = orderBean.getOrderDetails().stream()
                .map(orderDetail -> {
                    OrderDetailDto odDto = new OrderDetailDto();
                    odDto.setId(orderDetail.getId());
                    odDto.setQuantity(orderDetail.getQuantity());
                    odDto.setPrice(orderDetail.getPrice());
                    odDto.setSubTotal(orderDetail.getSubTotal());

                    // 複製 Food 資訊
                    if (orderDetail.getFood() != null && org.hibernate.Hibernate.isInitialized(orderDetail.getFood())) {
                    	FoodDto foodDto = new FoodDto();
                        foodDto.setId(orderDetail.getFood().getId());
                        foodDto.setName(orderDetail.getFood().getName());
                        foodDto.setPrice(orderDetail.getFood().getPrice());
                        foodDto.setScore(orderDetail.getFood().getScore());
                        // ... 複製其他食物屬性
                        odDto.setFood(foodDto);
                    }

                    // 複製 LikedFoods 資訊
                    // 檢查集合是否已初始化且不為 null
//                    if (orderDetail.getLikedFoods() != null && org.hibernate.Hibernate.isInitialized(orderDetail.getLikedFoods())) {
//                        List<LikedFoodDto> likedFoodDtos = orderDetail.getLikedFoods().stream()
//                            .map(likedFood -> {
//                                LikedFoodDto lfDto = new LikedFoodDto();
//                                lfDto.setId(likedFood.getId());
//                                lfDto.setIsLiked(likedFood.getIsLiked());
//                                // ... 複製其他 LikedFood 屬性 (例如 UserId)
//                                return lfDto;
//                            }).collect(Collectors.toList());
//                        odDto.setLikedFoods(likedFoodDtos);
//                    }

                    return odDto;
                }).collect(Collectors.toList());
            orderDto.setOrderDetails(orderDetailDtos);
        }

        return orderDto;
    }
    
}