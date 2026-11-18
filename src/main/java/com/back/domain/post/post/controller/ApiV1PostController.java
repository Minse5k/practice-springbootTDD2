package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
