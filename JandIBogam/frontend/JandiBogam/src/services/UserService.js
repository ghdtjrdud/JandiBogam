// src/services/UserService.js
import apiClient from './api'

export default {
  /**
   * 내 정보 조회
   * GET {BASE_URL}/users/{userId}
   */
  getMyInfo(userId) {
    return apiClient.get(`/users/${userId}`)
  },

  /**
   * 내 정보 수정
   * PUT {BASE_URL}/users/{userId}
   */
  updateMyInfo(userId, userData) {
    return apiClient.put(`/users/${userId}`, userData)
  },

  /**
   * 내가 속한 그룹 조회
   * GET {BASE_URL}/groups/my
   */
  getMyGroups() {
    return apiClient.get('/groups/my')
  },

  /**
   * 그룹 코드로 가입
   * POST {BASE_URL}/groups/join
   */
  joinGroup(code) {
    return apiClient.post('/groups/join', { code })
  },

  /**
   * 그룹 탈퇴
   * DELETE {BASE_URL}/groups/{groupId}
   */
  leaveGroup(groupId) {
    return apiClient.delete(`/groups/${groupId}`)
  },
}
