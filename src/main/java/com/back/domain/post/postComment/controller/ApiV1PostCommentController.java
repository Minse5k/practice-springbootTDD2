package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments") // 클래스 레벨 URL 에도 경로 변수를 쓸 수 있다.
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService; // 댓글은 글을 통해서 찾는다. (post.getComments())

    @GetMapping // GET /api/v1/posts/{postId}/comments
    public List<PostCommentDto> getItems(@PathVariable long postId) { // 클래스 URL 의 {postId} 도 받을 수 있다.
        // 글이 없으면 NoSuchElementException → 전역 처리기가 404 응답
        Post post = postService.findById(postId).get();

        return post.getComments() // 023 에서 만든 @OneToMany 목록 (이 순간 댓글 SELECT 실행, LAZY)
                .stream()
                .sorted(Comparator.comparing(PostComment::getId).reversed()) // 최신순
                .map(PostCommentDto::new)
                .toList();
    }

    @GetMapping("/{id}") // GET /api/v1/posts/{postId}/comments/{id}
    public PostCommentDto getItem(
            @PathVariable long postId, // 클래스 URL 의 {postId}
            @PathVariable long id      // 메서드 URL 의 {id}
    ) {
        Post post = postService.findById(postId).get();

        // 글이 없거나, 그 글에 해당 댓글이 없으면 NoSuchElementException → 404
        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }

    record PostCommentWriteReqBody(
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {
    }

    @PostMapping // POST /api/v1/posts/{postId}/comments
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<PostCommentDto> write(
            @PathVariable long postId,
            @Valid @RequestBody PostCommentWriteReqBody reqBody
    ) {
        PostComment postComment = postService.writeComment(postId, reqBody.content());

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다.".formatted(postComment.getId()),
                new PostCommentDto(postComment)
        );
    }

    record PostCommentModifyReqBody(
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {
    }

    @PutMapping("/{id}") // PUT /api/v1/posts/{postId}/comments/{id}
    public RsData<Void> modify(
            @PathVariable long postId,
            @PathVariable long id,
            @Valid @RequestBody PostCommentModifyReqBody reqBody
    ) {
        postService.modifyComment(postId, id, reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(id)
        );
    }
}
