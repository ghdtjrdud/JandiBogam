import api from './api'

export default{

    // 내 정보 조회
    getMyInfo(userId){
        return api.get(`/users/${userId}`)   // 이렇게 해야 userId가 변수로 치환됨
    },
    updateMyInfo(userId, userData){
        return api.put(`/users/${userId}`, userData)
    }
}