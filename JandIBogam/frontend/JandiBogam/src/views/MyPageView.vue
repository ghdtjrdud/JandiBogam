<script setup>
import { ref, watch, computed, reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'
import UserService from '@/services/UserService'
import { useRoute, useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

// 코드로 그룹 가입
const registerCode = ref('')

// 그룹 생성(모달로 이름작성)
const showModal = ref(false)
const newGroupName = ref('')

// toast 알림
const toast = useToast()

// 프로필 수정
const isEditMode = ref(false)

// 수정 폼
const editForm = reactive({
  name: '',
  gender: '',
  birthDate: '',
  diabetes: false,
  hypertension: false,
  heartDisease: false,
  kidneyDisease: false,
  liverDisease: false,
})

// 내 정보
const user = ref(null)
const loadingUser = ref(false)
const errorUser = ref(null)

// 그룹 정보
const groups = ref([])
const loadingGroups = ref(false)
const errorGroups = ref(null)
const selectedGroup = ref(null) // 선택된 그룹

// 유저 아이디는 authStore.user가 세팅된 후에만 값이 생깁니다
const userId = computed(() => authStore.user?.id)

// —————————————————————————————————————————
// 1) 내 정보 가져오기
async function fetchMyInfo(id) {
  loadingUser.value = true
  errorUser.value = null
  try {
    const { data } = await UserService.getMyInfo(id)
    user.value = data
  } catch {
    errorUser.value = '내 정보를 불러오지 못했습니다.'
  } finally {
    loadingUser.value = false
  }
}

// 2) 그룹 목록 가져오기
async function fetchMyGroups() {
  loadingGroups.value = true
  errorGroups.value = null
  try {
    const { data } = await UserService.getMyGroups()
    groups.value = Array.isArray(data) ? data : [data]
    // 첫 번째 그룹을 기본 선택
    if (groups.value.length > 0) {
      selectedGroup.value = groups.value[0]
    }
  } catch {
    errorGroups.value = '그룹 정보를 불러오지 못했습니다.'
  } finally {
    loadingGroups.value = false
  }
}

// 그룹 선택
function selectGroup(group) {
  selectedGroup.value = group
}

// 수정버튼 누르기
function startEdit() {
  isEditMode.value = true
  Object.assign(editForm, {
    name: user.value?.name ?? '',
    gender: user.value?.gender ?? '',
    birthDate: user.value?.birthDate ?? '',
    diabetes: user.value?.diabetes ?? false,
    hypertension: user.value?.hypertension ?? false,
    heartDisease: user.value?.heartDisease ?? false,
    kidneyDisease: user.value?.kidneyDisease ?? false,
    liverDisease: user.value?.liverDisease ?? false,
  })
}

// 수정 취소.
function cancelEdit() {
  isEditMode.value = false
}

// 수정 저장.
async function saveEdit() {
  console.log('saveEdit 호출됨', userId.value, editForm)
  console.log('accessToken:', localStorage.getItem('accessToken'))
  console.log('userId:', userId.value)
  console.log('editForm:', { ...editForm })
  if (!userId.value) {
    console.warn('userId가 없음!', userId.value)
    return
  }
  loadingUser.value = true

  try {
    const data = {
      name: editForm.name,
      gender: editForm.gender,
      birthDate: editForm.birthDate,
      diabetes: editForm.diabetes,
      hypertension: editForm.hypertension,
      heartDisease: editForm.heartDisease,
      kidneyDisease: editForm.kidneyDisease,
      liverDisease: editForm.liverDisease,
    }
    console.log('전송 데이터:', data)
    await UserService.updateMyInfo(userId.value, data)
    await fetchMyInfo(userId.value)
    isEditMode.value = false
  } catch (e) {
    errorUser.value = '프로필 수정에 실패했습니다'
    console.error(e)
  } finally {
    loadingUser.value = false
  }
}

// 그룹코드 복사 (선택된 그룹의 코드)
function copyGroupCode() {
  if (!selectedGroup.value) return

  navigator.clipboard
    .writeText(selectedGroup.value.code)
    .then(() => toast.success('그룹 코드가 복사되었습니다!'))
    .catch(() => toast.error('복사에 실패했습니다.'))
}

// 그룹 생성
async function onSubmitGroupName() {
  if (!newGroupName.value.trim()) {
    toast.error('그룹명을 입력해주세요!')
    return
  }
  try {
    const { data } = await UserService.createGroup({ name: newGroupName.value })
    toast.success('그룹이 생성되었습니다!')
    newGroupName.value = ''
    showModal.value = false
    // 그룹 목록 새로고침 함수 호출 필요
    await fetchMyGroups()
  } catch (e) {
    toast.error('그룹 생성 실패: ' + (e?.response?.data || e.message))
  }
}

// 그룹 생성
function handleCreateGroup() {
  showModal.value = true
}

// 그룹 가입
async function handleRegisterGroup() {
  if (!registerCode.value.trim()) {
    toast.error('그룹 코드를 입력하세요!')
    return
  }
  try {
    await UserService.joinGroup(registerCode.value.trim())
    toast.success('그룹 가입에 성공했습니다!')
    registerCode.value = ''
    await fetchMyGroups() // 목록 갱신
  } catch (e) {
    toast.error('그룹 가입 실패: ' + (e?.response?.data || e.message))
  }
}

// 그룹 상세로 이동
function goToGroupDetail(groupId) {
  router.push({ name: 'GroupDetail', params: { id: groupId } })
}

// —————————————————————————————————————————
// authStore.user가 바뀌어서 userId가 생기면(=로그인 복원 or 로그인 직후)
// 자동으로 두 API를 호출하도록 watch 설정
watch(
  () => userId.value,
  (id) => {
    console.log('watch 감지됨! userId:', id)

    if (id) {
      console.log('✅ fetchMyInfo 호출 준비 완료')

      fetchMyInfo(id)
      fetchMyGroups()
    } else {
      console.warn('⚠️ userId가 undefined 또는 falsy 상태입니다')
    }
  },
  { immediate: true }, // 컴포넌트 마운트 시에도 실행
)

// —————————————————————————————————————————
// computed 텍스트
const genderText = computed(() => {
  if (!user.value) return ''
  return user.value.gender === 'F' ? '여성' : '남성'
})
const illnessText = computed(() => {
  if (!user.value) return ''
  const list = []
  if (user.value.diabetes) list.push('당뇨')
  if (user.value.hypertension) list.push('고혈압')
  if (user.value.heartDisease) list.push('심장질환')
  if (user.value.kidneyDisease) list.push('신장질환')
  if (user.value.liverDisease) list.push('간질환')
  return list.length ? list.join(', ') : '없음'
})
const birthDateText = computed(() => {
  // 필드명 맞춰서 사용 (birth_date 또는 birthDate)
  const raw = user.value?.birthDate || user.value?.birthDate
  if (!raw) return ''
  const date = new Date(raw)
  if (isNaN(date)) return raw // 날짜 파싱 실패 시 원본 출력
  return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`
})

const joinedDateText = computed(() => {
  const raw = user.value?.createdAt || user.value?.createdAt
  if (!raw) return ''
  const date = new Date(raw)
  if (isNaN(date)) return raw
  return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`
})
</script>

<template>
  <div class="min-h-screen bg-brand-lightbg">
    <main class="max-w-3xl mx-auto px-4 py-8">
      <!-- 사용자 프로필 정보 섹션 -->
      <div class="grid grid-cols-4 gap-8 mb-8">
        <!-- 왼쪽 : 개인정보 카드 -->
        <div class="col-span-2 bg-white rounded-lg p-8 border border-[#B29888] shadow">
          <h2 class="text-xl font-bold mb-6 text-[#6A7D73]">개인 정보</h2>

          <div v-if="loadingUser">불러오는 중...</div>
          <div v-else-if="errorUser" class="text-red-500">{{ errorUser }}</div>

          <!-- 1. 읽기 모드 -->
          <div v-else-if="!isEditMode && user" class="space-y-4">
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">이름</div>
              <div>{{ user.name }}</div>
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">성별</div>
              <div>{{ genderText }}</div>
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">생년월일</div>
              <div>{{ birthDateText }}</div>
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">기저질환</div>
              <div>{{ illnessText }}</div>
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">가입일</div>
              <div>{{ joinedDateText }}</div>
            </div>
            <div class="mt-6">
              <button
                @click="startEdit"
                class="bg-[#C7D7CB] text-[#6A7D73] px-4 py-2 rounded-md text-sm hover:bg-[#5A6B63] hover:text-white transition-colors"
              >
                프로필 수정
              </button>
            </div>
          </div>

          <!-- 2. 수정 모드 -->
          <div v-else-if="isEditMode && user" class="space-y-4">
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">이름</div>
              <input
                v-model="editForm.name"
                class="border border-[#BAA89B] rounded px-2 py-1 focus:outline-none focus:border-[#6A7D73]"
              />
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">성별</div>
              <label> <input type="radio" value="M" v-model="editForm.gender" /> 남성 </label>
              <label class="ml-3">
                <input type="radio" value="F" v-model="editForm.gender" /> 여성
              </label>
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">생년월일</div>
              <input
                type="date"
                v-model="editForm.birthDate"
                class="border border-[#BAA89B] rounded px-2 py-1 focus:outline-none focus:border-[#6A7D73]"
              />
            </div>
            <div class="flex">
              <div class="w-24 text-[#9E8C7F]">기저질환</div>
              <label class="mr-2">
                <input type="checkbox" v-model="editForm.diabetes" /> 당뇨
              </label>
              <label class="mr-2">
                <input type="checkbox" v-model="editForm.hypertension" /> 고혈압
              </label>
              <label class="mr-2">
                <input type="checkbox" v-model="editForm.heartDisease" /> 심장질환
              </label>
              <label class="mr-2">
                <input type="checkbox" v-model="editForm.kidneyDisease" /> 신장질환
              </label>
              <label> <input type="checkbox" v-model="editForm.liverDisease" /> 간질환 </label>
            </div>
            <div class="mt-6">
              <button
                @click="
                  () => {
                    console.log('저장 클릭!')
                    saveEdit()
                  }
                "
                class="bg-[#6A7D73] text-white px-4 py-2 rounded-md text-sm mr-2 hover:bg-[#5A6B63]"
              >
                저장
              </button>

              <button
                @click="cancelEdit"
                class="bg-[#BAA89B] text-[#6A7D73] px-4 py-2 rounded-md text-sm hover:bg-[#9E8C7F] hover:text-white transition-colors"
              >
                취소
              </button>
            </div>
          </div>
        </div>

        <!-- 오른쪽 : 그룹 관리 카드 -->
        <div class="col-span-2 bg-white rounded-lg p-8 border border-[#B29888] shadow">
          <h2 class="text-xl font-bold mb-6 text-[#6A7D73]">그룹 관리</h2>

          <div v-if="loadingGroups">그룹 불러오는 중...</div>
          <div v-else-if="errorGroups" class="text-red-500">{{ errorGroups }}</div>
          <div v-else>
            <!-- 선택된 그룹 코드 표시 영역 -->
            <div
              v-if="selectedGroup"
              class="mb-6 p-4 bg-[#F6FAF7] rounded-lg border border-[#C7D7CB]"
            >
              <div class="flex items-center justify-between">
                <div>
                  <div class="text-sm text-[#9E8C7F] mb-1">그룹 코드</div>
                  <div class="text-2xl font-bold text-[#6A7D73] tracking-wider">
                    {{ selectedGroup.code }}
                  </div>
                </div>
                <button
                  @click="copyGroupCode"
                  class="bg-[#6A7D73] text-white px-4 py-2 rounded-md text-sm hover:bg-[#5A6B63] transition-colors"
                >
                  복사
                </button>
              </div>
            </div>

            <!-- 그룹 생성 버튼 -->
            <div class="mb-6">
              <button
                @click="handleCreateGroup"
                class="w-full bg-[#6A7D73] text-white py-3 rounded-lg font-medium hover:bg-[#5A6B63] transition-colors"
              >
                그룹 생성하기
              </button>

              <!-- daisyUI 모달 -->
              <dialog id="group-create-modal" class="modal" :open="showModal">
                <form method="dialog" class="modal-box" @submit.prevent="onSubmitGroupName">
                  <h3 class="font-bold text-lg text-[#6A7D73] mb-4">새 그룹 만들기</h3>
                  <input
                    v-model="newGroupName"
                    type="text"
                    placeholder="그룹명을 입력하세요"
                    class="input input-bordered w-full border-[#C7D7CB] mb-4"
                    required
                  />
                  <div class="modal-action">
                    <button
                      type="submit"
                      class="btn bg-[#6A7D73] text-white hover:bg-[#5A6B63] border-none"
                    >
                      생성
                    </button>
                    <button
                      type="button"
                      class="btn bg-[#C7D7CB] text-[#6A7D73] border-none"
                      @click="showModal = false"
                    >
                      취소
                    </button>
                  </div>
                </form>
              </dialog>
            </div>

            <!-- 그룹 코드 등록 -->
            <div class="mb-6">
              <div class="text-sm text-[#9E8C7F] mb-2">그룹 코드 등록</div>
              <div class="flex gap-2">
                <input
                  v-model="registerCode"
                  type="text"
                  placeholder="그룹 코드를 입력하세요"
                  class="flex-1 border border-[#C7D7CB] rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-[#6A7D73]"
                />
                <button
                  class="bg-[#6A7D73] text-white px-4 py-2 rounded-lg text-sm hover:bg-[#5A6B63] transition-colors"
                  @click="handleRegisterGroup"
                >
                  등록
                </button>
              </div>
            </div>

            <!-- 그룹 목록 -->
            <div v-if="groups.length === 0" class="text-[#9E8C7F] text-center py-8">
              가입된 그룹이 없습니다.
            </div>
            <div v-else class="space-y-3">
              <div
                v-for="group in groups"
                :key="group.id"
                @click="selectGroup(group)"
                :class="[
                  'flex items-center justify-between p-4 rounded-lg border cursor-pointer transition-all',
                  selectedGroup?.id === group.id
                    ? 'bg-[#F6FAF7] border-[#6A7D73] shadow-sm'
                    : 'bg-white border-[#C7D7CB] hover:bg-[#F6FAF7]',
                ]"
              >
                <div class="flex items-center">
                  <!-- 아바타 아이콘 -->
                  <div
                    class="w-10 h-10 rounded-full bg-[#6A7D73] flex items-center justify-center text-white font-bold mr-3"
                  >
                    {{ group.name.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <div class="font-medium text-[#6A7D73]">{{ group.name }}</div>
                    <div class="text-xs text-[#9E8C7F]">구성원 {{ group.memberCount }}명</div>
                  </div>
                </div>
                <button
                  class="text-xs text-[#9E8C7F] px-3 py-1 rounded hover:bg-[#C7D7CB] transition-colors"
                  @click.stop="goToGroupDetail(group.id)"
                >
                  상세 관리
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 테마 설정 -->
      <div class="mt-8">
        <div class="bg-white rounded-lg p-6 border border-[#B29888]">
          <h2 class="text-lg font-medium mb-6 text-[#6A7D73]">테마 설정</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <!-- 고령자 모드 -->
            <div
              class="border border-[#C7D7CB] rounded-lg p-4 bg-[#F6FAF7] flex items-center justify-center flex-col cursor-pointer hover:shadow-md transition-shadow"
            >
              <div class="text-[#6A7D73] text-3xl mb-2">😊</div>
              <div class="font-medium text-[#6A7D73] mb-1">고령자 모드</div>
              <div class="text-xs text-[#9E8C7F] text-center mb-2">
                큰 글씨와 간편한 인터페이스로 편하게 사용하실 수 있습니다
              </div>
            </div>

            <!-- 일반 모드 -->
            <div
              class="border border-[#B29888] rounded-lg p-4 flex items-center justify-center flex-col cursor-pointer hover:shadow-md transition-shadow"
            >
              <div class="text-[#9E8C7F] text-3xl mb-2">👤</div>
              <div class="font-medium text-[#6A7D73] mb-1">일반 모드</div>
              <div class="text-xs text-[#9E8C7F] text-center mb-2">
                표준 글씨와 기능을 사용할 수 있습니다
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
/* 전체 레이아웃이 헤더 너비에 맞도록 설정 */
@media (min-width: 768px) {
  main {
    max-width: 1280px; /* 대시보드와 동일한 최대 너비 설정 */
  }
}

/* 작은 화면에서 레이아웃 조정 */
@media (max-width: 767px) {
  main {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
