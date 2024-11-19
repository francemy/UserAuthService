package com.exemplo.usermanagement.service;

import com.exemplo.usermanagement.model.UserSession;
import com.exemplo.usermanagement.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    // Salvar uma nova sessão
    public UserSession saveUserSession(Long userId, String token) {
        if (userId == null || token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId or token");
        }
        
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setToken(token);
        
        try {
            return userSessionRepository.save(session);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving session", e);
        }
    }

    // Atualizar o token da sessão
    public UserSession updateUserSession(Long userId, String newToken) {
        if (userId == null || newToken == null || newToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId or new token");
        }
        
        UserSession session = userSessionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        
        session.setToken(newToken);
        
        try {
            return userSessionRepository.save(session);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating session", e);
        }
    }

    // Deletar a sessão
    public void deleteUserSession(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }
        
        UserSession session = userSessionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        
        try {
            userSessionRepository.delete(session);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting session", e);
        }
    }
}
