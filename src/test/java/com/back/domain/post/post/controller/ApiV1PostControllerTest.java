package com.back.domain.post.post.controller;

import com.back.domain.post.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
}
