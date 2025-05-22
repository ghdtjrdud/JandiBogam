<script setup>
import { ref, watch, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import UserService from '@/services/UserService'

const authStore = useAuthStore()

// console.log('▶ authStore.user:', authStore.user)

// 내 정보
const user = ref(null)
const loadingUser = ref(false)
const errorUser = ref(null)

// 그룹 정보
const groups = ref([])
const loadingGroups = ref(false)
const errorGroups = ref(null)

// 유저 아이디는 authStore.user가 세팅된 후에만 값이 생깁니다
const userId = computed(() => authStore.user?.id)

// —————————————————————————————————————————
// 1) 내 정보 가져오기
async function fetchMyInfo(id) {
  // console.log('▶ fetchMyInfo 호출, id =', id)

  loadingUser.value = true
  errorUser.value = null
  try {
    const { data } = await UserService.getMyInfo(id)
    user.value = data
  } catch {
    errorUser.value = '내 정보를 불러오지 못했습니다.'
  } finally {
    loadingUser.value = false
  }
}

// 2) 그룹 목록 가져오기
async function fetchMyGroups() {
  loadingGroups.value = true
  errorGroups.value = null
  try {
    const { data } = await UserService.getMyGroups()
    groups.value = Array.isArray(data) ? data : [data]
  } catch {
    errorGroups.value = '그룹 정보를 불러오지 못했습니다.'
  } finally {
    loadingGroups.value = false
  }
}

// —————————————————————————————————————————
// authStore.user가 바뀌어서 userId가 생기면(=로그인 복원 or 로그인 직후)
// 자동으로 두 API를 호출하도록 watch 설정
watch(
  () => userId.value,
  (id) => {
    console.log('watch 감지됨! userId:', id)

    if (id) {
      console.log('✅ fetchMyInfo 호출 준비 완료')

      fetchMyInfo(id)
      fetchMyGroups()
    } else {
      console.warn('⚠️ userId가 undefined 또는 falsy 상태입니다')
    }
  },
  { immediate: true }, // 컴포넌트 마운트 시에도 실행
)

// —————————————————————————————————————————
// computed 텍스트
const genderText = computed(() => {
  if (!user.value) return ''
  return user.value.gender === 'F' ? '여성' : '남성'
})
const illnessText = computed(() => {
  if (!user.value) return ''
  const list = []
  if (user.value.diabetes) list.push('당뇨')
  if (user.value.hypertension) list.push('고혈압')
  if (user.value.heartDisease) list.push('심장질환')
  if (user.value.kidneyDisease) list.push('신장질환')
  if (user.value.liverDisease) list.push('간질환')
  return list.length ? list.join(', ') : '없음'
})
const birthDateText = computed(() => {
  // 필드명 맞춰서 사용 (birth_date 또는 birthDate)
  const raw = user.value?.birthDate || user.value?.birthDate
  if (!raw) return ''
  const date = new Date(raw)
  if (isNaN(date)) return raw // 날짜 파싱 실패 시 원본 출력
  return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`
})

const joinedDateText = computed(() => {
  const raw = user.value?.createdAt || user.value?.createdAt
  if (!raw) return ''
  const date = new Date(raw)
  if (isNaN(date)) return raw
  return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`
})
</script>

<template>
  <div class="container mx-auto py-12 max-w-6xl">
    <!-- 사용자 프로필 정보 섹션 -->
    <div class="grid grid-cols-4 gap-8 mb-8">
      <!-- 왼쪽 : 개인정보 카드 -->
      <div class="col-span-2 bg-white rounded-lg p-8 border border-[#B29888] shadow">
        <h2 class="text-xl font-bold mb-6 text-[#6A7D73]">개인 정보</h2>

        <!-- ✏️ 변수명 수정 -->
        <div v-if="loadingUser">불러오는 중...</div>
        <div v-else-if="errorUser" class="text-red-500">{{ errorUser }}</div>
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
            <div>{{ birthDateText }}</div>
          </div>
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">기저질환</div>
            <div>{{ illnessText }}</div>
          </div>
          <div class="flex">
            <div class="w-24 text-[#9E8C7F]">가입일</div>
            <div>{{ joinedDateText }}</div>
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

        <div v-if="loadingGroups">그룹 불러오는 중...</div>
        <div v-else-if="errorGroups" class="text-red-500">{{ errorGroups }}</div>
        <div v-else>
          <div v-if="groups.length === 0" class="text-[#9E8C7F]">가입된 그룹이 없습니다.</div>
          <ul v-else class="space-y-4">
            <li
              v-for="group in groups"
              :key="group.id"
              class="flex items-center justify-between bg-[#F6FAF7] p-4 rounded-lg border border-[#C7D7CB]"
            >
              <div>
                <div class="text-[#6A7D73] font-medium">{{ group.name }}</div>
                <div class="text-xs text-[#9E8C7F]">코드: {{ group.code }}</div>
              </div>
              <button
                @click="fetchMyGroups"
                class="text-xs bg-[#C7D7CB] text-[#6A7D73] px-3 py-1 rounded hover:bg-[#5A6B63] hover:text-white transition-colors"
              >
                탈퇴
              </button>
            </li>
          </ul>
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
