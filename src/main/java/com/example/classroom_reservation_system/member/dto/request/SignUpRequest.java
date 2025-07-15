package com.example.classroom_reservation_system.member.dto.request;

import com.example.classroom_reservation_system.member.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "아이디는 필수입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$", message = "아이디는 6~15자 이내의 영어와 숫자만 입력할 수 있습니다.")
    private String id;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message =  "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;
}
