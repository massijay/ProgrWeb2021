package com.mcris.triprecorder.models.utils;

public class ErrorMessage {
    public static final int USERNAME_ALREADY_EXISTS = 1;
    public static final int EMAIL_ALREADY_EXISTS = 2;
    public static final int INVALID_EMAIL = 3;
    public String message;
    public int errorCode;

    public ErrorMessage(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
