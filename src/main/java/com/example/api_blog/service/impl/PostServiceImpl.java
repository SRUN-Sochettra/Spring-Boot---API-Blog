package com.example.api_blog.service.impl;

import com.example.api_blog.model.entity.Auth;
import com.example.api_blog.model.entity.Post;
import com.example.api_blog.model.entity.PostImage;
import com.example.api_blog.model.request.PostRequest;
import com.example.api_blog.model.response.PostResponse;
import com.example.api_blog.model.response.UserResponse;
import com.example.api_blog.repository.AuthRepo;
import com.example.api_blog.repository.PostImageRepo;
import com.example.api_blog.repository.PostLikeRepo;
import com.example.api_blog.repository.PostRepo;
import com.example.api_blog.service.PinataService;
import com.example.api_blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.api_blog.exception.ResourceNotFoundException;
import com.example.api_blog.exception.ForbiddenException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final PinataService pinataService;
    private final PostImageRepo postImageRepo;
    private final AuthRepo authRepo;
    private final PostLikeRepo postLikeRepo;

    @Override
    public List<PostResponse> getAllPosts() {
        List<PostResponse> posts = postRepo.getAllPosts();
        populateLikedByCurrentUser(posts);
        return posts;
    }

    @Override
    public PostResponse getPostById(long id) {
        PostResponse responsePost = postRepo.getPostById(id);
        if (responsePost != null) {
            List<PostResponse> posts = new ArrayList<>();
            posts.add(responsePost);
            populateLikedByCurrentUser(posts);
        }
        return responsePost;
    }

    @Override
    public List<PostResponse> getMyPosts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);
        if (auth == null) {
            throw new ResourceNotFoundException("User not found");
        }
        List<PostResponse> posts = postRepo.getPostsByUserId(auth.getUserId());
        populateLikedByCurrentUser(posts);
        return posts;
    }

    @Override
    @Transactional
    public void deletePost(long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);
        if (auth == null) {
            throw new ResourceNotFoundException("User not found");
        }

        PostResponse post = postRepo.getPostById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (post.getUser().getUserId() != auth.getUserId()) {
            throw new ForbiddenException("You are not authorized to delete this post");
        }

        postRepo.deletePost(id);
    }

    @Override
    @Transactional
    public PostResponse updatePost(long id, PostRequest postRequest, MultipartFile[] files) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);
        if (auth == null) {
            throw new ResourceNotFoundException("User not found");
        }

        PostResponse existingPost = postRepo.getPostById(id);
        if (existingPost == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (existingPost.getUser().getUserId() != auth.getUserId()) {
            throw new ForbiddenException("You are not authorized to update this post");
        }

        Post post = new Post();
        post.setPostId(id);
        post.setTitle(postRequest.getTitle() != null ? postRequest.getTitle() : existingPost.getTitle());
        post.setDescription(postRequest.getDescription() != null ? postRequest.getDescription() : existingPost.getDescription());

        postRepo.updatePost(post);

        if (files != null && files.length > 0) {
            List<PostImage> images = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String url = pinataService.uploadFile(file);
                    PostImage postImage = new PostImage();
                    postImage.setPostId(id);
                    postImage.setImageUrl(url);
                    images.add(postImage);
                }
            }

            if (!images.isEmpty()) {
                postImageRepo.insertImage(images);
            }
        }

        PostResponse responsePost = postRepo.getPostById(id);
        if (responsePost != null) {
            List<PostResponse> posts = new ArrayList<>();
            posts.add(responsePost);
            populateLikedByCurrentUser(posts);
        }
        return responsePost;
    }

    @Override
    @Transactional
    public PostResponse addPost(PostRequest postRequest, MultipartFile[] files) {

        // FIXED: get userId from JWT, not from request body
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);

        if (auth == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setDescription(postRequest.getDescription());
        post.setUserId(auth.getUserId()); // FIXED: use trusted userId from JWT

        postRepo.addPost(post);

        long postId = post.getPostId();

        List<PostImage> images = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = pinataService.uploadFile(file);
            PostImage postImage = new PostImage();
            postImage.setPostId(postId);
            postImage.setImageUrl(url);
            images.add(postImage);
        }

        if (!images.isEmpty()) {
            postImageRepo.insertImage(images);
        }

        // FIXED: use UserResponse instead of Auth to avoid exposing password
        return PostResponse.builder()
                .postId(postId)
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .user(new UserResponse(auth.getUserId(), auth.getUserName(), auth.getEmail()))
                .images(images)
                .build();
    }

    private void populateLikedByCurrentUser(List<PostResponse> posts) {
        if (posts == null || posts.isEmpty()) return;

        // Try to get current user ID
        long userId = -1;
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if (email != null && !email.equals("anonymousUser")) {
                Auth auth = authRepo.findByEmail(email);
                if (auth != null) {
                    userId = auth.getUserId();
                }
            }
        } catch (Exception e) {
            // Ignore authentication exceptions for public endpoints
        }

        if (userId != -1) {
            for (PostResponse post : posts) {
                post.setLikedByCurrentUser(postLikeRepo.hasUserLikedPost(post.getPostId(), userId));
            }
        }
    }

    @Override
    @Transactional
    public void likePost(long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);
        if (auth == null) {
            throw new ResourceNotFoundException("User not found");
        }

        PostResponse post = postRepo.getPostById(postId);
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }

        postLikeRepo.likePost(postId, auth.getUserId());
    }

    @Override
    @Transactional
    public void unlikePost(long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);
        if (auth == null) {
            throw new ResourceNotFoundException("User not found");
        }

        postLikeRepo.unlikePost(postId, auth.getUserId());
    }
}