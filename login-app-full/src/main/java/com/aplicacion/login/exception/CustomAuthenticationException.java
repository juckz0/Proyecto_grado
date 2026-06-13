package com.aplicacion.login.exception;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    private final String code;
    private final String description;
    private final HttpStatus status;

    public CustomAuthenticationException(String code, String description, HttpStatus status) {
        super(description);
        this.code = code;
        this.description = description;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public HttpStatus getStatus() { return status; }
}
