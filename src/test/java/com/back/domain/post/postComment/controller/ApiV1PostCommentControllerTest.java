package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.repository.PostCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    @DisplayName("댓글 다건조회 : GET /api/v1/posts/1/comments → 1번 글의 댓글을 최신순으로 응답")
    void t1() throws Exception {
        long postId = 1; // 샘플 데이터 : 1번 글에는 댓글 3개

        // 1. 요청
        ResultActions resultActions = mvc
                .perform(get("/api/v1/posts/%d/comments".formatted(postId)))
                .andDo(print());

        // 비교 기준 : 1번 글의 댓글 목록 (023 에서 만든 post.getComments() 활용), id 내림차순 정렬
        Post post = postRepository.findById(postId).get();
        List<PostComment> postComments = post.getComments()
                .stream()
                .sorted(Comparator.comparing(PostComment::getId).reversed())
                .toList();

        // 2. 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.length()").value(postComments.size())); // 3개

        for (int i = 0; i < postComments.size(); i++) {
            PostComment postComment = postComments.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(postComment.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(startsWith(postComment.getCreateDate().toString().substring(0, 19))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(startsWith(postComment.getModifyDate().toString().substring(0, 19))))
                    .andExpect(jsonPath("$[%d].postId".formatted(i)).value(postId)) // 어느 글의 댓글인지
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(postComment.getContent()));
        }
    }
}
