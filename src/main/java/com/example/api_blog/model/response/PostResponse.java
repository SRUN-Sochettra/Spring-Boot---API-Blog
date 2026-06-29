package com.example.api_blog.model.response;

import com.example.api_blog.model.entity.PostImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private long postId;
    private String title;
    private String description;
    private UserResponse user;
    private List<PostImage> images;
    private List<CommentResponse> comments;
    private int likeCount;
    private boolean likedByCurrentUser;
}
