package com.example.library.service;

import com.example.library.dto.LoginRequest;
import com.example.library.dto.LoginResponse;
import com.example.library.dto.RegisterRequest;
import com.example.library.exception.BusinessException;
import com.example.library.repository.UserRepository;
import com.example.library.util.JwtUtil;
import com.example.library.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 使用者註冊
     */
    @Transactional
    public Long register(RegisterRequest request) {
        // 驗證輸入 (防止 XSS)
        String phoneNumber = sanitizeInput(request.getPhoneNumber());
        String password = request.getPassword();
        String userName = sanitizeInput(request.getUserName());

        // 驗證手機號碼格式
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new BusinessException(1, "手機號碼格式不正確");
        }

        // 密碼強度驗證
        if (password.length() < 6) {
            throw new BusinessException(2, "密碼長度至少6個字元");
        }

        // 生成鹽值並加密密碼
        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(password, salt);

        // 呼叫 Stored Procedure
        UserRepository.StoredProcedureResult result = 
            userRepository.registerUser(phoneNumber, hashedPassword, salt, userName);

        if (result.code != 0) {
            throw new BusinessException(result.code, result.message);
        }

        return result.userId;
    }

    /**
     * 使用者登入
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 防止 SQL Injection (透過 Stored Procedure 和參數化查詢)
        String phoneNumber = sanitizeInput(request.getPhoneNumber());
        String password = request.getPassword();

        // 呼叫 Stored Procedure
        UserRepository.LoginResult result = userRepository.userLogin(phoneNumber);

        if (result.code != 0) {
            throw new BusinessException(result.code, result.message);
        }

        // 驗證密碼
        boolean isPasswordValid = PasswordUtil.verifyPassword(password, result.salt, result.password);
        if (!isPasswordValid) {
            throw new BusinessException(3, "密碼錯誤");
        }

        // 生成 JWT Token
        String token = jwtUtil.generateToken(result.userId, phoneNumber, result.userName);

        return new LoginResponse(token, result.userId, phoneNumber, result.userName, "登入成功");
    }

    /**
     * 防止 XSS 攻擊：清理輸入
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // 移除或轉義危險字元
        return input.replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;")
                   .trim();
    }

    /**
     * 驗證手機號碼格式 (台灣手機號碼)
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^09\\d{8}$");
    }
}
