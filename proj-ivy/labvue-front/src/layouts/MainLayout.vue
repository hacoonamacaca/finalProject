    <template>
        <div class="d-flex">
        <div>
            <Breadcrumb />
            <Sidebar />
        </div>
        <div class="flex-grow-1">
            <TopNavbar />
            <div class="container-fluid mt-2">
            <router-view />
            </div>
        </div>
        </div>
    </template>
    
    <script setup>
    import Sidebar from '@/components/Sidebar.vue'
    import TopNavbar from '@/components/TopNavbar.vue'
    import Breadcrumb from '@/components/Breadcrumb.vue'
    </script>
    
    <style scoped>
    /* 可依需求自訂 Sidebar、Breadcrumb 的寬度與外觀 */
    </style>
        