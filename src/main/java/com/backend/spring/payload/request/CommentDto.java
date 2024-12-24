package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class CommentDto {
    private Integer commentId;
    private Integer userId;
    private String text;
    private Integer parentId;

}