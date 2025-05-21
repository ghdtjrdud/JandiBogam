import { defineStore } from 'pinia'
import axios from 'axios'

// API 서비스 - AuthService 대신 직접 axios 요청으로 구현
const API_URL = '/api' // 백엔드 API 경로에 맞게 조정

const AuthService = {
  // 로그인: /api/auth/login
  async login(credentials) {
    return axios.post(`${API_URL}/auth/login`, credentials)
  },

  // 회원가입: /api/auth/signup
  async register(userData) {
    return axios.post(`${API_URL}/auth/signup`, userData)
  },

  // 로그아웃: /api/auth/logout (서버에 요청 + 로컬 스토리지 정리)
  async logout() {
    try {
      await axios.post(`${API_URL}/auth/logout`)
    } catch (error) {
      console.error('로그아웃 에러:', error)
    } finally {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('user')
    }
  },

  // 사용자 정보 조회: /api/users/{userId} 경로 사용
  async getUserInfo(userId) {
    return axios.get(`${API_URL}/users/${userId}`)
  },

  // 사용자 목록 조회: /api/users 경로 사용
  async getAllUsers() {
    return axios.get(`${API_URL}/users`)
  },

  // 사용자 업데이트: /api/users/{userId} 경로 사용
  async updateUser(userId, userData) {
    return axios.put(`${API_URL}/users/${userId}`, userData)
  },

  // 사용자 테마 업데이트: /api/users/{userId}/theme 경로 사용
  async updateUserTheme(userId, theme) {
    return axios.patch(`${API_URL}/users/${userId}/theme`, { themePreference: theme })
  },

  // 사용자 삭제: /api/users/{userId} 경로 사용
  async deleteUser(userId) {
    return axios.delete(`${API_URL}/users/${userId}`)
  },

  // 인증 헤더 설정
  setAuthHeader(token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
  },

  // 인증 헤더 제거
  removeAuthHeader() {
    delete axios.defaults.headers.common['Authorization']
  },
}


