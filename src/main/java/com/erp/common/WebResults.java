// POST 성공 이동과 저장 건수 검증
package com.erp.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.RedirectView;

public final class WebResults {
    // 인스턴스 생성 방지
    private WebResults() {
    }

    // 저장 성공 후 지정한 주소로 이동하는 HTTP 303 응답 생성
    public static RedirectView seeOther(String path) {
        var view = new RedirectView(path, true);
        view.setStatusCode(HttpStatus.SEE_OTHER);
        return view;
    }

    // DB 변경 건수가 정확히 1건인지 검증
    public static void requireOne(int changed) {
        if (changed != 1) {
            throw new IllegalStateException("Unexpected affected row count");
        }
    }
}
