package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.GroupDto;
import com.ssafy.mvc.model.dto.UserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupDao {

//    그룹생성
    int insertGroup(GroupDto groupDto);

//    생성자 그룹  자동 가입
    void insertUserGroup(@Param("userId") int createdBy, @Param("groupId") int id);

    int joinGroup(@Param("code") String code, @Param("userId") int userId);

    GroupDto getGroupByUserId(int userId);

    int leaveGroup(@Param("groupId") int groupId, @Param("userId") int userId);

    List<UserDto> getGroupMember(int groupId);
}
