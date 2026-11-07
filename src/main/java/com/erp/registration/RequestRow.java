// request 테이블 저장, 조회 정보 보관
package com.erp.registration;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestRow {
    private Long requestId;
    private String requestKey;
    private String operationType;
    private Long requestedBy;
    private String payloadHash;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long documentId;
}