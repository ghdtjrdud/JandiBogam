<template>
  <div class="min-h-screen bg-white">
    <!-- Main Content -->
    <main class="container mx-auto px-4 py-8">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">식단 기록하기</h1>
        <p class="text-gray-600">오늘 드신 음식을 기록해주세요</p>
      </div>

      <!-- Meal Time Selection -->
      <div class="flex justify-center mb-6">
        <div class="tabs tabs-boxed bg-gray-100 p-1 rounded-full inline-flex">
          <button
            @click="selectedMealTime = 'breakfast'"
            :class="[
              'tab rounded-full px-8 py-2',
              selectedMealTime === 'breakfast' ? 'bg-brand-primary text-white' : 'text-gray-600',
            ]"
          >
            아침
          </button>
          <button
            @click="selectedMealTime = 'lunch'"
            :class="[
              'tab rounded-full px-8 py-2',
              selectedMealTime === 'lunch' ? 'bg-brand-primary text-white' : 'text-gray-600',
            ]"
          >
            점심
          </button>
          <button
            @click="selectedMealTime = 'dinner'"
            :class="[
              'tab rounded-full px-8 py-2',
              selectedMealTime === 'dinner' ? 'bg-brand-primary text-white' : 'text-gray-600',
            ]"
          >
            저녁
          </button>
        </div>
      </div>

      <!-- Meal Record Form -->
      <div class="max-w-2xl mx-auto bg-white rounded-lg shadow-sm border p-8">
        <!-- Menu Name -->
        <div class="mb-6">
          <label for="menu-name" class="block text-lg font-medium text-gray-700 mb-2">메뉴명</label>
          <div class="relative">
            <input
              type="text"
              id="menu-name"
              v-model="menuName"
              placeholder="드신 메뉴를 입력해주세요 (예: 현미밥, 된장찌개)"
              class="input input-bordered w-full pr-10 focus:border-brand-primary"
            />
          </div>
        </div>

        <!-- Photo Upload -->
        <div class="mb-6">
          <label class="block text-lg font-medium text-gray-700 mb-2">사진 추가</label>
          <div
            class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center cursor-pointer hover:bg-gray-50"
            @click="triggerFileInput"
          >
            <input
              type="file"
              ref="fileInput"
              class="hidden"
              accept="image/*"
              @change="handleFileChange"
            />
            <div v-if="!previewImage" class="flex flex-col items-center">
              <div class="text-4xl mb-3">📷</div>
              <p class="text-gray-600 mb-1">식사 사진을 추가해주세요</p>
              <p class="text-gray-400 text-sm">클릭하거나 드래그하여 업로드</p>
            </div>
            <div v-else class="relative">
              <img :src="previewImage" alt="Meal preview" class="max-h-64 mx-auto rounded-lg" />
              <button
                @click.stop="removeImage"
                class="absolute top-2 right-2 bg-white rounded-full p-1 shadow-md hover:bg-gray-100"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-gray-600"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fill-rule="evenodd"
                    d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                    clip-rule="evenodd"
                  />
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- Memo -->
        <div class="mb-8">
          <label for="memo" class="block text-lg font-medium text-gray-700 mb-2">메모</label>
          <div class="relative">
            <textarea
              id="memo"
              v-model="memo"
              rows="4"
              placeholder="특별한 사항이 있으면 메모해주세요"
              class="textarea textarea-bordered w-full focus:border-brand-primary"
            ></textarea>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex gap-4">
          <button
            @click="cancel"
            class="btn btn-outline flex-1 border-brand-border text-brand-secondary hover:bg-brand-secondary hover:text-white"
          >
            취소
          </button>
          <button
            @click="saveMealRecord"
            class="btn btn-primary flex-1 bg-brand-primary hover:bg-brand-hover border-none"
          >
            저장하기
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedMealTime = ref('breakfast')
const menuName = ref('')
const memo = ref('')
const previewImage = ref(null)
const fileInput = ref(null)

// Trigger file input click
const triggerFileInput = () => {
  fileInput.value.click()
}

// Handle file selection
const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    const reader = new FileReader()
    reader.onload = (e) => {
      previewImage.value = e.target.result
    }
    reader.readAsDataURL(file)
  }
}

// Remove selected image
const removeImage = () => {
  previewImage.value = null
  fileInput.value.value = ''
}

// Cancel and go back
const cancel = () => {
  router.back()
}

// Save meal record
const saveMealRecord = () => {
  // Here you would typically send the data to your backend
  const mealData = {
    mealTime: selectedMealTime.value,
    menuName: menuName.value,
    memo: memo.value,
    // In a real app, you would upload the image file to your server
    // and store the URL or file reference
    hasImage: !!previewImage.value,
  }

  console.log('Saving meal record:', mealData)

  // Navigate back to meal list or dashboard
  router.push('/meal/record')
}
</script>

<style scoped>
.bg-brand-lightbg {
  background-color: #f6faf7;
}
.bg-brand-accent {
  background-color: #c7d7cb;
}
.text-brand-primary {
  color: #6a7d73;
}
.bg-brand-primary {
  background-color: #6a7d73;
}
.bg-brand-hover {
  background-color: #5a6b63;
}
.border-brand-primary {
  border-color: #6a7d73;
}
.border-brand-border {
  border-color: #b29888;
}
.text-brand-secondary {
  color: #9e8c7f;
}

.textarea,
.input {
  border-radius: 0.5rem;
}

.tabs-boxed {
  border-radius: 9999px;
}
</style>
