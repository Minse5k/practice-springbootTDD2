package com.back.global.initData;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.repository.PostCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 아직 샘플 데이터를 넣는 코드가 없다.
// "앱이 시작되면 이런 데이터가 있어야 한다"는 요구사항을 테스트로 먼저 적는다. → 🔴 실패해야 정상
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BaseInitDataTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Test
    @DisplayName("앱이 시작되면 샘플 글 3개가 저장되어 있다.")
    void t1() {
        List<Post> posts = postRepository.findAll();

        assertThat(posts).hasSize(3);
        assertThat(posts)
                .extracting(Post::getTitle)
                .containsExactly("제목 1", "제목 2", "제목 3");
        assertThat(posts)
                .extracting(Post::getContent)
                .containsExactly("내용 1", "내용 2", "내용 3");
    }

    @Test
    @DisplayName("앱이 시작되면 샘플 댓글 5개가 저장되어 있다. (1번 글에 3개, 2번 글에 2개, 3번 글에 0개)")
    void t2() {
        List<PostComment> postComments = postCommentRepository.findAll();

        assertThat(postComments).hasSize(5);
        assertThat(postComments)
                .extracting(PostComment::getContent)
                .containsExactly("댓글 1-1", "댓글 1-2", "댓글 1-3", "댓글 2-1", "댓글 2-2");
        assertThat(postComments)
                .extracting(postComment -> postComment.getPost().getTitle())
                .containsExactly("제목 1", "제목 1", "제목 1", "제목 2", "제목 2");
    }
}
