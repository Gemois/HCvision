package com.hcvision.hcvisionserver.exception;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String error) {
        super(error);
    }
}
