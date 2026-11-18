package com.back.global.globalExceptionHandler;

import com.back.global.rsData.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

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
    public RsData handle(NoSuchElementException ex) {
        return new RsData(
                "404-1",
                "해당 데이터가 존재하지 않습니다."
        );
    }
}
