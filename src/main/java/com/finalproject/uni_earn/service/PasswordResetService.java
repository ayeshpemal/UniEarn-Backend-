package com.finalproject.uni_earn.service;

public interface PasswordResetService {
    void createPasswordResetTokenForUser(String email);
    String validatePasswordResetToken(String token);
    void resetPassword(String token, String newPassword);
}
