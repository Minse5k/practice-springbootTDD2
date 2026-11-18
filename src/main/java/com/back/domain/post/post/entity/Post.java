package com.back.domain.post.post.entity;

import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity // 이 클래스는 DB의 post 테이블과 연결된다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필요하다. 외부에서 막 쓰지 못하게 protected.
public class Post extends BaseEntity { // id, createDate, modifyDate 는 BaseEntity 에서 물려받는다.
    private String title;

    @Column(columnDefinition = "TEXT") // 내용은 길 수 있으므로 VARCHAR(255) 대신 TEXT
    private String content;

    // 글 1개 : 댓글 N개 → "일대다(1:N)"
    // mappedBy = "post" : 연관관계의 주인은 PostComment.post 필드다.
    //                     (DB 의 post_id 외래키는 post_comment 테이블에 있으므로)
    //                     이 리스트는 "읽기용 거울"이라서 post 테이블에 컬럼이 추가되지 않는다.
    // cascade = REMOVE  : 글을 삭제하면 이 리스트에 있는 댓글들도 JPA 가 먼저 삭제해준다.
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<PostComment> comments = new ArrayList<>(); // null 대신 빈 리스트로 시작

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // setter 대신 "의미 있는 이름"의 메서드로 수정한다.
    // → 글을 수정하는 방법은 이 메서드 하나뿐이라는 것이 코드로 드러난다.
    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // "이 글의" 댓글 중에서 id 가 같은 댓글을 찾는다.
    // 다른 글의 댓글 번호를 넣으면 빈 Optional → 남의 글 댓글을 조회할 수 없다.
    public Optional<PostComment> findCommentById(long id) {
        return comments
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst();
    }
}
