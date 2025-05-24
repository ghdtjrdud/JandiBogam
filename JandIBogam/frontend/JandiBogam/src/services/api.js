// src/services/api.js
import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api', // 백엔드 서버 주소로 변경
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

// 토큰 자동 헤더/401 처리 등
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) config.headers['Authorization'] = `Bearer ${token}`
    return config
  },
  (error) => Promise.reject(error),
)

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.warn(`‼️ API 인증 실패! [${error.response.status}]`, error.config.url)

      // ✅ 로그인 요청이 아닌 경우에만 자동 리다이렉트
      const isLoginRequest = error.config.url?.includes('/auth/login')

      if (!isLoginRequest) {
        // 필요하다면 알림도 띄울 수 있음 (예: alert, toast 등)
        localStorage.removeItem('accessToken')
        localStorage.removeItem('user')
        localStorage.removeItem('refreshToken')
        location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export default apiClient
