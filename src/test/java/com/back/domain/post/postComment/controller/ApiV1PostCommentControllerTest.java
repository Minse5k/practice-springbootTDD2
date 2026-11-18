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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @DisplayName("댓글 단건조회 : GET /api/v1/posts/1/comments/1 → 1번 글의 1번 댓글을 응답")
    void t2() throws Exception {
        long postId = 1;
        long id = 1; // 댓글 번호

        // 1. 요청 : 경로 변수가 2개 (글 번호, 댓글 번호)
        ResultActions resultActions = mvc
                .perform(get("/api/v1/posts/%d/comments/%d".formatted(postId, id)))
                .andDo(print());

        // 비교 기준
        PostComment postComment = postCommentRepository.findById(id).get();

        // 2. 검증 : 객체 하나 → $.필드명
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.id").value(postComment.getId()))
                .andExpect(jsonPath("$.createDate").value(startsWith(postComment.getCreateDate().toString().substring(0, 19))))
                .andExpect(jsonPath("$.modifyDate").value(startsWith(postComment.getModifyDate().toString().substring(0, 19))))
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.content").value(postComment.getContent()));
    }

    @Test
    @DisplayName("댓글 작성 : POST /api/v1/posts/1/comments → 201 + 작성된 댓글 정보")
    void t3() throws Exception {
        long postId = 1;

        // 1. 요청
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts/%d/comments".formatted(postId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "댓글 new"
                                        }
                                        """)
                )
                .andDo(print());

        // 비교 기준 : 방금 작성된 댓글 = id 가 가장 큰 댓글
        PostComment postComment = postCommentRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).getFirst();

        // 2. 검증
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 작성되었습니다.".formatted(postComment.getId())))
                .andExpect(jsonPath("$.data.id").value(postComment.getId()))
                .andExpect(jsonPath("$.data.createDate").value(startsWith(postComment.getCreateDate().toString().substring(0, 19))))
                .andExpect(jsonPath("$.data.modifyDate").value(startsWith(postComment.getModifyDate().toString().substring(0, 19))))
                .andExpect(jsonPath("$.data.postId").value(postId))
                .andExpect(jsonPath("$.data.content").value("댓글 new"));
    }

    @Test
    @DisplayName("댓글 수정 : PUT /api/v1/posts/1/comments/1 → 200 + 수정 완료 메세지")
    void t4() throws Exception {
        long postId = 1;
        long id = 1;

        // 1. 요청
        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/%d/comments/%d".formatted(postId, id))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "댓글 수정"
                                        }
                                        """)
                )
                .andDo(print());

        // 2. 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 수정되었습니다.".formatted(id)));

        // (선택) 실제로 수정되었는지 확인
        PostComment postComment = postCommentRepository.findById(id).get();
        assertThat(postComment.getContent()).isEqualTo("댓글 수정");
    }

    @Test
    @DisplayName("댓글 삭제 : DELETE /api/v1/posts/1/comments/1 → 200 + 삭제 완료 메세지")
    void t5() throws Exception {
        long postId = 1;
        long id = 1;
        long postCommentCountBefore = postCommentRepository.count();

        // 1. 요청
        ResultActions resultActions = mvc
                .perform(delete("/api/v1/posts/%d/comments/%d".formatted(postId, id)))
                .andDo(print());

        // 2. 검증
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 삭제되었습니다.".formatted(id)));

        // (선택) 실제로 삭제되었는지 확인 : 댓글 개수가 1개 줄었다.
        assertThat(postCommentRepository.count()).isEqualTo(postCommentCountBefore - 1);
    }
}
