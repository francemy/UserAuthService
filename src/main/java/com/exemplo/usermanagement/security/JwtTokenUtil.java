package com.exemplo.usermanagement.security;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.exemplo.usermanagement.dto.JwtPayload;
import com.exemplo.usermanagement.exception.ExpiredTokenException;
import com.exemplo.usermanagement.exception.InvalidTokenException;
import com.exemplo.usermanagement.exception.RoleNotFoundException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static Key SECRET_KEY;

    @PostConstruct
    public void init() {
        // Converte o segredo em um objeto Key após o bean ser inicializado
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static Key getSecretKey() {
        return SECRET_KEY;
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

    private static final String ADMIN = "ADMIN";
    @Autowired
    private SessionRepository sessionRepository; // Para gerenciar a sessão

    // Gera o token JWT
    public String generateToken(JwtPayload payload) {
        return Jwts.builder()
                .setSubject(payload.getUsername())
                .claim("email", payload.getEmail())
                .claim("userId", payload.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationInMilliseconds()))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Verificação da Expiração no Filtro JWT
    public boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody().getExpiration();
    }

    // Valida o token JWT
    public boolean validateToken(String token) {
        try {
            // Parse e valida o token
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
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
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Verifica se o usuário tem o papel 'ADMIN'
    public boolean isAdmin(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String role = (String) claims.get("role");
        if (role == null) {
            throw new RoleNotFoundException("Role não encontrado no token.");
        }
        return ADMIN.equals(role);
    }

    // Método para buscar o token JWT da sessão Redis
    public String getJwtTokenFromSession() {
        // Extrai o ID da sessão da requisição HTTP
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionId = requestAttributes.getRequest().getSession().getId();

        // Recupera a sessão no Redis
        Session session = sessionRepository.findById(sessionId);
        
        // Busca o token JWT armazenado na sessão
        if (session != null) {
            return (String) session.getAttribute("jwtToken");
        }
        
        return null;  // Caso a sessão não exista ou não tenha o token
    }
     // Método para salvar o token JWT na sessão Redis
     public void saveJwtTokenToSession(String token) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionId = requestAttributes.getRequest().getSession().getId();

        Session session = sessionRepository.findById(sessionId);
        if (session == null) {
            session = sessionRepository.createSession(); // Cria uma nova sessão se não existir
        }

        session.setAttribute("jwtToken", token); // Armazena o token JWT na sessão
        sessionRepository.save(session); // Salva a sessão no Redis
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
