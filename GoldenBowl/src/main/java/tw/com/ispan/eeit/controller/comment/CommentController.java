package tw.com.ispan.eeit.controller.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.repository.comment.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 創建評論
    @PostMapping
    public ResponseEntity<CommentBean> create(@RequestBody CommentBean commentBean) {
        try {
            CommentBean savedComment = commentService.create(commentBean);
            return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 更新評論
    @PutMapping("/{id}")
    public ResponseEntity<CommentBean> update(@PathVariable Integer id, @RequestBody CommentBean commentBean) {
        try {
            CommentBean updatedComment = commentService.update(id, commentBean);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 根據 ID 查詢評論
    @GetMapping("/{id}")
    public ResponseEntity<CommentBean> getById(@PathVariable Integer id) {
        Optional<CommentBean> comment = commentService.findById(id);
        if (comment.isPresent()) {
            return new ResponseEntity<>(comment.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 查詢所有評論
    @GetMapping
    public ResponseEntity<List<CommentBean>> getAll() {
        List<CommentBean> comments = commentService.findAll();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 根據 orderId 查詢評論
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<CommentBean>> getByOrderId(@PathVariable Integer orderId) {
        List<CommentBean> comments = commentService.findByOrderId(orderId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 根據 userId 查詢評論
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentBean>> getByUserId(@PathVariable Integer userId) {
        List<CommentBean> comments = commentService.findByUserId(userId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 根據 storeId 查詢評論
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<CommentBean>> getByStoreId(@PathVariable Integer storeId) {
        List<CommentBean> comments = commentService.findByStoreId(storeId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 根據 isHidden 查詢評論
    @GetMapping("/hidden/{isHidden}")
    public ResponseEntity<List<CommentBean>> getByIsHidden(@PathVariable Boolean isHidden) {
        List<CommentBean> comments = commentService.findByIsHidden(isHidden);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    // 刪除評論
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        try {
            commentService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
