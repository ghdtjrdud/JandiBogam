import { defineStore } from 'pinia';
import AuthService from '@/services/AuthService';
import axios from 'axios';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    getUser: (state) => state.user,
    isLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    // 사용자 정보와 토큰 저장
    setAuth(user, token) {
      this.user = user;
      this.token = token;
      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('token', token);
      // Axios 요청 헤더에 토큰 설정
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    },

    // 로그인
    async login(credentials) {
      this.loading = true;
      this.error = null;

      try {
        const response = await AuthService.login(credentials);
        const { user, token } = response.data;
        this.setAuth(user, token);
        return true;
      } catch (error) {
        this.error = error.response?.data?.message || '로그인에 실패했습니다';
        return false;
      } finally {
        this.loading = false;
      }
    },

    // 회원가입
    async register(userData) {
      this.loading = true;
      this.error = null;

      try {
        await AuthService.register(userData);
        return true;
      } catch (error) {
        this.error = error.response?.data?.message || '회원가입에 실패했습니다';
        return false;
      } finally {
        this.loading = false;
      }
    },

    // 로그아웃
    logout() {
      AuthService.logout();
      this.user = null;
      this.token = null;
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      // Axios 헤더에서 토큰 제거
      delete axios.defaults.headers.common['Authorization'];
    },

    // 사용자 정보 갱신
    async fetchUserInfo() {
      if (!this.token) return;

      this.loading = true;

      try {
        const response = await AuthService.getUserInfo();
        this.user = response.data;
        localStorage.setItem('user', JSON.stringify(response.data));
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error);
        // 토큰이 만료된 경우 로그아웃 처리
        if (error.response?.status === 401) {
          this.logout();
        }
      } finally {
        this.loading = false;
      }
    },

    // 애플리케이션 시작 시 인증 상태 초기화
    async initAuth() {
      const token = localStorage.getItem('token');
      if (token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        await this.fetchUserInfo();
      }
    }
  }
});
