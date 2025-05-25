<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- Main Content - 대시보드와 동일한 레이아웃 적용 -->
    <main class="w-full max-w-[1024px] px-8 mx-auto py-10">
      <!-- Title -->
      <div class="mb-8">
        <h2 class="text-3xl font-bold text-gray-800">식단 기록</h2>
      </div>

      <!-- Filter Bar with Add Button -->
      <div class="bg-white rounded-xl p-6 shadow-sm mb-8 flex justify-between items-center">
        <!-- Filter Section -->
        <div class="flex gap-6">
          <div class="flex items-center gap-3">
            <span class="text-gray-700 font-medium">기간</span>
            <select
              v-model="selectedPeriod"
              @change="onFilterChange"
              class="px-4 py-2 border border-gray-200 rounded-lg bg-gray-50 min-w-[150px] focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option>최근 1주일</option>
              <option>최근 1개월</option>
              <option>최근 3개월</option>
            </select>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-gray-700 font-medium">식사 시간</span>
            <select
              v-model="selectedTimeSlot"
              @change="onFilterChange"
              class="px-4 py-2 border border-gray-200 rounded-lg bg-gray-50 min-w-[150px] focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option>전체</option>
              <option>아침</option>
              <option>점심</option>
              <option>저녁</option>
            </select>
          </div>
        </div>

        <!-- Add Button -->
        <div>
          <button
            v-if="isMyMealList"
            @click="goToAddMeal"
            class="px-6 py-3 bg-[#C7D7CB] hover:bg-green-700 text-white font-bold rounded-2xl transition-colors duration-200"
          >
            + 식단 추가
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-8">
        <div
          class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"
        ></div>
        <p class="mt-2 text-gray-600">식단 목록을 불러오는 중...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-8">
        <p class="text-red-600">{{ error }}</p>
        <button
          @click="fetchMealRecords"
          class="mt-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
        >
          다시 시도
        </button>
      </div>

      <!-- Empty State -->
      <div v-else-if="mealRecords.length === 0" class="text-center py-8">
        <p class="text-gray-600">등록된 식단이 없습니다.</p>
        <button
          v-if="isMyMealList"
          @click="goToAddMeal"
          class="mt-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
        >
          첫 식단 등록하기
        </button>
      </div>

      <!-- Meal Records by Date -->
      <div v-else class="space-y-8">
        <div
          v-for="record in mealRecords"
          :key="record.date"
          class="bg-white rounded-xl shadow-sm overflow-hidden"
        >
          <!-- 날짜 헤더 -->
          <h3 class="bg-green-50 px-6 py-4 border-b-2 border-green-100">
            {{ record.date }} ({{ record.dayOfWeek }})
          </h3>

          <!-- 각 식사 -->
          <div class="p-5">
            <div
              v-for="meal in record.meals"
              :key="meal.id"
              class="bg-white rounded-xl shadow-sm p-5 hover:bg-green-25 transition-colors duration-200"
            >
              <div class="flex items-center">
                <!-- 이모지+식사타입 -->
                <div
                  class="w-20 h-20 bg-green-100 rounded-xl flex flex-col items-center justify-center mr-5 flex-shrink-0"
                >
                  <span class="text-2xl mb-1">{{ getMealEmoji(meal.type) }}</span>
                  <span class="text-green-600 text-sm font-medium">{{ meal.typeText }}</span>
                </div>

                <!-- Meal Content -->
                <div class="flex-grow">
                  <div class="text-lg font-medium text-gray-800 mb-2">
                    {{ meal.foods }}
                  </div>

                  <!-- Meal Details -->
                  <div class="text-sm text-gray-500 flex flex-col gap-2">
                    <span v-if="meal.hasPhoto" class="flex items-center gap-1"> 📷 사진 </span>
                    <span v-if="meal.memo"> 메모: {{ meal.memo }} </span>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="flex gap-2">
                  <button @click="viewMealDetails(meal.id)">상세보기</button>
                  <button v-if="isMyMealList" @click="goToEditMeal(meal.id)">수정</button>
                  <button v-if="isMyMealList" @click="confirmDelete(meal.id)">삭제</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted, computed } from 'vue'
import { useToast } from 'vue-toastification'
import MealService from '@/services/MealService'

function goToEditMeal(mealId) {
  router.push(`/meal/${mealId}/edit`)
}

const router = useRouter()
const route = useRoute()
const toast = useToast()

// localStorage에서 userId 찾기
let myUserId = null
const userStr = localStorage.getItem('user')
if (userStr) {
  try {
    const userObj = JSON.parse(userStr)
    if (userObj && userObj.id) {
      myUserId = String(userObj.id)
    }
  } catch (e) {}
}
if (!myUserId) {
  const possibleKeys = ['userId', 'user_id', 'id', 'userID', 'memberId', 'member_id']
  for (const key of possibleKeys) {
    const value = localStorage.getItem(key)
    if (value && value !== 'null' && value !== 'undefined') {
      myUserId = value
      break
    }
  }
}

// 내 식단 리스트인지 판별
const isMyMealList = computed(() => {
  if (!myUserId || myUserId === 'null' || myUserId === 'undefined') return false
  return String(myUserId) === String(route.params.userId)
})

