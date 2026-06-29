package com.example.api_blog.service.impl;

import com.example.api_blog.exception.ForbiddenException;
import com.example.api_blog.exception.ResourceNotFoundException;
import com.example.api_blog.model.entity.Auth;
import com.example.api_blog.model.entity.Comment;
import com.example.api_blog.model.request.CommentRequest;
import com.example.api_blog.model.response.CommentResponse;
import com.example.api_blog.model.response.PostResponse;
import com.example.api_blog.repository.AuthRepo;
import com.example.api_blog.repository.CommentRepo;
import com.example.api_blog.repository.PostRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private AuthRepo authRepo;

    @Mock
    private PostRepo postRepo;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Auth auth;
    private PostResponse postResponse;
    private Comment comment;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        auth = new Auth();
        auth.setUserId(1L);
        auth.setEmail("test@example.com");
        auth.setUserName("testUser");

        postResponse = new PostResponse();
        postResponse.setPostId(1L);

        comment = new Comment();
        comment.setCommentId(1L);
        comment.setPostId(1L);
        comment.setUserId(1L);
        comment.setContent("Old comment content");
        comment.setCreatedAt(LocalDateTime.now());

        commentRequest = new CommentRequest();
        commentRequest.setPostId(1L);
        commentRequest.setContent("New comment content");
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddCommentSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostById(1L)).thenReturn(postResponse);
        doNothing().when(commentRepo).addComment(any(Comment.class));

        CommentResponse response = commentService.addComment(commentRequest);

        assertNotNull(response);
        assertEquals("New comment content", response.getContent());
        assertEquals("testUser", response.getUser().getUserName());
        verify(commentRepo, times(1)).addComment(any(Comment.class));
    }

    @Test
    void testAddCommentUserNotFound() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentRequest));
        verify(commentRepo, never()).addComment(any(Comment.class));
    }

    @Test
    void testAddCommentPostNotFound() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> commentService.addComment(commentRequest));
        verify(commentRepo, never()).addComment(any(Comment.class));
    }

    @Test
    void testDeleteCommentSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(commentRepo.getCommentById(1L)).thenReturn(comment);
        doNothing().when(commentRepo).deleteComment(1L);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));
        verify(commentRepo, times(1)).deleteComment(1L);
    }

    @Test
    void testDeleteCommentForbidden() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        Comment otherUserComment = new Comment();
        otherUserComment.setUserId(2L);
        when(commentRepo.getCommentById(1L)).thenReturn(otherUserComment);

        assertThrows(ForbiddenException.class, () -> commentService.deleteComment(1L));
        verify(commentRepo, never()).deleteComment(anyLong());
    }

    @Test
    void testUpdateCommentSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(commentRepo.getCommentById(1L)).thenReturn(comment);
        doNothing().when(commentRepo).updateComment(any(Comment.class));

        CommentResponse response = commentService.updateComment(1L, commentRequest);

        assertNotNull(response);
        assertEquals("New comment content", response.getContent());
        verify(commentRepo, times(1)).updateComment(any(Comment.class));
    }

    @Test
    void testUpdateCommentNotFound() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(commentRepo.getCommentById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, commentRequest));
        verify(commentRepo, never()).updateComment(any(Comment.class));
    }
}
