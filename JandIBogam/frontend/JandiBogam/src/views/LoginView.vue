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
            <label for="id" class="block text-[#9E8C7F] text-xl mb-3">
              아이디
            </label>
            <input
              id="id"
              v-model="loginForm.id"
              type="text"
              placeholder="아이디를 입력하세요"
              class="w-full rounded-xl border border-[#B29888] bg-[#F6FAF7] px-6 py-5 text-xl focus:outline-none focus:ring-2 focus:ring-[#6A7D73]"
            />
          </div>
          <div>
            <label for="password" class="block text-[#9E8C7F] text-xl mb-3">
              비밀번호
            </label>
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
          <span class="text-[#B29888]">  |  </span>
          <router-link to="/find-credentials" class="text-[#9E8C7F] hover:text-[#6A7D73]">
            아이디/비밀번호 찾기
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { authAPI } from '@/services/api';

const router = useRouter();
const isLoading = ref(false);
const error = ref('');

const loginForm = reactive({
  id: '',
  password: ''
});

const handleLogin = async () => {
  // 폼 유효성 검사
  if (!loginForm.id || !loginForm.password) {
    error.value = '아이디와 비밀번호를 모두 입력해주세요.';
    return;
  }

  try {
    isLoading.value = true;
    error.value = '';

    // 백엔드 요구 형식에 맞게 데이터 준비
    // 백엔드 API가 username을 사용하는 경우 다음과 같이 수정
    const credentials = {
      loginId: loginForm.id,  // 또는 loginForm.id 그대로 사용 (백엔드 요구사항에 따라)
      password: loginForm.password
    };

    console.log('요청 데이터', credentials);
    // API 호출
    const response = await authAPI.login(credentials);

    // 응답에서 토큰 추출
    const { accessToken, user } = response.data;

    // 토큰 저장
    localStorage.setItem('accessToken', accessToken);

    // 사용자 정보 저장 (필요한 경우)
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    }

    // 로그인 성공 후 메인 페이지나 대시보드로 이동
    router.push('/dashboard');

  } catch (err) {
    console.error('로그인 오류:', err);

    // 서버 응답에 따른 에러 메시지 처리
    if (err.response) {
      if (err.response.status === 401) {
        error.value = '아이디 또는 비밀번호가 일치하지 않습니다.';
      } else {
        error.value = '로그인에 실패했습니다.';
      }
    } else {
      error.value = '서버 연결에 실패했습니다.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>
