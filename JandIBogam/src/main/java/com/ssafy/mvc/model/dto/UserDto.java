package com.ssafy.mvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String loginId;
    private String passwordHash;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private double height;
    private double weight;
    private String uiTheme;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 건강 정보 필드 추가
    private boolean diabetes; // 당뇨
    private boolean hypertension; // 고혈압
    private boolean hyperlipidemia; // 고지혈증
    private boolean heartDisease; // 심장질환
    private boolean kidneyDisease; // 신장질환
    private boolean allergies; // 체질시약 알러지

    // 가족 코드 (선택사항)
    private String familyCode;
}