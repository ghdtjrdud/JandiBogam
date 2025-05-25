<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- Main Content - 대시보드와 동일한 레이아웃 적용 -->
    <main class="w-full max-w-[1024px] px-8 mx-auto py-10">
      <!--<main class="max-w-full w-full mx-auto px-8 py-6" style="max-width: calc(100% - 32px)">-->
      <div class="text-center mb-6 lg:mb-8">
        <h1 class="text-2xl lg:text-3xl font-bold text-gray-800 mb-2">식단 기록하기</h1>
        <p class="text-brand-secondary">오늘 드신 음식을 기록해주세요</p>
      </div>
      <!--날짜 선택 섹션-->
      <div class="flex justify-center mb-6 lg:mb-8">
        <div class="bg-white rounded-2xl shadow-sm p-6 w-full max-w-md">
          <div class="flex flex-col items-center gap-4">
            <label class="text-lg font-semibold text-gray-800">식사 날짜 선택</label>
            <div class="flex items-center gap-3">
              <div class="flex items-center gap-2">
                <span class="text-gray-700 font-medium">📅</span>
                <input
                  type="date"
                  v-model="selectedDate"
                  :max="maxDate"
                  class="px-4 py-2 border-2 border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary text-center"
                />
              </div>
              <button
                @click="setToday"
                :class="[
                  'px-4 py-2 text-sm rounded-full transition-colors font-medium',
                  isToday
                    ? 'bg-brand-primary text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300',
                ]"
              >
                오늘
              </button>
            </div>
            <!-- 선택된 날짜 표시 -->
            <div class="text-center">
              <p class="text-gray-600 text-sm">
                {{ formatSelectedDate }}
                <span
                  v-if="isToday"
                  class="ml-2 px-2 py-1 bg-green-100 text-green-700 text-xs rounded-full"
                >
                  오늘
                </span>
                <span
                  v-else-if="isYesterday"
                  class="ml-2 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full"
                >
                  어제
                </span>
                <span
                  v-else-if="daysDiff > 1"
                  class="ml-2 px-2 py-1 bg-orange-100 text-orange-700 text-xs rounded-full"
                >
                  {{ daysDiff }}일 전
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
      <!-- Meal Time Selection -->
      <div class="flex justify-center mb-6 lg:mb-8">
        <div class="flex gap-12 p-1 bg-white rounded-2xl shadow-sm">
          <button
            @click="selectedMealTime = 'breakfast'"
            :class="[
              'px-6 py-3 rounded-full font-medium transition-all duration-200',
              selectedMealTime === 'breakfast'
                ? 'bg-[#C7D7CB] text-white shadow-md'
                : 'text-brand-secondary hover:bg-brand-accent hover:text-brand-primary',
            ]"
          >
            🌅 아침
          </button>
          <button
            @click="selectedMealTime = 'lunch'"
            :class="[
              'px-6 py-3 rounded-full font-medium transition-all duration-200',
              selectedMealTime === 'lunch'
                ? 'bg-[#C7D7CB] text-white shadow-md'
                : 'text-brand-secondary hover:bg-brand-accent hover:text-brand-primary',
            ]"
          >
            🌞 점심
          </button>
          <button
            @click="selectedMealTime = 'dinner'"
            :class="[
              'px-6 py-3 rounded-full font-medium transition-all duration-200',
              selectedMealTime === 'dinner'
                ? 'bg-[#C7D7CB] text-white shadow-md'
                : 'text-brand-secondary hover:bg-brand-accent hover:text-brand-primary',
            ]"
          >
            🌙 저녁
          </button>
        </div>
      </div>

      <!-- Meal Record Form -->
      <div class="w-full max-w-4xl mx-auto">
        <div class="bg-white rounded-2xl shadow-lg p-6 lg:p-10">
          <!-- Menu Names -->
          <div class="mb-8">
            <div class="flex items-center mb-4">
              <label class="text-2xl font-semibold text-gray-800">
                메뉴명<span class="text-red-500">* </span>
              </label>
              <button
                @click="addMenuField"
                class="flex items-center gap-2 px-6 py-3 bg-[#C7D7CB] text-white rounded-2xl hover:bg-[#B29888] transition-colors text-sm font-medium whitespace-nowrap ml-4"
              >
                추가<span class="material-icons" style="font-size: 20px">add</span>
              </button>
            </div>

            <div class="space-y-4">
              <div
                v-for="(menu, index) in menuItems"
                :key="index"
                class="flex gap-4 items-start w-full"
              >
                <div class="flex-1 min-w-0">
                  <input
                    type="text"
                    v-model="menu.name"
                    :placeholder="`메뉴 ${index + 1}을 입력해주세요 (예: 현미밥, 된장찌개)`"
                    class="w-full px-4 py-4 text-lg border-2 border-brand-border rounded-xl focus:outline-none focus:border-brand-primary focus:ring-2 focus:ring-brand-primary/20 transition-all"
                    :class="{ 'border-red-300 focus:border-red-500': errors[`menu_${index}`] }"
                  />
                  <p v-if="errors[`menu_${index}`]" class="text-red-500 text-sm mt-1">
                    {{ errors[`menu_${index}`] }}
                  </p>
                </div>
                <button
                  v-if="menuItems.length > 1"
                  @click="removeMenuField(index)"
                  class="flex-shrink-0 w-10 h-10 flex items-center justify-center bg-[#C7D7CB] hover:bg-[#B29888] text-white rounded-full shadow transition-all duration-200"
                  title="메뉴 삭제"
                >
                  <span class="material-icons" style="font-size: 20px">remove</span>
                </button>
              </div>
            </div>
          </div>

          <!-- Photo Upload -->
          <div class="mb-8">
            <label class="block text-lg font-semibold text-gray-800 mb-4">
              사진 추가 <span class="text-brand-muted text-sm font-normal">(선택)</span>
            </label>

            <!-- Upload Area -->
            <div
              class="relative border-2 border-dashed border-brand-border rounded-xl p-8 text-center cursor-pointer hover:bg-brand-accent/30 hover:border-brand-primary transition-all duration-200"
              @click="triggerFileInput"
              @dragover.prevent
              @drop.prevent="handleFileDrop"
            >
              <input
                type="file"
                ref="fileInput"
                class="hidden"
                accept="image/*"
                @change="handleFileChange"
              />

              <!-- 삭제 버튼: 업로드 박스 우측 상단에 고정 -->
              <button
                v-if="previewImage"
                @click.stop="removeImage"
                class="absolute top-2 right-2 w-10 h-10 bg-[#C7D7CB] hover:bg-[#B29888] text-white rounded-full shadow flex items-center justify-center transition-all duration-200 z-10"
                title="사진 삭제"
              >
                <span class="material-icons" style="font-size: 20px">remove</span>
              </button>

              <!-- No Image State -->
              <div v-if="!previewImage" class="flex flex-col items-center">
                <div class="text-4xl mb-4">📷</div>
                <p class="text-brand-secondary text-lg">식사 사진을 추가해주세요</p>
                <p class="text-brand-muted text-sm">(클릭하거나 드래그하여 업로드)</p>
              </div>

              <!-- Image Preview -->
              <div v-else class="flex flex-col items-center">
                <img
                  :src="previewImage"
                  alt="Meal preview"
                  class="max-h-64 max-w-full rounded-lg shadow-md mx-auto"
                />
              </div>
            </div>
          </div>

          <!-- Memo -->
          <div class="mb-8">
            <label class="block text-lg font-semibold text-gray-800 mb-4">
              메모 <span class="text-brand-muted text-sm font-normal">(선택)</span>
            </label>
            <div class="relative">
              <textarea
                v-model="memo"
                rows="4"
                placeholder="특별한 사항이나 느낀 점을 메모해주세요&#10;예) 오늘은 집에서 만든 된장찌개가 특히 맛있었어요"
                class="w-full px-4 py-4 text-base border-2 border-brand-border rounded-xl focus:outline-none focus:border-brand-primary focus:ring-2 focus:ring-brand-primary/20 transition-all resize-none"
                maxlength="500"
              ></textarea>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex sm:flex-row gap-4">
            <button
              @click="cancel"
              class="flex-1 px-6 py-4 border-1 border-brand-border text-brand-secondary font-semibold rounded-xl hover:bg-brand-accent/50 hover:border-brand-primary transition-all duration-200"
            >
              취소
            </button>
            <button
              @click="saveMealRecord"
              :disabled="!isFormValid || loading"
              :class="[
                'flex-1 px-6 py-4 font-semibold rounded-xl transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2',
                isFormValid && !loading
                  ? 'bg-brand-primary text-white hover:bg-brand-hover'
                  : 'bg-gray-300 text-gray-500',
              ]"
            >
              <svg
                v-if="loading"
                class="animate-spin h-5 w-5"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  class="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  stroke-width="4"
                ></circle>
                <path
                  class="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                ></path>
              </svg>
              <span>{{ loading ? '저장 중...' : '저장하기' }}</span>
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import MealService from '@/services/MealService'
import FileService from '@/services/FileService'

