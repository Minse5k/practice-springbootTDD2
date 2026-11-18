package com.back.domain.post.post.repository;

import com.back.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// 인터페이스만 선언하면 Spring Data JPA가 구현체를 자동으로 만들어준다.
// save(), findById(), findAll(), count(), delete() 등을 바로 쓸 수 있다.
// JpaRepository<엔티티 타입, ID 타입>
public interface PostRepository extends JpaRepository<Post, Long> {
}
