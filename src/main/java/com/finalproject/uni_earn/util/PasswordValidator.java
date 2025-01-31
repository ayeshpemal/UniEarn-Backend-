package com.finalproject.uni_earn.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    // Regular expression for password complexity
    // Password must contain at least 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character
    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return pattern.matcher(password).matches();
    }
}
