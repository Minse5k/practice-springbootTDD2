package com.back.domain.post.postComment.dto;

import com.back.domain.post.postComment.entity.PostComment;

import java.time.LocalDateTime;

// 댓글 응답용 DTO
// 엔티티의 post(Post 객체 전체) 대신 postId(숫자)만 담는다.
// → JSON 이 간단해지고, Post ↔ PostComment 사이의 무한 순환도 막는다.
public record PostCommentDto(
        long id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        long postId,
        String content
) {
    public PostCommentDto(PostComment postComment) {
        this(
                postComment.getId(),
                postComment.getCreateDate(),
                postComment.getModifyDate(),
                postComment.getPost().getId(), // LAZY 프록시라도 id 는 이미 알고 있어서 추가 SELECT 가 없다.
                postComment.getContent()
        );
    }
}
