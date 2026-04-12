package com.bpi.M9Activity7;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req,
                         HttpServletResponse res,
                         AuthenticationException ex) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        res.setContentType("application/json");
        // Keep it simple; you can expand this payload if you want (timestamp, path, etc.)
        String body = String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
            sanitize(ex.getMessage()), sanitize(req.getRequestURI())
        );
        res.getWriter().write(body);
    }

    // Avoid breaking JSON if the message contains quotes/newlines.
    private String sanitize(String in) {
        if (in == null) return "";
        return in.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}