function getMyUserId() {
  // localStorage에서 userId 직접 꺼내기 (문자열일 수 있으므로 숫자로 변환)
  const userId = localStorage.getItem('userId')
  if (userId && !isNaN(userId)) return Number(userId)

  // user 객체에서 꺼내는 경우 (예비용)
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const userObj = JSON.parse(userStr)
      if (userObj.id && !isNaN(userObj.id)) return Number(userObj.id)
    } catch (e) {
      console.error('user 파싱 실패:', e)
    }
  }
  return null
}

const router = useRouter()
// const authStore = useAuthStore()
const toast = useToast()

// Form data
const selectedMealTime = ref('breakfast')
const menuItems = ref([{ name: '' }])
const memo = ref('')
const previewImage = ref(null)
const selectedFile = ref(null)

// UI state
const loading = ref(false)
const errors = ref({})
const loadingText = ref('저장하기')

// File input reference
const fileInput = ref(null)

const selectedDate = ref('')

// 날짜 관련 computed 속성들
const maxDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const isToday = computed(() => {
  const today = new Date().toISOString().split('T')[0]
  return selectedDate.value === today
})

const isYesterday = computed(() => {
  const yesterday = new Date()
  yesterday.setDate(yesterday.getDate() - 1)
  return selectedDate.value === yesterday.toISOString().split('T')[0]
})

