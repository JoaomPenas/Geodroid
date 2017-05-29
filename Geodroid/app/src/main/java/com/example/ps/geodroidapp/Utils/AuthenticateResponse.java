package com.example.ps.geodroidapp.Utils;

/**
 * Created by joao on 29/05/17.
 */

public class AuthenticateResponse {

    private  boolean success;
    private  String message;
    private  String token;

    public AuthenticateResponse(boolean success, String message, String token) {

        this.success = success;
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
