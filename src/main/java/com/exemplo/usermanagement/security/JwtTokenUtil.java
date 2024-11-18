package com.exemplo.usermanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; //
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.security.Key;
import java.util.Base64;

public class JwtTokenUtil {

    private static final String SECRET_KEY = "mySecretKey"; // Substitute with your secure secret key
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    private static final long EXPIRATION_TIME = 864_000_00L; // 10 days in milliseconds
    

    // Method to generate the JWT token
    public String generateToken(String username) {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY)); // Decode the secret key and create Key
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS512) // Use the key for signing
                .compact();
    }

    // Method to validate the token
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY)); // Decode the key for validation
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); // Use the key for validation
            return true;
        } catch (Exception e) {
            return false; // Return false if there's an error during validation
        }
    }

    // Method to extract the username from the token
    public String getUsernameFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY)); // Decode the key for extraction
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
