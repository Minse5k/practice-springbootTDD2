package com.back.global.globalExceptionHandler;

import com.back.global.rsData.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// 전역 예외 처리기
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
//   @ControllerAdvice : 모든 컨트롤러에서 발생한 예외를 이 클래스가 대신 처리한다.
//   @ResponseBody     : 리턴값을 JSON 으로 응답한다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 어떤 컨트롤러에서든 NoSuchElementException 이 터지면 이 메서드가 실행된다.
    // (예: Optional 이 비어있는데 .get() 을 호출한 경우 = 데이터가 없음)
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // HTTP 상태코드를 404 로 지정 (안 붙이면 200 이 나간다!)
    public RsData<Void> handle(NoSuchElementException ex) { // 돌려줄 data 가 없으므로 <Void>
        return new RsData<>(
                "404-1",
                "해당 데이터가 존재하지 않습니다."
        );
    }

    // @Valid 검증에 실패하면 스프링이 MethodArgumentNotValidException 을 던진다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public RsData<Void> handle(MethodArgumentNotValidException ex) {
        // 어긴 규칙이 여러 개일 수 있다. (예: 제목도 비고 내용도 빔)
        // 각각을 "필드명-규칙이름-메세지" 로 만들고, 정렬해서 줄바꿈으로 이어 붙인다.
        String message = ex.getBindingResult()
                .getFieldErrors()                  // 필드별 에러 목록
                .stream()
                .map(error -> error.getField() + "-" + error.getCode() + "-" + error.getDefaultMessage())
                //            "title"            "NotBlank"         "제목을 입력해주세요."
                .sorted()                          // 순서를 항상 같게 (검증 순서는 보장되지 않음)
                .collect(Collectors.joining("\n"));

        return new RsData<>(
                "400-1",
                message
        );
    }
}
