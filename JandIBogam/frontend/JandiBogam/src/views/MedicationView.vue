<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-full w-full mx-auto px-8 py-6" style="max-width: calc(100% - 32px)">
      <div class="text-center mb-8">
        <h1 class="text-2xl font-bold text-gray-800 mb-2">복약 관리</h1>
        <p class="text-gray-600">내 복약 기록을 확인하고 관리하세요</p>
      </div>

      <div class="max-w-4xl mx-auto">
        <!-- 상단 버튼 영역 -->
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-semibold text-gray-700">복약 기록</h2>
          <button
            @click="goToAddMedication"
            class="btn btn-primary bg-brand-primary hover:bg-brand-hover border-none px-4"
          >
            + 복약 추가
          </button>
        </div>

        <!-- 복약 목록 -->
        <div v-if="medications.length > 0" class="space-y-4">
          <div
            v-for="medication in medications"
            :key="medication.id"
            class="bg-white rounded-xl shadow-sm border p-5 flex items-center justify-between"
          >
            <!-- 약 정보 -->
            <div class="flex items-center space-x-4">
              <div
                class="w-12 h-12 rounded-full flex items-center justify-center bg-brand-accent bg-opacity-30"
              >
                <span class="text-xl">💊</span>
              </div>
              <div>
                <h3 class="font-medium text-lg text-gray-800">{{ medication.drugName }}</h3>
                <div class="flex items-center text-sm text-gray-600 mt-1">
                  <span
                    class="bg-brand-accent bg-opacity-20 text-brand-primary px-2 py-1 rounded-md mr-2"
                  >
                    {{ medication.drugType }}
                  </span>
                  <span>복용 시간: {{ formatTimeSlots(medication.timeSlot) }}</span>
                </div>
              </div>
            </div>

            <!-- 버튼 영역 -->
            <div class="flex gap-2">
              <button
                @click="editMedication(medication)"
                class="btn btn-outline btn-sm border-brand-border text-brand-secondary hover:bg-brand-secondary hover:text-white"
              >
                수정하기
              </button>
              <button @click="confirmDelete(medication.id)" class="btn btn-error btn-sm text-white">
                삭제
              </button>
            </div>
          </div>
        </div>

        <!-- 비어있는 상태 -->
        <div v-else class="bg-white rounded-xl shadow-sm border p-8 text-center">
          <div class="flex flex-col items-center">
            <div
              class="w-20 h-20 rounded-full bg-brand-accent bg-opacity-20 flex items-center justify-center mb-4"
            >
              <span class="text-4xl">💊</span>
            </div>
            <p class="text-gray-500 mb-4">등록된 복약 정보가 없습니다.</p>
            <button
              @click="goToAddMedication"
              class="btn btn-primary bg-brand-primary hover:bg-brand-hover border-none"
            >
              복약 정보 추가하기
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useToast } from 'vue-toastification'

const router = useRouter()
const toast = useToast()
const medications = ref([])

// 복약 기록 가져오기
const fetchMedications = async () => {
  try {
    // 실제로는 로그인된 사용자 ID를 가져와야 함
    const userId = 1 // 임시 사용자 ID, 실제로는 auth store 등에서 가져옴
    const response = await axios.get(`/api/medications/users/${userId}`)
    medications.value = response.data
  } catch (error) {
    toast.error('복약 정보를 불러오는데 실패했습니다: ' + error.message)
  }
}

// 시간대 표시 형식 변환
const formatTimeSlots = (timeSlot) => {
  if (!timeSlot) return '시간 정보 없음'

  // '08:00,12:00,22:00' → '아침(08:00), 점심(12:00), 취침 전(22:00)'으로 변환
  const timeMap = {
    '08:00': '아침',
    '12:00': '점심',
    '22:00': '취침 전',
  }

  return timeSlot
    .split(',')
    .map((time) => `${timeMap[time] || time}`)
    .join(', ')
}

// 복약 추가 페이지로 이동
const goToAddMedication = () => {
  router.push('/medication')
}

// 복약 수정
const editMedication = (medication) => {
  router.push(`/medication/edit/${medication.id}`)
}

// 삭제 확인 다이얼로그
const confirmDelete = (id) => {
  if (confirm('이 복약 정보를 삭제하시겠습니까?')) {
    deleteMedication(id)
  }
}

// 복약 정보 삭제
const deleteMedication = async (id) => {
  try {
    await axios.delete(`/api/medications/${id}`)
    toast.success('복약 정보가 삭제되었습니다.')
    // 목록 새로고침
    fetchMedications()
  } catch (error) {
    toast.error('삭제에 실패했습니다: ' + error.message)
  }
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchMedications()
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
.btn-error {
  background-color: #ff6b6b;
}
.btn-error:hover {
  background-color: #ff5252;
}

@media (min-width: 768px) {
  main {
    max-width: 1280px;
  }
}
</style>
