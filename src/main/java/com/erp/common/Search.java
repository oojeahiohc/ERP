// 공통 검색 조건, 기간, 페이지 검증
package com.erp.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Search {
    private String code, name, isActive, receiptNo, orderNo, status, movementType;
    private Long itemId, clinicId, receiptId, orderId;
    private LocalDate fromDate, toDate;
    private LocalDateTime fromAt, toExclusive;
    private Integer page = 1;

    // 검색 결과의 페이지별 조회 시작 위치 계산
    public long getOffset() {
        return ((long) page - 1) * 20;
    }

    // 검색 조건 정리 및 유효성 검증
    public void normalize() {
        try {
            if (page == null || page < 1)
                throw new IllegalArgumentException();
            code = blank(code);
            name = blank(name);
            isActive = blank(isActive);
            status = blank(status);
            movementType = blank(movementType);
            if (code != null)
                code = code.toUpperCase(Locale.ROOT);
            if (isActive != null && !java.util.Set.of("Y", "N").contains(isActive))
                throw new IllegalArgumentException();
            if (status != null
                    && !java.util.Set.of("RECEIVED", "SHIPPED", "DELIVERED", "CANCELLED").contains(status))
                throw new IllegalArgumentException();
            if (movementType != null && !java.util.Set.of("IN", "OUT").contains(movementType))
                throw new IllegalArgumentException();
            if (itemId != null && itemId < 1 || clinicId != null && clinicId < 1)
                throw new IllegalArgumentException();
            if (fromDate != null && toDate != null && fromDate.isAfter(toDate))
                throw new IllegalArgumentException();
            fromAt = fromDate == null ? null : fromDate.atStartOfDay();
            toExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
            receiptId = number(receiptNo, "IN-");
            orderId = number(orderNo, "SO-");
        } catch (RuntimeException ex) {
            throw Errors.http(400, "INVALID_SEARCH", "검색 조건 확인 필요");
        }
    }

    // 빈 입력의 null 처리 및 앞뒤 공백 제거
    private static String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    // 입고, 주문 번호의 접두사 검증 및 숫자 ID 추출
    private static Long number(String value, String prefix) {
        value = blank(value);
        if (value == null)
            return null;
        if (!value.startsWith(prefix))
            throw new IllegalArgumentException();
        long id = Long.parseLong(value.substring(prefix.length()));
        if (id < 1)
            throw new IllegalArgumentException();
        return id;
    }
}
