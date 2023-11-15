package com.hcvision.hcvisionserver.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String error) {
        super(error);
    }
}
