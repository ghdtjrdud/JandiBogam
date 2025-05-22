package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.GroupDao;
import com.ssafy.mvc.model.dto.GroupDto;
import com.ssafy.mvc.model.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;

    @Autowired
    public GroupServiceImpl(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Override
    public int createGroup(GroupDto groupDto) {
        int result = groupDao.insertGroup(groupDto);

        if (result > 0) {
            groupDao.insertUserGroup(groupDto.getCreatedBy(), groupDto.getId());
        }
        return result;
    }

    @Override
    public int joinGroup(String code, int userId) {
        return groupDao.joinGroup(code, userId);
    }

    @Override
    public List<GroupDto> getGroupByUserId(int userId) {
        return groupDao.getGroupByUserId(userId);
    }

    @Override
    public int leaveGroup(int groupId, int userId) {
        return groupDao.leaveGroup(groupId, userId);
    }

    @Override
    public List<UserDto> getGroupMembers(int groupId) {
        return groupDao.getGroupMember(groupId);
    }

    @Override
    public String getGroupCode(int groupId) {
        return groupDao.getGroupCode(groupId);
    }
}
