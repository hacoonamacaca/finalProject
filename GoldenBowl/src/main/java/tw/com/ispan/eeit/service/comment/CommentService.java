package tw.com.ispan.eeit.service.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.repository.comment.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // 創建評論
    public CommentBean createComment(CommentBean comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setIsHidden(false); // 預設不隱藏
        return commentRepository.save(comment);
    }

    // 根據 ID 查找評論
    public Optional<CommentBean> findCommentById(Integer id) {
        return commentRepository.findById(id);
    }

    // 新增此方法以根據 storeId 查找評論列表
    public List<CommentBean> findByStoreId(Integer storeId) {
        return commentRepository.findByStoreId(storeId);
    }

    // 查找所有評論
    public List<CommentBean> findAll() {
        return commentRepository.findAll();
    }

    // 更新評論
    public Optional<CommentBean> updateComment(Integer id, CommentBean updatedComment) {
        return commentRepository.findById(id).map(comment -> {
            comment.setContent(updatedComment.getContent());
            comment.setScore(updatedComment.getScore());
            comment.setReply(updatedComment.getReply());
            comment.setReplyUpdateTime(
                    updatedComment.getReply() != null ? LocalDateTime.now() : comment.getReplyUpdateTime());
            comment.setIsHidden(updatedComment.getIsHidden());
            return commentRepository.save(comment);
        });
    }

    // 刪除評論（邏輯刪除，將 isHidden 設為 true）
    public void deleteById(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Record with ID " + id + " does not exist");
        }
        commentRepository.deleteById(id);

    }
}