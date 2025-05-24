import apiClient from './api'

const MealService = {
  async createMeal(mealData) {
    // 데이터 변환
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus ? mealData.menus.filter((menu) => menu.trim()) : [], // menus를 foodNames로 변환
      memo: mealData.memo || '',
      eatDate: mealData.eatDate || new Date().toISOString().split('T')[0],
      photoUrl: mealData.photoUrl || null,
    }

    console.log('전송할 payload:', payload)
    return apiClient.post('/meals/with-search', payload)
  },

  // createMealWithFoodSearch
  async createMealWithFoodSearch(mealData) {
    return this.createMeal(mealData)
  },

  // 식단 상세 조회
  async getMealById(mealId) {
    return apiClient.get(`/meals/${mealId}`)
  },

  async updateMeal(mealId, mealData) {
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus ? mealData.menus.filter((menu) => menu.trim()) : [],
      memo: mealData.memo || '',
      eatDate: mealData.eatDate || new Date().toISOString().split('T')[0],
      photoUrl: mealData.photoUrl || null,
    }

    return apiClient.put(`/meals/${mealId}`, payload)
  },

  async deleteMeal(mealId) {
    return apiClient.delete(`/meals/${mealId}`)
  },

  async getMealsByFilter(params = {}) {
    const queryParams = new URLSearchParams()

    // ✅ 반드시 소문자 userId로!
    if (params.userId) queryParams.append('userId', params.userId)
    if (params.startDate) queryParams.append('startDate', params.startDate)
    if (params.endDate) queryParams.append('endDate', params.endDate)
    if (params.timeSlot && params.timeSlot !== '전체') {
      queryParams.append('timeSlot', params.timeSlot)
    }

    const url = `/meals/filter${queryParams.toString() ? '?' + queryParams.toString() : ''}`
    return apiClient.get(url)
  },

  async getMealsByUserId(userId, params = {}) {
    return this.getMealsByFilter(params) // JWT로 사용자 인증하므로 userId 불필요
  },

  // 특정 날짜의 식단 조회 메서드 추가
  async getMealsByDate(date) {
    return apiClient.get(`/meals/daily/${date}`)
  },

  // 날짜 유효성 검사 헬퍼 메서드
  validateDate(dateString) {
    const date = new Date(dateString)
    const today = new Date()

    // 유효한 날짜인지 확인
    if (isNaN(date.getTime())) {
      throw new Error('유효하지 않은 날짜 형식입니다.')
    }

    // 미래 날짜 체크
    if (date > today) {
      throw new Error('미래 날짜는 선택할 수 없습니다.')
    }

    // 너무 과거 날짜 체크 (예: 1년 전)
    const oneYearAgo = new Date()
    oneYearAgo.setFullYear(today.getFullYear() - 1)

    if (date < oneYearAgo) {
      throw new Error('1년 이전의 날짜는 선택할 수 없습니다.')
    }

    return true
  },
}

// 시간대 변환 함수
function convertMealTimeToKorean(mealTime) {
  const timeMap = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
  }
  return timeMap[mealTime] || '아침'
}

function convertKoreanToMealTime(koreanTime) {
  const timeMap = {
    아침: 'breakfast',
    점심: 'lunch',
    저녁: 'dinner',
  }
  return timeMap[koreanTime] || 'breakfast'
}

export default MealService
