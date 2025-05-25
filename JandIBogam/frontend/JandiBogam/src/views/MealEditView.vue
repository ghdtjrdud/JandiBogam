<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="w-full max-w-[1024px] px-8 mx-auto py-10">
      <!-- 제목 -->
      <div class="mb-8">
        <h2 class="text-3xl font-bold text-gray-800">식단 수정하기</h2>
      </div>
      <!-- 날짜 선택 -->
      <div class="flex justify-center mb-6 lg:mb-8">
        <div class="bg-white rounded-2xl shadow-sm p-6 w-full max-w-md">
          <div class="flex flex-col items-center gap-4">
            <label class="text-lg font-semibold text-gray-800">식사 날짜</label>
            <input
              type="date"
              v-model="selectedDate"
              :max="maxDate"
              class="px-4 py-2 border-2 border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-brand-primary text-center"
            />
          </div>
        </div>
      </div>
      <!-- 식사 시간 -->
      <div class="flex justify-center mb-6 lg:mb-8">
        <div class="flex gap-12 p-1 bg-white rounded-2xl shadow-sm">
          <button
            @click="selectedMealTime = '아침'"
            :class="[
              'px-6 py-3 rounded-full font-medium transition-all duration-200',
              selectedMealTime === '아침'
                ? 'bg-[#C7D7CB] text-white shadow-md'
                : 'text-brand-secondary hover:bg-brand-accent hover:text-brand-primary',
            ]"
          >
            🌅 아침
          </button>
          <button
            @click="selectedMealTime = '점심'"
            :class="[
              'px-6 py-3 rounded-full font-medium transition-all duration-200',
              selectedMealTime === '점심'
                ? 'bg-[#C7D7CB] text-white shadow-md'
                : 'text-brand-secondary hover:bg-brand-accent hover:text-brand-primary',
            ]"
          >
            ☀️ 점심
          </button>
          <button
            @click="selectedMealTime = '저녁'"
            :class="[
              'px-6 py-3 rounded-full font-medium transition-all duration-200',
              selectedMealTime === '저녁'
                ? 'bg-[#C7D7CB] text-white shadow-md'
                : 'text-brand-secondary hover:bg-brand-accent hover:text-brand-primary',
            ]"
          >
            🌙 저녁
          </button>
        </div>
      </div>
      <!-- 메뉴명 -->
      <div class="w-full max-w-4xl mx-auto">
        <div class="bg-white rounded-2xl shadow-lg p-6 lg:p-10">
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
          <!-- 사진 업로드 -->
          <div class="mb-8">
            <label class="block text-lg font-semibold text-gray-800 mb-4">
              사진 추가 <span class="text-brand-muted text-sm font-normal">(선택)</span>
            </label>
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
              <!-- 사진 미리보기/기존 사진 -->
              <div v-else class="flex flex-col items-center">
                <img
                  v-if="previewImage"
                  :src="previewImage"
                  alt="업로드된 미리보기"
                  class="max-h-64 max-w-full rounded-lg shadow-md mx-auto"
                />
                <img
                  v-else-if="existingPhotoUrl"
                  :src="getFullImageUrl(existingPhotoUrl)"
                  alt="기존 사진"
                  class="max-h-64 max-w-full rounded-lg shadow-md mx-auto"
                />
              </div>
            </div>
          </div>
          <!-- 메모 -->
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
          <!-- 버튼 -->
          <div class="flex sm:flex-row gap-4">
            <button
              @click="cancel"
              class="flex-1 px-6 py-4 border-1 border-brand-border text-brand-secondary font-semibold rounded-xl hover:bg-brand-accent/50 hover:border-brand-primary transition-all duration-200"
            >
              취소
            </button>
            <button
              @click="saveEditMeal"
              :disabled="!isFormValid || loading"
              :class="[
                'flex-1 px-6 py-4 font-semibold rounded-xl transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2',
                isFormValid && !loading
                  ? 'bg-brand-primary text-white hover:bg-brand-hover'
                  : 'bg-gray-300 text-gray-500',
              ]"
            >
              <span>{{ loading ? '저장 중...' : '수정하기' }}</span>
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from 'vue-toastification'
import MealService from '@/services/MealService'
import FileService from '@/services/FileService'

const router = useRouter()
const route = useRoute()
const toast = useToast()

// 사진 변경
const existingPhotoUrl = ref(null)

const mealId = route.params.id

const selectedMealTime = ref('아침')
const selectedDate = ref('')
const menuItems = ref([{ name: '' }])
const memo = ref('')
const previewImage = ref(null)
const selectedFile = ref(null)
const fileInput = ref(null)
const loading = ref(false)
const errors = ref({})

