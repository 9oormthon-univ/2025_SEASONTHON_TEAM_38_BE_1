package com.goormthon.univ.simhae.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goormthon.univ.simhae.domain.auth.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.goormthon.univ.simhae.global.dto.ErrorResponse;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService jwt;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 직렬화용

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth/apple") || path.equals("/auth/logout") || path.equals("/auth/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        // 토큰 없음
        if (auth == null || !auth.startsWith("Bearer ")) {
            setErrorResponse(response, ErrorMessage.UNAUTHORIZED);
            return;
        }

        String token = auth.substring(7);
        try {
            var claims = jwt.parse(token).getPayload();

            if ("access".equals(claims.get("typ"))) {
                Long userId = Long.valueOf(claims.getSubject());

                var authn = new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(userId), null, List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(authn);
            }
        } catch (Exception e) {
            setErrorResponse(response, ErrorMessage.INVALID_TOKEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorMessage errorMessage) throws IOException {
        response.setStatus(errorMessage.getStatus());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse body = ErrorResponse.of(errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(body));;
    }

    public record UserPrincipal(Long id) {}
}