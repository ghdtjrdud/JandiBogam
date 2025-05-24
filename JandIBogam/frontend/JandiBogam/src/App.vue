<template>
  <div>
    <router-view v-if="isAuthPage" />
    <BaseLayout v-else>
      <router-view />
    </BaseLayout>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import BaseLayout from './components/common/BaseLayout.vue'
import { onMounted } from 'vue'

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

onMounted(()=>{
  document.documentElement.setAttribute('data-theme','diningbom')
})
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: 'Noto Sans KR', sans-serif;
  overflow-x: hidden;
}

#app {
  overflow-x: hidden;
}

/* container 관련 스타일 제거하거나 특정 클래스로 변경 */
.full-width-container {
  max-width: 100%;
  width: 100%;
  padding: 0;
}
</style>
