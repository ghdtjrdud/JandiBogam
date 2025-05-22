// src/services/AuthService.js
import apiClient from './api'

const AuthService = {
  async login(credentials) {
    const response = await apiClient.post('/auth/login', credentials)
    const { accessToken, user } = response.data

    if (!accessToken || !user) {
      throw new Error('로그인 응답에 accessToken이나 user가 없습니다.')
    }

    // 토큰과 사용자 정보 저장
    localStorage.setItem('accessToken', accessToken)
    localStorage.setItem('user', JSON.stringify(user))

    return response
  },

  async register(userData) {
    return apiClient.post('/auth/signup', userData)
  },

  async logout() {
    try {
      // 서버에 로그아웃 요청 (선택사항)
      await apiClient.post('/auth/logout')
    } catch (error) {
      console.error('서버 로그아웃 에러:', error)
    } finally {
      // 로컬 데이터 정리
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
      localStorage.removeItem('refreshToken')
    }
  },

  async getAllUsers() {
    return apiClient.get('/users')
  },

  async getUserInfo(userId) {
    return apiClient.get(`/users/${userId}`)
  },

  async updateUser(userId, userData) {
    return apiClient.put(`/users/${userId}`, userData)
  },

  async updateUserTheme(userId, theme) {
    return apiClient.patch(`/users/${userId}/theme`, { themePreference: theme })
  },

  async deleteUser(userId) {
    return apiClient.delete(`/users/${userId}`)
  },

  isLoggedIn() {
    const token = localStorage.getItem('accessToken')
    return !!token
  },
}

export default AuthService
