import apiClient from './api'

const MealService = {
  async createMeal(mealData) {
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus ? mealData.menus.filter((menu) => menu.trim()) : [],
      memo: mealData.memo || '',
      eatDate: mealData.eatDate || new Date().toISOString().split('T')[0],
      photoUrl: mealData.photoUrl || null,
    }
    console.log('전송할 payload:', payload)
    return apiClient.post('/meals/with-search', payload)
  },

  async createMealWithFoodSearch(mealData) {
    return this.createMeal(mealData)
  },

  async getMealById(mealId) {
    return apiClient.get(`/meals/${mealId}`)
  },

  async updateMeal(mealId, mealData) {
    const payload = {
      // mealData에서 timeSlot이 직접 오는 경우와 mealTime으로 오는 경우 모두 처리
      timeSlot: mealData.timeSlot || convertMealTimeToKorean(mealData.mealTime),
      // foodNames가 직접 오는 경우와 menus로 오는 경우 모두 처리
      foodNames:
        mealData.foodNames || (mealData.menus ? mealData.menus.filter((menu) => menu.trim()) : []),
      memo: mealData.memo || '',
      eatDate: mealData.eatDate || new Date().toISOString().split('T')[0],
      photoUrl: mealData.photoUrl || null,
    }
    console.log('updateMeal payload:', payload) // 디버깅용
    return apiClient.put(`/meals/${mealId}`, payload)
  },

  async deleteMeal(mealId) {
    return apiClient.delete(`/meals/${mealId}`)
  },

  async getMealsByFilter(params = {}) {
    const queryParams = new URLSearchParams()
    if (params.userId) queryParams.append('userId', params.userId)
    if (params.startDate) queryParams.append('startDate', params.startDate)
    if (params.endDate) queryParams.append('endDate', params.endDate)
    if (params.timeSlot && params.timeSlot !== '전체') {
      queryParams.append('timeSlot', params.timeSlot)
    }
    const url = `/meals/filter${queryParams.toString() ? '?' + queryParams.toString() : ''}`
    return apiClient.get(url)
  },

  // 사용처 없으면 삭제 가능
  async getMealsByUserId(userId, params = {}) {
    return this.getMealsByFilter(params)
  },

  async getMealsByDate(date) {
    return apiClient.get(`/meals/daily/${date}`)
  },

  validateDate(dateString) {
    const date = new Date(dateString)
    const today = new Date()
    if (isNaN(date.getTime())) throw new Error('유효하지 않은 날짜 형식입니다.')
    if (date > today) throw new Error('미래 날짜는 선택할 수 없습니다.')
    const oneYearAgo = new Date()
    oneYearAgo.setFullYear(today.getFullYear() - 1)
    if (date < oneYearAgo) throw new Error('1년 이전의 날짜는 선택할 수 없습니다.')
    return true
  },
}

function convertMealTimeToKorean(mealTime) {
  const timeMap = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
  }
  // 만약 한글이 이미 들어오면 그대로 반환
  if (['아침', '점심', '저녁'].includes(mealTime)) return mealTime
  return timeMap[mealTime] || mealTime || '아침'
}

function convertKoreanToMealTime(koreanTime) {
  const timeMap = {
    아침: 'breakfast',
    점심: 'lunch',
    저녁: 'dinner',
  }
  if (['breakfast', 'lunch', 'dinner'].includes(koreanTime)) return koreanTime
  return timeMap[koreanTime] || koreanTime || 'breakfast'
}

export default MealService
