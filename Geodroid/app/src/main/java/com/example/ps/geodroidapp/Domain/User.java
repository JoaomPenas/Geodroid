package com.example.ps.geodroidapp.Domain;

public class User {

    private String email;
    private String pass;
    private long salt;
    private String token;

    public User(){}
    public User(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public void setEmail(String name) {
        this.email = name;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public void setSalt(long salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }
    public String getPass() {
        return pass;
    }
    public long getSalt() {
        return salt;
    }
    public String getToken() {return token;}
}
