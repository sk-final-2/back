package com.backend.recruitAi.member.dto;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import lombok.Data;

@Data
public class LoginResponseDto {
    String email;
    String name;
    Provider provider;
    String formattedDate;
    String reasonMessage;

    public LoginResponseDto(){
    }
    public LoginResponseDto(Member member){
        this.email=member.getEmail();
        this.name = member.getName();
        this.provider = member.getProvider();
    }
}
