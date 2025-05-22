<template>
  <div class="flex min-h-screen w-full">
    <!-- 좌측: 브랜드 영역 -->
    <div class="w-1/2 bg-[#C7D7CB] flex items-center justify-center p-6">
      <div class="w-full text-center px-8">
        <img
          src="/src/assets/images/logo(m).png"
          alt="Dining Bom Logo"
          class="mx-auto mb-8 mt-2 w-44 h-auto"
        />
        <h3 class="mb-4 text-2xl text-[#BAA89B]">식사와 돌봄의 따뜻한 만남</h3>
        <h3 class="text-2xl text-[#BAA89B]">가족과 함께하는 건강한 일상</h3>
      </div>
    </div>

    <!-- 우측: 로그인 폼 영역 -->
    <div class="w-1/2 bg-white flex items-center justify-center p-6">
      <div class="w-[420px] px-12 py-16 bg-white rounded-3xl shadow-lg flex flex-col items-center">
        <h1 class="mb-5 text-center text-4xl font-bold text-[#6A7D73]">로그인</h1>
        <h3 class="mb-10 text-center text-[#9E8C7F]">건강한 일상을 함께합니다</h3>

        <form @submit.prevent="handleLogin" class="w-full flex flex-col gap-8">
          <div>
            <label for="id" class="block text-[#9E8C7F] text-xl mb-3"> 아이디 </label>
            <input
              id="id"
              v-model="loginForm.id"
              type="text"
              placeholder="아이디를 입력하세요"
              class="w-full rounded-xl border border-[#B29888] bg-[#F6FAF7] px-6 py-5 text-xl focus:outline-none focus:ring-2 focus:ring-[#6A7D73]"
            />
          </div>
          <div>
            <label for="password" class="block text-[#9E8C7F] text-xl mb-3"> 비밀번호 </label>
            <input
              id="password"
              v-model="loginForm.password"
              type="password"
              placeholder="비밀번호를 입력하세요"
              class="w-full rounded-xl border border-[#B29888] bg-[#F6FAF7] px-6 py-5 text-xl focus:outline-none focus:ring-2 focus:ring-[#6A7D73]"
            />
          </div>
          <button
            type="submit"
            class="w-full py-5 rounded-xl bg-[#6A7D73] text-white text-2xl font-bold hover:bg-[#5A6B63]/90 transition-colors"
          >
            로그인
          </button>
        </form>

        <div class="mt-8 flex justify-center space-x-8 text-lg">
          <router-link to="/signup" class="text-[#9E8C7F] hover:text-[#6A7D73]">
            회원가입
          </router-link>
          <span class="text-[#B29888]"> | </span>
          <router-link to="/find-credentials" class="text-[#9E8C7F] hover:text-[#6A7D73]">
            아이디/비밀번호 찾기
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const loginForm = reactive({
  id: '',
  password: '',
})

const handleLogin = async () => {
  if (!loginForm.id || !loginForm.password) {
    toast.error('아이디와 비밀번호를 모두 입력해주세요.')
    return
  }

  const credentials = {
    loginId: loginForm.id,
    password: loginForm.password,
  }

  const result = await authStore.login(credentials)

  if (result.success) {
    toast.success('로그인되었습니다')
    router.push('/dashboard')
  } else {
    toast.error(result.message || '로그인 중 오류가 발생했습니다')
  }
}
</script>
