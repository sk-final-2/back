package com.backend.recruitAi.mypage.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageUpdateRequestDto {
    // 새 비밀번호 설정 (변경 요청이 있을시에만)
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String newPassword;
    // 주소 정보
    @Size(max = 10, message = "우편번호는 10자 이하로 입력해주세요.")
    private String postcode;
    @Size(max = 255, message = "주소는 255자 이하로 입력해주세요.")
    private String address1;
    @Size(max = 255, message = "상세주소는 255자 이하로 입력해주세요.")
    private String address2;
}
