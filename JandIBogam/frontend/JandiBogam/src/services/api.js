import axios from 'axios'

// Axios 인스턴스 생성 (baseURL, 헤더, 인터셉터 등 설정)
const apiClient = axios.create({
    baseURL: '/api', // 프론트 서버에서 /api로 시작하면 백엔드로 프록시됨
    timeout: 10000,  // 요청 10초 넘으면 취소
    headers: {       // 소문자 headers!
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    }
})

// 요청 인터셉터 (토큰 자동 추가)
apiClient.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken')
        if(token){
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 응답 인터셉터 (401 에러시 바로 로그아웃)
apiClient.interceptors.response.use(
    response => response,
    error => {

        // 401 에러(인증 실패)이고 토큰 재발급 시도가 아직 안 된 경우
        if(error.response && error.response.status === 401){

            // 401 인증 실패 시 바로 로그아우 ㅅ처리
            localStorage.removeItem('accessToken')
            localStorage.removeItem('refreshToken')
            location.href = '/login'
        }
        return Promise.reject(error)
    }
)

// api.js에 추가
export const authAPI = {
    // 로그인 API
    login: (credentials) => {
        return apiClient.post('/auth/login', credentials);
    }
}

export default apiClient;
