<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-3xl mx-auto px-4 py-8">
      <!-- 로딩 상태 -->
      <div v-if="loading" class="text-center py-8">
        <div
          class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600"
        ></div>
        <p class="mt-2 text-gray-600">리포트를 생성하는 중...</p>
      </div>

      <!-- 에러 상태 -->
      <div v-else-if="error" class="text-center py-8">
        <p class="text-red-600 mb-4">{{ error }}</p>
        <button
          @click="loadReport"
          class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
        >
          다시 시도
        </button>
      </div>

      <!-- 리포트 내용 -->
      <div v-else>
        <!-- 타이틀과 날짜 선택기 -->
        <div class="text-center mb-8">
          <h1 class="text-2xl font-bold text-gray-800 mb-4">주간 건강 리포트</h1>

          <!-- 날짜 선택 컨트롤 -->
          <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
            <div class="flex flex-col sm:flex-row items-center justify-center gap-4">
              <div class="flex items-center gap-2">
                <label class="text-gray-700 font-medium">리포트 기간:</label>
                <input
                  type="date"
                  v-model="selectedDate"
                  @change="onDateChange"
                  class="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                  :max="maxDate"
                />
              </div>

              <!-- 빠른 선택 버튼들 -->
              <div class="flex gap-2">
                <button
                  @click="goToThisWeek"
                  :class="[
                    'px-3 py-1 text-sm rounded-full transition-colors',
                    isCurrentWeek
                      ? 'bg-green-600 text-white'
                      : 'bg-gray-200 text-gray-700 hover:bg-gray-300',
                  ]"
                >
                  이번 주
                </button>
                <button
                  @click="goToLastWeek"
                  class="px-3 py-1 text-sm bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-colors"
                >
                  지난 주
                </button>
                <button
                  @click="goToPreviousWeek"
                  class="px-3 py-1 text-sm bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-colors"
                >
                  이전 주
                </button>
                <button
                  @click="goToNextWeek"
                  :disabled="isNextWeekDisabled"
                  :class="[
                    'px-3 py-1 text-sm rounded-full transition-colors',
                    isNextWeekDisabled
                      ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                      : 'bg-gray-200 text-gray-700 hover:bg-gray-300',
                  ]"
                >
                  다음 주
                </button>
              </div>
            </div>

            <!-- 선택된 주간 표시 -->
            <div class="mt-4 text-center">
              <p class="text-gray-600 text-sm">
                {{ reportDateRange }}
                <span
                  v-if="isCurrentWeek"
                  class="ml-2 px-2 py-1 bg-green-100 text-green-700 text-xs rounded-full"
                >
                  현재 주
                </span>
              </p>
            </div>
          </div>
        </div>

        <!-- 건강점수 카드 -->
        <div class="bg-green-50 rounded-lg shadow-md p-10 mb-8 text-center">
          <div class="text-5xl mb-3">
            <span v-if="healthScore >= 80">😊</span>
            <span v-else-if="healthScore < 40">😔</span>
            <span v-else>😐</span>
          </div>
          <h2 class="text-4xl font-bold text-gray-800 mb-3">{{ healthScore }}점</h2>
          <p class="text-gray-700 mb-1">{{ getHealthScoreMessage() }}</p>
          <p class="text-gray-600 text-base">{{ getHealthScoreDetail() }}</p>
        </div>

        <!-- 건강점수 카드 아래에 추가할 개인화된 영양소 분석 섹션 -->
        <div
          v-if="reportData?.priorityNutrients?.length"
          class="bg-white rounded-lg shadow p-8 mb-8"
        >
          <h3 class="text-xl font-bold text-gray-800 mb-4 flex items-center">
            <span class="text-purple-500 mr-2">📊</span>
            이번 주 주요 영양소 분석
          </h3>

          <!-- 영양소 상태 요약 -->
          <div class="bg-purple-50 border-l-4 border-purple-400 p-4 rounded-r-lg mb-6">
            <p class="text-gray-700 leading-relaxed">{{ reportData.nutrientSummary }}</p>
          </div>

          <!-- 주요 영양소 3개 상세 표시 -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div
              v-for="nutrient in reportData.priorityNutrients"
              :key="nutrient.nutrientId"
              class="p-4 border-2 rounded-xl transition-all hover:shadow-md"
              :class="getNutrientStatusClass(nutrient.status)"
            >
              <div class="text-center">
                <div class="text-2xl mb-2">
                  <span v-if="nutrient.status === '적정'">✅</span>
                  <span v-else-if="nutrient.status === '부족'">📉</span>
                  <span v-else-if="nutrient.status === '초과'">📈</span>
                  <span v-else>❓</span>
                </div>
                <h4 class="font-semibold text-gray-800 mb-2">{{ nutrient.nutrientName }}</h4>
                <div class="space-y-1">
                  <div class="text-sm text-gray-600">
                    평균 {{ formatNutrientValue(nutrient.avg) }}{{ nutrient.nutrientUnit }}
                  </div>
                  <div
                    class="text-xs px-2 py-1 rounded-full font-medium"
                    :class="getStatusBadgeClass(nutrient.status)"
                  >
                    {{ nutrient.status }}
                  </div>
                  <div v-if="nutrient.complianceRate" class="text-xs text-gray-500">
                    준수율 {{ formatPercentage(nutrient.complianceRate) }}%
                  </div>
                </div>

                <!-- 개별 조언 표시 -->
                <div v-if="nutrient.recommendation" class="mt-3 p-2 bg-gray-50 rounded-lg">
                  <p class="text-xs text-gray-600 leading-relaxed">{{ nutrient.recommendation }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- AI 기반 추천식단 -->
        <div class="bg-white rounded-lg shadow p-8 mb-8">
          <div class="flex items-center justify-between mb-5">
            <div class="flex items-center">
              <div class="bg-blue-100 rounded-full p-2 mr-3">
                <span class="text-2xl">🤖</span>
              </div>
              <h3 class="text-xl font-bold text-gray-800">AI 맞춤 식단 분석 & 추천</h3>
            </div>
            <button
              v-if="!aiLoading && aiRecommendation"
              @click="refreshAiRecommendation"
              class="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded-full hover:bg-blue-200 transition-colors"
              title="새로운 추천 받기"
            >
              🔄 새로고침
            </button>
          </div>

          <!-- AI 추천 로딩 -->
          <div v-if="aiLoading" class="text-center py-12">
            <div
              class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"
            ></div>
            <p class="mt-4 text-gray-600">AI가 맞춤 식단을 분석하고 있습니다...</p>
            <p class="text-sm text-gray-500 mt-2">
              영양소 데이터를 기반으로 개인화된 추천을 생성 중
            </p>
          </div>

          <!-- AI 추천 에러 -->
          <div v-else-if="aiError" class="text-center py-8">
            <div class="text-red-500 text-4xl mb-4">⚠️</div>
            <p class="text-red-600 mb-4">{{ aiError }}</p>
            <button
              @click="loadAiRecommendation"
              class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              다시 생성
            </button>
          </div>

          <!-- AI 추천 내용 (텍스트 형태) -->
          <div v-else-if="aiRecommendation" class="space-y-6">
            <!-- AI 추천 텍스트를 예쁘게 표시 -->
            <div class="bg-blue-50 border-l-4 border-blue-400 p-6 rounded-r-lg">
              <div class="prose prose-sm max-w-none">
                <div class="whitespace-pre-line text-gray-700 leading-relaxed">
                  {{ aiRecommendation }}
                </div>
              </div>
            </div>
          </div>

          <!-- AI 추천 없을 때 -->
          <div v-else class="text-center py-12">
            <div class="text-6xl mb-4">🤖</div>
            <h4 class="text-lg font-semibold text-gray-800 mb-2">AI 맞춤 식단 추천</h4>
            <p class="text-gray-600 mb-6">
              영양 분석 결과를 바탕으로 개인화된 식단을 추천해드릴게요!
            </p>
            <button
              @click="loadAiRecommendation"
              class="px-8 py-4 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-xl hover:from-blue-700 hover:to-purple-700 transition-all shadow-lg"
            >
              ✨ AI 식단 추천 받기
            </button>
          </div>
        </div>

        <!-- 맞춤 건강 조언  -->
        <div class="bg-green-50 rounded-lg shadow-md p-8">
          <h3 class="text-xl font-bold text-gray-800 mb-5 flex items-center">
            <span class="text-green-500 mr-2">💡</span>
            AI 맞춤 건강 조언
          </h3>

          <!-- AI 개인화된 건강 조언 -->
          <div v-if="reportData?.healthTips" class="space-y-4">
            <div
              v-for="(tip, index) in parsedHealthTips"
              :key="index"
              class="flex items-start p-4 bg-white rounded-lg shadow-sm border-l-4"
              :class="getTipBorderClass(index)"
            >
              <span class="text-2xl mr-4 flex-shrink-0">{{ getTipIcon(index) }}</span>
              <div class="flex-grow">
                <p class="text-gray-700 leading-relaxed">{{ tip }}</p>
              </div>
            </div>
          </div>

          <!-- AI 조언 로딩 중 -->
          <div v-else-if="loading" class="text-center py-4">
            <div class="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-green-600"></div>
            <p class="mt-2 text-gray-600 text-sm">맞춤 건강 조언을 생성하는 중...</p>
          </div>

          <!-- 기본 조언 (AI 실패 시) -->
          <div v-else class="space-y-4">
            <div class="flex items-start p-4 bg-white rounded-lg shadow-sm border-l-4 border-blue-400">
              <span class="text-blue-500 mr-4 text-2xl">💧</span>
              <p class="text-gray-700">충분한 수분 섭취로 신체 기능을 원활하게 유지하세요.</p>
            </div>
            <div class="flex items-start p-4 bg-white rounded-lg shadow-sm border-l-4 border-green-400">
              <span class="text-green-500 mr-4 text-2xl">🥗</span>
              <p class="text-gray-700">다양한 색깔의 채소와 과일로 영양소 균형을 맞춰보세요.</p>
            </div>
            <div class="flex items-start p-4 bg-white rounded-lg shadow-sm border-l-4 border-orange-400">
              <span class="text-orange-500 mr-4 text-2xl">🚶</span>
              <p class="text-gray-700">가벼운 산책이나 스트레칭으로 건강한 생활습관을 만들어보세요.</p>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import ReportService from '@/services/ReportService'

const route = useRoute()
const router = useRouter()
const toast = useToast()

// 상태 관리
const loading = ref(false)
const error = ref(null)
const aiLoading = ref(false)
const aiError = ref(null)

// 데이터
const reportData = ref(null)
const aiRecommendation = ref(null)
const selectedDate = ref('')

// 날짜 관련 계산
const maxDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const currentWeekEnd = computed(() => {
  const today = new Date()
  const dayOfWeek = today.getDay()
  const diff = dayOfWeek === 0 ? 0 : 7 - dayOfWeek // 일요일까지의 차이
  const sunday = new Date(today)
  sunday.setDate(today.getDate() + diff)
  return sunday.toISOString().split('T')[0]
})

const isCurrentWeek = computed(() => {
  return selectedDate.value === currentWeekEnd.value
})

const isNextWeekDisabled = computed(() => {
  const selected = new Date(selectedDate.value)
  const current = new Date(currentWeekEnd.value)
  return selected >= current
})

// 계산된 속성
const healthScore = computed(() => reportData.value?.healthScore || 0)

const reportDateRange = computed(() => {
  if (!selectedDate.value) return ''

  const endDate = new Date(selectedDate.value)
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

// 영양소 카운트
const optimalCount = computed(
  () => reportData.value?.nutrients?.filter((n) => n.status === '적정').length || 0,
)
const deficientCount = computed(
  () => reportData.value?.nutrients?.filter((n) => n.status === '부족').length || 0,
)
const excessiveCount = computed(
  () => reportData.value?.nutrients?.filter((n) => n.status === '초과').length || 0,
)

// 건강 조언 파싱
const parsedHealthTips = computed(() => {
  const tips = reportData.value?.healthTips
  console.log('healthTips 원본:', tips)

  if (!tips) return []

  // 줄바꿈으로 분리해서 3개 조언 추출
  const parsed = tips
    .split('\n')
    .filter(tip => tip.trim().length > 0)
    .slice(0, 3)

  console.log('파싱된 healthTips:', parsed)
  return parsed
})

// 날짜 관련 메서드
const setDateToWeekEnd = (date) => {
  const d = new Date(date)
  const dayOfWeek = d.getDay()
  const diff = dayOfWeek === 0 ? 0 : 7 - dayOfWeek
  d.setDate(d.getDate() + diff)
  return d.toISOString().split('T')[0]
}

const goToThisWeek = () => {
  selectedDate.value = currentWeekEnd.value
  updateRoute()
}

const goToLastWeek = () => {
  const current = new Date(selectedDate.value)
  current.setDate(current.getDate() - 7)
  selectedDate.value = current.toISOString().split('T')[0]
  updateRoute()
}

const goToPreviousWeek = () => {
  const current = new Date(selectedDate.value)
  current.setDate(current.getDate() - 7)
  selectedDate.value = current.toISOString().split('T')[0]
  updateRoute()
}

const goToNextWeek = () => {
  if (isNextWeekDisabled.value) return

  const current = new Date(selectedDate.value)
  current.setDate(current.getDate() + 7)
  selectedDate.value = current.toISOString().split('T')[0]
  updateRoute()
}

const onDateChange = () => {
  // 선택된 날짜를 해당 주의 일요일로 조정
  selectedDate.value = setDateToWeekEnd(selectedDate.value)
  updateRoute()
}

const updateRoute = () => {
  // URL을 업데이트하되 페이지 새로고침은 하지 않음
  const newPath =
    selectedDate.value === currentWeekEnd.value ? '/report' : `/report/${selectedDate.value}`

  if (route.path !== newPath) {
    router.push(newPath)
  }
}

// 헬퍼 함수
const getWeekStartDate = (endDate) => {
  const end = new Date(endDate)
  const start = new Date(end)
  start.setDate(end.getDate() - 6)
  return start.toISOString().split('T')[0]
}

// 데이터 로딩 메서드
const loadReport = async () => {
  loading.value = true
  error.value = null

  try {
    console.log('리포트 로딩 시작...', selectedDate.value)

    // 실제 API 호출
    const targetDate = selectedDate.value || new Date().toISOString().split('T')[0]
    const response = await ReportService.getCompleteWeeklyReport(targetDate)

    console.log('API 응답 받음:', response.data)

    // 건강 분석 데이터 설정
    if (response.data.healthAnalysis) {
      reportData.value = response.data.healthAnalysis
      console.log('건강 분석 데이터 설정됨:', reportData.value)
    } else {
      console.warn('건강 분석 데이터가 없습니다.')
      // 기본값 설정
      reportData.value = {
        startDate: targetDate,
        endDate: targetDate,
        healthScore: 0,
        mealCount: 0,
        nutrients: [],
        priorityNutrients: [],
        nutrientSummary: '분석할 데이터가 부족합니다.',
        healthTips: null
      }
    }

    // AI 식단 추천 데이터 설정
    if (response.data.aiRecommendation) {
      aiRecommendation.value = response.data.aiRecommendation
      console.log('AI 추천 데이터 설정됨')
    } else {
      console.warn('AI 추천 데이터가 없습니다.')
      aiRecommendation.value = null
    }

  } catch (err) {
    console.error('리포트 로드 실패:', err)
    console.error('에러 상세:', err.response?.data)

    // 사용자 친화적 에러 메시지 설정
    if (err.response?.status === 401) {
      error.value = '로그인이 만료되었습니다. 다시 로그인해주세요.'
    } else if (err.response?.status === 404) {
      error.value = '해당 기간의 리포트 데이터가 없습니다.'
    } else if (err.response?.data?.error) {
      error.value = err.response.data.error
    } else {
      error.value = '리포트를 불러오는데 실패했습니다. 다시 시도해주세요.'
    }

    toast.error(error.value)

    // 에러 발생 시 기본 데이터라도 표시
    reportData.value = {
      startDate: selectedDate.value,
      endDate: selectedDate.value,
      healthScore: 0,
      mealCount: 0,
      nutrients: [],
      priorityNutrients: [],
      nutrientSummary: '데이터를 불러올 수 없습니다.',
      healthTips: null
    }

  } finally {
    loading.value = false
  }
}

// AI 추천 로딩 메서드 (중복 제거됨)
const loadAiRecommendation = async () => {
  if (!reportData.value) {
    toast.error('먼저 리포트 데이터를 불러와주세요.')
    return
  }

  aiLoading.value = true
  aiError.value = null

  try {
    console.log('AI 추천 로딩 시작...', {
      startDate: reportData.value.startDate,
      endDate: reportData.value.endDate
    })

    const response = await ReportService.getAiRecommendation(
      reportData.value.startDate,
      reportData.value.endDate
    )

    console.log('AI 추천 API 응답:', response.data)

    if (response.data.recommendation) {
      aiRecommendation.value = response.data.recommendation
      toast.success('AI 식단 추천이 생성되었습니다!')
    } else {
      throw new Error('AI 추천 데이터가 비어있습니다.')
    }

  } catch (err) {
    console.error('AI 추천 로드 실패:', err)
    console.error('AI 에러 상세:', err.response?.data)

    // 사용자 친화적 에러 메시지
    if (err.response?.status === 401) {
      aiError.value = '로그인이 만료되었습니다.'
    } else if (err.response?.data?.error) {
      aiError.value = err.response.data.error
    } else {
      aiError.value = 'AI 추천을 생성하는데 실패했습니다. 다시 시도해주세요.'
    }

    toast.error(aiError.value)

  } finally {
    aiLoading.value = false
  }
}

const refreshAiRecommendation = async () => {
  aiRecommendation.value = null
  await loadAiRecommendation()
}

// 유틸리티 함수들
const getHealthScoreMessage = () => {
  if (healthScore.value >= 80) return '이번 주 건강 관리가 매우 우수합니다!'
  if (healthScore.value >= 60) return '이번 주 건강 관리가 양호합니다.'
  if (healthScore.value >= 40) return '건강 관리에 조금 더 신경써주세요.'
  return '건강 관리가 필요합니다.'
}

const getHealthScoreDetail = () => {
  if (healthScore.value >= 80)
    return '규칙적인 식사와 꾸준한 관리로 건강한 생활을 유지하고 계시네요.'
  if (healthScore.value >= 60) return '대체로 좋은 건강 상태를 유지하고 있습니다.'
  if (healthScore.value >= 40) return '몇 가지 영양소가 부족하거나 과다합니다.'
  return '전반적인 식단 개선이 필요합니다.'
}

const getNutrientStatusClass = (status) => {
  switch (status) {
    case '적정':
      return 'bg-green-50 border-green-200 hover:bg-green-100'
    case '부족':
      return 'bg-yellow-50 border-yellow-200 hover:bg-yellow-100'
    case '초과':
      return 'bg-red-50 border-red-200 hover:bg-red-100'
    default:
      return 'bg-gray-50 border-gray-200 hover:bg-gray-100'
  }
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case '적정':
      return 'bg-green-100 text-green-800'
    case '부족':
      return 'bg-yellow-100 text-yellow-800'
    case '초과':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const formatNutrientValue = (value) => {
  if (!value) return '0'
  return typeof value === 'number' ? value.toFixed(1) : parseFloat(value).toFixed(1)
}

const formatPercentage = (value) => {
  if (!value) return '0'
  return typeof value === 'number' ? value.toFixed(0) : parseFloat(value).toFixed(0)
}

// 조언별 아이콘
const getTipIcon = (index) => {
  const icons = ['💪', '🌟', '❤️']
  return icons[index] || '💡'
}

// 조언별 테두리 색상
const getTipBorderClass = (index) => {
  const classes = ['border-blue-400', 'border-green-400', 'border-purple-400']
  return classes[index] || 'border-gray-400'
}

// 디버깅을 위한 데이터 확인 메서드
const debugReportData = () => {
  console.log('=== 현재 리포트 데이터 디버깅 ===')
  console.log('reportData.value:', reportData.value)
  console.log('healthScore:', healthScore.value)
  console.log('priorityNutrients:', reportData.value?.priorityNutrients)
  console.log('nutrientSummary:', reportData.value?.nutrientSummary)
  console.log('healthTips:', reportData.value?.healthTips)
  console.log('parsedHealthTips:', parsedHealthTips.value)
  console.log('aiRecommendation:', aiRecommendation.value)
  console.log('=============================')
}

// 라이프사이클
onMounted(() => {
  console.log('WeeklyReportView 마운트됨')
  console.log('초기 selectedDate:', selectedDate.value)
  console.log('URL params:', route.params)

  // URL 파라미터에서 날짜 확인
  if (route.params.date) {
    selectedDate.value = setDateToWeekEnd(route.params.date)
    console.log('URL에서 날짜 설정됨:', selectedDate.value)
  } else {
    selectedDate.value = currentWeekEnd.value
    console.log('현재 주 날짜 설정됨:', selectedDate.value)
  }

  loadReport()

  // 개발 환경에서만 디버깅 메서드를 전역으로 노출
  if (import.meta.env.DEV) {
    window.debugReportData = debugReportData
    console.log('디버깅: window.debugReportData() 함수 사용 가능')
  }
})

// 워처
watch(selectedDate, (newDate, oldDate) => {
  console.log('날짜 변경됨:', oldDate, '→', newDate)
  if (selectedDate.value) {
    aiRecommendation.value = null // AI 추천 초기화
    loadReport()
  }
})
</script>

<style scoped>
.bg-brand-lightbg {
  background-color: #f6faf7;
}

.transition-all {
  transition: all 0.3s ease;
}

.prose {
  line-height: 1.6;
}

.whitespace-pre-line {
  white-space: pre-line;
}

/* DatePicker 스타일 커스터마이징 */
input[type='date'] {
  color-scheme: light;
}

input[type='date']::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.7;
}

input[type='date']::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}
</style>
