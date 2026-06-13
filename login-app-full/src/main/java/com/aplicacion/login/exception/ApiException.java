package com.aplicacion.login.exception;



import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String code;
    private final String description;
    private final HttpStatus status;

    public ApiException(String code, String description, HttpStatus status) {
        super(description);
        this.code = code;
        this.description = description;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
