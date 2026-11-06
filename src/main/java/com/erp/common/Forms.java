// 서버 입력 검증과 항목별 오류 수집
package com.erp.common;

import jakarta.validation.Validator;
import java.util.LinkedHashMap;
import org.springframework.stereotype.Component;

@Component
public class Forms {
    // Validator:  입력 검증 수행(@NotBlank, @Size 등)
    private final Validator validator;

    // 규칙 검사 후 위반 항목 반환
    public Forms(Validator validator) {
        this.validator = validator;
    }
    // 위반 항목을 하나씩 꺼내 errors에 저장
    public void validate(Object form) {
        // LinkedHashMap: 키와 값을 짝지어 저장하면서, 처음 넣은 순서대로 꺼낼 수 있는 자료구조
        var errors = new LinkedHashMap<String, String>();
        // putIfAbsent: 같은 필드에 오류가 여러 개면 첫 번째 문구만 저장
        validator
                .validate(form)
                .forEach(e -> errors.putIfAbsent(e.getPropertyPath().toString(), e.getMessage()));
        // 오류가 하나라도 있는지 확인 후 400 에러 던짐
        if (!errors.isEmpty())
            throw Errors.http(400, "VALIDATION_ERROR", "입력값 확인 필요", errors, java.util.List.of());
    }
}