// 반응형 데이터
const mealRecords = ref([])
const loading = ref(false)
const error = ref(null)

// 필터 데이터
const selectedPeriod = ref('최근 1주일')
const selectedTimeSlot = ref('전체')

// 식단 등록 페이지 이동
const goToAddMeal = () => {
  router.push('/meal/record')
}

// 식사 이모지 반환
const getMealEmoji = (type) => {
  const emojiMap = { 아침: '🌅', 점심: '☀️', 저녁: '🌙' }
  return emojiMap[type] || '🍽️'
}

// 기간 선택에 따른 날짜 계산
const getDateRange = (period) => {
  const today = new Date()
  const endDate = today.toISOString().split('T')[0]
  let startDate
  switch (period) {
    case '최근 1주일':
      startDate = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      break
    case '최근 1개월':
      startDate = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      break
    case '최근 3개월':
      startDate = new Date(today.getTime() - 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      break
    default:
      startDate = endDate
  }
  return { startDate, endDate }
}

// 식단 목록 조회
const fetchMealRecords = async () => {
  try {
    loading.value = true
    error.value = null
    const { startDate, endDate } = getDateRange(selectedPeriod.value)
    const params = {
      userId: route.params.userId,
      startDate,
      endDate,
      timeSlot: selectedTimeSlot.value === '전체' ? null : selectedTimeSlot.value,
    }
    const response = await MealService.getMealsByFilter(params)
    const groupedMeals = groupMealsByDate(response.data || [])
    mealRecords.value = groupedMeals
  } catch (err) {
    error.value = '식단 목록을 불러오는데 실패했습니다.'
    if (err.response?.status !== 401 && err.response?.status !== 403) {
      toast.error('식단 목록을 불러오는데 실패했습니다.')
    }
  } finally {
    loading.value = false
  }
}

// 날짜별로 식단 그룹화
const groupMealsByDate = (meals) => {
  if (!Array.isArray(meals) || meals.length === 0) {
    return []
  }
  const grouped = meals.reduce((acc, meal) => {
    const eatDate = meal.eatDate
    if (!acc[eatDate]) {
      const date = new Date(eatDate)
      acc[eatDate] = {
        date: date.toLocaleDateString('ko-KR', {
          year: 'numeric',
          month: 'long',
          day: 'numeric',
        }),
        dayOfWeek: date.toLocaleDateString('ko-KR', { weekday: 'short' }),
        meals: [],
      }
    }
    let foodNames = '음식 정보 없음'
    if (meal.foodNames && Array.isArray(meal.foodNames) && meal.foodNames.length > 0) {
      foodNames = meal.foodNames.join(', ')
    } else if (
      meal.foodNutrients &&
      Array.isArray(meal.foodNutrients) &&
      meal.foodNutrients.length > 0
    ) {
      const foods = meal.foodNutrients.map((fn) => fn.foodName).filter(Boolean)
      if (foods.length > 0) foodNames = foods.join(', ')
    } else if (meal.mealFoods && Array.isArray(meal.mealFoods) && meal.mealFoods.length > 0) {
      const foods = meal.mealFoods
        .map((mf) => {
          return (
            mf.foodNutrientDto?.foodName ||
            mf.foodName ||
            mf.name ||
            mf.food?.name ||
            mf.food?.foodName
          )
        })
        .filter(Boolean)
      if (foods.length > 0) foodNames = foods.join(', ')
    }
    acc[eatDate].meals.push({
      id: meal.id,
      type: meal.timeSlot,
      typeText: meal.timeSlot,
      foods: foodNames,
      hasPhoto: !!meal.photoUrl,
      memo: meal.memo || '',
    })
    return acc
  }, {})
  return Object.values(grouped).sort((a, b) => {
    const dateA = new Date(Object.keys(grouped).find((key) => grouped[key] === a))
    const dateB = new Date(Object.keys(grouped).find((key) => grouped[key] === b))
    return dateB - dateA
  })
}

const onFilterChange = () => {
  fetchMealRecords()
}

const viewMealDetails = (mealId) => {
  router.push(`/meal/${mealId}/detail`)
}

const confirmDelete = (id) => {
  if (confirm('이 식단 기록을 삭제하시겠습니까?')) {
    deleteMeal(id)
  }
}

const deleteMeal = async (id) => {
  try {
    await MealService.deleteMeal(id)
    toast.success('식단 기록이 삭제되었습니다.')
    fetchMealRecords()
  } catch (error) {
    let errorMessage = '삭제에 실패했습니다.'
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    }
    toast.error(errorMessage)
  }
}

onMounted(() => {
  fetchMealRecords()
})
</script>

<style scoped>
.bg-green-25 {
  background-color: #f9fdfb;
}
/* (이하 스타일은 그대로 유지) */
@media (max-width: 768px) {
  .main-content {
    padding-left: 1rem;
    padding-right: 1rem;
  }
  .filter-section {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  .filter-controls {
    flex-direction: column;
    gap: 1rem;
  }
  .meal-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  .meal-icon {
    align-self: center;
  }
}
@media (min-width: 768px) {
  main {
    max-width: 1280px;
  }
}
@media (max-width: 767px) {
  main {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
