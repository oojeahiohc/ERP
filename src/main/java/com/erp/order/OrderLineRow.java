// order_line 테이블 저장, 조회 정보 보관
package com.erp.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderLineRow {
    private Long orderLineId;
    private Long orderId;
    private Integer lineNo;
    private Long itemId;
    private String itemNameSnapshot;
    private String packageSpecSnapshot;
    private String unitNameSnapshot;
    private Integer quantity;
    private Integer unitPrice;
    private Long lineAmount;
}