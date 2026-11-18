package com.back.global.initData;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 앱이 시작될 때 샘플 데이터를 넣어주는 클래스
// ApplicationRunner : 스프링이 모든 빈을 준비한 직후, run() 을 딱 한 번 실행해준다.
@Component
@RequiredArgsConstructor // final 필드를 받는 생성자를 롬복이 만들어준다 → 생성자 주입
public class BaseInitData implements ApplicationRunner {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    @Override
    @Transactional // 글/댓글 저장을 하나의 작업으로 묶는다. 중간에 실패하면 전부 취소.
    public void run(ApplicationArguments args) {
        // dev 환경은 파일 DB라서 데이터가 남아있다. 이미 있으면 또 넣지 않는다.
        if (postRepository.count() > 0) return;

        Post post1 = postRepository.save(new Post("제목 1", "내용 1"));
        Post post2 = postRepository.save(new Post("제목 2", "내용 2"));
        postRepository.save(new Post("제목 3", "내용 3")); // 3번 글은 댓글이 없는 글

        postCommentRepository.save(new PostComment(post1, "댓글 1-1"));
        postCommentRepository.save(new PostComment(post1, "댓글 1-2"));
        postCommentRepository.save(new PostComment(post1, "댓글 1-3"));
        postCommentRepository.save(new PostComment(post2, "댓글 2-1"));
        postCommentRepository.save(new PostComment(post2, "댓글 2-2"));
    }
}
