import apiClient from './api'

const MealService = {
  async createMeal(mealData) {
    // 데이터 변환
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus.filter((menu) => menu.trim()),
      memo: mealData.memo || '',
      eatDate: new Date().toISOString().split('T')[0], // YYYY-MM-DD 형식
      photoUrl: mealData.photoUrl || null,
    }

    return apiClient.post('/meals/with-search', payload)
  },

  async getMealById(mealId) {
    return apiClient.get(`/meals/${mealId}`)
  },

  async updateMeal(mealId, mealData) {
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus.filter((menu) => menu.trim()),
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

  async getMealById(mealId) {
    return apiClient.get(`/meals/${mealId}`)
  },

  async deleteMeal(mealId) {
    return apiClient.delete(`/meals/${mealId}`)
  },

  async getMealsByUserId(userId, params = {}) {
    return this.getMealsByFilter(params) // JWT로 사용자 인증하므로 userId 불필요
  },
}

// 시간대 변환 함수
function convertMealTimeToKorean(mealTime) {
  const timeMap = {
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
