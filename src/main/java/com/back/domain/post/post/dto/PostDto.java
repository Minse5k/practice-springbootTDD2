package com.back.domain.post.post.dto;

import com.back.domain.post.post.entity.Post;

import java.time.LocalDateTime;

// DTO(Data Transfer Object) : 클라이언트에게 "보여줄 데이터만" 담는 상자
// record : 필드, 생성자, getter(id(), title() ...), equals/hashCode/toString 을 자동으로 만들어주는 불변 클래스
public record PostDto(
        long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String title,
        String content
) {
    // 엔티티 → DTO 변환 생성자
    public PostDto(Post post) {
        this(
                post.getId(),
                post.getCreateDate(),
                post.getModifyDate(),
                post.getTitle(),
                post.getContent()
        );
    }
}
