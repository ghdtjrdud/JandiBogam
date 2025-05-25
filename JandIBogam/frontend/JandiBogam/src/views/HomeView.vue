<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- Main Content - 헤더 너비에 맞춤 -->
    <main class="w-full max-w-[1024px] px-8 mx-auto py-10">
      <!-- Family Members Section -->
      <section class="bg-[#C7D7CB] bg-opacity-50 rounded-2xl p-6 mb-6 shadow-md">
        <div class="flex justify-between items-center mb-5">
          <h2 class="text-lg font-bold text-gray-800">가족 구성원</h2>
        </div>
        <div
          class="flex flex-row gap-x-8 pb-2 overflow-x-auto overflow-y-hidden scroll-smooth whitespace-nowrap"
        >
          <div
            v-for="(member, index) in allMembers"
            :key="member.id"
            class="flex flex-col items-center cursor-pointer transition-all duration-200 hover:scale-105 min-w-[120px] max-w-[120px]"
            @click="goToMealList(member.id)"
          >
            <div
              class="w-24 h-24 bg-white rounded-full flex items-center justify-center mb-2 shadow-family-profile"
            >
              <div class="w-20 h-20 rounded-full bg-amber-300 flex items-center justify-center">
                <span class="emoji-large">{{ getMemberEmoji(index) }}</span>
              </div>
            </div>
            <h3 class="text-base text-gray-800 font-medium">{{ member.name }}</h3>
            <p class="text-xs text-gray-600">{{ member.groupName || '' }}</p>
          </div>
          <!-- Add Member Button(더보기) -->
          <div
            class="flex flex-col items-center cursor-pointer transition-all duration-200 hover:scale-105 min-w-[120px] max-w-[120px]"
            @click="goToMyPage"
          >
            <div
              class="w-24 h-24 bg-white rounded-full flex items-center justify-center mb-2 shadow-family-profile"
            >
              <div class="w-20 h-20 rounded-full bg-white flex items-center justify-center">
                <span class="emoji-large text-green-500">+</span>
              </div>
            </div>
            <h3 class="text-base text-gray-800 font-medium">더보기</h3>
            <p class="text-xs text-gray-600"></p>
          </div>
        </div>
      </section>

      <!-- Feature Cards Section - 3열 그리드로 가로 배치 -->
      <section class="grid grid-cols-3 gap-4 mb-6">
        <!-- 식단 기록 카드 -->
        <div
          class="card bg-base-100 shadow-sm hover:shadow-md transition-all duration-300 cursor-pointer h-full"
          @click="goToPage('meal')"
        >
          <div class="card-body items-center text-center p-5">
            <div class="mb-3 text-4xl">🍽️</div>
            <h2 class="text-lg font-bold text-gray-800 mb-1">식단 기록</h2>
            <p class="text-sm text-gray-600">오늘의 식사를 기록해주세요</p>
          </div>
        </div>
        <!-- 복약 체크 카드 -->
        <div
          class="card bg-base-100 shadow-sm hover:shadow-md transition-all duration-300 cursor-pointer h-full"
          @click="goToPage('medication')"
        >
          <div class="card-body items-center text-center p-5">
            <div class="mb-3 text-4xl">💊</div>
            <h2 class="text-lg font-bold text-gray-800 mb-1">복약 체크</h2>
            <p class="text-sm text-gray-600">약 복용을 확인해주세요</p>
          </div>
        </div>
        <!-- 주간 리포트 카드 -->
        <div
          class="card bg-base-100 shadow-sm hover:shadow-md transition-all duration-300 cursor-pointer h-full"
          @click="goToPage('report')"
        >
          <div class="card-body items-center text-center p-5">
            <div class="mb-3 text-4xl">📊</div>
            <h2 class="text-lg font-bold text-gray-800 mb-1">주간 리포트</h2>
            <p class="text-sm text-gray-600">이번 주 건강 상태를 확인하세요</p>
          </div>
        </div>
      </section>

      <!-- Health Tips Section - 크기 조절 -->
      <section class="bg-brand-accent bg-opacity-30 rounded-lg p-5 shadow-sm">
        <div class="flex items-center mb-3">
          <span class="text-xl mr-2">❤️</span>
          <h2 class="text-lg font-bold text-brand-primary">건강 팁</h2>
        </div>
        <div class="space-y-3">
          <div class="flex items-start">
            <span class="text-xl mr-2 mt-1">🍎</span>
            <p class="text-sm text-gray-700">
              식사 전 물을 충분히 마시면 식욕을 조절하는 데 도움이 되며, 하루에 최소 8잔의 물을
              섭취하는 것이 좋습니다.
            </p>
          </div>
          <div class="flex items-start">
            <span class="text-xl mr-2 mt-1">⏰</span>
            <p class="text-sm text-gray-700">
              정해진 시간에 약을 복용하는 것이 약효를 높이는 데 중요하며, 규칙적인 복용 습관을
              들이는 것이 좋습니다.
            </p>
          </div>
          <div class="flex items-start">
            <span class="text-xl mr-2 mt-1">🚶</span>
            <p class="text-sm text-gray-700">
              최근 일교차가 심하므로 외출 시 체온 조절에 신경 쓰는 것이 감기 예방에 도움이 됩니다.
            </p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import UserService from '@/services/UserService'

