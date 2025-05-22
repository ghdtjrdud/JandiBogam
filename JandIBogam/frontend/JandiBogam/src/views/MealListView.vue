<template>
  <div class="min-h-screen bg-white">
    <!-- Main Content -->
    <main class="container mx-auto px-4 py-8">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">
          {{ isMyPage ? '내 식단 기록' : `${targetUser?.name}님의 식단 기록` }}
        </h1>
        <p class="text-gray-600">
          {{
            isMyPage ? '나의 식사 기록을 확인해보세요' : '가족의 식사 기록을 확인하고 응원해주세요'
          }}
        </p>
      </div>

      <!-- Add New Meal Button (본인만) -->
      <div v-if="isMyPage" class="flex justify-end mb-6">
        <button
          @click="goToMealRecord"
          class="btn bg-brand-primary hover:bg-brand-hover text-white border-none px-6"
        >
          + 새 식단 기록
        </button>
      </div>

      <!-- Meal Records List -->
      <div class="max-w-4xl mx-auto">
        <div v-if="mealRecords.length === 0" class="text-center py-12">
          <div class="text-6xl mb-4">🍽️</div>
          <h3 class="text-xl font-medium text-gray-600 mb-2">
            {{ isMyPage ? '아직 기록된 식단이 없습니다' : '아직 기록된 식단이 없네요' }}
          </h3>
          <p class="text-gray-500 mb-6">
            {{ isMyPage ? '첫 번째 식단을 기록해보세요!' : '곧 맛있는 식단이 올라올 거예요' }}
          </p>
          <button
            v-if="isMyPage"
            @click="goToMealRecord"
            class="btn bg-brand-primary hover:bg-brand-hover text-white border-none"
          >
            식단 기록하기
          </button>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="meal in mealRecords"
            :key="meal.id"
            class="bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-all duration-200"
          >
            <div class="p-6">
              <div class="flex justify-between items-start mb-4">
                <div class="flex-1">
                  <div class="flex items-center gap-2 mb-2">
                    <span class="text-lg">{{ getMealTimeEmoji(meal.mealTime) }}</span>
                    <span class="font-medium text-gray-800">{{
                      getMealTimeText(meal.mealTime)
                    }}</span>
                    <span class="text-sm text-gray-500">{{ formatDate(meal.createdAt) }}</span>
                  </div>
                  <h3
                    class="text-xl font-semibold text-gray-800 mb-2 cursor-pointer hover:text-brand-primary"
                    @click="goToMealDetail(meal.id)"
                  >
                    {{ meal.menuName }}
                  </h3>
                  <p v-if="meal.memo" class="text-gray-600 text-sm">{{ meal.memo }}</p>
                </div>
                <div v-if="meal.imageUrl" class="ml-4">
                  <img
                    :src="meal.imageUrl"
                    :alt="meal.menuName"
                    class="w-20 h-20 object-cover rounded-lg cursor-pointer"
                    @click="goToMealDetail(meal.id)"
                  />
                </div>
              </div>

              <!-- 댓글 섹션 -->
              <div class="border-t pt-4 mt-4">
                <!-- 기존 댓글 표시 -->
                <div v-if="meal.comments && meal.comments.length > 0" class="space-y-3 mb-4">
                  <div
                    v-for="comment in meal.comments"
                    :key="comment.id"
                    class="flex items-start gap-3"
                  >
                    <div
                      class="w-8 h-8 bg-brand-primary rounded-full flex items-center justify-center text-white text-sm"
                    >
                      {{ comment.author.name.charAt(0) }}
                    </div>
                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <span class="font-medium text-sm text-gray-800">{{
                          comment.author.name
                        }}</span>
                        <span class="text-xs text-gray-500">{{
                          formatDate(comment.createdAt)
                        }}</span>
                      </div>
                      <p class="text-sm text-gray-700">{{ comment.content }}</p>
                    </div>
                  </div>
                </div>

                <!-- 댓글 작성 (모든 사용자) -->
                <div class="flex gap-3">
                  <div
                    class="w-8 h-8 bg-brand-primary rounded-full flex items-center justify-center text-white text-sm"
                  >
                    {{ currentUser?.name?.charAt(0) }}
                  </div>
                  <div class="flex-1 flex gap-2">
                    <input
                      v-model="newComment[meal.id]"
                      type="text"
                      placeholder="응원의 댓글을 남겨주세요..."
                      class="flex-1 px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:border-brand-primary"
                      @keyup.enter="addComment(meal.id)"
                    />
                    <button
                      @click="addComment(meal.id)"
                      :disabled="!newComment[meal.id]?.trim()"
                      class="px-4 py-2 bg-brand-primary text-white rounded-lg text-sm hover:bg-brand-hover disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                      등록
                    </button>
                  </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const mealRecords = ref([])
