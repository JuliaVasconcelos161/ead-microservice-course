package com.ead.course.config.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Log4j2
@Component
public class AuthenticationJwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    public AuthenticationJwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtStr = this.getTokenHeader(httpServletRequest);
            if(jwtStr != null && jwtProvider.validateJwt(jwtStr)) {
                String userId = jwtProvider.getSubjectJwt(jwtStr);
                String rolesStr = jwtProvider.getClaimNameJwt(jwtStr, "roles");
                UserDetails userDetails = UserDetailsImpl.build(UUID.fromString(userId), rolesStr);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e) {
            logger.error("Cannot set User Authentication: {}", e);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getTokenHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
            return headerAuth.substring(7);
        return null;
    }
}

