package com.example.api_blog.service.impl;

import com.example.api_blog.model.entity.Auth;
import com.example.api_blog.model.entity.Post;
import com.example.api_blog.model.entity.PostImage;
import com.example.api_blog.model.request.PostRequest;
import com.example.api_blog.model.response.PostResponse;
import com.example.api_blog.model.response.UserResponse;
import com.example.api_blog.repository.AuthRepo;
import com.example.api_blog.repository.PostImageRepo;
import com.example.api_blog.repository.PostRepo;
import com.example.api_blog.service.PinataService;
import com.example.api_blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepo.getAllPosts();
    }

    @Override
    public PostResponse getPostById(long id) {
        return postRepo.getPostById(id);
    }

    @Override
    @Transactional
    public PostResponse addPost(PostRequest postRequest, MultipartFile[] files) {

        // FIXED: get userId from JWT, not from request body
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Auth auth = authRepo.findByEmail(email);

        if (auth == null) {
            throw new RuntimeException("User not found");
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
}