    <template>
        <div class="user-dropdown-container">
        <a href="#" class="user-link" @click.prevent="toggleDropdown">
            <i class="bi bi-person-circle me-1"></i>
            <span v-if="!storeFullName">餐廳業者*</span>
            <span v-else>{{ storeFullName }}</span>
        </a>
        <div class="dropdown-menu" v-if="showDropdown">
            <ul class="list-unstyled mb-0">
            <li @click="navigateTo('editStoreUser')" class="d-flex align-items-center gap-2 px-3 py-2">
                <i class="bi bi-person"></i> 餐廳業者資料
            </li>
            <li @click="navigateTo('editStore')" class="d-flex align-items-center gap-2 px-3 py-2">
                <i class="bi bi-shop"></i> 餐廳資料
            </li>
            <li @click="logout" class="d-flex align-items-center gap-2 px-3 py-2">
                <i class="bi bi-box-arrow-right"></i> 登出
            </li>
            </ul>
        </div>
        </div>
    </template>
    
    <script setup>
    import { ref, onMounted, onUnmounted, computed } from 'vue'
    import { useRouter } from 'vue-router'
    import { useUserStore } from '@/stores/user.js'
    
    const showDropdown = ref(false)
    const router = useRouter()
    
    const store = useUserStore()
    // 用 computed 自動響應
    const storeFullName = computed(() => store.ownerFullName)
    
    // 切換下拉選單
    const toggleDropdown = () => {
        showDropdown.value = !showDropdown.value
    }
    
    // 導航到管理頁
    const navigateTo = (path) => {
        router.push(`/${path}`)
        showDropdown.value = false
    }
    
    // 業者登出（用 pinia 提供的 logout，會自動清 localStorage 和狀態）
    const logout = () => { 
        store.ownerLogout();
        showDropdown.value = false;
        router.push('/store') 
    }
    
    // 點擊外部收合
    const handleClickOutside = (event) => {
        if (!event.target.closest('.user-dropdown-container')) {
            showDropdown.value = false
        }
    }
    
    onMounted(() => {
        document.addEventListener('click', handleClickOutside)
        // 不需再 set storeFullName，Pinia 狀態已自動對應 localStorage
        // console.log('storeFullName:', storeFullName.value)
    })
    
    onUnmounted(() => {
        document.removeEventListener('click', handleClickOutside)
    })
    </script>