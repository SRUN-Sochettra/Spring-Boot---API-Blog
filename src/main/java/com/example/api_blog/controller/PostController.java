package com.example.api_blog.controller;

import com.example.api_blog.model.request.PostRequest;
import com.example.api_blog.model.response.ApiResponse;
import com.example.api_blog.model.response.PostResponse;
import com.example.api_blog.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@SecurityRequirement(name = "bearerAuth") // FIXED: only once, removed duplicate
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(
                new ApiResponse<>("Posts retrieved successfully", posts, 200, LocalDateTime.now())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable long id) {
        PostResponse post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.status(404).body(
                    new ApiResponse<>("Post not found", null, 404, LocalDateTime.now())
            );
        }
        return ResponseEntity.ok(
                new ApiResponse<>("Post retrieved successfully", post, 200, LocalDateTime.now())
        );
    }

    @PostMapping(value = "/add-post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> addPost(
            @ModelAttribute PostRequest postRequest,
            @RequestPart("files") MultipartFile[] files
    ) {
        PostResponse post = postService.addPost(postRequest, files);
        return ResponseEntity.ok(
                new ApiResponse<>("Post added successfully", post, 200, LocalDateTime.now())
        );
    }

    @GetMapping("/my-posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getMyPosts() {
        List<PostResponse> posts = postService.getMyPosts();
        return ResponseEntity.ok(
                new ApiResponse<>("Posts retrieved successfully", posts, 200, LocalDateTime.now())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable long id) {
        postService.deletePost(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Post deleted successfully", null, 200, LocalDateTime.now())
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable long id,
            @ModelAttribute PostRequest postRequest,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    ) {
        PostResponse post = postService.updatePost(id, postRequest, files);
        return ResponseEntity.ok(
                new ApiResponse<>("Post updated successfully", post, 200, LocalDateTime.now())
        );
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable long id) {
        postService.likePost(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Post liked successfully", null, 200, LocalDateTime.now())
        );
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(@PathVariable long id) {
        postService.unlikePost(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Post unliked successfully", null, 200, LocalDateTime.now())
        );
    }
}