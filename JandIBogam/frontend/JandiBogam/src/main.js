import './assets/main.css'
import './services/api'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'

import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

import { createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth' // Pinia auth 스토어

async function bootstrap() {
  const app = createApp(App)

  // 1. Pinia 설치
  const pinia = createPinia()
  app.use(pinia)

  // 2. authStore 초기화 (로컬스토리지에서 유저/토큰 복원)
  const authStore = useAuthStore(pinia)
  await authStore.initAuth() // 반드시 await!

  // 3. 라우터 등록 (초기화 이후!)
  app.use(router)

  // 4. 글로벌 컴포넌트 등록 (VueDatePicker)
  app.component('VueDatePicker', VueDatePicker)

  // 5. Toastification 등록
  app.use(Toast, {
    timeout: 3000,
    hideProgressBar: true,
  })

  // 6. 앱 마운트
  app.mount('#app')
}

bootstrap()
