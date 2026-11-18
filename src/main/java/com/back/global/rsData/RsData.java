package com.back.global.rsData;

// RsData (Result Data) : 처리 결과를 담는 공통 응답 형식
//   resultCode : "HTTP상태코드-세부번호" (예: "201-1", "404-1")
//   msg        : 사람이 읽을 수 있는 결과 메세지
//   data       : 결과와 함께 돌려줄 데이터 (작성된 글 등). 없으면 null
//
// <T> : 제네릭. data 에 어떤 타입이든 담을 수 있게 한다.
//   RsData<PostDto> → data 는 PostDto
//   RsData<Void>    → data 없음 (항상 null)
public record RsData<T>(
        String resultCode,
        String msg,
        T data
) {
    // data 가 필요 없을 때 쓰는 편의 생성자 : new RsData<>("404-1", "메세지")
    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }
}