const newComment = ref({})
const targetUser = ref(null)
const loading = ref(false)

// 현재 로그인한 사용자
const currentUser = computed(() => authStore.getUser)

// 본인 페이지인지 확인
const isMyPage = computed(() => {
  return !route.params.userId || route.params.userId === currentUser.value?.id?.toString()
})

// 대상 사용자 ID
const targetUserId = computed(() => {
  return route.params.userId || currentUser.value?.id
})

onMounted(() => {
  loadMealRecords()
  if (!isMyPage.value) {
    loadTargetUser()
  }
})

const loadMealRecords = async () => {
  loading.value = true
  try {
    // 실제로는 API 호출
    // const response = await MealService.getMealRecords(targetUserId.value)
    // mealRecords.value = response.data

    // 샘플 데이터
    mealRecords.value = [
      {
        id: 1,
        mealTime: 'breakfast',
        menuName: '현미밥, 된장찌개, 김치',
        memo: '오늘은 집에서 만든 된장찌개가 특히 맛있었어요',
        imageUrl: '/api/placeholder/200/200',
        createdAt: '2025-05-22',
        comments: [
          {
            id: 1,
            content: '건강한 아침식사네요! 👍',
            author: { name: '김철수' },
            createdAt: '2025-05-22',
          },
        ],
      },
    ]
  } catch (error) {
    console.error('식단 기록 조회 실패:', error)
  } finally {
    loading.value = false
  }
}

const loadTargetUser = async () => {
  try {
    // 실제로는 API 호출
    // const response = await UserService.getUserInfo(route.params.userId)
    // targetUser.value = response.data

    // 샘플 데이터
    targetUser.value = { name: '김영희' }
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
  }
}

const addComment = async (mealId) => {
  const content = newComment.value[mealId]?.trim()
  if (!content) return

  try {
    // 실제로는 API 호출
    // await CommentService.addComment(mealId, content)

    // 임시로 댓글 추가
    const meal = mealRecords.value.find((m) => m.id === mealId)
    if (meal) {
      if (!meal.comments) meal.comments = []
      meal.comments.push({
        id: Date.now(),
        content,
        author: currentUser.value,
        createdAt: new Date().toISOString(),
      })
    }

    newComment.value[mealId] = ''
  } catch (error) {
    console.error('댓글 작성 실패:', error)
  }
}

const goToMealRecord = () => {
  router.push('/meal/record')
}

const goToMealDetail = (mealId) => {
  router.push(`/meal/${mealId}/detail`)
}

const getMealTimeEmoji = (mealTime) => {
  const emojis = {
    breakfast: '🌅',
    lunch: '🌞',
    dinner: '🌙',
  }
  return emojis[mealTime] || '🍽️'
}

const getMealTimeText = (mealTime) => {
  const texts = {
    breakfast: '아침',
    lunch: '점심',
    dinner: '저녁',
  }
  return texts[mealTime] || '식사'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) {
    return '오늘'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return '어제'
  } else {
    return date.toLocaleDateString('ko-KR', {
      month: 'long',
      day: 'numeric',
    })
  }
}
</script>

<style scoped>
.bg-brand-primary {
  background-color: #6a7d73;
}
.bg-brand-hover {
  background-color: #5a6b63;
}
.text-brand-primary {
  color: #6a7d73;
}
</style>
