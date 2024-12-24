package com.backend.spring.payload.request;

import lombok.Data;

@Data
public class FeedbackDto {
    private String name;     // Tên người gửi phản hồi
    private String email;    // Địa chỉ email của người gửi phản hồi
    private String review;   // Nội dung đánh giá hoặc ý kiến
    private Integer rating;  // Điểm số đánh giá (có thể là số nguyên hoặc số thực, tùy theo yêu cầu)
}
