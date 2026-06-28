package com.example.api_blog.service;

import com.example.api_blog.model.request.CommentRequest;
import com.example.api_blog.model.response.CommentResponse;

public interface CommentService {
    CommentResponse addComment(CommentRequest commentRequest);
    void deleteComment(long commentId);
    CommentResponse updateComment(long commentId, CommentRequest commentRequest);
}
