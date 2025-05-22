<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- Main Content - 대시보드와 동일한 레이아웃 적용 -->
    <main class="max-w-3xl mx-auto px-4 py-8">
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
        <button
          @click="goToAddMeal"
          class="px-6 py-3 bg-[#C7D7CB] hover:bg-green-700 text-white font-bold rounded-2xl transition-colors duration-200"
        >
          + 식단 추가
        </button>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-8">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"></div>
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
          @click="goToAddMeal"
          class="mt-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
        >
          첫 식단 등록하기
        </button>
      </div>

      <!-- Meal Records by Date -->
      <div v-else class="space-y-8">
        <div v-for="record in mealRecords" :key="record.date"  class="bg-white rounded-xl shadow-sm overflow-hidden">
          <!-- 날짜 헤더 카드 외부로 이동 -->
          <h3 class="bg-green-50 px-6 py-4 border-b-2 border-green-100">{{ record.date }} ({{ record.dayOfWeek }})</h3>

          <!-- 각 식사 개별 카드 -->
          <div class="p-5">
            <div
              v-for="meal in record.meals"
              :key="meal.id"
              class="bg-white rounded-xl shadow-sm p-5 hover:bg-green-25 transition-colors duration-200"
            >
              <div class="flex items-center">
                <!-- 이모지+식사타입 통합 -->
                <div class="w-20 h-20 bg-green-100 rounded-xl flex flex-col items-center justify-center mr-5 flex-shrink-0">
                  <span class="text-2xl mb-1">{{ getMealEmoji(meal.type) }}</span>
                  <span class="text-green-600 text-sm font-medium">{{ meal.typeText }}</span>
                </div>

                <!-- Meal Content -->
                <div class="flex-grow">
                  <!-- Meal Menu -->
                  <div class="text-lg font-medium text-gray-800 mb-2">
                    {{ meal.foods }}
                  </div>

                  <!-- Meal Details -->
                  <div class="text-sm text-gray-500">
                    <span v-if="meal.memo">
                      메모: {{ meal.memo }}
                    </span>
                    <span v-else-if="meal.hasPhoto" class="flex items-center gap-1">
                      📷 사진
                    </span>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="flex gap-2">
                  <button
                    @click="viewMealDetails(meal.id)"
                    class="px-5 py-2 bg-green-50 text-green-600 border border-green-200 rounded-2xl hover:bg-green-100 transition-colors duration-200 font-medium"
                  >
                    상세보기
                  </button>
                  <button @click="confirmDelete(meal.id)" class="btn btn-error btn-sm text-white">
                    삭제
                  </button>
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
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { useToast } from 'vue-toastification'
import MealService from '@/services/MealService'

const router = useRouter()
const toast = useToast()

// 반응형 데이터
const mealRecords = ref([])
const loading = ref(false)
const error = ref(null)

// 필터 데이터
const selectedPeriod = ref('최근 1주일')
const selectedTimeSlot = ref('전체')

const goToAddMeal = () => {
  router.push('/meal/record')
}

const getMealEmoji = (type) => {
  const emojiMap = {
    '아침': '🌅',
    '점심': '☀️',
    '저녁': '🌙'
  }
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
      startDate,
      endDate,
      timeSlot: selectedTimeSlot.value === '전체' ? null : selectedTimeSlot.value
    }

    const response = await MealService.getMealsByFilter(params)

    // 백엔드 데이터를 프론트엔드 형식으로 변환
    const groupedMeals = groupMealsByDate(response.data || [])
    mealRecords.value = groupedMeals

  } catch (err) {
    console.error('식단 목록 조회 실패:', err)
    error.value = '식단 목록을 불러오는데 실패했습니다.'

    // 인증 오류가 아닌 경우에만 에러 메시지 표시
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

  console.log('백엔드에서 받은 식단 데이터:', meals) // 디버깅용

  const grouped = meals.reduce((acc, meal) => {
    const eatDate = meal.eatDate

    if (!acc[eatDate]) {
      const date = new Date(eatDate)
      acc[eatDate] = {
        date: date.toLocaleDateString('ko-KR', {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        }),
        dayOfWeek: date.toLocaleDateString('ko-KR', { weekday: 'short' }),
        meals: []
      }
    }

    // 음식 이름들을 조합 - 실제 응답 구조에 맞게 수정
    let foodNames = '음식 정보 없음'

    console.log('개별 식단 데이터:', meal) // 디버깅용

    // 백엔드 응답 구조에 따라 수정 - foodNames 배열 우선 처리
    if (meal.foodNames && Array.isArray(meal.foodNames) && meal.foodNames.length > 0) {
      // foodNames 배열이 직접 있는 경우 (실제 응답 구조)
      foodNames = meal.foodNames.join(', ')
    } else if (meal.foodNutrients && Array.isArray(meal.foodNutrients) && meal.foodNutrients.length > 0) {
      // foodNutrients 배열에서 foodName 추출
      const foods = meal.foodNutrients
        .map(fn => fn.foodName)
        .filter(Boolean)
      if (foods.length > 0) {
        foodNames = foods.join(', ')
      }
    } else if (meal.mealFoods && Array.isArray(meal.mealFoods) && meal.mealFoods.length > 0) {
      // mealFoods 배열에서 추출 (기존 구조)
      const foods = meal.mealFoods
        .map(mf => {
          return mf.foodNutrientDto?.foodName ||
                 mf.foodName ||
                 mf.name ||
                 mf.food?.name ||
                 mf.food?.foodName
        })
        .filter(Boolean)

      if (foods.length > 0) {
        foodNames = foods.join(', ')
      }
    }

    acc[eatDate].meals.push({
      id: meal.id,
      type: meal.timeSlot,
      typeText: meal.timeSlot,
      foods: foodNames,
      hasPhoto: !!meal.photoUrl,
      memo: meal.memo || ''
    })

    return acc
  }, {})

  // 날짜 순으로 정렬 (최신순)
  return Object.values(grouped).sort((a, b) => {
    const dateA = new Date(Object.keys(grouped).find(key => grouped[key] === a))
    const dateB = new Date(Object.keys(grouped).find(key => grouped[key] === b))
    return dateB - dateA
  })
}

// 필터 변경 시 데이터 다시 조회
const onFilterChange = () => {
  fetchMealRecords()
}

// 상세보기
const viewMealDetails = (mealId) => {
  router.push(`/meal/${mealId}/detail`)
}

// 삭제 확인 다이얼로그
const confirmDelete = (id) => {
  if (confirm('이 식단 기록을 삭제하시겠습니까?')) {
    deleteMeal(id)
  }
}

// 복약 정보 삭제
const deleteMeal = async (id) => {
  try {
    await MealService.deleteMeal(id)
    toast.success('식단 기록이 삭제되었습니다.')

    // 목록 새로고침
    fetchMealRecords()
  } catch (error) {
    console.error('삭제 실패:', error)

    let errorMessage = '삭제에 실패했습니다.'
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    }

    toast.error(errorMessage)
  }
}

// 컴포넌트 마운트 시 데이터 조회
onMounted(() => {
  fetchMealRecords()
})
</script>

<style scoped>
/* 추가 스타일링 */
.bg-green-25 {
  background-color: #f9fdfb;
}

/* 반응형 디자인 */
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

/* 전체 레이아웃이 헤더 너비에 맞도록 설정 */
@media (min-width: 768px) {
  main {
    max-width: 1280px; /* 대시보드와 동일한 최대 너비 설정 */
  }
}

/* 작은 화면에서 레이아웃 조정 */
@media (max-width: 767px) {
  main {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
