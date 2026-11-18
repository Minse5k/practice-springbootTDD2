package com.back.domain.post.postComment.entity;

import com.back.domain.post.post.entity.Post;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment extends BaseEntity {
    // 댓글 N개 : 글 1개 → "다대일(N:1)"
    // DB에는 post_comment 테이블에 post_id 컬럼(외래키, FK)으로 저장된다.
    // LAZY : 댓글을 조회할 때 글까지 바로 가져오지 않고, 실제로 post 를 사용할 때 가져온다.
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(columnDefinition = "TEXT")
    private String content;

    public PostComment(Post post, String content) {
        this.post = post;
        this.content = content;
    }

    // 글과 마찬가지로 setter 대신 의미 있는 이름의 수정 메서드
    public void modify(String content) {
        this.content = content;
    }
}
