<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Main Content -->
    <main class="max-w-3xl mx-auto px-4 py-8">
      <!-- Back Button -->
      <div class="mb-6">
        <a href="#" class="text-green-600 flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          목록으로 돌아가기
        </a>
      </div>

      <div class="flex flex-col lg:flex-row gap-6">
        <!-- Meal Details Card -->
        <div class="bg-white rounded-lg shadow-sm p-6 flex-1">
          <!-- Meal Header -->
          <div class="flex items-start mb-4">
            <div class="bg-green-100 text-green-700 rounded-full px-4 py-2 mr-4">
              여름
            </div>
            <div>
              <div class="text-gray-600">
                2025년 5월 1일 (목)
              </div>
            </div>
          </div>

          <!-- Meal Title -->
          <h1 class="text-xl font-bold mb-6">현미밥, 미역국, 시금치무침, 계란찜</h1>

          <!-- Meal Image -->
          <div class="bg-gray-100 rounded-lg mb-6 flex items-center justify-center h-80">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>

          <!-- Meal Description -->
          <p class="text-gray-700">
            이참에서 점심 맛있어요. 시금치무침도 잘기를 참이 좋았습니다.
          </p>
        </div>

        <!-- Comments Card -->
        <div class="bg-white rounded-lg shadow-sm p-6 lg:w-96">
          <h2 class="text-lg font-bold mb-4">그룹 댓글 (3)</h2>

          <!-- Comments List -->
          <div class="space-y-6 mb-6">
            <!-- Comment 1 -->
            <div class="relative">
              <div class="flex items-start">
                <div class="avatar placeholder mr-3">
                  <div class="bg-purple-900 text-white rounded-full w-10">
                    <span>김</span>
                  </div>
                </div>
                <div class="flex-1">
                  <div class="flex justify-between items-center mb-1">
                    <div class="font-bold">김철수</div>
                    <button class="btn btn-ghost btn-xs">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z" />
                      </svg>
                    </button>
                  </div>
                  <p class="text-gray-700 mb-1">
                    어머니, 아침 든든하게 드셨네요! 미역국은 영양이 풍부해서 참 좋은 것 같아요.
                  </p>
                  <div class="text-gray-500 text-sm">30분 전</div>
                  <div class="flex mt-2">
                    <button class="btn btn-xs mr-2">수정</button>
                    <button class="btn btn-xs">삭제</button>
                  </div>
                </div>
              </div>

              <!-- Reply to Comment 1 -->
              <div class="ml-12 mt-3 border-l-2 border-gray-200 pl-3">
                <div class="bg-gray-50 p-3 rounded-lg">
                  <p class="text-gray-700 mb-1">
                    어머니, 아침 든든하게 드셨네요! 미역국은 영양이 풍부해서 참 좋은 것 같아요. 😊
                  </p>
                </div>
                <div class="flex justify-end mt-2">
                  <button class="btn btn-ghost btn-xs mr-2">취소</button>
                  <button class="btn btn-success btn-xs">저장</button>
                </div>
              </div>
            </div>

            <!-- Comment 2 -->
            <div class="relative">
              <div class="flex items-start">
                <div class="avatar placeholder mr-3">
                  <div class="bg-purple-900 text-white rounded-full w-10">
                    <span>김</span>
                  </div>
                </div>
                <div class="flex-1">
                  <div class="flex justify-between items-center mb-1">
                    <div class="font-bold">김미영</div>
                    <button class="btn btn-ghost btn-xs">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z" />
                      </svg>
                    </button>
                  </div>
                  <p class="text-gray-700 mb-1">
                    영양 균형 잘 맞추셔서 잘 챙겨드시네요! 시금치나물은 엄마가 제일 좋아하시죠. ❤️
                  </p>
                  <div class="text-gray-500 text-sm">1시간 전</div>
                </div>
              </div>
            </div>

            <!-- Comment 3 -->
            <div class="relative">
              <div class="flex items-start">
                <div class="avatar placeholder mr-3">
                  <div class="bg-purple-900 text-white rounded-full w-10">
                    <span>김</span>
                  </div>
                </div>
                <div class="flex-1">
                  <div class="flex justify-between items-center mb-1">
                    <div class="font-bold">김철수</div>
                    <button class="btn btn-ghost btn-xs">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z" />
                      </svg>
                    </button>
                  </div>
                  <p class="text-gray-700 mb-1">
                    어머니, 식전으로 보니 정말 맛있어 보여요. 계란찜도 부드러워 보이네요 👍
                  </p>
                  <div class="text-gray-500 text-sm">24시간 전</div>
                </div>
              </div>
            </div>
          </div>

          <!-- Comment Input -->
          <div>
            <textarea
              class="textarea textarea-bordered w-full h-24 mb-3"
              placeholder="따뜻한 한마디를 남겨주세요"
              v-model="newComment"
            ></textarea>
            <div class="flex justify-end">
              <button class="btn btn-success">댓글 작성</button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// State for new comment
const newComment = ref('')

// You can add more functionality here like:
// - Adding new comments
// - Editing comments
// - Deleting comments
// - Replying to comments
</script>

<style>
/* You can add custom styles here if needed */
/* DaisyUI and Tailwind should handle most styling needs */
</style>
