package com.backend.spring.controller;

import com.backend.spring.entity.Comment;
import com.backend.spring.payload.request.CommentDto;
import com.backend.spring.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/rootComments")
    public ResponseEntity<List<Comment>> getRootCommentsByUserId(@PathVariable Long userId) {
        List<Comment> rootComments = commentService.getRootCommentsByUserId(userId);
        return new ResponseEntity<>(rootComments, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentDto commentDto) {
        Comment createdComment = commentService.createComment(commentDto);
        if (createdComment != null) {
            if (commentDto.getParentId() != null) {
                // Gửi thông báo chỉ khi có trả lời cho bình luận cha
                messagingTemplate.convertAndSend("/topic/newComment", createdComment);
            }
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }




}
