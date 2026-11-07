// stock_movement 테이블 저장, 조회 정보 보관
package com.erp.stock;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovementRow {
    private Long movementId;
    private Long itemId;
    private Long movementSeq;
    private String movementType;
    private Long receiptId;
    private Long orderLineId;
    private Integer quantityDelta;
    private Integer balanceAfter;
    private Long createdBy;
    private LocalDateTime createdAt;
}