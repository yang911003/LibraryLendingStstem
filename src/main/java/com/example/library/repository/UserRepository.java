package com.example.library.repository;

import com.example.library.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 註冊使用者 (呼叫 Stored Procedure)
     */
    public StoredProcedureResult registerUser(String phoneNumber, String password, String salt, String userName) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_register_user");

        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_password", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_salt", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_code", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_message", String.class, ParameterMode.OUT);

        query.setParameter("p_phone_number", phoneNumber);
        query.setParameter("p_password", password);
        query.setParameter("p_salt", salt);
        query.setParameter("p_user_name", userName);

        query.execute();

        Long userId = (Long) query.getOutputParameterValue("p_user_id");
        Integer resultCode = (Integer) query.getOutputParameterValue("p_result_code");
        String resultMessage = (String) query.getOutputParameterValue("p_result_message");

        return new StoredProcedureResult(resultCode, resultMessage, userId);
    }

    /**
     * 使用者登入驗證 (呼叫 Stored Procedure)
     */
    public LoginResult userLogin(String phoneNumber) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_user_login");

        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_password", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_salt", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_user_name", String.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_is_active", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_code", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_result_message", String.class, ParameterMode.OUT);

        query.setParameter("p_phone_number", phoneNumber);

        query.execute();

        Long userId = (Long) query.getOutputParameterValue("p_user_id");
        String password = (String) query.getOutputParameterValue("p_password");
        String salt = (String) query.getOutputParameterValue("p_salt");
        String userName = (String) query.getOutputParameterValue("p_user_name");
        Integer isActive = (Integer) query.getOutputParameterValue("p_is_active");
        Integer resultCode = (Integer) query.getOutputParameterValue("p_result_code");
        String resultMessage = (String) query.getOutputParameterValue("p_result_message");

        return new LoginResult(resultCode, resultMessage, userId, password, salt, userName, isActive);
    }

    /**
     * Stored Procedure 執行結果
     */
    public static class StoredProcedureResult {
        public final Integer code;
        public final String message;
        public final Long userId;

        public StoredProcedureResult(Integer code, String message, Long userId) {
            this.code = code;
            this.message = message;
            this.userId = userId;
        }
    }

    /**
     * 登入結果
     */
    public static class LoginResult {
        public final Integer code;
        public final String message;
        public final Long userId;
        public final String password;
        public final String salt;
        public final String userName;
        public final Integer isActive;

        public LoginResult(Integer code, String message, Long userId, String password, 
                          String salt, String userName, Integer isActive) {
            this.code = code;
            this.message = message;
            this.userId = userId;
            this.password = password;
            this.salt = salt;
            this.userName = userName;
            this.isActive = isActive;
        }
    }
}
