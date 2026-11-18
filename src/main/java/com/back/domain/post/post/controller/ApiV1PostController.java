package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody : 리턴값을 JSON 으로 변환해서 응답 본문에 담는다.
@RequestMapping("/api/v1/posts") // 이 컨트롤러의 모든 메서드는 /api/v1/posts 로 시작
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    @GetMapping // GET /api/v1/posts
    public List<PostDto> getItems() {
        return postService.findAllByOrderByIdDesc()
                .stream()
                .map(PostDto::new) // Post 엔티티 → PostDto 변환 (post -> new PostDto(post) 와 같다)
                .toList();
    }

    @GetMapping("/{id}") // GET /api/v1/posts/1, /api/v1/posts/2 ... {id} 자리에 오는 값을 변수로 받는다.
    public PostDto getItem(@PathVariable long id) { // URL 의 {id} 값이 매개변수 id 에 들어온다.
        // 글이 없으면 .get() 에서 NoSuchElementException 발생
        // → GlobalExceptionHandler 가 잡아서 404 응답
        return new PostDto(postService.findById(id).get());
    }

    // 글 작성 요청 본문(JSON)을 담을 그릇
    // { "title": "...", "content": "..." } → PostWriteReqBody(title, content)
    // 이 컨트롤러에서만 쓰므로 컨트롤러 안에 중첩 record 로 선언
    record PostWriteReqBody(
            @NotBlank(message = "제목을 입력해주세요.") // null, "", "   " 모두 거부
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {
    }

    @PostMapping // POST /api/v1/posts
    @ResponseStatus(HttpStatus.CREATED) // 성공 시 200 대신 201 응답
    // @Valid : reqBody 에 붙은 검증 규칙(@NotBlank 등)을 실행한다.
    //          하나라도 어기면 메서드 본문은 실행되지 않고 MethodArgumentNotValidException 이 던져진다.
    public RsData<PostDto> write(@Valid @RequestBody PostWriteReqBody reqBody) { // 요청 본문 JSON → 자바 객체
        Post post = postService.write(reqBody.title(), reqBody.content());

        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );
    }

    // 글 수정 요청 본문. 작성과 모양은 같지만, 나중에 수정만의 규칙이 생길 수 있으므로 따로 둔다.
    record PostModifyReqBody(
            @NotBlank(message = "제목을 입력해주세요.")
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {
    }

    @PutMapping("/{id}") // PUT /api/v1/posts/1
    public RsData<Void> modify( // 돌려줄 data 가 없으므로 RsData<Void>
            @PathVariable long id,                          // 무엇을 : URL 의 글 번호
            @Valid @RequestBody PostModifyReqBody reqBody   // 어떻게 : 본문의 새 제목/내용
    ) {
        postService.modify(id, reqBody.title(), reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(id)
        );
    }

    @DeleteMapping("/{id}") // DELETE /api/v1/posts/1
    public RsData<Void> delete(@PathVariable long id) { // 삭제는 "무엇을"만 알면 되므로 본문이 없다.
        postService.delete(id);

        return new RsData<>(
                "200-1",
                "%d번 글이 삭제되었습니다.".formatted(id)
        );
    }
}
