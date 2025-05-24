<template>
  <div class="min-h-screen bg-brand-lightbg">
    <!-- Main Content - 헤더 너비에 맞춤 -->
    <main class="w-full max-w-[1024px] px-8 mx-auto py-10">
      <!-- Family Members Section -->
      <section class="bg-[#C7D7CB] bg-opacity-50 rounded-2xl p-6 mb-6 shadow-md">
        <div class="flex justify-between items-center mb-5">
          <p></p>
        </div>

        <!-- 가족 구성원 나열 -->
        <div class="flex justify-center flex-wrap gap-x-12 gap-y-8 pb-2">
          <!-- Family Member Profiles -->
          <div
            v-for="(member, index) in familyMembers"
            :key="index"
            class="flex flex-col items-center cursor-pointer transition-all duration-200 hover:scale-105"
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
            <p class="text-xs text-gray-600">{{ member.relation }}</p>
          </div>

          <!-- Add Member Button -->
          <div
            class="flex flex-col items-center cursor-pointer transition-all duration-200 hover:scale-105"
          >
            <div
              class="w-24 h-24 bg-white rounded-full flex items-center justify-center mb-2 shadow-family-profile"
            >
              <div class="w-20 h-20 rounded-full bg-white flex items-center justify-center">
                <span class="emoji-large text-green-500">+</span>
              </div>
            </div>
            <h3 class="text-base text-gray-800 font-medium">가족 추가</h3>
            <p class="text-xs text-gray-600">연결하기</p>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Sample family members data
const familyMembers = ref([
  {
    id: 1,
    name: '김영희',
    relation: '가족',
  },
  {
    id: 2,
    name: '김철수',
    relation: '가족',
  },
  {
    id: 3,
    name: '김미영',
    relation: '가족',
  },
  {
    id: 4,
    name: '김영수',
    relation: '친구',
  },
])

// 이모지 매핑 함수
const getMemberEmoji = (index) => {
  const emojis = ['👵', '👴', '👩', '👨']
  return emojis[index % emojis.length]
}

// 가족 구성원 클릭 시 그들의 식단 기록 조회페이지로 넘어가
const goToMealList = (memberId) => {
  router.push(`/meals/${memberId}`)
}

//로그인한 사용자 기준: 식단 기록, 복약 체크, 주간 리포트 페이지로 이동
const goToPage = (page) => {
  switch (page) {
    case `meal`:
      router.push('/meals')
      break
    case 'medication':
      router.push('/medication')
      break
    case 'report':
      router.push('/report')
      break
    default:
      break
  }
}
</script>

<style scoped>
/* 이모지 크기 키우기 */
.emoji-large {
  font-size: 1.5rem;
  line-height: 1;
}

/* 가족 구성원 카드 간격 조정 */
.gap-x-12 {
  column-gap: 3rem; /* 48px */
}

.gap-y-8 {
  row-gap: 2rem; /* 32px */
}

/* 프로필 그림자 */
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

/* 카드 호버 효과 */
.hover\:scale-105:hover {
  transform: scale(1.05);
  transition: transform 0.2s ease-in-out;
}
.hover\:shadow-md:hover {
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* 트랜지션 효과 */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

/* 카드 내 요소 간격 조정 */
.card-body {
  padding: 1.25rem;
}

/* 전체 레이아웃이 헤더 너비에 맞도록 설정 */
@media (min-width: 768px) {
  main {
    max-width: 1280px; /* 최대 너비 설정 */
  }
}

/* 작은 화면에서 카드 레이아웃 조정 */
@media (max-width: 767px) {
  .grid-cols-3 {
    grid-template-columns: 1fr;
  }
}
</style>
