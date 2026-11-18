package com.back.domain.post.post.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // 이 클래스는 DB의 post 테이블과 연결된다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요하다. 외부에서 막 쓰지 못하게 protected.
public class Post extends BaseEntity { // id, createDate, modifyDate 는 BaseEntity 에서 물려받는다.
    private String title;

    @Column(columnDefinition = "TEXT") // 내용은 길 수 있으므로 VARCHAR(255) 대신 TEXT
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
