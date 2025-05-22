<!-- 로그인 하기전에는 헤더가 안보이게 -->
<template>
  <div>
    <router-view v-if="isAuthPage" />
    <div v-else class="min-h-screen w-full">
      <Header />
      <!-- container 클래스 제거하고 각 페이지에서 개별 설정하도록 변경 -->
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import Header from './components/common/Header.vue'

const route = useRoute() // 반응형
const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const isAuthPage = computed(() => {
  return ['/login', '/signup', 'find-credentials'].includes(route.path)
})

const loginForm = reactive({
  loginId: '', // 백엔드 요구사항에 맞춰 loginId로 변경
  password: '',
})

const handleLogin = async () => {
  // 폼 유효성 검사
  if (!loginForm.loginId || !loginForm.password) {
    toast.error('아이디와 비밀번호를 모두 입력해주세요.')
    return
  }

  try {
    // loginId를 사용하도록 수정
    const success = await authStore.login({
      loginId: loginForm.loginId,
      password: loginForm.password,
    })

    if (success) {
      toast.success('로그인되었습니다')
      router.push('/dashboard')
    }
  } catch (err) {
    console.error('로그인 오류:', err)
    toast.error('로그인 중 오류가 발생했습니다')
  }
}
</script>

<style>
/* 전역 스타일 */
body {
  margin: 0;
  padding: 0;
  font-family: 'Noto Sans KR', sans-serif;
  overflow-x: hidden;
}

#app {
  overflow-x: hidden;
}

/* 모든 container 클래스의 제약 제거 */
.container {
  max-width: 100% !important;
  width: 100% !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
}
</style>
