package com.exemplo.usermanagement.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public JwtAuthenticationFilter() {

    }

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("Interceptando requisição para: {}", request.getRequestURI());

        // Lógica do filtro...
        // Exemplo: verificar se o token JWT está presente
        String token = jwtTokenUtil.getTokenFromRequest(request);
        if (token != null) {
            logger.debug("Token JWT encontrado: {}", token);
        }

        // Extrair token do cabeçalho
        String token = jwtTokenUtil.getTokenFromRequest(request);
        System.out.println("Token JWT: " + token); // Adicione um log aqui

        // Validar token e autenticar o usuário
        if (token != null && jwtTokenUtil.validateToken(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar o fluxo da requisição
        filterChain.doFilter(request, response);
    }
}
