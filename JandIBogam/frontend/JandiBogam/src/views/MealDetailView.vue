<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <div
          class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-green-600 mb-4"
        ></div>
        <p class="text-gray-600">식단 정보를 불러오는 중...</p>
      </div>
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="flex items-center justify-center min-h-screen">
      <div class="text-center">
        <p class="text-red-600 mb-4">{{ error }}</p>
        <button
          @click="fetchMealDetail"
          class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
        >
          다시 시도
        </button>
      </div>
    </div>

    <!-- 메인 콘텐츠 -->
    <main v-else class="w-full max-w-[1024px] px-8 mx-auto py-10">
      <!-- 뒤로가기 -->
      <a
        href="#"
        @click.prevent="goBack"
        class="inline-flex items-center text-[#5d9a74] text-sm mb-6 hover:underline"
      >
        <svg class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M15 19l-7-7 7-7"
          />
        </svg>
        목록으로 돌아가기
      </a>

      <!-- 좌우 정렬 -->
      <div class="flex flex-col lg:flex-row gap-8">
        <!-- 좌측: 식단 카드 -->
        <div class="flex-1 bg-white rounded-3xl shadow-lg overflow-hidden">
          <div class="p-6">
            <div class="flex items-center mb-3">
              <div
                class="w-10 h-10 bg-[#e8f4ed] rounded-full flex items-center justify-center text-base font-medium text-[#4a5b52] mr-3"
              >
                {{ getMealEmoji(mealData.timeSlot) }}
              </div>
              <div>
                <div class="text-sm text-[#6e8c76]">{{ formatDate(mealData.eatDate) }}</div>
                <div class="text-xs text-[#8fa097] mt-1">{{ mealData.timeSlot }}</div>
              </div>
            </div>
            <h2 class="text-2xl font-semibold text-[#4a5b52] mt-2">{{ getFoodNames() }}</h2>
          </div>

          <!-- 사진 영역 -->
          <div
            class="mx-6 mb-6 bg-[#f3f6f5] rounded-xl overflow-hidden flex items-center justify-center relative cursor-pointer p-4"
            style="height: 300px"
            @click="openImageModal"
          >
            <img
              v-if="mealData.photoUrl"
              :src="getFullImageUrl(mealData.photoUrl)"
              alt="식사 사진"
              class="max-w-full max-h-full object-contain rounded-lg hover:opacity-90 transition-opacity shadow-md"
              @error="handleImageError"
              @load="handleImageLoad"
            />
            <div v-else class="text-center">
              <div class="text-4xl mb-2 opacity-50">📷</div>
              <p class="text-[#8fa097] text-sm">{{ imageErrorMessage || '사진이 없습니다' }}</p>
            </div>
            <!-- 확대 아이콘 -->
            <div
              v-if="mealData.photoUrl"
              class="absolute top-2 right-2 bg-black bg-opacity-50 rounded-full p-2 opacity-0 hover:opacity-100 transition-opacity"
            >
              <svg class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM10 7v3m0 0v3m0-3h3m-3 0H7"
                />
              </svg>
            </div>
            <!-- 클릭 힌트 텍스트 -->
            <div
              v-if="mealData.photoUrl"
              class="absolute bottom-2 left-1/2 transform -translate-x-1/2 bg-black bg-opacity-50 text-white text-xs px-2 py-1 rounded opacity-0 hover:opacity-100 transition-opacity"
            >
              클릭해서 확대
            </div>
          </div>

          <!-- 메모 영역 -->
          <div
            v-if="mealData.memo"
            class="mx-6 mb-6 p-4 bg-[#f9fdfb] rounded-xl text-[#6e8c76] text-base"
          >
            {{ mealData.memo }}
          </div>

          <!-- 영양정보 영역 (있는 경우) -->
          <div v-if="nutritionInfo.length > 0" class="mx-6 mb-6">
            <h3 class="text-lg font-semibold text-[#4a5b52] mb-3">영양 정보</h3>
            <div class="space-y-2">
              <div
                v-for="food in nutritionInfo"
                :key="food.foodName"
                class="bg-[#f9fdfb] rounded-lg p-3"
              >
                <div class="font-medium text-[#4a5b52] mb-1">{{ food.foodName }}</div>
                <div class="text-sm text-[#6e8c76] grid grid-cols-2 gap-2">
                  <span v-if="food.calories">칼로리: {{ food.calories }}kcal</span>
                  <span v-if="food.protein">단백질: {{ food.protein }}g</span>
                  <span v-if="food.carbohydrate">탄수화물: {{ food.carbohydrate }}g</span>
                  <span v-if="food.fat">지방: {{ food.fat }}g</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 우측: 댓글 카드 -->
        <div class="lg:w-[380px] bg-white rounded-3xl shadow-lg p-6">
          <h3 class="text-lg font-semibold text-[#4a5b52] mb-4">
            그룹 댓글 ({{ comments.length }})
          </h3>

          <!-- 댓글 목록 -->
          <div
            v-for="(comment, index) in comments"
            :key="comment.id"
            class="mb-6 border-b border-gray-100 pb-4"
          >
            <div class="flex items-start">
              <div class="flex-1">
                <div class="flex justify-between items-center mb-1">
                  <span class="text-sm font-medium text-[#4a5b52]">
                    작성자: {{ comment.userId }}
                  </span>
                </div>

                <!-- 텍스트/수정 영역 -->
                <div v-if="editingCommentId === comment.id">
                  <textarea
                    v-model="editingText"
                    class="w-full p-2 text-sm border border-gray-300 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-green-400"
                    rows="3"
                  ></textarea>
                  <div class="flex justify-end gap-2 mt-2">
                    <button
                      @click="cancelEdit"
                      class="text-sm px-3 py-1 rounded-md border border-gray-300 hover:bg-gray-100"
                    >
                      취소
                    </button>
                    <button
                      @click="saveEdit"
                      class="text-sm px-3 py-1 rounded-md bg-green-600 text-white hover:bg-green-700"
                    >
                      저장
                    </button>
                  </div>
                </div>
                <div v-else>
                  <p class="text-sm text-[#4a5b52] mb-1">{{ comment.content }}</p>
                  <span class="text-xs text-[#8fa097]">{{ comment.createdAt }}</span>
                </div>

                <!-- 내가 쓴 댓글만 수정/삭제 버튼 표시 -->
                <div
                  v-if="isMyComment(comment) && editingCommentId !== comment.id"
                  class="flex mt-2 space-x-2"
                >
                  <button
                    @click="startEditing(comment)"
                    class="text-xs text-gray-600 hover:text-green-600 transition-colors"
                  >
                    수정
                  </button>
                  <button
                    @click="deleteComment(comment.id)"
                    class="text-xs text-gray-600 hover:text-red-600 transition-colors"
                  >
                    삭제
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- 댓글이 없을 때 -->
          <div v-if="comments.length === 0" class="text-center py-8">
            <p class="text-gray-500 text-sm">아직 댓글이 없습니다.</p>
            <p class="text-gray-400 text-xs mt-1">첫 번째 댓글을 남겨보세요!</p>
          </div>

          <!-- 댓글 입력 -->
          <div class="mt-6 pt-4 border-t border-[#f3f6f5]">
            <textarea
              v-model="newComment"
              class="w-full p-3 border border-[#e0eae5] rounded-lg text-sm resize-none focus:outline-none focus:ring-2 focus:ring-[#5d9a74]"
              placeholder="따뜻한 한마디를 남겨주세요"
              rows="3"
            ></textarea>
            <div class="flex justify-end mt-3">
              <button
                @click="addComment"
                :disabled="!newComment.trim()"
                class="bg-[#5d9a74] hover:bg-[#4e855f] text-white text-sm font-medium px-4 py-2 rounded-lg shadow-sm disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                댓글 작성
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 이미지 확대 모달 -->
    <div
      v-if="showImageModal"
      class="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50"
      style="z-index: 9999"
      @click="closeImageModal"
    >
      <div class="relative max-w-4xl max-h-full p-4" @click.stop>
        <img
          :src="getFullImageUrl(mealData.photoUrl)"
          alt="식사 사진 확대"
          class="max-w-full max-h-full object-contain rounded-lg"
        />
        <button
          @click="closeImageModal"
          class="absolute top-2 right-2 text-white hover:text-gray-300 bg-black bg-opacity-50 rounded-full p-2"
        >
          <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from 'vue-toastification'
