package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // 지금은 "있는 글"만 생각한다. (.get() 은 값이 없으면 NoSuchElementException 발생)
        // 없는 글 조회 시 404 응답하기는 다음 단계에서 테스트를 먼저 쓰고 처리한다.
        return new PostDto(postService.findById(id).get());
    }
}
