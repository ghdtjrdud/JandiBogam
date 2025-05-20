<script setup>
import { ref, onMounted, computed } from 'vue'
import userService from '@/services/user.service'

const user = ref(null)
const loading = ref(false)
const error = ref(null)
const groupCode = ref('')

const userId = 2

const fetchMyInfo = async () => {
  loading.value = true
  try {
    const response = await userService.getMyInfo(userId)
    user.value = response.data
  } catch (e) {
    error.value = '내 정보를 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const genderText = computed(() => {
  if (!user.value) return ''
  return user.value.gender === 'F' ? '여성' : '남성'
})

const illnessText = computed(() => {
  if (!user.value) return ''
  const illnessList = []
  if (user.value.diabetes) illnessList.push('당뇨')
  if (user.value.hypertension) illnessList.push('고혈압')
  if (user.value.heart_disease) illnessList.push('심장질환')
  if (user.value.kidney_disease) illnessList.push('신장질환')
  if (user.value.liver_disease) illnessList.push('간질환')
  return illnessList.length ? illnessList.join(', ') : '없음'
})

onMounted(fetchMyInfo)
</script>

<template>
  <div class="container mx-auto py-12 max-w-6xl">
    <!-- 사용자 프로필 정보 섹션 -->
    <div class="grid grid-cols-4 gap-8 mb-8">
      <!-- 왼쪽 : 개인정보 카드 -->
      <div class="col-span-2 bg-white rounded-lg p-8 border border-[#B29888] shadow">
        <h2 class="text-xl font-bold mb-6 text-[#6A7D73]">개인 정보</h2>
        <div v-if="loading">불러오는 중...</div>
        <div v-else-if="error">{{ error }}</div>
        <div v-else-if="user" class="space-y-4">
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">이름</div>
            <div>{{ user.name }}</div>
          </div>
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">성별</div>
            <div>{{ genderText }}</div>
          </div>
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">생년월일</div>
            <div>{{ user.birth_date }}</div>
          </div>
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">기저질환</div>
            <div>{{ illnessText }}</div>
          </div>
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">가입일</div>
            <div>{{ user.created_at }}</div>
          </div>
          <div class="mt-6">
            <button
              class="bg-[#C7D7CB] text-[#6A7D73] px-4 py-2 rounded-md text-sm hover:bg-[#5A6B63] hover:text-white transition-colors"
            >
              프로필 수정
            </button>
          </div>
        </div>
      </div>

      <!-- 오른쪽 : 그룹 관리 카드 -->
      <div class="col-span-2 bg-white rounded-lg p-8 border border-[#B29888] shadow">
        <h2 class="text-xl font-bold mb-6 text-[#6A7D73]">그룹 관리</h2>

        <!-- 그룹 코드 -->
        <div class="mb-6">
          <div class="flex justify-between items-center mb-2">
            <div class="text-[#9E8C7F]">그룹 코드</div>
            <div class="flex items-center">
              <span class="text-[#6A7D73] font-medium mr-2">FAM1234</span>
              <span
                class="bg-[#C7D7CB] text-[#6A7D73] text-xs px-2 py-1 rounded cursor-pointer hover:bg-[#6A7D73] hover:text-white transition-colors"
                >복사</span
              >
            </div>
          </div>
          <button
            class="w-full bg-[#6A7D73] text-white py-2 rounded-md text-sm mb-6 hover:bg-[#5A6B63] transition-colors"
          >
            본가 그룹 코드 설정하기
          </button>
        </div>

        <!-- 그룹 코드 등록 -->
        <div class="mb-8">
          <div class="text-[#9E8C7F] mb-2">그룹 코드 등록</div>
          <div class="flex">
            <input
              v-model="groupCode"
              type="text"
              class="flex-1 border border-[#B29888] rounded-l-md px-3 py-2 text-sm focus:outline-none focus:border-[#6A7D73]"
              placeholder="그룹 코드를 입력하세요"
            />
            <button
              class="bg-[#6A7D73] text-white px-4 py-2 rounded-r-md text-sm hover:bg-[#5A6B63] transition-colors"
            >
              등록
            </button>
          </div>
        </div>

        <!-- 가족 목록 -->
        <div class="mb-6">
          <div class="flex items-center mb-4">
            <div class="mr-4">
              <div class="bg-[#C7D7CB] p-2 rounded-md">
                <span class="text-[#6A7D73] text-xl">👨‍👩‍👧</span>
              </div>
            </div>
            <div class="flex-1">
              <div class="text-[#6A7D73] font-medium">가족</div>
              <div class="text-[#9E8C7F] text-sm">구성원 3명</div>
            </div>
            <button
              class="bg-[#F6FAF7] text-[#6A7D73] px-3 py-1 rounded-md text-sm hover:bg-[#C7D7CB] transition-colors"
            >
              상세 관리
            </button>
          </div>

          <!-- 의료진 목록 -->
          <div class="flex items-center mb-4">
            <div class="mr-4">
              <div class="bg-[#C7D7CB] p-2 rounded-md">
                <span class="text-[#6A7D73] text-xl">💊</span>
              </div>
            </div>
            <div class="flex-1">
              <div class="text-[#6A7D73] font-medium">의료진</div>
              <div class="text-[#9E8C7F] text-sm">구성원 2명</div>
            </div>
            <button
              class="bg-[#F6FAF7] text-[#6A7D73] px-3 py-1 rounded-md text-sm hover:bg-[#C7D7CB] transition-colors"
            >
              상세 관리
            </button>
          </div>

          <!-- 친구들 목록 -->
          <div class="flex items-center">
            <div class="mr-4">
              <div class="bg-[#C7D7CB] p-2 rounded-md">
                <span class="text-[#6A7D73] text-xl">👥</span>
              </div>
            </div>
            <div class="flex-1">
              <div class="text-[#6A7D73] font-medium">친구들</div>
              <div class="text-[#9E8C7F] text-sm">구성원 4명</div>
            </div>
            <button
              class="bg-[#F6FAF7] text-[#6A7D73] px-3 py-1 rounded-md text-sm hover:bg-[#C7D7CB] transition-colors"
            >
              상세 관리
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 테마 설정 -->
    <div class="mt-8">
      <div class="bg-white rounded-lg p-6 border border-[#B29888]">
        <h2 class="text-lg font-medium mb-6 text-[#6A7D73]">테마 설정</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- 고령자 모드 -->
          <div
            class="border border-[#C7D7CB] rounded-lg p-4 bg-[#F6FAF7] flex items-center justify-center flex-col cursor-pointer hover:shadow-md transition-shadow"
          >
            <div class="text-[#6A7D73] text-3xl mb-2">😊</div>
            <div class="font-medium text-[#6A7D73] mb-1">고령자 모드</div>
            <div class="text-xs text-[#9E8C7F] text-center mb-2">
              큰 글씨와 간편한 인터페이스로 편하게 사용하실 수 있습니다
            </div>
          </div>

          <!-- 일반 모드 -->
          <div
            class="border border-[#B29888] rounded-lg p-4 flex items-center justify-center flex-col cursor-pointer hover:shadow-md transition-shadow"
          >
            <div class="text-[#9E8C7F] text-3xl mb-2">👤</div>
            <div class="font-medium text-[#6A7D73] mb-1">일반 모드</div>
            <div class="text-xs text-[#9E8C7F] text-center mb-2">
              표준 글씨와 기능을 사용할 수 있습니다
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
