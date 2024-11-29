package ao.okayula.forum.security;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ao.okayula.forum.exception.ExpiredTokenException;
import ao.okayula.forum.exception.InvalidTokenException;
import io.jsonwebtoken.Claims; //
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Value("${jwt.expiration}")
    private Duration expiration;

    public Duration getExpiration() {
        return expiration;
    }

    public long getExpirationInMilliseconds() {
        return expiration.toMillis();
    }

    public long getExpirationInSeconds() {
        return expiration.toSeconds();
    }

    // Method to generate the JWT token
    public String generateToken(String username) {
        // Decode the secret key and create Key
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationInMilliseconds()))
                .signWith(SignatureAlgorithm.HS256, getSigningKey()) // Use the key for signing
                .compact();
    }

    // Method to validate the token
   public boolean validateToken(String token) {
        try {
            // Parse e valida o token
            Jwts.parser()
                    .setSigningKey(getSigningKey())
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

    // Method to extract the username from the token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
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
