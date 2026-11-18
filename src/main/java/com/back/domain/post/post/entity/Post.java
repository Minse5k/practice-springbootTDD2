package com.back.domain.post.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // 이 클래스는 DB의 post 테이블과 연결된다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요하다. 외부에서 막 쓰지 못하게 protected.
@EntityListeners(AuditingEntityListener.class) // @CreatedDate, @LastModifiedDate 를 자동으로 채워준다.
public class Post {
    @Id // 기본키(PK)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // INSERT 할 때 DB가 1, 2, 3 ... 자동 증가
    private long id;

    @CreatedDate
    private LocalDateTime createDate; // 처음 저장될 때의 시각

    @LastModifiedDate
    private LocalDateTime modifyDate; // 마지막으로 수정된 시각

    private String title;

    @Column(columnDefinition = "TEXT") // 내용은 길 수 있으므로 VARCHAR(255) 대신 TEXT
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
