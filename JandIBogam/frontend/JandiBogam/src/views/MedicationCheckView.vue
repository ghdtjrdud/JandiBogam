<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-full w-full mx-auto px-8 py-6" style="max-width: calc(100% - 32px)">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">복약 일정 추가</h1>
        <p class="text-gray-600">규칙적인 복약으로 건강을 지켜보세요</p>
      </div>

      <div class="max-w-2xl mx-auto bg-white rounded-xl shadow-md border p-8">
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
            <div class="text-yellow-500 mr-3 mt-1"><span class="text-xl">💡</span></div>
            <div>
              <h3 class="font-medium text-brand-primary mb-2">복약 관리 팁</h3>
              <ul class="space-y-2 text-gray-600 text-sm">
                <li><span class="mr-2">•</span> 식사 위치 보호 시간에 먹는 것이 좋습니다.</li>
                <li><span class="mr-2">•</span> 식사/식후 복용 지침을 잘 지켜주세요.</li>
                <li><span class="mr-2">•</span> 복용 시간을 통일해 알림을 설정하세요.</li>
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
            :disabled="loading"
          >
            {{ loading ? '저장 중...' : '저장하기' }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useAuthStore } from '@/stores/auth'
import MedicationService from '@/services/MedicationService' // ✅ 서비스 import

const router = useRouter()
const route = useRoute()
const toast = useToast()
const authStore = useAuthStore()

const medicineName = ref('')
const medicineType = ref('')
const selectedTimes = ref([])
const medicationId = ref(null)
const medDate = ref(new Date().toISOString().split('T')[0]) // 기본값 오늘
const loading = ref(false) // ✅ 로딩 상태 추가

const isEditing = computed(() => !!medicationId.value)
const timeSlotString = computed(() => {
  return selectedTimes.value.map((time) => time.time).join(',')
})

const timeOptions = [
  { label: '아침/점심/저녁 식전', value: 'before_meal', time: '08:00' },
  { label: '아침/점심/저녁 식후', value: 'after_meal', time: '12:00' },
  { label: '취침 전', value: 'bedtime', time: '22:00' },
]

const getOptionByTime = (time) => {
  return timeOptions.find((option) => option.time === time)
}

const loadMedicationData = async () => {
  if (route.params.id) {
    try {
      medicationId.value = parseInt(route.params.id)
      // ✅ MedicationService 사용
      const response = await MedicationService.getMedicationById(medicationId.value)
      const medication = response.data

      medicineName.value = medication.drugName
      medicineType.value = medication.drugType

      if (medication.timeSlot) {
        const times = medication.timeSlot.split(',')
        selectedTimes.value = times.map((time) => getOptionByTime(time)).filter(Boolean)
      }
    } catch (error) {
      console.error('복약 정보 로드 실패:', error)
      toast.error('복약 정보를 불러오는데 실패했습니다')
    }
  }
}

const scheduleNotification = (timeString, drugName, dateString) => {
  const [hours, minutes] = timeString.split(':').map(Number)
  const target = new Date(dateString)
  target.setHours(hours, minutes, 0, 0)

  const now = new Date()
  if (target <= now) return

  const msUntilTarget = target - now

  setTimeout(() => {
    toast.info(`💊 ${drugName} 복약 시간입니다!`, {
      timeout: 10000,
      closeOnClick: false,
    })
  }, msUntilTarget)
}

const handleCancel = () => {
  router.back()
}

const handleSubmit = async () => {
  // 디버깅용 - 토큰 확인
  const token = localStorage.getItem('accessToken')
  const user = authStore.user
  console.log('현재 토큰:', token)
  console.log('현재 사용자:', user)

  if (!token) {
    toast.error('토큰이 없습니다. 다시 로그인해주세요.')
    return
  }

  if (!medicineName.value.trim()) {
    toast.error('약 이름을 입력해주세요')
    return
  }

  if (!medicineType.value) {
    toast.error('약 종류를 선택해주세요')
    return
  }

  if (selectedTimes.value.length === 0) {
    toast.error('복용 시간대를 최소 1개 이상 선택해주세요')
    return
  }

  if (!medDate.value) {
    toast.error('복용 날짜를 선택해주세요')
    return
  }

  const userId = authStore.user?.id
  if (!userId) {
    toast.error('사용자 정보를 찾을 수 없습니다. 다시 로그인 해주세요.')
    return
  }

  loading.value = true

  try {
    const medicationData = {
      drugName: medicineName.value.trim(),
      drugType: medicineType.value,
      timeSlot: timeSlotString.value,
      medDate: medDate.value,
      userId,
    }

    if (isEditing.value) {
      await MedicationService.updateMedication(medicationId.value, medicationData)
      toast.success('복약 일정이 수정되었습니다!')
    } else {
      await MedicationService.createMedication(medicationData)
      toast.success('복약 일정이 저장되었습니다!')

      // 알림 스케줄링
      selectedTimes.value.forEach((time) => {
        scheduleNotification(time.time, medicineName.value, medDate.value)
      })
    }

    setTimeout(() => {
      router.push('/medication/list')
    }, 2000)
  } catch (error) {
    console.error('저장/수정 실패:', error)

    // 에러 메시지 개선
    let errorMessage = '저장에 실패했습니다'
    if (error.response) {
      if (error.response.status === 401) {
        errorMessage = '인증이 만료되었습니다. 다시 로그인해주세요.'
      } else if (error.response.status === 403) {
        errorMessage = '권한이 없습니다.'
      } else if (error.response.data?.message) {
        errorMessage = error.response.data.message
      }
    }

    toast.error(errorMessage)
  } finally {
    loading.value = false // ✅ 로딩 종료
  }
}

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
.btn-primary:disabled {
  background-color: #9ca3af;
  opacity: 0.6;
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
/* .shadow-md {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
} */
@media (min-width: 768px) {
  main {
    max-width: 1280px;
  }
}
</style>
