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
            <span class="text-gray-700 font-medium">기간 </span>
            <select
              class="px-4 py-2 border border-gray-200 rounded-lg bg-gray-50 min-w-[150px] focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option>최근 1주일</option>
              <option>최근 1개월</option>
              <option>최근 3개월</option>
              <option>직접 설정</option>
            </select>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-gray-700 font-medium">식사 시간</span>
            <select
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

      <!-- Meal Records by Date -->
      <div class="space-y-8">
        <div
          v-for="record in mealRecords"
          :key="record.date"
          class="bg-white rounded-xl shadow-sm overflow-hidden"
        >
          <!-- Date Header -->
          <div class="bg-green-50 px-6 py-4 border-b-2 border-green-100">
            <h3 class="text-xl font-semibold text-gray-800">
              {{ record.date }} ({{ record.dayOfWeek }})
            </h3>
          </div>

          <!-- Meal Items -->
          <div class="p-5">
            <div
              v-for="(meal, idx) in record.meals"
              :key="meal.id"
              class="flex items-center p-5 hover:bg-green-25 transition-colors duration-200 rounded-lg"
              :class="idx !== record.meals.length - 1 ? 'border-b border-gray-100 mb-4' : ''"
            >
              <!-- Meal Icon -->
              <div
                class="w-16 h-16 bg-green-100 rounded-xl flex items-center justify-center mr-5 flex-shrink-0"
              >
                <span class="text-3xl">{{ getMealEmoji(meal.type) }}</span>
              </div>

              <!-- Meal Content -->
              <div class="flex-grow">
                <!-- Meal Type Badge -->
                <span
                  class="inline-block px-3 py-1 rounded-full text-sm font-medium mb-2"
                  :class="getMealBadgeClass(meal.type)"
                >
                  {{ meal.typeText }}
                </span>

                <!-- Meal Menu -->
                <div class="text-lg font-medium text-gray-800 mb-1">
                  {{ meal.foods }}
                </div>

                <!-- Meal Details -->
                <div class="text-sm text-green-600">
                  <span v-if="meal.memo"> 메모: {{ meal.memo }} </span>
                  <span v-else-if="meal.hasPhoto" class="flex items-center gap-1"> 📷 사진 </span>
                </div>
              </div>

              <!-- Action Button -->
              <div>
                <button
                  @click="viewMealDetails(meal.id)"
                  class="px-5 py-2 bg-green-50 text-green-600 border border-green-200 rounded-lg hover:bg-green-100 transition-colors duration-200 font-medium"
                >
                  상세보기
                </button>
                <button
                  @click="deleteMeal(meal.id, record.date)"
                  class="px-5 py-2 bg-red-50 text-red-600 border border-red-200 rounded-lg hover:bg-red-100 transition-colors duration-200 font-medium"
                >
                  삭제
                </button>
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

const router = useRouter()

const goToAddMeal = () => {
  router.push('/meal/record')
}

const getMealEmoji = (type) => {
  const emojiMap = {
    breakfast: '🌅',
    lunch: '☀️',
    dinner: '🌙',
  }
  return emojiMap[type] || '🍽️'
}

const getMealBadgeClass = (type) => {
  const classMap = {
    breakfast: 'bg-green-50 text-green-600',
    lunch: 'bg-green-50 text-green-600',
    dinner: 'bg-green-50 text-green-600',
  }
  return classMap[type] || 'bg-gray-50 text-gray-600'
}

const mealRecords = [
  {
    date: '2025년 5월 1일',
    dayOfWeek: '목',
    meals: [
      {
        id: 1,
        type: 'breakfast',
        typeText: '아침',
        foods: '현미밥, 미역국, 시금치무침, 계란말이',
        hasPhoto: true,
        memo: '',
      },
      {
        id: 2,
        type: 'lunch',
        typeText: '점심',
        foods: '잡곡밥, 된장찌개, 갈치구이, 김치',
        hasPhoto: false,
        memo: '엄마 된장찌개가 추억 돋네요',
      },
    ],
  },
  {
    date: '2025년 4월 30일',
    dayOfWeek: '수',
    meals: [
      {
        id: 3,
        type: 'breakfast',
        typeText: '아침',
        foods: '죽, 나물반찬, 멸치볶음',
        hasPhoto: true,
        memo: '',
      },
      {
        id: 4,
        type: 'lunch',
        typeText: '점심',
        foods: '비빔밥, 콩나물국',
        hasPhoto: false,
        memo: '여름에 먹어서 남아서 싱겁좀음',
      },
      {
        id: 5,
        type: 'dinner',
        typeText: '저녁',
        foods: '현미밥, 생선구이, 김치, 콩자반',
        hasPhoto: true,
        memo: '',
      },
    ],
  },
]

const viewMealDetails = (mealId) => {
  // 상세보기 페이지로 이동
  router.push(`/meal/detail/${mealId}`)
}

const deleteMeal = (mealId, date) => {
  if (confirm('이 식단 기록을 삭제하시겠습니까?')) {
    // 해당 식사 기록을 삭제하는 로직
    mealRecords.value = mealRecords.value
      .map((record) => {
        if (record.date === date) {
          return {
            ...record,
            meals: record.meals.filter((meal) => meal.id !== mealId),
          }
        }
        return record
      })
      .filter((record) => record.meals.length > 0) // 식사가 없는 날짜는 제거

    alert('식단 기록이 삭제되었습니다.')
  }
}
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
