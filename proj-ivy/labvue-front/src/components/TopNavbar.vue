<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom" style="height:56px;">
        <div class="container-fluid d-flex justify-content-end w-100 align-items-center">
            <div class="dropdown me-3">
                <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="quickActionsDropdown" data-bs-toggle="dropdown">
                    功能
                </button>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><button class="dropdown-item">我的領率</button></li>
                    <li><button class="dropdown-item">我的商品</button></li>
                    <li><button class="dropdown-item">行銷活動</button></li>
                    <li><button class="dropdown-item">廣告</button></li>
                    <li><button class="dropdown-item">意見反饋</button></li>
                    <li><button class="dropdown-item">專家諮詢</button></li>
                </ul>
            </div>
            <div class="dropdown">
                <a class="d-flex align-items-center text-decoration-none dropdown-toggle" href="#" data-bs-toggle="dropdown">
                    <span class="user-icon"></span>
                    <img src="@/assets/images/123.jpg" alt="avatar" class="rounded-circle me-2" style="width:32px; height:32px; object-fit:cover;" />
                    <span>eattiy</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li><a class="dropdown-item" href="#">個人資料</a></li>
                    <li><a class="dropdown-item" href="#">登出</a></li>
                </ul>
            </div>
        </div>
    </nav>
</template>

<style scoped>

</style>
