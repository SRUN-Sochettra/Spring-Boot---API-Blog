package com.example.api_blog.repository;

import com.example.api_blog.model.entity.Post;
import com.example.api_blog.model.response.PostResponse;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PostRepo {
    @Insert("insert into posts(title,description,user_id,created_at) values (#{title},#{description},#{userId}, now())")

    @Results(id = "PostMapper",value = {
            @Result(property = "title",column = "title"),
            @Result(property = "description",column = "description"),
            @Result(property = "userId",column = "user_id")

    })
    @Options(useGeneratedKeys = true, keyProperty = "postId", keyColumn = "post_id")
    void addPost(Post post);

    @Select("select post_id, title, description, user_id from posts order by created_at desc")
    @Results({
            @Result(property = "postId", column = "post_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "user", column = "user_id", javaType = com.example.api_blog.model.response.UserResponse.class,
                    one = @One(select = "com.example.api_blog.repository.AuthRepo.findUserResponseById")),
            @Result(property = "images", column = "post_id", javaType = List.class,
                    many = @Many(select = "com.example.api_blog.repository.PostImageRepo.findByPostId")),
            @Result(property = "comments", column = "post_id", javaType = List.class,
                    many = @Many(select = "com.example.api_blog.repository.CommentRepo.findByPostId")),
            @Result(property = "likeCount", column = "post_id", javaType = Integer.class,
                    one = @One(select = "com.example.api_blog.repository.PostLikeRepo.getLikeCountForPost"))
    })
    List<PostResponse> getAllPosts();

    @Select("select post_id, title, description, user_id from posts where post_id = #{id}")
    @Results({
            @Result(property = "postId", column = "post_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "user", column = "user_id", javaType = com.example.api_blog.model.response.UserResponse.class,
                    one = @One(select = "com.example.api_blog.repository.AuthRepo.findUserResponseById")),
            @Result(property = "images", column = "post_id", javaType = List.class,
                    many = @Many(select = "com.example.api_blog.repository.PostImageRepo.findByPostId")),
            @Result(property = "comments", column = "post_id", javaType = List.class,
                    many = @Many(select = "com.example.api_blog.repository.CommentRepo.findByPostId")),
            @Result(property = "likeCount", column = "post_id", javaType = Integer.class,
                    one = @One(select = "com.example.api_blog.repository.PostLikeRepo.getLikeCountForPost"))
    })
    PostResponse getPostById(long id);

    @Delete("delete from posts where post_id = #{postId}")
    void deletePost(long postId);

    @Update("update posts set title = #{title}, description = #{description} where post_id = #{postId}")
    void updatePost(Post post);

    @Select("select post_id, title, description, user_id from posts where user_id = #{userId} order by created_at desc")
    @Results({
            @Result(property = "postId", column = "post_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "user", column = "user_id", javaType = com.example.api_blog.model.response.UserResponse.class,
                    one = @One(select = "com.example.api_blog.repository.AuthRepo.findUserResponseById")),
            @Result(property = "images", column = "post_id", javaType = List.class,
                    many = @Many(select = "com.example.api_blog.repository.PostImageRepo.findByPostId")),
            @Result(property = "comments", column = "post_id", javaType = List.class,
                    many = @Many(select = "com.example.api_blog.repository.CommentRepo.findByPostId")),
            @Result(property = "likeCount", column = "post_id", javaType = Integer.class,
                    one = @One(select = "com.example.api_blog.repository.PostLikeRepo.getLikeCountForPost"))
    })
    List<PostResponse> getPostsByUserId(long userId);
}
