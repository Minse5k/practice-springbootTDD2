package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 글 API(/api/v1/posts) 컨트롤러 테스트
// 컨트롤러 테스트의 변하지 않는 2단계 구조
//   1. 요청을 날린다.  (mvc.perform(...))
//   2. 결과를 검증한다. (resultActions.andExpect(...))
@SpringBootTest        // 실제 앱과 똑같이 스프링 전체(컨트롤러, 서비스, 리포지터리, DB)를 띄운다.
@ActiveProfiles("test") // 테스트용 메모리 DB 사용
@AutoConfigureMockMvc  // MockMvc 를 빈으로 등록 → 실제 서버(포트)를 띄우지 않고도 HTTP 요청을 흉내낼 수 있다.
@Transactional         // 각 테스트가 끝나면 롤백 → 글 작성/수정/삭제 테스트를 해도 다른 테스트에 영향 없음
class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc; // 가짜 HTTP 클라이언트 (Postman 역할을 코드로)

    @Autowired
    private PostRepository postRepository; // 응답 결과를 DB의 실제 데이터와 비교할 때 사용

    @Test
    @DisplayName("글 다건조회 : GET /api/v1/posts → 모든 글을 최신순(id 내림차순)으로 응답")
    void t1() throws Exception {
        // 1. 요청
        ResultActions resultActions = mvc
                .perform(get("/api/v1/posts"))
                .andDo(print()); // 요청/응답 내용을 콘솔에 출력 (디버깅용)

        // 비교 기준이 될 실제 DB 데이터 (최신글이 먼저 오도록 id 내림차순)
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        // 2. 검증
        resultActions
                .andExpect(status().isOk())                        // HTTP 200
                .andExpect(handler().methodName("getItems"))       // 어떤 액션메서드가 처리했는지
                .andExpect(jsonPath("$.length()").value(posts.size())); // 응답 배열의 크기 == DB의 글 개수

        // 응답 JSON 배열의 모든 요소, 모든 필드를 검사한다.
        // (프론트 개발자는 이 필드들을 믿고 화면을 만든다 → 하나라도 빠지면 안 된다)
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
                    // 날짜는 밀리초 이하 자릿수 표현이 다를 수 있어서 '초' 단위까지만 비교
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(startsWith(post.getCreateDate().toString().substring(0, 19))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(startsWith(post.getModifyDate().toString().substring(0, 19))))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
        }
    }

    @Test
    @DisplayName("글 단건조회 : GET /api/v1/posts/1 → 1번 글을 응답")
    void t2() throws Exception {
        long id = 1;

        // 1. 요청 : URL 경로에 글 번호를 넣는다.
        ResultActions resultActions = mvc
                .perform(get("/api/v1/posts/%d".formatted(id)))
                .andDo(print());

        // 비교 기준이 될 실제 DB 데이터
        Post post = postRepository.findById(id).get();

        // 2. 검증 : 이번에는 배열이 아니라 객체 하나({ ... })가 응답된다. → $.필드명
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().methodName("getItem")) // 다건은 getItems, 단건은 getItem
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.createDate").value(startsWith(post.getCreateDate().toString().substring(0, 19))))
                .andExpect(jsonPath("$.modifyDate").value(startsWith(post.getModifyDate().toString().substring(0, 19))))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));
    }

    @Test
    @DisplayName("글 단건조회 : 존재하지 않는 글 GET /api/v1/posts/999999 → 404 + 에러 메세지")
    void t3() throws Exception {
        long id = 999_999; // 샘플 데이터에 없는 번호 (_ 는 숫자를 읽기 쉽게 끊어주는 자바 문법)

        // 1. 요청
        ResultActions resultActions = mvc
                .perform(get("/api/v1/posts/%d".formatted(id)))
                .andDo(print());

        // 2. 검증
        // 실패 응답 형식 : { "resultCode": "404-1", "msg": "해당 데이터가 존재하지 않습니다." }
        //   resultCode : "HTTP상태코드-세부번호" → 프론트가 상황별로 분기 처리하기 쉽다.
        //   msg        : 사용자에게 그대로 보여줄 수 있는 메세지
        resultActions
                .andExpect(status().isNotFound())             // HTTP 404
                .andExpect(handler().methodName("getItem"))   // 요청 자체는 getItem 이 받았다.
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }
}
