package com.example.demo.customer_auth.dto;

public class CustomerAuthResponse {
    private String token;
    private Integer customerId;
    private String email;
    private String ten;
    private String role;

    public CustomerAuthResponse() {}
    public CustomerAuthResponse(String token, Integer customerId, String email, String ten, String role) {
        this.token = token;
        this.customerId = customerId;
        this.email = email;
        this.ten = ten;
        this.role = role;
    }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
