package com.bpi.M9Activity7;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       AccessDeniedException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        res.setContentType("application/json");
        String body = String.format(
            "{\"error\":\"Forbidden\",\"message\":\"%s\",\"path\":\"%s\"}",
            sanitize(ex.getMessage()), sanitize(req.getRequestURI())
        );
        res.getWriter().write(body);
    }

    private String sanitize(String in) {
        if (in == null) return "";
        return in.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}