const router = useRouter()
const allMembers = ref([])

// 내 userId 가져오기 함수
const getMyUserId = () => {
  // 첫 번째 시도: localStorage에서 'userId' 직접 가져오기
  const userIdStr = localStorage.getItem('userId')
  if (userIdStr && !isNaN(userIdStr)) {
    return Number(userIdStr)
  }

  // 두 번째 시도: localStorage에서 'user' 객체에서 id 추출
  const userObjStr = localStorage.getItem('user')
  if (userObjStr) {
    try {
      const userObj = JSON.parse(userObjStr)
      if (userObj.id && !isNaN(userObj.id)) {
        return Number(userObj.id)
      }
    } catch (e) {
      console.error('사용자 정보 파싱 실패:', e)
    }
  }

  return null
}

const getMemberEmoji = (index) => {
  const emojis = ['👵', '👴', '👩', '👨']
  return emojis[index % emojis.length]
}

// 가족 프로필 클릭 시: 해당 멤버의 식단으로 이동
const goToMealList = (memberId) => {
  console.log('가족 멤버 식단으로 이동:', memberId)
  if (!memberId) {
    alert('잘못된 사용자입니다')
    return
  }
  router.push(`/meals/${memberId}`)
}

// 메인 기능 버튼 클릭 처리
const goToPage = (page) => {
  console.log('페이지 이동:', page)

  const myUserId = Number(getMyUserId())
  console.log('현재 사용자 ID:', myUserId)

  switch (page) {
    case 'meal':
      // userId가 유효하지 않으면 로그인으로 보냄
      if (!myUserId || isNaN(myUserId) || myUserId <= 0) {
        alert('로그인 정보가 없습니다. 다시 로그인해주세요.')
        router.push('/login')
        return
      }
      console.log('식단 페이지로 이동:', `/meals/${myUserId}`)
      router.push(`/meals/${myUserId}`)
      break
    case 'medication':
      console.log('복약 페이지로 이동')
      router.push('/medication')
      break
    case 'report':
      console.log('리포트 페이지로 이동')
      router.push('/report')
      break
    default:
      console.warn('알 수 없는 페이지:', page)
      break
  }
}

const goToMyPage = () => {
  console.log('마이페이지로 이동')
  router.push('/mypage')
}

// 컴포넌트 마운트 시 가족 멤버 목록 로드
onMounted(async () => {
  console.log('컴포넌트 마운트됨')
  const myUserId = getMyUserId()
  console.log('내 userId:', myUserId)

  try {
    const { data } = await UserService.getAllGroupMembersOfMine()
    console.log('가족 멤버 데이터:', data)

    // 내 자신은 가족 목록에서 제외
    allMembers.value = data.filter((member) => member.id !== myUserId)
    console.log('필터링된 가족 멤버:', allMembers.value)
  } catch (e) {
    console.error('멤버 조회 실패:', e)
    // 에러 발생 시 빈 배열로 설정
    allMembers.value = []
  }
})
</script>

<style scoped>
.emoji-large {
  font-size: 1.5rem;
  line-height: 1;
}
.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
.shadow-family-profile {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.bg-brand-accent {
  background-color: #c7d7cb;
}
.bg-brand-lightbg {
  background-color: #f6faf7;
}
.text-brand-primary {
  color: #6a7d73;
}
.hover\:scale-105:hover {
  transform: scale(1.05);
  transition: transform 0.2s ease-in-out;
}
.hover\:shadow-md:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}
.card-body {
  padding: 1.25rem;
}
.bg-base-100 {
  background-color: #ffffff;
}
.card {
  border-radius: 0.5rem;
  background-color: #ffffff;
  box-shadow:
    0 1px 3px 0 rgba(0, 0, 0, 0.1),
    0 1px 2px 0 rgba(0, 0, 0, 0.06);
}

@media (min-width: 768px) {
  main {
    max-width: 1280px;
  }
}
@media (max-width: 767px) {
  .grid-cols-3 {
    grid-template-columns: 1fr;
  }
}
::-webkit-scrollbar {
  height: 8px;
}
::-webkit-scrollbar-thumb {
  background: #b29888;
  border-radius: 8px;
}
</style>
