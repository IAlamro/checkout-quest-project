package com.checkout.shared.infrastructure.idempotency;

import com.checkout.shared.idempotency.IdempotencyRecord;
import com.checkout.shared.idempotency.IdempotencyStore;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;

/**
 * Intercepts POST requests bearing an Idempotency-Key header.
 * On first call: reserves the key, proceeds, stores the response.
 * On replay: returns the cached response without invoking the handler.
 * On concurrent in-progress call: returns 409.
 */
@Component
@RequiredArgsConstructor
public class IdempotencyFilter implements Filter {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final IdempotencyStore idempotencyStore;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String key = request.getHeader(IDEMPOTENCY_KEY_HEADER);

        if (key == null || !request.getMethod().equalsIgnoreCase("POST")) {
            chain.doFilter(req, res);
            return;
        }

        String endpoint = request.getRequestURI();
        String fingerprint = key;

        Optional<IdempotencyRecord> existing = idempotencyStore.reserve(key, endpoint, fingerprint);

        if (existing.isPresent()) {
            IdempotencyRecord record = existing.get();
            if (record.isCompleted()) {
                response.setStatus(record.statusCode());
                response.setContentType("application/json");
                response.getWriter().write(record.responseBody());
                return;
            }
            response.setStatus(HttpStatus.CONFLICT.value());
            response.getWriter().write("{\"error\":\"Request with this Idempotency-Key is already in progress\"}");
            return;
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, wrappedResponse);

        String responseBody = new String(wrappedResponse.getContentAsByteArray());
        idempotencyStore.complete(key, wrappedResponse.getStatus(), responseBody);
        wrappedResponse.copyBodyToResponse();
    }
}
