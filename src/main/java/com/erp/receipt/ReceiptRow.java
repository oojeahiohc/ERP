// receipt 테이블 저장, 조회 정보 보관
package com.erp.receipt;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptRow {
    private Long receiptId;
    private Long requestId;
    private Long itemId;
    private String itemNameSnapshot;
    private String packageSpecSnapshot;
    private String unitNameSnapshot;
    private Integer quantity;
    private Long createdBy;
    private LocalDateTime createdAt;
    private String creatorName;
}