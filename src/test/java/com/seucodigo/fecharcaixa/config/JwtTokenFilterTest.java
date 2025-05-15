package com.seucodigo.fecharcaixa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    private JwtTokenFilter jwtTokenFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenValidToken_thenSetAuthentication() throws Exception {
        String token = "valid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(authentication);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider).getAuthentication(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void whenInvalidToken_thenDoNotSetAuthentication() throws Exception {
        String token = "invalid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider).validateToken(token);
        verify(jwtTokenProvider, never()).getAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void whenNoToken_thenDoNotSetAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtTokenFilter.doFilterInternal(request, response, filterChain);

        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(jwtTokenProvider, never()).getAuthentication(anyString());
        verify(filterChain).doFilter(request, response);
    }
} 