import MealService from '@/services/MealService'
import MealCommentService from '@/services/MealCommentService'

const router = useRouter()
const route = useRoute()
const toast = useToast()

const mealData = ref({})
const nutritionInfo = ref([])
const loading = ref(true)
const error = ref(null)

// 댓글 데이터
const comments = ref([])
const editingCommentId = ref(null)
const editingText = ref('')
const myUserId = ref('') // 항상 문자열로 사용
const newComment = ref('')
const imageErrorMessage = ref('')
const showImageModal = ref(false)

// 내 댓글인지 확인
const isMyComment = (comment) => {
  return String(myUserId.value) === String(comment.userId)
}

// 이미지 모달 관련
const openImageModal = () => {
  if (mealData.value.photoUrl) showImageModal.value = true
}
const closeImageModal = () => {
  showImageModal.value = false
}

const getFullImageUrl = (photoUrl) => {
  if (!photoUrl) return null
  if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) return photoUrl
  const backendUrl = 'http://localhost:8080'
  return photoUrl.startsWith('/') ? `${backendUrl}${photoUrl}` : `${backendUrl}/${photoUrl}`
}
const handleImageError = (event) => {
  imageErrorMessage.value = '이미지를 불러올 수 없습니다'
  if (!event.target.src.includes('retry=')) {
    const photoUrl = mealData.value.photoUrl
    if (photoUrl) {
      const directUrl = `http://localhost:8080${photoUrl.startsWith('/') ? '' : '/'}${photoUrl}?retry=1`
      event.target.src = directUrl
    }
  }
}
const handleImageLoad = () => {
  imageErrorMessage.value = ''
}

