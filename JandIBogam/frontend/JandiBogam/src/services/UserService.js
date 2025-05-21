// src/services/UserService.js
import api from './api' // src/services/api.js 에서 export한 axios 인스턴스

export default {
  /**
   * 내 정보 조회
   * GET {BASE_URL}/users/{userId}
   * @param {number|string} userId
   * @returns AxiosResponse<{ id, name, gender, birth_date, … }>
   */
  getMyInfo(userId) {
    return api.get(`/users/${userId}`)
  },

  /**
   * 내 정보 수정
   * PUT {BASE_URL}/users/{userId}
   * @param {number|string} userId
   * @param {object} userData
   * @returns AxiosResponse
   */
  updateMyInfo(userId, userData) {
    return api.put(`/users/${userId}`, userData)
  },

  /**
   * 내가 속한 그룹 조회
   * GET {BASE_URL}/groups/my
   * (JWT 토큰에서 userId를 뽑아서 처리하므로 별도 파라미터 불필요)
   * @returns AxiosResponse<GroupDto|GroupDto[]>
   */
  getMyGroups() {
    return api.get('/groups/my')
  },

  /**
   * 그룹 코드로 가입 (예시)
   * POST {BASE_URL}/groups/join
   * @param {string} code
   * @returns AxiosResponse
   */
  joinGroup(code) {
    return api.post('/groups/join', { code })
  },

  /**
   * 그룹 탈퇴
   * DELETE {BASE_URL}/groups/{groupId}
   * @param {number|string} groupId
   * @returns AxiosResponse
   */
  leaveGroup(groupId) {
    return api.delete(`/groups/${groupId}`)
  },
}
