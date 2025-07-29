package com.backend.recruitAi.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

@Getter
public class OAuth2RegistrationRequiredException extends AuthenticationException {
    private final Map<String, Object> attributes;

    public OAuth2RegistrationRequiredException(String msg, Map<String, Object> attributes) {
        super(msg);
        this.attributes = attributes;
    }
}
