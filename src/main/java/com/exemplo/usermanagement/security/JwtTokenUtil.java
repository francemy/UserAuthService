package com.exemplo.usermanagement.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.exemplo.usermanagement.dto.JwtPayload;
import com.exemplo.usermanagement.exception.ExpiredTokenException;
import com.exemplo.usermanagement.exception.InvalidTokenException;
import com.exemplo.usermanagement.exception.RoleNotFoundException;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    private static final long EXPIRATION_TIME = 864_000_00L; // 10 days in milliseconds
    private static final String ADMIN = "ADMIN";

    // Gera o token JWT
    public String generateToken(JwtPayload payload) {
        return Jwts.builder()
                .setSubject(payload.getUsername())
                .claim("email", payload.getEmail())
                .claim("userId", payload.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }
    // Verificação da Expiração no Filtro JWT
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody().getExpiration();
    }

    // Valida o token JWT
    public boolean validateToken(String token) {
        try {
            // Parse e valida o token
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new InvalidTokenException("Token JWT não suportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Token JWT malformado: " + e.getMessage());
        } catch (SignatureException e) {
            throw new InvalidTokenException("Assinatura do token inválida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Token JWT está vazio ou inválido: " + e.getMessage());
        }
    }
    // Extraí o nome de usuário do token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Verifica se o usuário tem o papel 'ADMIN'
    public boolean isAdmin(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = (String) claims.get("role");
        if (role == null) {
            throw new RoleNotFoundException("Role não encontrado no token.");
        }
        return ADMIN.equals(role);
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
