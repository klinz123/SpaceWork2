package com.spacework.core.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String error;
    private String codigo;
    private LocalDateTime timestamp;

    public ErrorResponse(String error, String codigo) {
        this.error = error;
        this.codigo = codigo;
        this.timestamp = LocalDateTime.now();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
