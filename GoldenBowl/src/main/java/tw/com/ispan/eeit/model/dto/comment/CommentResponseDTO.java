package tw.com.ispan.eeit.model.dto.comment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Integer id;
    private String content;
    private Integer score;
    private LocalDateTime createTime;
    private String reply;
    private LocalDateTime replyUpdateTime;
    private Boolean isHidden; // 如果前端需要知道評論是否被隱藏

    // 來自 UserBean 的資訊
    private Integer userId; // 用戶 ID (可選)
    private String userName; // 使用者名稱

    // 如果前端需要，可以加入其他關聯實體的簡單資訊，例如：
    private Integer storeId;
    private String storeName;
}