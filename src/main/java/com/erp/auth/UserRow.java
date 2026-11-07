// user 테이블 저장, 조회 정보 보관
package com.erp.auth;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRow {
    private Long userId;
    private String loginId;
    private String passwordHash;
    private String displayName;
    private String isActive;
    private LocalDateTime createdAt;
}