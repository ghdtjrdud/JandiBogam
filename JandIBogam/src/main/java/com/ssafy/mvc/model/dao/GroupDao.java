package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.GroupDto;
import org.apache.ibatis.annotations.Param;

public interface GroupDao {

//    그룹생성
    int insertGroup(GroupDto groupDto);

//    생성자 그룹  자동 가입
    void insertUserGroup(@Param("userId") int createdBy, @Param("groupId") int id);
}
