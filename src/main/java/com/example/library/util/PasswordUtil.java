package com.example.library.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密碼加密工具類
 * 使用 SHA-256 + Salt 進行密碼雜湊
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 32;
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * 生成隨機鹽值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用 SHA-256 + Salt 加密密碼
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            String saltedPassword = password + salt;
            byte[] hashBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密碼加密失敗", e);
        }
    }

    /**
     * 驗證密碼是否正確
     */
    public static boolean verifyPassword(String inputPassword, String salt, String storedHashedPassword) {
        String hashedInput = hashPassword(inputPassword, salt);
        return hashedInput.equals(storedHashedPassword);
    }
}
