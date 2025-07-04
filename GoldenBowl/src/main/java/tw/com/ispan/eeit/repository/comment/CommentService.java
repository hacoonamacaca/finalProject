package tw.com.ispan.eeit.repository.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.comment.CommentBean;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 創建評論
    public CommentBean create(CommentBean commentBean) {
        if (commentBean.getOrderId() == null || commentBean.getUserId() == null || commentBean.getStoreId() == null) {
            throw new IllegalArgumentException("orderId, userId, and storeId cannot be null");
        }
        if (commentBean.getContent() == null || commentBean.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (commentBean.getScore() == null || commentBean.getScore() < 0 || commentBean.getScore() > 5) {
            throw new IllegalArgumentException("Score must be between 0 and 5");
        }
        commentBean.setCreateTime(LocalDateTime.now());
        commentBean.setIsHidden(false);
        return commentRepository.save(commentBean);
    }

    // 更新評論
    public CommentBean update(Integer id, CommentBean commentBean) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment with ID " + id + " does not exist");
        }
        commentBean.setId(id);
        if (commentBean.getReply() != null) {
            commentBean.setReplyUpdateTime(LocalDateTime.now());
        }
        return commentRepository.save(commentBean);
    }

    // 根據 ID 查詢單筆評論
    public Optional<CommentBean> findById(Integer id) {
        return commentRepository.findById(id);
    }

    // 查詢所有評論
    public List<CommentBean> findAll() {
        return commentRepository.findAll();
    }

    // 根據 orderId 查詢評論
    public List<CommentBean> findByOrderId(Integer orderId) {
        return commentRepository.findByOrderId(orderId);
    }

    // 根據 userId 查詢評論
    public List<CommentBean> findByUserId(Integer userId) {
        return commentRepository.findByUserId(userId);
    }

    // 根據 storeId 查詢評論
    public List<CommentBean> findByStoreId(Integer storeId) {
        return commentRepository.findByStoreId(storeId);
    }

    // 根據 isHidden 查詢評論
    public List<CommentBean> findByIsHidden(Boolean isHidden) {
        return commentRepository.findByIsHidden(isHidden);
    }

    // 刪除評論
    public void deleteById(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment with ID " + id + " does not exist");
        }
        commentRepository.deleteById(id);
    }
}
