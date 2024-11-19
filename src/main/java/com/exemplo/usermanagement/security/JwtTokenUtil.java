package com.exemplo.usermanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; //
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import org.springframework.stereotype.Component;

import com.exemplo.usermanagement.dto.JwtPayload;

import java.security.Key;
@Component
public class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Use Key diretamente
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    private static final long EXPIRATION_TIME = 864_000_00L; // 10 days in milliseconds

    // Method to generate the JWT token
    public String generateToken(JwtPayload payload) {
        return Jwts.builder()
        .setSubject(payload.getUsername())  // Define o nome de usuário
        .claim("email", payload.getEmail()) // Adiciona o e-mail
        .claim("userId", payload.getUserId()) // Adiciona o ID do usuário
        .setIssuedAt(new Date()) // Define a data de emissão
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Define a data de expiração
        .signWith(SECRET_KEY, SignatureAlgorithm.HS512) // Define o algoritmo de assinatura
        .compact(); // Gera o token JWT
    }

    // Method to validate the token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token); // Usando a chave diretamente
            return true;
        } catch (Exception e) {
            return false; // Return false if there's an error during validation
        }
    }

    // Method to extract the username from the token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // Method to extract the JWT token from the request header
    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length()).trim(); // Remove "Bearer " prefix
        }
        return null;
    }
}
