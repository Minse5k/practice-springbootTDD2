package com.back.global.rsData;

// RsData (Result Data) : 처리 결과를 담는 공통 응답 형식
//   resultCode : "HTTP상태코드-세부번호" (예: "404-1")
//   msg        : 사람이 읽을 수 있는 결과 메세지
public record RsData(
        String resultCode,
        String msg
) {
}
