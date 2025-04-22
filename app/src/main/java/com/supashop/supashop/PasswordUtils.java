//package com.example.com.supashop.supashop;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//public class PasswordUtils {
//    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    // Method to hash a password
//    public static String hashPassword(String plainTextPassword) {
//        return encoder.encode(plainTextPassword);
//    }
//
//    // Method to check a password against a hashed password
//    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
//        return encoder.matches(plainTextPassword, hashedPassword);
//    }
//}