// 상세 정보 조회
const fetchMealDetail = async () => {
  try {
    loading.value = true
    error.value = null
    const mealId = route.params.id
    if (!mealId) throw new Error('식단 ID가 없습니다.')
    const response = await MealService.getMealById(mealId)
    mealData.value = response.data
    if (response.data.foodNutrients && Array.isArray(response.data.foodNutrients)) {
      nutritionInfo.value = response.data.foodNutrients.map((item) => ({
        foodName: item.foodName,
        calories: item.calories,
        protein: item.protein,
        carbohydrate: item.carbohydrate,
        fat: item.fat,
      }))
    }
  } catch (err) {
    if (err.response?.status === 404) {
      error.value = '식단 정보를 찾을 수 없습니다.'
    } else if (err.response?.status === 401 || err.response?.status === 403) {
      error.value = '접근 권한이 없습니다.'
    } else {
      error.value = '식단 정보를 불러오는데 실패했습니다.'
    }
    toast.error(error.value)
  } finally {
    loading.value = false
  }
}

// 음식 이름 문자열
const getFoodNames = () => {
  if (!mealData.value) return '음식 정보 없음'
  if (
    mealData.value.foodNames &&
    Array.isArray(mealData.value.foodNames) &&
    mealData.value.foodNames.length > 0
  ) {
    return mealData.value.foodNames.join(', ')
  }
  if (
    mealData.value.foodNutrients &&
    Array.isArray(mealData.value.foodNutrients) &&
    mealData.value.foodNutrients.length > 0
  ) {
    const foods = mealData.value.foodNutrients.map((item) => item.foodName).filter(Boolean)
    if (foods.length > 0) return foods.join(', ')
  }
  if (
    mealData.value.mealFoods &&
    Array.isArray(mealData.value.mealFoods) &&
    mealData.value.mealFoods.length > 0
  ) {
    const foods = mealData.value.mealFoods
      .map((mf) => mf.foodNutrientDto?.foodName || mf.foodName || mf.name)
      .filter(Boolean)
    if (foods.length > 0) return foods.join(', ')
  }
  return '음식 정보 없음'
}
const getMealEmoji = (timeSlot) => {
  const emojiMap = { 아침: '🌅', 점심: '☀️', 저녁: '🌙' }
  return emojiMap[timeSlot] || '🍽️'
}
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'short',
  })
}
const goBack = () => router.back()

// 댓글 관련 함수
const addComment = async () => {
  if (!newComment.value.trim()) return
  try {
    await MealCommentService.createComment(route.params.id, newComment.value.trim())
    toast.success('댓글이 작성되었습니다.')
    newComment.value = ''
    await fetchComments()
  } catch (error) {
    toast.error('댓글 작성 실패: ' + (error.response?.data || error.message))
  }
}
const startEditing = (comment) => {
  editingCommentId.value = comment.id
  editingText.value = comment.content
}
const saveEdit = async () => {
  if (!editingText.value.trim()) return
  try {
    await MealCommentService.updateComment(editingCommentId.value, editingText.value)
    toast.success('댓글이 수정되었습니다.')
    await fetchComments()
    editingCommentId.value = null
    editingText.value = ''
  } catch (error) {
    toast.error('댓글 수정 실패: ' + (error.response?.data || error.message))
  }
}
const cancelEdit = () => {
  editingCommentId.value = null
  editingText.value = ''
}
const deleteComment = async (commentId) => {
  if (confirm('댓글을 삭제하시겠습니까?')) {
    try {
      await MealCommentService.deleteComment(commentId)
      toast.success('댓글이 삭제되었습니다.')
      await fetchComments()
    } catch (error) {
      toast.error('댓글 삭제 실패: ' + (error.response?.data || error.message))
    }
  }
}
const fetchComments = async () => {
  try {
    const mealId = route.params.id
    const res = await MealCommentService.fetchComments(mealId)
    comments.value = (Array.isArray(res.data) ? res.data : []).map((c) => ({
      id: c.id,
      userId: c.userId,
      content: c.content,
      createdAt: c.createdAt,
    }))
  } catch (err) {
    comments.value = []
  }
}

onMounted(async () => {
  // 항상 user 객체의 id를 userId로 쓴다!
  const userObj = JSON.parse(localStorage.getItem('user') || '{}')
  myUserId.value = String(userObj.id ?? '')
  await fetchMealDetail()
  await fetchComments()
})
</script>

<style scoped>
.shadow-lg {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.06);
}
.fixed {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.cursor-pointer:hover {
  cursor: pointer;
}
.transition-colors {
  transition: color 0.2s ease-in-out;
}
</style>
