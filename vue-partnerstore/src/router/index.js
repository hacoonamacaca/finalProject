import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import RegisterBusiness from '@/views/RegisterBusiness.vue'
import RegisterVerify from '@/views/RegisterVerify.vue'
import RegisterStoreInfo from '@/views/RegisterStoreInfo.vue'
import VerifyAddress from '@/views/VerifyAddress.vue'
import VerifyPending from '@/views/VerifyPending.vue'
import EditStoreUser from '@/views/EditStoreUser.vue'
import EditStore from '@/views/EditStore.vue'

const routes = [

    { path: '/', component: Home },
    { path: '/registerBusiness', component: RegisterBusiness },
    { path: '/registerVerify', component: RegisterVerify },
    { path: '/registerStoreInfo', component: RegisterStoreInfo },
    { path: '/verifyAddress', component: VerifyAddress },
    { path: '/verifyPending', component: VerifyPending },
    {
        path: '/resetPasswordEmail',
        name: 'ResetPasswordEmail',
        component: () => import('@/views/ResetPasswordEmail.vue'),
        props: router => ({ email: router.query.email })
    },
    {
        path: '/editStoreUser',
        name: 'EditStoreUser',
        component: EditStoreUser,
    },
    {
        path: '/editStore',
        name: 'EditStore',
        component: EditStore,
    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router