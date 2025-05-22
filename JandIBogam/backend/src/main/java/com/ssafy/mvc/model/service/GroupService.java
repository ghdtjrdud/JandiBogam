package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dto.GroupDto;
import com.ssafy.mvc.model.dto.UserDto;

import java.util.List;

public interface GroupService {
    int createGroup(GroupDto groupDto);

    int joinGroup(String code, int userId);

    List<GroupDto> getGroupByUserId(int userId);

    int leaveGroup(int groupId, int userId);

    List<UserDto> getGroupMembers(int groupId);

    String getGroupCode(int groupId);

}
