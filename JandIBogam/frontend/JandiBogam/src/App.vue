<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useToast } from 'vue-toastification';

const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();

const loginForm = reactive({
  loginId: '', // 백엔드 요구사항에 맞춰 loginId로 변경
  password: ''
});

const handleLogin = async () => {
  // 폼 유효성 검사
  if (!loginForm.loginId || !loginForm.password) {
    toast.error('아이디와 비밀번호를 모두 입력해주세요.');
    return;
  }

  try {
    // loginId를 사용하도록 수정
    const success = await authStore.login({
      loginId: loginForm.loginId,
      password: loginForm.password
    });

    if (success) {
      toast.success('로그인되었습니다');
      router.push('/dashboard');
    }
  } catch (err) {
    console.error('로그인 오류:', err);
    toast.error('로그인 중 오류가 발생했습니다');
  }
};
</script>

<template>
  <RouterView />
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}

nav a {
  color: var(--color-text);
  text-decoration: none;
}

nav a.router-link-active {
  font-weight: bold;
  color: #4f46e5;
}
</style>