export const useAuthStore = defineStore('auth', {
 state: () => {
  const userRaw = localStorage.getItem('user');
  const user = userRaw && userRaw !== 'undefined' ? JSON.parse(userRaw) : null;

  return {
    user,
    token: localStorage.getItem('accessToken') || null,
    loading: false,
    error: null,
  };
},


  getters: {
    isAuthenticated: (state) => !!state.token,
    getUser: (state) => state.user,
    isLoading: (state) => state.loading,
    getError: (state) => state.error,
  },

  actions: {
    // 사용자 정보와 토큰 저장
    setAuth(user, token) {
      this.user = user
      this.token = token
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('accessToken', token)
      AuthService.setAuthHeader(token)
    },

    // 로그인 (백엔드 응답 구조에 맞게 수정)
    async login(credentials) {
      this.loading = true
      this.error = null

      try {
        // loginRequest 객체 형식에 맞춰서 데이터 전송
        const response = await AuthService.login({
          loginId: credentials.loginId, // 또는 credentials.id (백엔드 요구 형식에 맞게)
          password: credentials.password,
        })

        // 백엔드 응답에서 TokenResponse 형식으로 데이터 추출
        const { accessToken, user } = response.data
        this.setAuth(user, accessToken)
        return true
      } catch (error) {
        console.error('로그인 에러:', error)

        if (error.response) {
          if (error.response.status === 401) {
            this.error = '아이디 또는 비밀번호가 일치하지 않습니다.'
          } else {
            this.error = error.response.data || '로그인에 실패했습니다'
          }
        } else {
          this.error = '서버 연결에 실패했습니다.'
        }

        return false
      } finally {
        this.loading = false
      }
    },

    // 회원가입 (백엔드 API에 맞게 수정)
    async register(userData) {
      this.loading = true
      this.error = null

      try {
        // 백엔드 SignupRequest 형식에 맞춰 데이터 변환
        const signupData = {
          loginId: userData.loginId, // 또는 userData.loginId (백엔드 요구 형식에 맞게)
          password: userData.password,
          name: userData.name,
          gender: userData.gender,
          birthDate: userData.birthdate,
          // 기타 필요한 필드들...
        }

        const response = await AuthService.register(signupData)

        // 회원가입 성공 메시지만 반환하고 자동 로그인은 없음
        return true
      } catch (error) {
        console.error('회원가입 에러:', error)

        if (error.response) {
          // 서버 응답 에러 처리
          this.error = error.response.data || '회원가입에 실패했습니다'

          // 특정 에러 상태에 따른 메시지 처리
          if (error.response.status === 409) {
            this.error = '이미 존재하는 아이디입니다'
          } else if (error.response.status === 400) {
            this.error = '입력 정보가 올바르지 않습니다'
          }
        } else {
          this.error = '서버 연결에 실패했습니다'
        }

        return false
      } finally {
        this.loading = false
      }
    },

    // 로그아웃 (서버에 요청 후 로컬 상태 정리)
    async logout() {
      try {
        this.loading = true
        await AuthService.logout()
      } catch (error) {
        console.error('로그아웃 에러:', error)
      } finally {
        this.user = null
        this.token = null
        AuthService.removeAuthHeader()
        this.loading = false
      }
    },

    // 사용자 정보 조회
    async getUserDetail(userId) {
      this.loading = true
      this.error = null

      try {
        const response = await AuthService.getUserInfo(userId)
        return response.data // UserDto 객체
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error)

        if (error.response) {
          if (error.response.status === 400) {
            this.error = '잘못된 요청입니다.'
          } else {
            this.error = error.response.data || '사용자 정보 조회에 실패했습니다.'
          }
        } else {
          this.error = '서버 연결에 실패했습니다.'
        }

        return null
      } finally {
        this.loading = false
      }
    },

    // 사용자 목록 조회
    async getAllUsers() {
      this.loading = true
      this.error = null

      try {
        const response = await AuthService.getAllUsers()
        return response.data // UserDto 목록
      } catch (error) {
        console.error('사용자 목록 조회 실패:', error)
        this.error = error.response?.data || '사용자 목록 조회에 실패했습니다.'
        return []
      } finally {
        this.loading = false
      }
    },

    // 사용자 정보 업데이트
    async updateUser(userId, userData) {
      this.loading = true
      this.error = null

      try {
        const response = await AuthService.updateUser(userId, userData)

        // 현재 로그인한 사용자 정보 업데이트인 경우 로컬 상태도 갱신
        if (this.user && this.user.id === userId) {
          this.user = response.data
          localStorage.setItem('user', JSON.stringify(response.data))
        }

        return true
      } catch (error) {
        console.error('사용자 정보 업데이트 실패:', error)
        this.error = error.response?.data || '사용자 정보 업데이트에 실패했습니다.'
        return false
      } finally {
        this.loading = false
      }
    },

    // 사용자 테마 업데이트
    async updateUserTheme(userId, theme) {
      this.loading = true
      this.error = null

      try {
        await AuthService.updateUserTheme(userId, theme)

        // 현재 로그인한 사용자의 테마 업데이트인 경우 로컬 상태도 갱신
        if (this.user && this.user.id === userId) {
          this.user.themePreference = theme
          localStorage.setItem('user', JSON.stringify(this.user))
        }

        return true
      } catch (error) {
        console.error('테마 업데이트 실패:', error)
        this.error = error.response?.data || '테마 업데이트에 실패했습니다.'
        return false
      } finally {
        this.loading = false
      }
    },

    // 애플리케이션 시작 시 인증 상태 초기화
    async initAuth() {
      const token = localStorage.getItem('accessToken')
      if (token) {
        this.token = token
        AuthService.setAuthHeader(token)

        // 저장된 사용자 정보가 있으면 사용
        const user = JSON.parse(localStorage.getItem('user'))
        if (user) {
          this.user = user
        }
      }
    },
  },
})
