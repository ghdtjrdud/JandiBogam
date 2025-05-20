import axios from 'axios';

// API 기본 URL (환경 변수 또는 설정 파일에서 가져오는 것이 좋습니다)
const API_URL = '/api';

class AuthService {
  async register(userData) {
    return axios.post(`${API_URL}/auth/register`, userData);
  }

  async login(credentials) {
    const response = await axios.post(`${API_URL}/auth/login`, credentials);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response;
  }

  logout() {
    localStorage.removeItem('token');
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }

  isLoggedIn() {
    const token = localStorage.getItem('token');
    return !!token;
  }

  // 토큰을 사용하여 사용자 정보 조회
  async getUserInfo() {
    return axios.get(`${API_URL}/auth/me`);
  }
}

export default new AuthService();
