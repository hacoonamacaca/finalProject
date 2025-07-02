import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Register from '@/components/RegisterModal.vue'
import VerifyPending from '@/views/VerifyPending.vue'
import RegisterProfile from '@/views/RegisterProfile.vue'
import Edituser from '@/views/Edituser.vue'

const routes = [

    { path: '/', component: Home },
    { path: '/register', component: Register },
    { path: '/verify-pending', component: VerifyPending },
    { path: '/register-profile', component: RegisterProfile },
    {
        path: '/resetPasswordEmail',
        name: 'ResetPasswordEmail',
        component: () => import('@/views/ResetPasswordEmail.vue'),
        props: router => ({ email: router.query.email })
    },
    {
        path: '/profile',
        name: 'Edituser',
        component: Edituser
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router