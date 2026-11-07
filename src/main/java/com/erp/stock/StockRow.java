// stock 테이블 저장, 조회 정보 보관
package com.erp.stock;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockRow {
    private Long itemId;
    private Integer quantity;
    private Long movementSeq;
    private LocalDateTime updatedAt;
}