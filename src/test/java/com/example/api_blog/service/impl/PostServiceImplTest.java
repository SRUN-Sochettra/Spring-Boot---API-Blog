package com.example.api_blog.service.impl;

import com.example.api_blog.exception.ForbiddenException;
import com.example.api_blog.exception.ResourceNotFoundException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepo postRepo;

    @Mock
    private PinataService pinataService;

    @Mock
    private PostImageRepo postImageRepo;

    @Mock
    private AuthRepo authRepo;

    @Mock
    private PostLikeRepo postLikeRepo;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostServiceImpl postService;

    private Auth auth;
    private PostResponse postResponse;
    private PostRequest postRequest;

    @BeforeEach
    void setUp() {
        auth = new Auth();
        auth.setUserId(1L);
        auth.setEmail("test@example.com");
        auth.setUserName("testUser");

        postResponse = new PostResponse();
        postResponse.setPostId(1L);
        postResponse.setTitle("Test Post");
        postResponse.setDescription("Test Description");
        postResponse.setUser(new UserResponse(1L, "testUser", "test@example.com"));

        postRequest = new PostRequest();
        postRequest.setTitle("New Title");
        postRequest.setDescription("New Description");
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllPosts() {
        when(postRepo.getAllPosts()).thenReturn(Collections.singletonList(postResponse));

        // Mock anonymous user context for populateLikedByCurrentUser
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        List<PostResponse> posts = postService.getAllPosts();

        assertFalse(posts.isEmpty());
        assertEquals(1, posts.size());
        verify(postRepo, times(1)).getAllPosts();
    }

    @Test
    void testGetPostById() {
        when(postRepo.getPostById(1L)).thenReturn(postResponse);

        // Mock anonymous user context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        PostResponse response = postService.getPostById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPostId());
        verify(postRepo, times(1)).getPostById(1L);
    }

    @Test
    void testGetMyPosts() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostsByUserId(1L)).thenReturn(Collections.singletonList(postResponse));
        when(postLikeRepo.hasUserLikedPost(1L, 1L)).thenReturn(true);

        List<PostResponse> posts = postService.getMyPosts();

        assertFalse(posts.isEmpty());
        assertTrue(posts.get(0).isLikedByCurrentUser());
        verify(postRepo, times(1)).getPostsByUserId(1L);
    }

    @Test
    void testDeletePostSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostById(1L)).thenReturn(postResponse);
        doNothing().when(postRepo).deletePost(1L);

        assertDoesNotThrow(() -> postService.deletePost(1L));
        verify(postRepo, times(1)).deletePost(1L);
    }

    @Test
    void testDeletePostForbidden() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);

        PostResponse otherUserPost = new PostResponse();
        otherUserPost.setPostId(1L);
        otherUserPost.setUser(new UserResponse(2L, "otherUser", "other@example.com"));
        when(postRepo.getPostById(1L)).thenReturn(otherUserPost);

        assertThrows(ForbiddenException.class, () -> postService.deletePost(1L));
        verify(postRepo, never()).deletePost(anyLong());
    }

    @Test
    void testUpdatePostSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostById(1L)).thenReturn(postResponse);
        doNothing().when(postRepo).updatePost(any(Post.class));

        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        MultipartFile[] files = {file};

        when(pinataService.uploadFile(any(MultipartFile.class))).thenReturn("http://ipfs.io/ipfs/testHash");
        doNothing().when(postImageRepo).insertImage(anyList());

        PostResponse response = postService.updatePost(1L, postRequest, files);

        assertNotNull(response);
        verify(postRepo, times(1)).updatePost(any(Post.class));
        verify(pinataService, times(1)).uploadFile(any(MultipartFile.class));
        verify(postImageRepo, times(1)).insertImage(anyList());
    }

    @Test
    void testAddPostSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        doNothing().when(postRepo).addPost(any(Post.class));

        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        MultipartFile[] files = {file};

        when(pinataService.uploadFile(any(MultipartFile.class))).thenReturn("http://ipfs.io/ipfs/testHash");
        doNothing().when(postImageRepo).insertImage(anyList());

        PostResponse response = postService.addPost(postRequest, files);

        assertNotNull(response);
        assertEquals("New Title", response.getTitle());
        assertEquals("testUser", response.getUser().getUserName());
        assertFalse(response.getImages().isEmpty());
        verify(postRepo, times(1)).addPost(any(Post.class));
        verify(pinataService, times(1)).uploadFile(any(MultipartFile.class));
        verify(postImageRepo, times(1)).insertImage(anyList());
    }

    @Test
    void testAddPostUserNotFound() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> postService.addPost(postRequest, new MultipartFile[0]));
        verify(postRepo, never()).addPost(any(Post.class));
    }

    @Test
    void testLikePostSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostById(1L)).thenReturn(postResponse);
        doNothing().when(postLikeRepo).likePost(1L, 1L);

        assertDoesNotThrow(() -> postService.likePost(1L));
        verify(postLikeRepo, times(1)).likePost(1L, 1L);
    }

    @Test
    void testLikePostNotFound() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        when(postRepo.getPostById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> postService.likePost(1L));
        verify(postLikeRepo, never()).likePost(anyLong(), anyLong());
    }

    @Test
    void testUnlikePostSuccess() {
        mockSecurityContext();
        when(authRepo.findByEmail("test@example.com")).thenReturn(auth);
        doNothing().when(postLikeRepo).unlikePost(1L, 1L);

        assertDoesNotThrow(() -> postService.unlikePost(1L));
        verify(postLikeRepo, times(1)).unlikePost(1L, 1L);
    }
}
