package com.example.api_blog.controller;

import com.example.api_blog.model.request.CommentRequest;
import com.example.api_blog.model.response.ApiResponse;
import com.example.api_blog.model.response.CommentResponse;
import com.example.api_blog.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add-comment")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.addComment(commentRequest);
        return ResponseEntity.ok(
                new ApiResponse<>("Comment added successfully", comment, 200, LocalDateTime.now())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Comment deleted successfully", null, 200, LocalDateTime.now())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable long id,
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse comment = commentService.updateComment(id, commentRequest);
        return ResponseEntity.ok(
                new ApiResponse<>("Comment updated successfully", comment, 200, LocalDateTime.now())
        );
    }
}