// 날짜 최대값 (오늘)
const maxDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const isFormValid = computed(() => {
  return menuItems.value.some((item) => item.name.trim().length > 0)
})

// 기존 데이터 fetch 및 form 세팅
onMounted(async () => {
  try {
    loading.value = true
    // 기존 데이터 가져오기
    const res = await MealService.getMealById(mealId)
    const data = res.data

    // 날짜
    selectedDate.value = data.eatDate
    // 시간대(아침/점심/저녁)
    selectedMealTime.value = data.timeSlot || '아침'
    // 메뉴
    if (Array.isArray(data.foodNames) && data.foodNames.length > 0) {
      menuItems.value = data.foodNames.map((name) => ({ name }))
    } else {
      menuItems.value = [{ name: '' }]
    }
    // 메모
    memo.value = data.memo || ''
    // 사진
    existingPhotoUrl.value = data.photoUrl ? data.photoUrl : null
    previewImage.value = null // 최초엔 미리보기 없음
    selectedFile.value = null // 새 업로드 전까지 null
  } catch (err) {
    toast.error('식단 정보를 불러올 수 없습니다.')
    router.back()
  } finally {
    loading.value = false
  }
})

// 메뉴 추가/삭제
const addMenuField = () => {
  if (menuItems.value.length < 5) {
    menuItems.value.push({ name: '' })
  }
}
const removeMenuField = (index) => {
  if (menuItems.value.length > 1) {
    menuItems.value.splice(index, 1)
    delete errors.value[`menu_${index}`]
  }
}

// 파일 관련
const triggerFileInput = () => {
  fileInput.value?.click()
}
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (file) await handleFile(file)
}
const handleFileDrop = async (event) => {
  const file = event.dataTransfer.files[0]
  if (file && file.type.startsWith('image/')) await handleFile(file)
}
const handleFile = async (file) => {
  if (file.size > 10 * 1024 * 1024) {
    toast.error('파일 크기는 10MB 이하만 가능합니다.')
    return
  }
  if (!file.type.startsWith('image/')) {
    toast.error('이미지 파일만 업로드 가능합니다.')
    return
  }
  try {
    selectedFile.value = file
    const reader = new FileReader()
    reader.onload = (e) => {
      previewImage.value = e.target.result
    }
    reader.readAsDataURL(file)
  } catch (error) {
    toast.error('이미지 처리 중 오류가 발생했습니다.')
  }
}
const removeImage = () => {
  previewImage.value = null
  selectedFile.value = null
  if (fileInput.value) fileInput.value.value = ''
}
const getFullImageUrl = (photoUrl) => {
  if (!photoUrl) return null
  if (photoUrl.startsWith('http://') || photoUrl.startsWith('https://')) return photoUrl
  const backendUrl = 'http://localhost:8080'
  return photoUrl.startsWith('/') ? `${backendUrl}${photoUrl}` : `${backendUrl}/${photoUrl}`
}

// 저장
// 저장 함수 수정
const saveEditMeal = async () => {
  if (!isFormValid.value) {
    toast.error('메뉴를 한 개 이상 입력해주세요.')
    return
  }
  loading.value = true
  try {
    // 메뉴명 배열로 변환
    const validMenus = menuItems.value
      .filter((item) => item.name.trim())
      .map((item) => item.name.trim())

    let photoUrl = null

    // 새 이미지가 선택되면 업로드, 아니면 기존 url 사용
    if (selectedFile.value) {
      const imageResponse = await FileService.uploadImage(selectedFile.value)
      photoUrl = imageResponse.data.photoUrl
    } else if (existingPhotoUrl.value) {
      photoUrl = existingPhotoUrl.value
    }

    // MealService.updateMeal에서 변환을 처리하므로 원본 데이터 구조로 전달
    const data = {
      mealTime: selectedMealTime.value, // timeSlot 대신 mealTime으로 전달
      menus: validMenus, // foodNames 대신 menus로 전달
      memo: memo.value,
      photoUrl,
      eatDate: selectedDate.value,
    }

    await MealService.updateMeal(mealId, data)
    toast.success('식단이 성공적으로 수정되었습니다!')

    setTimeout(() => {
      router.push(`/meal/${mealId}/detail`)
    }, 1000)
  } catch (error) {
    console.error('식단 수정 오류:', error)
    toast.error('식단 수정에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

const cancel = () => {
  if (menuItems.value.some((item) => item.name.trim()) || memo.value.trim() || previewImage.value) {
    if (confirm('작성 중인 내용이 있습니다. 정말 취소하시겠습니까?')) {
      router.back()
    }
  } else {
    router.back()
  }
}
</script>

<style scoped>
/* 기존 MealRecordView 스타일 그대로 사용 */
</style>
