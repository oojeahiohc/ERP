// item 테이블 저장, 조회 정보 보관
package com.erp.item;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRow {
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String packageSpec;
    private String unitName;
    private String isActive;
    private String everUsed;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}