<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-3xl mx-auto px-4 py-8">
      <!-- 상단 헤더 -->
      <div class="flex items-center justify-between mb-8">
        <div class="flex items-center space-x-2">
          <router-link to="/mypage" class="text-sm text-[#6A7D73] font-medium hover:underline">
            &larr; 마이페이지로 돌아가기
          </router-link>
          <span class="text-xl font-bold text-[#6A7D73] ml-4"> {{ group.name }} 그룹 관리 </span>
          <span
            class="ml-4 px-3 py-1 rounded font-mono font-bold text-[#6A7D73] bg-[#F6FAF7] border border-[#C7D7CB]"
          >
            그룹 코드: <span class="text-[#51C17C]">{{ group.code }}</span>
          </span>
        </div>
        <button
          @click="copyGroupCode"
          class="bg-[#F6FAF7] border border-[#C7D7CB] text-[#6A7D73] text-sm rounded px-3 py-1 hover:bg-[#C7D7CB] hover:text-white transition"
        >
          그룹 코드 복사
        </button>
      </div>

      <!-- 그룹 정보 카드 -->
      <div class="bg-white rounded-lg shadow border border-[#B29888] mb-8 p-8">
        <div class="mb-4 font-bold text-[#6A7D73] text-lg">그룹 정보</div>
        <div class="grid grid-cols-2 gap-y-2 gap-x-8 text-[#6A7D73]">
          <div class="font-medium">그룹명</div>
          <div>{{ group.name }}</div>
          <div class="font-medium">생성일</div>
          <div>{{ formatDate(group.createdAt) }}</div>
          <div class="font-medium">구성원 수</div>
          <div>{{ members.length }}명</div>
          <div class="font-medium">그룹 코드</div>
          <div class="font-mono text-[#51C17C] font-bold tracking-wider">{{ group.code }}</div>
        </div>
      </div>

      <!-- 그룹원 리스트 카드 -->
      <div class="bg-[#F6FAF7] rounded-lg shadow border border-[#C7D7CB] p-8">
        <div class="flex items-center justify-between mb-4">
          <div class="font-bold text-[#6A7D73] text-lg">연결된 그룹 구성원</div>
          <div class="text-[#9E8C7F] font-medium text-sm">{{ members.length }}명</div>
        </div>
        <ul>
          <li
            v-for="member in members"
            :key="member.id"
            class="flex items-center justify-between py-4 border-b last:border-0 border-[#E8E4DF]"
          >
            <div class="flex items-center space-x-4">
              <!-- 아바타 -->
              <div
                class="w-10 h-10 rounded-full bg-[#C7D7CB] flex items-center justify-center text-[#6A7D73] text-2xl font-bold"
              >
                {{ member.name.charAt(0) }}
              </div>
              <div>
                <div class="font-medium text-[#6A7D73]">{{ member.name }}</div>
                <div class="text-xs text-[#9E8C7F]">{{ member.email }}</div>
                <div class="text-xs text-[#B29888]">{{ formatDate(member.joinedAt) }} 가입</div>
              </div>
            </div>
            <button
              v-if="member.id === userId"
              @click="handleLeaveGroup"
              class="bg-[#F8DEDE] text-[#D85C5C] px-4 py-2 rounded text-sm hover:bg-[#D85C5C] hover:text-white border border-[#D85C5C] transition"
            >
              그룹 탈퇴
            </button>
          </li>
        </ul>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import UserService from '@/services/UserService' // 그룹, 멤버 API 작성해둔 곳
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const authStore = useAuthStore()

const userId = computed(() => authStore.user?.id)
const groupId = route.params.id

// 그룹, 멤버 데이터 예시 (API 연동 부분 교체)
const group = ref({})
const members = ref([])

// 그룹 상세정보 조회, 그룹 멤버 목록 조회
onMounted(async () => {
  try {
    const { data: groupData } = await UserService.getGroupDetail(groupId)
    group.value = groupData

    // console.log('groupData:', groupData)

    const { data: memberList } = await UserService.getGroupMembers(groupId)
    members.value = memberList
  } catch (e) {
    toast.error('그룹 정보/멤버 조회 실패: ' + (e?.response?.data || e.message))
  }
})

function copyGroupCode() {
  navigator.clipboard
    .writeText(group.value.code)
    .then(() => toast.success('그룹 코드가 복사되었습니다!'))
    .catch(() => toast.error('복사에 실패했습니다.'))
}

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  if (isNaN(d)) return date
  return `${d.getFullYear()}년 ${String(d.getMonth() + 1).padStart(2, '0')}월 ${String(d.getDate()).padStart(2, '0')}일`
}

async function handleLeaveGroup() {
  if (!confirm('정말로 그룹에서 탈퇴하시겠습니까?')) return
  try {
    // group.value.id 또는 groupId가 제대로 된 값인지 콘솔로 확인
    // console.log('탈퇴 요청 group id:', group.value.id)
    await UserService.leaveGroup(group.value.id) // 이게 0이면 안 됨!
    toast.success('그룹에서 탈퇴되었습니다.')
    router.push('/mypage')
  } catch (e) {
    toast.error('탈퇴 실패: ' + (e?.response?.data || e.message))
  }
}
</script>

<style scoped>
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
