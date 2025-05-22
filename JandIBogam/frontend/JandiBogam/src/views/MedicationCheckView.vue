<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-full w-full mx-auto px-8 py-6" style="max-width: calc(100% - 32px)">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">복약 일정 추가</h1>
        <p class="text-gray-600">규칙적인 복약으로 건강을 지켜보세요</p>
      </div>

      <div class="max-w-2xl mx-auto bg-white rounded-2xl shadow-md border p-8">
        <!-- 약 이름 -->
        <div class="mb-6">
          <label for="medicine-name" class="block text-lg font-medium text-gray-700 mb-2"
            >약 이름</label
          >
          <input
            type="text"
            id="medicine-name"
            v-model="medicineName"
            placeholder="약 이름을 입력하세요 (예: 혈압약)"
            class="input input-bordered w-full focus:border-brand-primary"
          />
        </div>

        <!-- 약 종류 -->
        <div class="mb-6">
          <label for="medicine-type" class="block text-lg font-medium text-gray-700 mb-2"
            >약 종류</label
          >
          <select
            id="medicine-type"
            v-model="medicineType"
            class="select select-bordered w-full focus:border-brand-primary"
          >
            <option disabled value="">선택하세요</option>
            <option>처방약</option>
            <option>일반약</option>
            <option>영양제</option>
          </select>
        </div>
        <!-- 복용 날짜 -->
        <div class="mb-6">
          <label for="med-date" class="block text-lg font-medium text-gray-700 mb-2"
            >복용 날짜</label
          >
          <input id="med-date" type="date" v-model="medDate" class="input input-bordered w-full" />
        </div>

        <!-- 복용 시간대 -->
        <div class="mb-6">
          <p class="block text-lg font-medium text-gray-700 mb-2">
            복용 시간대 (여러 개 선택 가능)
          </p>
          <div class="space-y-3">
            <div v-for="option in timeOptions" :key="option.value" class="form-control">
              <label class="cursor-pointer flex items-center gap-2">
                <input
                  type="checkbox"
                  v-model="selectedTimes"
                  :value="option"
                  class="checkbox checkbox-primary"
                />
                <span class="text-gray-700">{{ option.label }} ({{ option.time }})</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 복약 관리 팁 -->
        <div class="bg-brand-accent bg-opacity-30 rounded-lg p-4 mb-8 shadow-sm">
          <div class="flex items-start">
            <div class="text-yellow-500 mr-3 mt-1">
              <span class="text-xl">💡</span>
            </div>
            <div>
              <h3 class="font-medium text-brand-primary mb-2">복약 관리 팁</h3>
              <ul class="space-y-2 text-gray-600 text-sm">
                <li class="flex items-start">
                  <span class="mr-2">•</span>
                  <span>식사 위치 보호 시간에 먹는 것이 좋습니다.</span>
                </li>
                <li class="flex items-start">
                  <span class="mr-2">•</span>
                  <span>식사/식후 복용 지침을 잘 지켜주세요.</span>
                </li>
                <li class="flex items-start">
                  <span class="mr-2">•</span>
                  <span>복용 시간을 통일해 알림을 설정하세요.</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 버튼 영역 -->
        <div class="flex gap-4 mt-4">
          <button
            type="button"
            class="btn btn-outline flex-1 w-1/2 border-brand-border text-brand-secondary hover:bg-brand-secondary hover:text-white"
            @click="handleCancel"
          >
            취소
          </button>
          <button
            @click="handleSubmit"
            class="btn btn-primary flex-1 w-1/2 bg-brand-primary hover:bg-brand-hover border-none"
          >
            저장하기
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const toast = useToast()
const authStore = useAuthStore()

const medicineName = ref('')
const medicineType = ref('')
const selectedTimes = ref([])
const medicationId = ref(null)

// 편집 모드인지 확인
const isEditing = computed(() => !!medicationId.value)

// 시간대 옵션
const timeOptions = [
  { label: '아침/점심/저녁 식전', value: 'before_meal', time: '08:00' },
  { label: '아침/점심/저녁 식후', value: 'after_meal', time: '12:00' },
  { label: '취침 전', value: 'bedtime', time: '22:00' },
]

