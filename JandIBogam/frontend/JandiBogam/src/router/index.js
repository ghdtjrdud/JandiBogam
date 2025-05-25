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
import GroupDetailView from '@/views/GroupDetailView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/',
      redirect: '/dashboard',
    },
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
      path: '/meals/:userId?',
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
      meta: { requiresAuth: true, title: '주간 건강 리포트' },
    },
    {
      path: '/reports',
      redirect: '/report',
    },
    {
      path: '/report/weekly',
      name: 'WeeklyReportWeekly',
      component: WeeklyReportView,
      meta: {
        requiresAuth: true,
        title: '주간 건강 리포트',
      },
    },
    {
      path: '/report/:date',
      name: 'WeeklyReportByDate',
      component: WeeklyReportView,
      meta: {
        requiresAuth: true,
        title: '주간 건강 리포트',
      },
    },

    {
      path: '/find-credentials',
      name: 'FindCredentials',
      component: FindCredentialsView,
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: MyPageView,
      meta: { requiresAuth: true },
    },
    {
      path: '/group/:id/detail',
      name: 'GroupDetail',
      component: GroupDetailView,
      meta: { requiresAuth: true },
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      redirect: '/dashboard', // 존재하지 않는 경로는 대시보드로 리다이렉트
    },
    {
      path: '/meal/:id/edit',
      name: 'MealEdit',
      component: MealEditView, // ← 수정 폼 컴포넌트
      meta: { requiresAuth: true },
    },
  ],
})

// 인증 가드 - 수정된 버전
router.beforeEach((to, from, next) => {
  console.log('Router Guard:', {
    to: to.path,
    from: from.path,
    name: to.name,
    params: to.params,
  })

  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)
  const isAuthPage = ['/login', '/signup', '/find-credentials'].includes(to.path)

  // 인증이 필요한 페이지에 접근했는데 로그인이 안 되어 있으면 로그인 페이지로
  if (requiresAuth && !authStore.isAuthenticated) {
    console.log('인증 필요, 로그인으로 이동')
    next('/login')
    return
  }

  // 이미 로그인된 상태에서 로그인/회원가입 페이지에 접근하면 대시보드로
  if (isAuthPage && authStore.isAuthenticated) {
    console.log('이미 로그인됨, 대시보드로 이동')
    next('/dashboard')
    return
  }

  // MealList 라우트의 특별 처리
  if (to.name === 'MealList') {
    let userId = to.params.userId
    console.log('MealList 라우트 처리:', { userId, toPath: to.path })

    // userId가 없거나 유효하지 않은 경우에만 리다이렉트
    if (!userId || isNaN(userId) || Number(userId) <= 0) {
      const myUserId = localStorage.getItem('userId')
      console.log('userId 무효, myUserId로 리다이렉트:', myUserId)

      if (myUserId && !isNaN(myUserId)) {
        next(`/meals/${myUserId}`)
        return
      } else {
        console.log('myUserId도 없음, 로그인으로 이동')
        next('/login')
        return
      }
    }
  }

  // 그 외 경우는 정상 진행
  console.log('정상 진행')
  next()
})

export default router
