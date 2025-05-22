import apiClient from './api'

const MealService = {
  async createMeal(mealData) {
    // 데이터 변환
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus.filter(menu => menu.trim()),
      memo: mealData.memo || '',
      eatDate: new Date().toISOString().split('T')[0], // YYYY-MM-DD 형식
      photoUrl: mealData.photoUrl || null
    }

    return apiClient.post('/meals/with-search', payload)
  },

  async getMealsByUserId(userId) {
    return apiClient.get(`/meals/filter?userId=${userId}`)
  },

  async getMealById(mealId) {
    return apiClient.get(`/meals/${mealId}`)
  },

  async updateMeal(mealId, mealData) {
    const payload = {
      timeSlot: convertMealTimeToKorean(mealData.mealTime),
      foodNames: mealData.menus.filter(menu => menu.trim()),
      memo: mealData.memo || '',
      eatDate: mealData.eatDate || new Date().toISOString().split('T')[0],
      photoUrl: mealData.photoUrl || null
    }

    return apiClient.put(`/meals/${mealId}`, payload)
  },

  async deleteMeal(mealId) {
    return apiClient.delete(`/meals/${mealId}`)
  }

}



// 시간대 변환 함수
function convertMealTimeToKorean(mealTime) {
  const timeMap = {
    'breakfast': '아침',
    'lunch': '점심',
    'dinner': '저녁'
  }
  return timeMap[mealTime] || '아침'
}

export default MealService
