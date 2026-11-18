package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.repository.PostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// 댓글 API(/api/v1/posts/{postId}/comments) 컨트롤러 테스트
// 구조는 ApiV1PostControllerTest 와 똑같다. (요청 → 검증 2단계)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ApiV1PostCommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostRepository postRepository; // 댓글은 항상 "어떤 글"에 속하므로 글도 조회해야 한다.

    @Autowired
    private PostCommentRepository postCommentRepository;
}
