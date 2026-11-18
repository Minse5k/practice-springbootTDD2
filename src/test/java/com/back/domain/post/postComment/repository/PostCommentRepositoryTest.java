package com.back.domain.post.postComment.repository;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostCommentRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Test
    @DisplayName("댓글을 저장하면 어떤 글에 달린 댓글인지(post)와 id, 생성일이 함께 저장된다.")
    void t1() {
        // given: 댓글을 달 글이 먼저 DB에 있어야 한다.
        Post post = postRepository.save(new Post("제목", "내용"));
        PostComment postComment = new PostComment(post, "댓글 내용");

        // when
        postCommentRepository.save(postComment);

        // then
        assertThat(postComment.getId()).isPositive();
        assertThat(postComment.getCreateDate()).isNotNull();
        assertThat(postComment.getModifyDate()).isNotNull();
        assertThat(postComment.getContent()).isEqualTo("댓글 내용");
        assertThat(postComment.getPost().getId()).isEqualTo(post.getId());
    }
}
