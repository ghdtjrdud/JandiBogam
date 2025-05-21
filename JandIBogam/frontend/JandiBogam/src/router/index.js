import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import SignupView from '../views/SignupView.vue'
import MealListView from '../views/MealListView.vue'
import { useAuthStore } from '@/stores/auth'
import MealDetailView from '@/views/MealDetailView.vue'
import MealRecordView from '@/views/MealRecordView.vue'
import MedicationView from '@/views/MedicationView.vue'
import MedicationCheckView from '@/views/MedicationCheckView.vue'
import WeeklyReportView from '@/views/WeeklyReportView.vue'
import FindCredentialsView from '../views/FindCredentialsView.vue'
import MyPageView from '../views/MyPageView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/dashboard',
      name: 'dashboard',
      component: HomeView,
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },
    // 식단 관련 라우트
    {
      path: '/group/:id/meals',
      name: 'MealList',
      component: MealListView,
      meta: { requiresAuth: true },
    },
    {
      path: '/meal/:id/detail',
      name: 'MealDetail',
      component: MealDetailView,
      meta: { requiresAuth: true },
    },
    {
      path: '/meal/record',
      name: 'MealRecord',
      component: MealRecordView,
      meta: { requiresAuth: true },
    },
    // 복약 관련 라우트
    {
      path: '/medication',
      name: 'MedicationCheck',
      component: MedicationCheckView,
      meta: { requiresAuth: true },
    },
    {
      path: '/medication/add',
      name: 'AddMedication',
      component: MedicationCheckView,
      meta: { requiresAuth: true },
    },
    {
      path: '/medication/edit/:id',
      name: 'EditMedication',
      component: MedicationCheckView,
      meta: { requiresAuth: true },
    },
    {
      path: '/medication/list',
      name: 'MedicationList',
      component: MedicationView,
      meta: { requiresAuth: true },
    },
    // 주간 리포트 라우트
    {
      path: '/report',
      name: 'WeeklyReport',
      component: WeeklyReportView,
      meta: { requiresAuth: true },
    },

    {
      path: '/find-credentials',
      name: 'FindCredentials',
      component: FindCredentialsView,
    },
    {
      path: '/mypage', // ← /mypage 경로 등록
      name: 'mypage',
      component: MyPageView, // ← 컴포넌트 연결
      meta: { requiresAuth: true },
    },
  ],
})

// 인증 가드
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
  const isAuthPage = ['/login', '/signup', '/find-credentials'].includes(to.path)

  // 인증이 필요한 페이지에 접근했는데 로그인이 안 되어 있으면 로그인 페이지로
  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  }
  // 이미 로그인된 상태에서 로그인/회원가입 페이지에 접근하면 대시보드로
  else if (isAuthPage && authStore.isAuthenticated) {
    next('/dashboard')
  }
  // 그 외 경우는 정상 진행
  else {
    next()
  }
})

export default router
