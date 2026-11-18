package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
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

    @Transactional
    public void modify(long id, String title, String content) {
        // 글이 없으면 NoSuchElementException → GlobalExceptionHandler 가 404 응답
        Post post = postRepository.findById(id).get();

        post.modify(title, content);

        // save() 를 호출하지 않는다!
        // 트랜잭션 안에서 조회한 엔티티는 JPA가 "관리"하고 있어서,
        // 값이 바뀌면 트랜잭션이 끝날 때 알아서 UPDATE 쿼리를 실행한다. (변경 감지, Dirty Checking)
    }

    @Transactional
    public void delete(long id) {
        // 글이 없으면 NoSuchElementException → GlobalExceptionHandler 가 404 응답
        Post post = postRepository.findById(id).get();

        postRepository.delete(post);
    }

    // 댓글은 항상 글을 통해서 다룬다. (글이 댓글의 "주인")
    @Transactional
    public PostComment writeComment(long postId, String content) {
        Post post = postRepository.findById(postId).get(); // 글이 없으면 404

        // 글의 댓글 목록에 추가만 하면 cascade = PERSIST 덕분에 트랜잭션이 끝날 때 INSERT 된다.
        PostComment postComment = post.addComment(content);

        // 그런데 "끝날 때"까지 기다리면 응답을 만드는 시점에 id 가 0, 날짜가 null 이다.
        // flush() : 지금까지 쌓인 변경사항(INSERT)을 DB 에 즉시 반영한다. (커밋은 아님!)
        postRepository.flush();

        return postComment;
    }
}
