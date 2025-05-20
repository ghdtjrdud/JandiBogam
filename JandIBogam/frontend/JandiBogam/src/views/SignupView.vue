<template>
  <div class="min-h-screen bg-gray-50 flex justify-center items-center py-8 px-4">
    <div class="w-full max-w-2xl bg-white rounded-lg shadow-md p-8">
      <!-- Logo and Header -->
      <div class="flex flex-col items-center mb-8">
        <img
          src="/src/assets/images/logo.png"
          alt="Dining Bom Logo"
          class="mx-auto mb-4 mt-2 w-44 h-auto "
        />
        <h1 class="text-[#6A7D73] text-xl font-bold text-center mb-1">회원가입</h1>
        <p class="text-[#9E8C7F] text-center text-sm">다이닝봄과 함께 건강한 일상을 시작하세요</p>
      </div>

      <!-- Signup Form -->
      <form @submit.prevent="handleSignup" class="space-y-6">
        <!-- Basic Information Section -->
        <div>
          <h3 class="text-[#6A7D73] font-medium mb-4">기본 정보</h3>

          <div class="space-y-4">
            <!-- ID Field -->
            <div>
              <label for="id" class="block text-[#9E8C7F] text-sm mb-1">아이디</label>
              <input
                id="id"
                v-model="signupForm.id"
                type="text"
                placeholder="6-12자의 영문+숫자"
                class="w-full rounded-md border border-[#D0B8A8] bg-white px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#9EB0A2]"
              />
            </div>

            <!-- Password Field -->
            <div>
              <label for="password" class="block text-[#9E8C7F] text-sm mb-1">비밀번호</label>
              <input
                id="password"
                v-model="signupForm.password"
                type="password"
                placeholder="8자 이상, 영문+숫자+특수문자"
                class="w-full rounded-md border border-[#D0B8A8] bg-white px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#9EB0A2]"
              />
            </div>

            <!-- Password Confirmation Field -->
            <div>
              <label for="passwordConfirm" class="block text-[#9E8C7F] text-sm mb-1">비밀번호 확인</label>
              <input
                id="passwordConfirm"
                v-model="signupForm.passwordConfirm"
                type="password"
                placeholder="비밀번호를 다시 입력하세요"
                class="w-full rounded-md border border-[#D0B8A8] bg-white px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#9EB0A2]"
              />
            </div>

            <!-- Name Field -->
            <div>
              <label for="name" class="block text-[#9E8C7F] text-sm mb-1">이름</label>
              <input
                id="name"
                v-model="signupForm.name"
                type="text"
                placeholder="실명을 입력하세요"
                class="w-full rounded-md border border-[#D0B8A8] bg-white px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#9EB0A2]"
              />
            </div>

            <!-- Gender Selection -->
            <div>
              <label class="block text-[#9E8C7F] text-sm mb-2">성별</label>
              <div class="flex space-x-6">
                <label class="inline-flex items-center">
                  <input
                    type="radio"
                    v-model="signupForm.gender"
                    value="male"
                    class="radio radio-sm radio-primary"
                  />
                  <span class="ml-2 text-sm text-[#6A7D73]">남성</span>
                </label>
                <label class="inline-flex items-center">
                  <input
                    type="radio"
                    v-model="signupForm.gender"
                    value="female"
                    class="radio radio-sm radio-primary"
                  />
                  <span class="ml-2 text-sm text-[#6A7D73]">여성</span>
                </label>
              </div>
            </div>

            <!-- 생년월일 (달력) -->
            <div>
              <label class="block text-[#9E8C7F] text-lg font-semibold mb-3">생년월일</label>
                <VueDatePicker
                  v-model="signupForm.birthdate"
                  :format="'yyyy-MM-dd'"
                  placeholder="생년월일을 선택하세요"
                  :enable-time-picker="false"
                  :max-date="new Date()"
                  input-class-name="w-full rounded-xl border border-[#B29888] bg-[#F6FAF7] px-6 py-4 text-lg focus:outline-none focus:ring-2 focus:ring-[#6A7D73]"
                />
            </div>

            <!-- Family Code -->
            <div>
              <label for="familyCode" class="block text-[#9E8C7F] text-sm mb-1">가족코드 (선택사항)</label>
              <input
                id="familyCode"
                v-model="signupForm.familyCode"
                type="text"
                placeholder="공유받은 가족코드가 있다면 입력하세요"
                class="w-full rounded-md border border-[#D0B8A8] bg-white px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#9EB0A2]"
              />
              <p class="text-xs text-[#9E8C7F] mt-1">* 가족코드가 없어도 회원가입이 가능합니다</p>
            </div>
          </div>
        </div>

        <!-- Health Information Section -->
        <div>
          <h3 class="text-[#6A7D73] font-medium mb-4">건강 정보</h3>

          <div class="grid grid-cols-2 gap-3">
            <label class="inline-flex items-center">
              <input
                type="checkbox"
                v-model="signupForm.healthInfo.diabetes"
                class="checkbox checkbox-sm checkbox-primary"
              />
              <span class="ml-2 text-sm text-[#6A7D73]">당뇨</span>
            </label>

            <label class="inline-flex items-center">
              <input
                type="checkbox"
                v-model="signupForm.healthInfo.highBloodPressure"
                class="checkbox checkbox-sm checkbox-primary"
              />
              <span class="ml-2 text-sm text-[#6A7D73]">고혈압</span>
            </label>

            <label class="inline-flex items-center">
              <input
                type="checkbox"
                v-model="signupForm.healthInfo.highCholesterol"
                class="checkbox checkbox-sm checkbox-primary"
              />
              <span class="ml-2 text-sm text-[#6A7D73]">심장질환</span>
            </label>

            <label class="inline-flex items-center">
              <input
                type="checkbox"
                v-model="signupForm.healthInfo.kidneyDisease"
                class="checkbox checkbox-sm checkbox-primary"
              />
              <span class="ml-2 text-sm text-[#6A7D73]">신장질환</span>
            </label>

            <label class="inline-flex items-center">
              <input
                type="checkbox"
                v-model="signupForm.healthInfo.heartDisease"
                class="checkbox checkbox-sm checkbox-primary"
              />
              <span class="ml-2 text-sm text-[#6A7D73]">간질환</span>
            </label>
          </div>
        </div>

        <!-- Submit Button -->
        <button
          type="submit"
          class="w-full py-3 rounded-md bg-[#6A9B5E] text-white text-base font-medium hover:bg-[#5A8B4E] transition-colors"
        >
          회원가입 완료
        </button>
      </form>

      <!-- Return to Login Link -->
      <div class="text-center mt-6">
        <router-link
          to="/login"
          class="text-[#9EB0A2] hover:text-[#6A7D73] text-sm"
        >
          로그인 화면으로 돌아가기
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>

</script>
