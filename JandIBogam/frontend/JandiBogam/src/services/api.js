import axios from 'axios'

// Axios 인스턴스 생성 (baseURL, 헤더 , 인터셉더 등 설정)
const apiClient = axios.create({
    baseURL: '/api', // 프론트 서버에서 /api로 시작하면 백엔드로 프록시됨
    timeout: 10000, // 요청 10초 넘으면 취소
    Headers: {
        'Content-Type': 'application/json', // 모든 요청의 Content-Type 헤더
        'Accept': 'application/json',
    }
})

// 요청 인터셉터 (토큰 자동 추가)
apiClient.interceptors.request.user(
    config => {
        // 요청 전송 전 처리
        // 토큰 가져와서 헤더에 추가
        const token = localStorage.getItem('accessToken')
        if(token){
            config.Headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 응답 인터셉터 (401 에러시 토큰 재발급)
apiClient.interceptors.response.use(
    response => response, // 성공 응답은 그대로 통과
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
