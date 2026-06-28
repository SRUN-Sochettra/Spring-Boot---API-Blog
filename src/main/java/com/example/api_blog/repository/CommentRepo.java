package com.example.api_blog.repository;

import com.example.api_blog.model.entity.Comment;
import com.example.api_blog.model.response.CommentResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentRepo {
    @Insert("insert into comments(content, post_id, user_id, created_at) values (#{content}, #{postId}, #{userId}, now())")
    @Options(useGeneratedKeys = true, keyProperty = "commentId", keyColumn = "comment_id")
    void addComment(Comment comment);

    @Select("select comment_id, content, user_id, created_at from comments where post_id = #{postId} order by created_at desc")
    @Results({
            @Result(property = "commentId", column = "comment_id"),
            @Result(property = "content", column = "content"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "user", column = "user_id", javaType = com.example.api_blog.model.response.UserResponse.class,
                    one = @One(select = "com.example.api_blog.repository.AuthRepo.findUserResponseById"))
    })
    List<CommentResponse> findByPostId(long postId);

    @Delete("delete from comments where comment_id = #{commentId}")
    void deleteComment(long commentId);

    @Select("select comment_id, content, post_id, user_id, created_at from comments where comment_id = #{commentId}")
    @Results({
        @Result(property = "commentId", column = "comment_id"),
        @Result(property = "content", column = "content"),
        @Result(property = "postId", column = "post_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "createdAt", column = "created_at")
    })
    Comment getCommentById(long commentId);
}
