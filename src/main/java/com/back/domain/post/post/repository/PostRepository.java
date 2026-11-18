package com.back.domain.post.post.repository;

import com.back.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 인터페이스만 선언하면 Spring Data JPA가 구현체를 자동으로 만들어준다.
// save(), findById(), findAll(), count(), delete() 등을 바로 쓸 수 있다.
// JpaRepository<엔티티 타입, ID 타입>
public interface PostRepository extends JpaRepository<Post, Long> {
    // 쿼리 메서드 : 메서드 이름만 규칙대로 지으면 JPA가 SQL을 만들어준다.
    // findAll + OrderBy + Id + Desc → SELECT * FROM post ORDER BY id DESC
    List<Post> findAllByOrderByIdDesc();
}
