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
    private int memberCount;
    private LocalDateTime createdAt;
//    getAllMembersOfMyGroups 메서드에서 그룹명을 보여주기 위해서.
//    private String groupName;

}
