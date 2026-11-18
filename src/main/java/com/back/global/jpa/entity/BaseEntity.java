package com.back.global.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 모든 엔티티가 공통으로 가지는 필드(id, 생성일, 수정일)를 모아둔 부모 클래스
@MappedSuperclass // 이 클래스 자체는 테이블이 아니다. 자식 엔티티의 테이블에 컬럼만 물려준다.
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity { // 혼자서 new 할 일이 없으므로 abstract
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;
}
