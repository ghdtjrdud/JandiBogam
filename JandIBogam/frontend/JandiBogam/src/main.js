import './assets/main.css'
import './services/api'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import Toast from 'vue-toastification';
import 'vue-toastification/dist/index.css';
import { createPinia } from 'pinia'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.mount('VueDatePicker', VueDatePicker)

app.use(Toast, {
  timeout: 3000,
  hideProgressBar: true,
});

app.mount('#app')
