package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "手機號碼不可為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phoneNumber;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, max = 50, message = "密碼長度須在6-50字元之間")
    private String password;

    @NotBlank(message = "使用者名稱不可為空")
    @Size(min = 2, max = 100, message = "使用者名稱長度須在2-100字元之間")
    private String userName;
}
