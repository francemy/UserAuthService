package com.exemplo.usermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class RedisConfiguration {
    
        @Value("${spring.redis.host}")
        private String redisHost;
    
        @Value("${spring.redis.port}")
        private int redisPort;
    
        @Value("${spring.redis.password:}") // Valor padrão se não houver senha
        private String redisPassword;
    
        // Configuração do RedisConnectionFactory para uso geral e de sessões HTTP
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
            if (!redisPassword.isEmpty()) {
                configuration.setPassword(redisPassword);
            }
            return new LettuceConnectionFactory(configuration);
        }
    
        // Configuração do RedisTemplate para operações gerais
        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }
    
    }
    
