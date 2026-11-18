package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// 서비스 : 비즈니스 로직 담당. 컨트롤러와 리포지터리 사이의 중간 다리.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 "읽기 전용" 트랜잭션. 쓰기 메서드에는 따로 @Transactional 을 붙인다.
public class PostService {
    private final PostRepository postRepository;

    public List<Post> findAllByOrderByIdDesc() {
        return postRepository.findAllByOrderByIdDesc();
    }

    // Optional : "글이 있을 수도, 없을 수도 있다"를 타입으로 표현
    public Optional<Post> findById(long id) {
        return postRepository.findById(id);
    }

    @Transactional // 쓰기 작업 → 클래스의 readOnly = true 를 덮어쓴다.
    public Post write(String title, String content) {
        Post post = new Post(title, content);

        // IDENTITY 전략은 save() 하는 순간 INSERT 가 실행되어 id 가 바로 채워진다.
        return postRepository.save(post);
    }
}
