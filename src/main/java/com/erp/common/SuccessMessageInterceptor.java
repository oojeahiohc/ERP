// 업무 저장 성공 후 일회성 안내 메시지 전달
package com.erp.common;

import jakarta.servlet.http.*;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

public class SuccessMessageInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView view) {
        if ("POST".equals(request.getMethod())
                && view != null
                && view.getView() instanceof RedirectView) {
            RequestContextUtils.getOutputFlashMap(request).put("successMessage", "처리 완료");
        }
    }
}