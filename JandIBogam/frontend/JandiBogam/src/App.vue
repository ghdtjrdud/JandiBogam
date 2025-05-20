<script setup>
<<<<<<< HEAD
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
=======
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const loginForm = reactive({
  loginId: '', // 백엔드 요구사항에 맞춰 loginId로 변경
  password: '',
})
>>>>>>> df20e65a94fa80dc6f56dca8f19a934932eb2100

const handleLogin = async () => {
  // 폼 유효성 검사
  if (!loginForm.loginId || !loginForm.password) {
<<<<<<< HEAD
    toast.error('아이디와 비밀번호를 모두 입력해주세요.');
    return;
=======
    toast.error('아이디와 비밀번호를 모두 입력해주세요.')
    return
>>>>>>> df20e65a94fa80dc6f56dca8f19a934932eb2100
  }

  try {
    // loginId를 사용하도록 수정
    const success = await authStore.login({
      loginId: loginForm.loginId,
<<<<<<< HEAD
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
=======
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
>>>>>>> df20e65a94fa80dc6f56dca8f19a934932eb2100
</script>

<template>
  <RouterView />
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

/* .logo, HelloWorld 관련 스타일 삭제 */

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  /* .logo 관련 스타일 삭제 */

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;
    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
