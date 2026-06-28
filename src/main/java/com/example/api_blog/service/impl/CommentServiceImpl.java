package com.example.api_blog.service.impl;

import com.example.api_blog.model.entity.Auth;
import com.example.api_blog.model.entity.Comment;
import com.example.api_blog.model.request.CommentRequest;
import com.example.api_blog.model.response.CommentResponse;
import com.example.api_blog.model.response.PostResponse;
import com.example.api_blog.model.response.UserResponse;
import com.example.api_blog.repository.AuthRepo;
import com.example.api_blog.repository.CommentRepo;
import com.example.api_blog.repository.PostRepo;
import com.example.api_blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;
    private final AuthRepo authRepo;
    private final PostRepo postRepo;

    @Override
    @Transactional
    public CommentResponse addComment(CommentRequest commentRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);

        if (auth == null) {
            throw new RuntimeException("User not found");
        }

        PostResponse post = postRepo.getPostById(commentRequest.getPostId());
        if (post == null) {
            throw new RuntimeException("Post not found");
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPostId(commentRequest.getPostId());
        comment.setUserId(auth.getUserId());

        commentRepo.addComment(comment);

        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .user(new UserResponse(auth.getUserId(), auth.getUserName(), auth.getEmail()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public void deleteComment(long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);

        if (auth == null) {
            throw new RuntimeException("User not found");
        }

        Comment comment = commentRepo.getCommentById(commentId);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }

        if (comment.getUserId() != auth.getUserId()) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepo.deleteComment(commentId);
    }
}
