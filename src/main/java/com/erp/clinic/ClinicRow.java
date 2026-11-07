// clinic 테이블 저장, 조회 정보 보관
package com.erp.clinic;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClinicRow {
    private Long clinicId;
    private String clinicCode;
    private String clinicName;
    private String phone;
    private String deliveryAddress;
    private String isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}