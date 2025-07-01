import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import { createPinia } from 'pinia'

import '@/assets/styles/common.css'

const pinia = createPinia()

createApp(App)
    .use(router)
    .use(pinia)
    .mount('#app')