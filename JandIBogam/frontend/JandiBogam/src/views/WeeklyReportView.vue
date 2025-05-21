<template>
  <!-- 타이틀 -->
  <div class="text-center mb-7">
    <h1 class="text-2xl font-bold text-gray-800 mb-2">주간 건강 리포트</h1>
    <p class="text-gray-600 text-base">{{ reportDateRange }}</p>
  </div>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-4xl w-full mx-auto px-4 py-10">
      <!-- 건강점수 카드 -->
      <div class="bg-green-50 rounded-lg shadow-md p-10 mb-8 text-center">
        <div class="text-5xl mb-3">
          <span v-if="healthScore >= 80">😊</span>
          <span v-else-if="healthScore < 40">😔</span>
          <span v-else>😐</span>
        </div>
        <h2 class="text-4xl font-bold text-gray-800 mb-3">{{ healthScore }}점</h2>
        <p class="text-gray-700 mb-1">이번 주 건강 관리가 매우 우수합니다!</p>
        <p class="text-gray-600 text-base">
          규칙적인 식사와 꾸준한 약물 복용으로 건강한 생활을 유지하고 계시네요.
        </p>
      </div>

      <!-- AI 기반 추천식단 -->
      <div class="bg-white rounded-lg shadow p-8 mb-8">
        <div class="flex items-center mb-5">
          <div class="bg-blue-100 rounded-full p-2 mr-2">
            <span class="text-2xl">🤖</span>
          </div>
          <h3 class="text-xl font-bold text-gray-800">AI 기반 분석 및 추천 식단</h3>
        </div>
        <p class="text-gray-700 mb-6">
          이번 주에는 단백질과 채소 위주의 균형 잡힌 식단을 유지하셨으나, 과일 섭취가 다소
          부족했습니다. 다음 주에는 아침 식사에 계절 과일을 추가하는 것이 좋겠습니다.
        </p>

        <div class="flex gap-4">
          <!-- 왼쪽 열 (index 0, 1) -->
          <div class="flex-1 space-y-4">
            <div
              v-for="(meal, index) in recommendedMeals.slice(0, 2)"
              :key="'left-' + index"
              class="border rounded-lg shadow-sm p-4 bg-white"
            >
              <h4 class="font-medium text-gray-800 mb-3">다음 주 추천 식단</h4>
              <div>
                <div class="flex items-center mb-2">
                  <span class="text-green-500 font-medium mr-2">아침</span>
                  <span class="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full">{{
                    meal.breakfast
                  }}</span>
                </div>
                <div class="flex items-center mb-2">
                  <span class="text-amber-500 font-medium mr-2">점심</span>
                  <span class="text-xs bg-amber-100 text-amber-700 px-2 py-0.5 rounded-full">{{
                    meal.lunch
                  }}</span>
                </div>
                <div class="flex items-center">
                  <span class="text-blue-500 font-medium mr-2">저녁</span>
                  <span class="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">{{
                    meal.dinner
                  }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 오른쪽 열 (index 2, 3) -->
          <div class="flex-1 space-y-4">
            <div
              v-for="(meal, index) in recommendedMeals.slice(2, 4)"
              :key="'right-' + index"
              class="border rounded-lg shadow-sm p-4 bg-white"
            >
              <h4 class="font-medium text-gray-800 mb-3">다음 주 추천 식단</h4>
              <div>
                <div class="flex items-center mb-2">
                  <span class="text-green-500 font-medium mr-2">아침</span>
                  <span class="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full">{{
                    meal.breakfast
                  }}</span>
                </div>
                <div class="flex items-center mb-2">
                  <span class="text-amber-500 font-medium mr-2">점심</span>
                  <span class="text-xs bg-amber-100 text-amber-700 px-2 py-0.5 rounded-full">{{
                    meal.lunch
                  }}</span>
                </div>
                <div class="flex items-center">
                  <span class="text-blue-500 font-medium mr-2">저녁</span>
                  <span class="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">{{
                    meal.dinner
                  }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 맞춤 건강 조언 -->
      <div class="bg-green-50 rounded-lg shadow-md p-8 mb-8">
        <h3 class="text-xl font-bold text-gray-800 mb-5 flex items-center">
          <span class="text-blue-500 mr-2">💧</span>
          맞춤 건강 조언
        </h3>
        <ul class="space-y-4">
          <li class="flex items-start">
            <span class="text-blue-500 mr-3">💧</span>
            <span class="text-gray-700">하루 8잔의 물을 마시면 혈액 순환에 도움이 됩니다.</span>
          </li>
          <li class="flex items-start">
            <span class="text-green-500 mr-3">🥗</span>
            <span class="text-gray-700">매 식사마다 채소를 충분히 섭취하시면 좋습니다.</span>
          </li>
          <li class="flex items-start">
            <span class="text-red-500 mr-3">🩸</span>
            <span class="text-gray-700">실내 온도가 기온차가 심하면 건강에 좋지 않습니다.</span>
          </li>
        </ul>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const healthScore = ref(85)

const recommendedMeals = [
  {
    breakfast: '오트밀, 견과류, 계절과일',
    lunch: '현미밥, 시금치 된장국, 고등어 아귀찜',
    dinner: '잡곡밥, 콩나물국, 두부조림',
  },
  {
    breakfast: '통밀빵, 계란프라이, 야채카레',
    lunch: '현미밥, 미역국, 불고기, 부추무침',
    dinner: '잡곡밥, 다시마 된장국, 생선구이',
  },
  {
    breakfast: '요거트, 블루베리, 그래놀라',
    lunch: '현미밥, 콩나물국, 고구마구이',
    dinner: '현미밥, 버섯찌개, 계란말이',
  },
  {
    breakfast: '계란 스무디, 통곡물빵',
    lunch: '현미밥, 청국장, 연어구이',
    dinner: '잡곡밥 샐러드, 현미밥, 참치국',
  },
]

const reportDateRange = computed(() => {
  const today = new Date()
  const endDate = new Date(today)
  endDate.setDate(today.getDate() - today.getDay())
  const startDate = new Date(endDate)
  startDate.setDate(endDate.getDate() - 6)
  const formatDate = (date) => {
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    return `${year}년 ${month}월 ${day}일`
  }
  return `${formatDate(startDate)} - ${formatDate(endDate)}`
})
</script>

<style scoped>
.bg-brand-lightbg {
  background-color: #f6faf7;
}
.bg-brand-primary {
  background-color: #6a7d73;
}
.bg-brand-hover {
  background-color: #5a6b63;
}
.text-brand-primary {
  color: #6a7d73;
}
.border-brand-primary {
  border-color: #6a7d73;
}
</style>