const daysDiff = computed(() => {
  if (!selectedDate.value) return 0

  const today = new Date()
  const selected = new Date(selectedDate.value)
  const diffTime = today.getTime() - selected.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  return diffDays
})

const formatSelectedDate = computed(() => {
  if (!selectedDate.value) return ''

  const date = new Date(selectedDate.value)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const dayNames = ['일', '월', '화', '수', '목', '금', '토']
  const dayName = dayNames[date.getDay()]

  return `${year}년 ${month}월 ${day}일 (${dayName})`
})

// 날짜 관련 메서드들
const setToday = () => {
  const today = new Date()
  selectedDate.value = today.toISOString().split('T')[0]
}

const setDefaultDate = () => {
  // 기본값을 오늘로 설정
  setToday()
}

// Form validation
const isFormValid = computed(() => {
  return menuItems.value.some((item) => item.name.trim().length > 0)
})

// Set default meal time based on current time
onMounted(() => {
  // 기본 날짜 설정
  setDefaultDate()

  // 기존 시간대 설정 로직
  const now = new Date()
  const hour = now.getHours()

  if (hour < 11) {
    selectedMealTime.value = 'breakfast'
  } else if (hour < 17) {
    selectedMealTime.value = 'lunch'
  } else {
    selectedMealTime.value = 'dinner'
  }
})

// Menu management methods
const addMenuField = () => {
  if (menuItems.value.length < 5) {
    // 최대 5개 메뉴
    menuItems.value.push({ name: '' })
  }
}

const removeMenuField = (index) => {
  if (menuItems.value.length > 1) {
    menuItems.value.splice(index, 1)
    // 해당 인덱스의 에러도 제거
    delete errors.value[`menu_${index}`]
  }
}

// File handling methods
const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (file) {
    await handleFile(file)
  }
}

const handleFileDrop = async (event) => {
  const file = event.dataTransfer.files[0]
  if (file && file.type.startsWith('image/')) {
    await handleFile(file)
  }
}

const handleFile = async (file) => {
  // Validate file size (max 10MB for original file)
  if (file.size > 10 * 1024 * 1024) {
    toast.error('파일 크기는 10MB 이하만 가능합니다.')
    return
  }

  // Validate file type
  if (!file.type.startsWith('image/')) {
    toast.error('이미지 파일만 업로드 가능합니다.')
    return
  }

  try {
    // 이미지 리사이즈 처리
    const resizedFile = await resizeImage(file, 800, 600, 0.8) // 최대 800x600, 품질 80%
    selectedFile.value = resizedFile

    // 미리보기용 URL 생성
    const reader = new FileReader()
    reader.onload = (e) => {
      previewImage.value = e.target.result
    }
    reader.readAsDataURL(resizedFile)

    console.log(
      `원본 크기: ${(file.size / 1024 / 1024).toFixed(2)}MB → 압축 후: ${(resizedFile.size / 1024 / 1024).toFixed(2)}MB`,
    )
  } catch (error) {
    console.error('이미지 처리 실패:', error)
    toast.error('이미지 처리 중 오류가 발생했습니다.')
  }
}

const resizeImage = (file, maxWidth = 800, maxHeight = 600, quality = 0.8) => {
  return new Promise((resolve, reject) => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    const img = new Image()

    img.onload = () => {
      // 원본 이미지 크기
      const { width: originalWidth, height: originalHeight } = img

      // 비율 계산
      let { width, height } = calculateResizeRatio(
        originalWidth,
        originalHeight,
        maxWidth,
        maxHeight,
      )

      // 캔버스 크기 설정
      canvas.width = width
      canvas.height = height

      // 이미지 품질 향상을 위한 설정
      ctx.imageSmoothingEnabled = true
      ctx.imageSmoothingQuality = 'high'

      // 이미지 그리기
      ctx.drawImage(img, 0, 0, width, height)

      // Blob으로 변환
      canvas.toBlob(
        (blob) => {
          if (blob) {
            // File 객체로 변환 (원본 파일명 유지)
            const resizedFile = new File([blob], file.name, {
              type: file.type,
              lastModified: Date.now(),
            })
            resolve(resizedFile)
          } else {
            reject(new Error('이미지 변환 실패'))
          }
        },
        file.type,
        quality,
      )
    }

    img.onerror = () => reject(new Error('이미지 로드 실패'))
    img.src = URL.createObjectURL(file)
  })
}

