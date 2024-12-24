package com.backend.spring.payload.response;

import java.util.List;

public class CommentResponse {
    private Integer id;
    private String text;
    private String user;
    private String date;
    private List<CommentResponse> parents;

    // Constructors, getters, and setters
}
