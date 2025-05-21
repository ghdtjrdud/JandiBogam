// src/services/AuthService.js
import apiClient from './api'

const AuthService = {
  async register(userData) {
    return apiClient.post('/auth/register', userData)
  },

  async login(credentials) {
    const response = await apiClient.post('/auth/login', credentials)
    const { accessToken, user } = response.data

    if (!accessToken || !user) {
      throw new Error('로그인 응답에 accessToken이나 user가 없습니다.')
    }

    // 토큰 저장
    localStorage.setItem('accessToken', accessToken)
    // 사용자 정보 저장
    localStorage.setItem('user', JSON.stringify(user))

    // apiClient는 인터셉터로 자동 헤더 관리하므로 추가로 설정할 필요 없음!

    return response
  },

  logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('user')
    // apiClient의 헤더도 자동 관리됨(토큰 없으면 헤더 미포함)
  },

  isLoggedIn() {
    const token = localStorage.getItem('accessToken')
    return !!token
  },
}

export default AuthService
