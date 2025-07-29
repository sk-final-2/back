package com.backend.recruitAi.member.dto;

import com.backend.recruitAi.member.entity.GenderType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class KakaoSignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 30자 이하로 입력해주세요.")
    private String name;

    @NotNull(message = "성별은 필수입니다.")
    private GenderType gender;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birth;

    @Size(max = 10, message = "우편번호는 10자 이하로 입력해주세요.")
    private String zipcode;

    @Size(max = 255, message = "주소는 255자 이하로 입력해주세요.")
    private String address1;

    @Size(max = 255, message = "상세주소는 255자 이하로 입력해주세요.")
    private String address2;
}
