import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import SignupView from '../views/SignupView.vue'
<<<<<<< HEAD
import { useAuthStore } from '@/stores/auth';
=======
import MyPageView from '../views/MyPageView.vue'
import { useAuthStore } from '@/stores/auth'
>>>>>>> df20e65a94fa80dc6f56dca8f19a934932eb2100
//import FindCredentialsView from '../views/FindCredentialsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/dashboard',
      name: 'dashboard',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
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
    {
      path: '/mypage',
      name: 'mypage',
      // 동적 import 방식(권장)
      component: MyPageView,
    },
  ],
})

// 인증 가드
router.beforeEach((to, from, next) => {
<<<<<<< HEAD
  const authStore = useAuthStore();
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});

=======
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})
>>>>>>> df20e65a94fa80dc6f56dca8f19a934932eb2100

export default router
