package com.example.api_blog.repository;

import com.example.api_blog.model.entity.PostImage;
import org.apache.ibatis.annotations.*;
import org.springframework.core.env.PropertyResolver;

import java.util.List;

@Mapper
public interface PostImageRepo {


    @Insert("""
    <script>
    INSERT INTO post_image (post_id, image_url) VALUES 
    <foreach collection='images' item='image' separator=','>
        (#{image.postId}, #{image.imageUrl})
    </foreach>
    </script>
""")
    @Results(id = "postImageMapper",value = {
            @Result(property = "postId",column = "post_id"),
            @Result(property = "imageUrl",column = "image_url")
    })
    void insertImage(@Param("images") List<PostImage> images);

    @Select("select * from post_image where post_id = #{postId}")
    @ResultMap("postImageMapper")
    List<PostImage> findByPostId(long postId);
}