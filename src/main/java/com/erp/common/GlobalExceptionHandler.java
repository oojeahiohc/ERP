// 기본 예외의 HTML 오류 응답 변환
package com.erp.common;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // HTTP 예외를 공통 오류 화면으로 변환
    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView business(ResponseStatusException ex) {
        var view = new ModelAndView("common/error");
        view.setStatus(ex.getStatusCode());
        view.addObject("errorCode", Errors.code(ex));
        view.addObject("message", ex.getReason());
        view.addObject("fieldErrors", Errors.fields(ex));
        view.addObject("shortages", Errors.shortages(ex));
        return view;
    }

    // 잘못된 입력에 대한 HTTP 400 응답
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView invalid(IllegalArgumentException ex) {
        return business(Errors.http(400, "VALIDATION_ERROR", "입력값을 확인해 주세요."));
    }

    // 대상이 없을 때 HTTP 404 응답
    @ExceptionHandler(NoSuchElementException.class)
    public ModelAndView missing(NoSuchElementException ex) {
        return business(Errors.http(404, "RESOURCE_NOT_FOUND", "대상이 없습니다."));
    }

    // DB 잠금 실패 시 HTTP 503 응답 및 재시도 안내
    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ModelAndView locking(PessimisticLockingFailureException ex) {
        return business(Errors.http(503, "RETRYABLE_CONFLICT", "저장되지 않았습니다. 최신 상태 확인 후 다시 시도해 주세요."));
    }

    // 예상치 못한 오류의 추적 번호 기록 및 HTTP 500 응답
    @ExceptionHandler(Exception.class)
    public ModelAndView unexpected(Exception ex) {
        String traceId = org.slf4j.MDC.get("traceId");
        if (traceId == null)
            traceId = UUID.randomUUID().toString();
        log.error("ERP failure traceId={}", traceId, ex);
        var view = business(Errors.http(500, "INTERNAL_ERROR", "처리 결과를 확인해 주세요."));
        view.addObject("traceId", traceId);
        return view;
    }
}
