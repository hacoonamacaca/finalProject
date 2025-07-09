package tw.com.ispan.eeit.controller.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.comment.CommentResponseDTO;
import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.service.comment.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 創建評論
    @PostMapping
    public ResponseEntity<CommentBean> createComment(@RequestBody CommentBean comment) {
        CommentBean savedComment = commentService.createComment(comment);
        return ResponseEntity.ok(savedComment);
    }

    // 根據 ID 查找評論 (返回 DTO)
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Integer id) {
        return commentService.findCommentDtoById(id) // 調用返回 DTO 的方法
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 查找所有評論 (返回 DTO 列表)
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        List<CommentResponseDTO> comments = commentService.findAllAsDto(); // 調用返回 DTO 列表的方法
        return ResponseEntity.ok(comments);
    }

    // 更新評論
    @PutMapping("/{id}")
    public ResponseEntity<CommentBean> updateComment(@PathVariable Integer id, @RequestBody CommentBean comment) {
        return commentService.updateComment(id, comment)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 刪除評論（邏輯刪除）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer id) {
        try {
            commentService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 根據 Store ID 查找評論列表 (返回 DTO 列表)
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByStoreId(@PathVariable Integer storeId) {
        List<CommentResponseDTO> comments = commentService.findByStoreIdAsDto(storeId); // 調用返回 DTO 列表的方法
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }
}