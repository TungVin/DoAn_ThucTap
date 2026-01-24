package com.EduQuiz.Project_intel.dto;

import com.EduQuiz.Project_intel.model.User;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String role; // "STUDENT" hoặc "TEACHER"
    private Boolean acceptTerms;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password, String role, Boolean acceptTerms) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.acceptTerms = acceptTerms;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getAcceptTerms() {
        return acceptTerms;
    }

    public void setAcceptTerms(Boolean acceptTerms) {
        this.acceptTerms = acceptTerms;
    }

    public User.Role getRoleEnum() {
        if ("TEACHER".equalsIgnoreCase(role)) {
            return User.Role.TEACHER;
        }
        return User.Role.STUDENT; // Mặc định là học sinh
    }
}

