package com.back.domain.post.postComment.entity;

import com.back.domain.post.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

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
}
