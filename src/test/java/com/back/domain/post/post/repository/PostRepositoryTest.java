package com.back.domain.post.post.repository;

import com.back.domain.post.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // 테스트가 끝나면 자동으로 롤백 → 다음 테스트에 영향을 주지 않는다.
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("글을 저장하면 id, 생성일, 수정일이 자동으로 채워진다.")
    void t1() {
        // given: 아직 저장 전인 글 객체 (id = 0, 날짜 = null)
        Post post = new Post("제목", "내용");

        // when: 저장
        postRepository.save(post);

        // then: JPA와 Auditing이 값을 채워줬는지 검증
        assertThat(post.getId()).isPositive();
        assertThat(post.getCreateDate()).isNotNull();
        assertThat(post.getModifyDate()).isNotNull();
        assertThat(post.getTitle()).isEqualTo("제목");
        assertThat(post.getContent()).isEqualTo("내용");
    }
}
