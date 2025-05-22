<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- Main Content - 대시보드와 동일한 레이아웃 적용 -->
    <main class="max-w-3xl mx-auto px-4 py-8">
      <!--<main class="max-w-full w-full mx-auto px-8 py-6" style="max-width: calc(100% - 32px)">-->
      <div class="text-center mb-6 lg:mb-8">
        <h1 class="text-2xl lg:text-3xl font-bold text-gray-800 mb-2">식단 기록하기</h1>
        <p class="text-brand-secondary">오늘 드신 음식을 기록해주세요</p>
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
              <label class="text-lg font-semibold text-gray-800">
                메뉴명<span class="text-red-500">* </span>
              </label>
              <button
                @click="addMenuField"
                class="flex items-center gap-2 px-6 py-3 bg-[#C7D7CB] text-white rounded-full hover:bg-[#B29888] transition-colors text-sm font-medium whitespace-nowrap ml-4"
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
              class="border-2 border-dashed border-brand-border rounded-xl p-8 text-center cursor-pointer hover:bg-brand-accent/30 hover:border-brand-primary transition-all duration-200"
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

              <!-- No Image State -->
              <div v-if="!previewImage" class="flex flex-col items-center">
                <div class="text-5xl mb-4">📷</div>
                <p class="text-brand-secondary mb-2 text-lg">식사 사진을 추가해주세요</p>
                <p class="text-brand-muted text-sm">클릭하거나 드래그하여 업로드</p>
              </div>

              <!-- Image Preview -->
              <div v-else class="relative inline-block">
                <img
                  :src="previewImage"
                  alt="Meal preview"
                  class="max-h-64 max-w-full rounded-lg shadow-md"
                />
                <button
                  @click.stop="removeImage"
                  class="absolute -top-2 -right-2 w-8 h-8 bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors flex items-center justify-center"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
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

      <!-- Toast for feedback -->
      <div v-if="showToast" class="fixed top-4 left-1/2 transform -translate-x-1/2 z-50">
        <div
          class="px-6 py-3 rounded-lg shadow-lg"
          :class="toastType === 'success' ? 'bg-green-500 text-white' : 'bg-red-500 text-white'"
        >
          {{ toastMessage }}
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// Form data
const selectedMealTime = ref('breakfast')
const menuItems = ref([{ name: '' }])
const memo = ref('')
const previewImage = ref(null)
const selectedFile = ref(null)

// UI state
const loading = ref(false)
const errors = ref({})
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

// File input reference
const fileInput = ref(null)

// Form validation
const isFormValid = computed(() => {
  return menuItems.value.some((item) => item.name.trim().length > 0)
})

// Set default meal time based on current time
onMounted(() => {
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

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    handleFile(file)
  }
}

const handleFileDrop = (event) => {
  const file = event.dataTransfer.files[0]
  if (file && file.type.startsWith('image/')) {
    handleFile(file)
  }
}

const handleFile = (file) => {
  // Validate file size (max 5MB)
  if (file.size > 5 * 1024 * 1024) {
    showToastMessage('파일 크기는 5MB 이하만 가능합니다.', 'error')
    return
  }

  // Validate file type
  if (!file.type.startsWith('image/')) {
    showToastMessage('이미지 파일만 업로드 가능합니다.', 'error')
    return
  }

  selectedFile.value = file
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
  }
  reader.readAsDataURL(file)
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

// Toast message
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true

  setTimeout(() => {
    showToast.value = false
  }, 3000)
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

    const mealData = {
      mealTime: selectedMealTime.value,
      menus: validMenus,
      memo: memo.value.trim(),
      userId: authStore.getUser?.id,
      createdAt: new Date().toISOString(),
    }

    if (selectedFile.value) {
      mealData.hasImage = true
    }

    console.log('Saving meal record:', mealData)

    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1000))

    showToastMessage('식단이 성공적으로 기록되었습니다!', 'success')

    // Navigate back to meal list after short delay
    setTimeout(() => {
      router.push('/meals')
    }, 1500)
  } catch (error) {
    console.error('식단 기록 저장 실패:', error)
    showToastMessage('식단 기록 저장에 실패했습니다. 다시 시도해주세요.', 'error')
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