const medDate = ref(new Date().toISOString().split('T')[0]) // 기본값 오늘

// 1. 복약 시간 알림 예약 함수 추가
const scheduleNotification = (timeString, drugName, dateString) => {
  const [hours, minutes] = timeString.split(':').map(Number)

  const target = new Date(dateString)
  target.setHours(hours, minutes, 0, 0)

  const now = new Date()
  if (target <= now) return // 과거는 예약 안함

  const msUntilTarget = target - now

  setTimeout(() => {
    toast.info(`💊 ${drugName} 복약 시간입니다!`, {
      timeout: 10000,
      closeOnClick: false,
    })
  }, msUntilTarget)
}

// 2. 취소 버튼 핸들러 추가
const handleCancel = () => {
  router.back()
}

// 시간 문자열로 옵션 찾기
const getOptionByTime = (time) => {
  return timeOptions.find((option) => option.time === time)
}

// 편집 시 기존 데이터 로드
const loadMedicationData = async () => {
  if (route.params.id) {
    try {
      medicationId.value = parseInt(route.params.id)
      const response = await axios.get(`/api/medications/${medicationId.value}`)
      const medication = response.data

      medicineName.value = medication.drugName
      medicineType.value = medication.drugType

      // timeSlot 문자열 파싱 후 선택된 시간대 설정
      if (medication.timeSlot) {
        const times = medication.timeSlot.split(',')
        selectedTimes.value = times.map((time) => getOptionByTime(time)).filter(Boolean)
      }
    } catch (error) {
      toast.error('복약 정보를 불러오는데 실패했습니다: ' + error.message)
    }
  }
}

// 저장 또는 수정 처리
const handleSubmit = async () => {
  if (!medicineName.value || !medicineType.value || selectedTimes.value.length === 0) {
    toast.error('모든 필드를 입력해주세요')
    return
  }

  if (!authStore.user || !authStore.user.id) {
    toast.error('로그인이 필요한 기능입니다.')
    return
  }

  try {
    const medicationData = {
      drugName: medicineName.value,
      drugType: medicineType.value,
      timeSlot: selectedTimes.value.map((time) => time.time).join(','),
      medDate: medDate.value,
      userId: authStore.user.id,
    }

    if (isEditing.value) {
      // 기존 데이터 수정
      await axios.put(`/api/medications/${medicationId.value}`, medicationData)
      toast.success('복약 일정이 수정되었습니다!')
    } else {
      // 새 데이터 생성
      await axios.post('/api/medications', medicationData)
      toast.success('복약 일정이 저장되었습니다!')

      // 3. 새 약 등록 시 알림 예약 추가
      // 복약 등록 성공 시 알림 예약
      selectedTimes.value.forEach((time) => {
        scheduleNotification(time.time, medicineName.value, medDate.value)
      })
    }

    // 목록 페이지로 리다이렉트
    setTimeout(() => {
      router.push('/medication/list')
    }, 2000)
  } catch (error) {
    toast.error(isEditing.value ? '수정에 실패했습니다: ' : '저장에 실패했습니다: ' + error.message)
  }
}

// 컴포넌트 마운트 시 기존 데이터 로드
onMounted(() => {
  loadMedicationData()
})
</script>

<style scoped>
.bg-brand-accent {
  background-color: #c7d7cb;
}
.bg-brand-lightbg {
  background-color: #f6faf7;
}
.text-brand-primary {
  color: #6a7d73;
}
.btn-primary {
  background-color: #6a7d73;
  color: #fff;
}
.btn-primary:hover,
.bg-brand-hover {
  background-color: #4f5d52;
}
.btn-outline {
  border: 1.5px solid #b0bfb3;
  color: #6a7d73;
}
.btn-outline:hover,
.text-brand-secondary {
  background-color: #b0bfb3;
  color: #fff;
}
.input,
.select {
  border-radius: 0.75rem;
  border: 1.5px solid #b0bfb3;
  padding: 0.75rem 1rem;
  font-size: 1rem;
}
.input:focus,
.select:focus {
  border-color: #6a7d73;
  outline: none;
}
.shadow-md {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}
@media (min-width: 768px) {
  main {
    max-width: 1280px;
  }
}
</style>
