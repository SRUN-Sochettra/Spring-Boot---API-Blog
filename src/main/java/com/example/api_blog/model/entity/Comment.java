package com.example.api_blog.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    private long commentId;
    private String content;
    private long postId;
    private long userId;
    private LocalDateTime createdAt;
}
