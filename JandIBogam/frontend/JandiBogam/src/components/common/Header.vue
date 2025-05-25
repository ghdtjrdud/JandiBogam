<template>
  <header
    class="bg-white border-b border-gray-200 shadow"
    style="box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1)"
  >
    <div class="max-w-full px-8 py-3 mx-auto flex justify-between items-center">
      <!-- 로고 -->
      <router-link to="/dashboard" class="flex items-center">
        <img src="/src/assets/images/logo.png" alt="Dining Bom Logo" class="h-2 w-auto" />
      </router-link>

      <!-- 네비게이션 -->
      <nav class="flex items-center" style="gap: 2.5rem">
        <!-- 홈 -->
        <!-- 홈 -->
        <router-link
          to="/dashboard"
          :class="[
            'px-5 py-2 rounded-full transition-colors duration-200 text-lg',
            $route.path === '/dashboard'
              ? 'bg-[#e7f1e9] text-[#4CAF50] font-medium relative'
              : 'text-gray-600 hover:text-[#4CAF50]',
          ]"
        >
          홈
          <span
            v-if="$route.path === '/dashboard'"
            class="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#4CAF50]"
          ></span>
          <span
            v-if="$route.path === '/dashboard'"
            class="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#4CAF50]"
          ></span>
        </router-link>

        <!-- 식단 관리 -->
        <router-link
          to="/meals"
          :class="[
            'px-5 py-2 rounded-full transition-colors duration-200 text-lg',
            $route.path.startsWith('/meals')
              ? 'bg-[#e7f1e9] text-[#4CAF50] font-medium relative'
              : 'text-gray-600 hover:text-[#4CAF50]',
          ]"
        >
          식단 관리
          <span
            v-if="$route.path.startsWith('/meals')"
            class="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#4CAF50]"
          ></span>
        </router-link>

        <!-- 복약 관리 -->
        <router-link
          to="/medication/list"
          :class="[
            'px-5 py-2 rounded-full transition-colors duration-200 text-lg',
            $route.path.startsWith('/medication')
              ? 'text-[#4CAF50] font-medium'
              : 'text-gray-600 hover:text-[#4CAF50]',
          ]"
        >
          복약 관리
          <span
            v-if="$route.path.startsWith('/medication')"
            class="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#4CAF50]"
          ></span>
        </router-link>

        <!-- 건강 리포트 -->
        <router-link
          to="/report"
          :class="[
            'px-5 py-2 rounded-full transition-colors duration-200 text-lg',
            $route.path.startsWith('/report')
              ? 'bg-[#e7f1e9] text-[#4CAF50] font-medium relative'
              : 'text-gray-600 hover:text-[#4CAF50]',
          ]"
        >
          건강 리포트
          <span
            v-if="$route.path.startsWith('/report')"
            class="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#4CAF50]"
          ></span>
        </router-link>

        <!-- 마이페이지 -->
        <router-link
          to="/mypage"
          :class="[
            'px-5 py-2 rounded-full transition-colors duration-200 text-lg',
            $route.path.startsWith('/mypage')
              ? 'bg-[#e7f1e9] text-[#4CAF50] font-medium relative'
              : 'text-gray-600 hover:text-[#4CAF50]',
          ]"
        >
          마이페이지
          <span
            v-if="$route.path.startsWith('/mypage')"
            class="absolute bottom-[-4px] left-0 w-full h-0.5 bg-[#4CAF50]"
          ></span>
        </router-link>

        <!-- 사용자 정보 및 로그아웃 -->
        <div class="flex items-center gap-6 ml-4">
          <span class="text-gray-700 font-bold text-lg">{{ userName }} 님</span>
          <button
            @click="logout"
            class="text-sm text-white bg-red-500 hover:bg-red-600 px-4 py-2 rounded-full shadow transition duration-200"
          >
            로그아웃
          </button>
        </div>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const toast = useToast()

// 인증 상태 및 사용자 정보
const isAuthenticated = computed(() => authStore.isAuthenticated)
const userName = computed(() => authStore.user?.name || '김싸피')

// 로그아웃 처리
const logout = async () => {
  try {
    await authStore.logout()
    toast.success('로그아웃 되었습니다')
    router.push('/login')
  } catch (err) {
    console.error('로그아웃 실패:', err)
  }
}
</script>

<style scoped>
/* 헤더 입체감 있는 경계선 */
header {
  border-bottom: 1px solid #e5e7eb;
  box-shadow:
    0 2px 4px rgba(0, 0, 0, 0.08),
    0 1px 0 rgba(255, 255, 255, 0.8) inset;
}

/* 네비게이션 간격 */
/* 네비게이션 간격 */
nav {
  gap: 2.5rem !important;
}

/* 글씨 크기 증가 */
.text-lg {
  font-size: 1.125rem;
}

/* 홈 버튼 패딩 조정 */
.px-5.py-2 {
  padding: 0.6rem 1.5rem;
}
</style>
