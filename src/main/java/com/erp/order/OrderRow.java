// order 테이블 저장, 조회 정보 보관
package com.erp.order;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRow {
    private Long orderId;
    private Long requestId;
    private Long clinicId;
    private String clinicNameSnapshot;
    private String phoneSnapshot;
    private String deliveryAddressSnapshot;
    private String status;
    private Long version;
    private Long totalAmount;
    private String cancelReason;
    private Long cancelledBy;
    private LocalDateTime cancelledAt;
    private Long shippedBy;
    private LocalDateTime shippedAt;
    private Long deliveredBy;
    private LocalDateTime deliveredAt;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}