// 리사이즈 비율 계산 함수
const calculateResizeRatio = (originalWidth, originalHeight, maxWidth, maxHeight) => {
  let width = originalWidth
  let height = originalHeight

  // 최대 크기보다 큰 경우에만 리사이즈
  if (width > maxWidth || height > maxHeight) {
    const widthRatio = maxWidth / width
    const heightRatio = maxHeight / height
    const ratio = Math.min(widthRatio, heightRatio)

    width = Math.round(width * ratio)
    height = Math.round(height * ratio)
  }

  return { width, height }
}

const removeImage = () => {
  previewImage.value = null
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// Form validation
const validateForm = () => {
  errors.value = {}
  let isValid = true

  menuItems.value.forEach((item, index) => {
    if (index === 0 && !item.name.trim()) {
      errors.value[`menu_${index}`] = '첫 번째 메뉴는 필수입니다.'
      isValid = false
    } else if (item.name.trim() && item.name.trim().length < 2) {
      errors.value[`menu_${index}`] = '메뉴명은 2글자 이상 입력해주세요.'
      isValid = false
    }
  })

  return isValid
}

// Navigation methods
const cancel = () => {
  const hasContent =
    menuItems.value.some((item) => item.name.trim()) || memo.value.trim() || previewImage.value

  if (hasContent) {
    if (confirm('작성 중인 내용이 있습니다. 정말 취소하시겠습니까?')) {
      router.back()
    }
  } else {
    router.back()
  }
}

// Save meal record
const saveMealRecord = async () => {
  if (!validateForm()) {
    return
  }

  loading.value = true

  try {
    const validMenus = menuItems.value
      .filter((item) => item.name.trim())
      .map((item) => item.name.trim())

    let photoUrl = null

    if (selectedFile.value) {
      try {
        console.log('사진 업로드 중')
        const imageResponse = await FileService.uploadImage(selectedFile.value)
        photoUrl = imageResponse.data.photoUrl
        console.log('사진 업로드 성공:', photoUrl)
      } catch (imageError) {
        console.error('사진 업로드 실패', imageError)
        toast.error('사진 업로드에 실패했습니다. 사진 없이 등록할까요?')

        const proceed = confirm('사진 업로드에 실패했습니다. 사진 없이 등록할까요?')
        if (!proceed) {
          loading.value = false
          return
        }
      }
    }

    loadingText.value = '식단 저장 중...'
    const mealData = {
      mealTime: selectedMealTime.value, // 이 부분이 중요!
      menus: validMenus, // MealService에서 foodNames로 변환됨
      memo: memo.value.trim(),
      photoUrl: photoUrl,
      eatDate: selectedDate.value,
    }

    console.log('식단 데이터 저장 시작...', mealData)
    const response = await MealService.createMealWithFoodSearch(mealData)

    const myUserId = getMyUserId()

    console.log('식단 저장 성공:', response.data)

    // 성공 메시지에 날짜 정보 포함
    const dateText = isToday.value ? '오늘' : formatSelectedDate.value
    toast.success(`${dateText}의 식단이 성공적으로 기록되었습니다!`)

    // Navigate back to meal list after short delay
    setTimeout(() => {
      router.push(`/meals/${myUserId}`)
    }, 2000)
  } catch (error) {
    console.error('식단 기록 저장 실패:', error)

    let errorMessage = '식단 기록 저장에 실패했습니다.'
    if (error.response?.data) {
      if (typeof error.response.data === 'string') {
        errorMessage = error.response.data
      } else if (error.response.data.message) {
        errorMessage = error.response.data.message
      }
    }

    toast.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Custom scrollbar for textarea */
.textarea::-webkit-scrollbar {
  width: 8px;
}

.textarea::-webkit-scrollbar-track {
  background: transparent;
}

.textarea::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

.textarea::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* DatePicker 스타일 커스터마이징 */
input[type='date'] {
  color-scheme: light;
  font-weight: 500;
}

input[type='date']::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.7;
  filter: invert(0.5);
}

input[type='date']::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}

input[type='date']:focus {
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

/* Smooth transitions */
* {
  transition-property:
    background-color, border-color, color, fill, stroke, opacity, box-shadow, transform;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
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
