// src/stores/auth.js
import { defineStore } from 'pinia'
import AuthService from '@/services/AuthService'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const userRaw = localStorage.getItem('user')
    const user = userRaw && userRaw !== 'undefined' ? JSON.parse(userRaw) : null

    return {
      user,
      token: localStorage.getItem('accessToken') || null,
      loading: false,
      error: null,
      authLoaded: false, // 인증 복원 완료 flag
    }
  },

  getters: {
    isAuthenticated: (state) => !!state.token,
    getUser: (state) => state.user,
    isLoading: (state) => state.loading,
    getError: (state) => state.error,
  },

  actions: {
    setAuth(user, token) {
      this.user = user
      this.token = token
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('accessToken', token)
    },

    // 로그인
    async login(credentials) {
      this.loading = true
      this.error = null

      try {
        const response = await AuthService.login({
          loginId: credentials.loginId,
          password: credentials.password,
        })

        const { accessToken, user } = response.data

        // 스토어 상태 업데이트
        this.user = user
        this.token = accessToken

        return true
      } catch (error) {
        console.error('로그인 에러:', error)

        if (error.response) {
          if (error.response.status === 401) {
            this.error = '아이디 또는 비밀번호가 일치하지 않습니다.'
          } else {
            this.error = error.response.data?.message || '로그인에 실패했습니다'
          }
        } else {
          this.error = '서버 연결에 실패했습니다.'
        }

        return false
      } finally {
        this.loading = false
      }
    },

    // 회원가입
    async register(userData) {
      this.loading = true
      this.error = null

      try {
        const signupData = {
          loginId: userData.loginId,
          password: userData.password,
          name: userData.name,
          gender: userData.gender,
          birthDate: userData.birthdate,
        }

        await AuthService.register(signupData)
        return true
      } catch (error) {
        console.error('회원가입 에러:', error)

        if (error.response) {
          this.error = error.response.data?.message || '회원가입에 실패했습니다'
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

    // 로그아웃
    async logout() {
      try {
        this.loading = true
        await AuthService.logout()
      } catch (error) {
        console.error('로그아웃 에러:', error)
      } finally {
        // 스토어 상태 초기화
        this.user = null
        this.token = null
        this.loading = false
      }
    },

    // 사용자 정보 조회
    async getUserDetail(userId) {
      this.loading = true
      this.error = null

      try {
        const response = await AuthService.getUserInfo(userId)
        return response.data
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error)

        if (error.response) {
          if (error.response.status === 400) {
            this.error = '잘못된 요청입니다.'
          } else {
            this.error = error.response.data?.message || '사용자 정보 조회에 실패했습니다.'
          }
        } else {
          this.error = '서버 연결에 실패했습니다.'
        }

        return null
      } finally {
        this.loading = false
      }
    },

    // 모든 사용자 조회
    async getAllUsers() {
      this.loading = true
      this.error = null

      try {
        const response = await AuthService.getAllUsers()
        return response.data
      } catch (error) {
        console.error('사용자 목록 조회 실패:', error)
        this.error = error.response?.data?.message || '사용자 목록 조회에 실패했습니다.'
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

        // 현재 로그인한 사용자의 정보를 업데이트한 경우 스토어 동기화
        if (this.user && this.user.id === userId) {
          this.user = response.data
          localStorage.setItem('user', JSON.stringify(response.data))
        }

        return true
      } catch (error) {
        console.error('사용자 정보 업데이트 실패:', error)
        this.error = error.response?.data?.message || '사용자 정보 업데이트에 실패했습니다.'
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

        // 현재 로그인한 사용자의 테마를 업데이트한 경우 스토어 동기화
        if (this.user && this.user.id === userId) {
          this.user.themePreference = theme
          localStorage.setItem('user', JSON.stringify(this.user))
        }

        return true
      } catch (error) {
        console.error('테마 업데이트 실패:', error)
        this.error = error.response?.data?.message || '테마 업데이트에 실패했습니다.'
        return false
      } finally {
        this.loading = false
      }
    },

    // 인증 상태 초기화 (앱 시작 시 호출)
    async initAuth() {
      const token = localStorage.getItem('accessToken')
      const userRaw = localStorage.getItem('user')

      if (token && userRaw && userRaw !== 'undefined') {
        try {
          this.token = token
          this.user = JSON.parse(userRaw)
        } catch (e) {
          console.warn('로컬스토리지의 user 파싱 실패:', userRaw, e)
          this.user = null
          this.token = null
          // 파싱 실패 시 로컬스토리지 정리
          localStorage.removeItem('user')
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
        }
      } else {
        this.token = null
        this.user = null
      }

      this.authLoaded = true
    },
  },
})
