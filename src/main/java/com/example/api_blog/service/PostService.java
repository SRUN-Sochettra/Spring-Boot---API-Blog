package com.example.api_blog.service;

import com.example.api_blog.model.request.PostRequest;
import com.example.api_blog.model.response.PostResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostResponse addPost(PostRequest postRequest, MultipartFile[] files);

    List<PostResponse> getAllPosts();
    PostResponse getPostById(long id);
    List<PostResponse> getMyPosts();
    void deletePost(long id);
    PostResponse updatePost(long id, PostRequest postRequest, MultipartFile[] files);
}