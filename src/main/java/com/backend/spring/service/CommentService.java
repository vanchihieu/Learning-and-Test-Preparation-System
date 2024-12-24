package com.backend.spring.service;

import com.backend.spring.entity.Comment;
import com.backend.spring.entity.User;
import com.backend.spring.payload.request.CommentDto;
import com.backend.spring.repository.CommentRepository;
import com.backend.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Comment> getAllComments() {
        List<Comment> allComments = commentRepository.findAll();
        List<Comment> rootComments = new ArrayList<>();
        for (Comment comment : allComments) {
            if (comment.getParentComment() == null) {
                rootComments.add(comment);
            }
        }
        // Sắp xếp danh sách bình luận theo thời gian giảm dần (từ mới nhất đến cũ nhất)
        Collections.sort(rootComments, (c1, c2) -> c2.getDate().compareTo(c1.getDate()));
        return rootComments;
    }

    public List<Comment> getRootCommentsByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Lấy tất cả các bình luận của người dùng
            List<Comment> userComments = commentRepository.findByUser(user);
            // Lọc ra các bình luận gốc (không có bình luận cha)
            List<Comment> rootComments = userComments.stream()
                    .filter(comment -> comment.getParentComment() == null)
                    .collect(Collectors.toList());
            // Sắp xếp danh sách bình luận theo thời gian giảm dần (từ mới nhất đến cũ nhất)
            Collections.sort(rootComments, (c1, c2) -> c2.getDate().compareTo(c1.getDate()));
            return rootComments;
        }
        return Collections.emptyList(); // hoặc có thể trả về null tùy thuộc vào logic của bạn
    }

    public Comment createComment(CommentDto commentDto) {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(commentDto.getUserId()));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setText(commentDto.getText());
            comment.setDate(LocalDateTime.now());

            if (commentDto.getParentId() != null) {
                Optional<Comment> parentCommentOptional = commentRepository.findById(commentDto.getParentId());

                if (parentCommentOptional.isPresent()) {
                    Comment parentComment = parentCommentOptional.get();
                    comment.setParentComment(parentComment);
                    parentComment.getReplies().add(comment); // Thêm phản hồi vào danh sách phản hồi của bình luận cha
                    commentRepository.save(parentComment); // Cập nhật bình luận cha
                    return parentComment; // Trả về bình luận
                }
            }

            return commentRepository.save(comment);
        }
        return null;
    }

}
