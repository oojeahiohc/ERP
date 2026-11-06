// Spring 기본 예외 생성과 HTML 오류 정보 조회
package com.erp.common;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public final class Errors {
    // 인스턴스 생성 방지(모든 메서드가 static으로 객체 생성 불필요)
    private Errors() {}

    // HTTP 상태와 오류 코드, 메시지를 담은 예외 생성
    public static ResponseStatusException http(int status, String code, String message) {
        return http(status, code, message, Map.of(), List.of());
    }

    // 필드별 오류와 재고 부족 내역을 포함한 HTTP 예외 생성
    public static ResponseStatusException http(
            int status, String code, String message, Map<String, String> fields, List<?> shortages) {
        var ex = new ResponseStatusException(HttpStatusCode.valueOf(status), message);
        // 오류 화면에 전달할 상세 정보를 예외에 저장
        ex.getBody().setProperty("errorCode", code);
        ex.getBody().setProperty("fieldErrors", Map.copyOf(fields));
        ex.getBody().setProperty("shortages", List.copyOf(shortages));
        return ex;
    }

    // 조건 불충족 시 HTTP 409 예외 발생
    public static void check(boolean condition, String code) {
        if (!condition)
            throw http(409, code, message(code));
    }

    // 오류 코드 조회 및 누락 시 HTTP 상태 기반 코드 반환
    public static String code(ResponseStatusException ex) {
        Object value = property(ex, "errorCode");
        return value instanceof String text ? text : "HTTP_" + ex.getStatusCode().value();
    }

    // 필드별 오류 메시지 조회
    @SuppressWarnings("unchecked")
    public static Map<String, String> fields(ResponseStatusException ex) {
        Object value = property(ex, "fieldErrors");
        return value instanceof Map<?, ?> ? (Map<String, String>) value : Map.of();
    }

    // 재고 부족 내역 조회
    public static List<?> shortages(ResponseStatusException ex) {
        Object value = property(ex, "shortages");
        return value instanceof List<?> list ? list : List.of();
    }

    // 예외 본문의 부가 정보 조회
    private static Object property(ResponseStatusException ex, String name) {
        var values = ex.getBody().getProperties();
        return values == null ? null : values.get(name);
    }

    // 오류 코드별 기본 안내 문구 조회
    private static String message(String code) {
        return switch (code) {
            case "SPEC_LOCKED" -> "거래에 사용한 품목의 규격·단위는 변경할 수 없습니다.";
            case "INACTIVE_MASTER" -> "사용 중지된 항목으로 처리할 수 없습니다.";
            case "VERSION_CONFLICT" -> "다른 작업에서 변경되었습니다. 최신 내용을 확인해 주세요.";
            case "INVALID_STATE" -> "현재 상태에서는 처리할 수 없습니다.";
            case "STOCK_LIMIT_EXCEEDED" -> "입고 후 재고가 허용 상한을 초과합니다.";
            case "REQUEST_KEY_CONFLICT" -> "같은 요청 식별자로 다른 내용이 제출되었습니다.";
            default -> "처리 조건을 확인해 주세요.";
        };
    }
}
