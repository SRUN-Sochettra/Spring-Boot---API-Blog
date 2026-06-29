package com.example.api_blog.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PostLikeRepo {
    @Insert("insert into post_likes(post_id, user_id) values (#{postId}, #{userId}) on conflict do nothing")
    void likePost(@Param("postId") long postId, @Param("userId") long userId);

    @Delete("delete from post_likes where post_id = #{postId} and user_id = #{userId}")
    void unlikePost(@Param("postId") long postId, @Param("userId") long userId);

    @Select("select count(*) from post_likes where post_id = #{postId}")
    int getLikeCountForPost(long postId);

    @Select("select count(*) > 0 from post_likes where post_id = #{postId} and user_id = #{userId}")
    boolean hasUserLikedPost(@Param("postId") long postId, @Param("userId") long userId);
}
