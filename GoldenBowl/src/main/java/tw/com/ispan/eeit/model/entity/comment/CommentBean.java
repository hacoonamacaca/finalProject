package tw.com.ispan.eeit.model.entity.comment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
public class CommentBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "store_id")
    private Integer storeId;

    @Column(length = 500)
    private String content;

    private Integer score;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(length = 500)
    private String reply;

    @Column(name = "reply_update_time")
    private LocalDateTime replyUpdateTime;

    @Column(name = "is_hidden")
    private Boolean isHidden = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    @JsonIgnore
    private OrderBean order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private UserBean user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    @JsonIgnore
    private StoreBean store;
}