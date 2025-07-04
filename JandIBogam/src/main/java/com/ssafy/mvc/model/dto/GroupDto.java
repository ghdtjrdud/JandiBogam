package com.ssafy.mvc.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {

    private int id;
    private String name;
    private String code;
    private int createdBy;
    private LocalDateTime createdAt;

}
