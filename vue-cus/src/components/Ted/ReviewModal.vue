<script setup>
import { ref, watch, computed } from 'vue';
import { Modal } from 'bootstrap'; // 引入 Bootstrap Modal JavaScript
import axios from '@/plungins/axios.js'; // 確保路徑正確
import { useUserStore } from '@/stores/user.js'; // 假設您有用戶 Pinia Store 來獲取 userId

const props = defineProps({
    order: {
        type: Object,
        required: true
    }
});

const emit = defineEmits(['commentSubmitted', 'ratingUpdated']); // 定義發射事件

// 從 Pinia Store 獲取用戶 ID
const userStore = useUserStore();
const currentUserId = computed(() => userStore.userId); // 假設您在 userStore 中存儲了 userId

// Modal 相關
const ratingModalRef = ref(null); // 用於綁定 Modal 元素的 ref
let bsModal = null; // Bootstrap Modal 實例

// 評分相關
const currentRating = ref(0); // 當前選擇的星級評分 (1-5)
const hoverRating = ref(0); // 滑鼠懸停時的星級評分
const commentContent = ref(''); // 評論內容
const existingCommentId = ref(null); // 如果是編輯現有評論，則存儲評論 ID

// 食物點讚/倒讚狀態
const foodLikes = ref({}); // 儲存每個 orderDetailId 對應的 isLiked 狀態 { orderDetailId: isLiked (boolean) }

// 初始化評分 Modal 數據
const initializeModalData = () => {
    // 初始化評分和評論內容
    if (props.order.comment) {
        currentRating.value = props.order.comment.score || 0;
        commentContent.value = props.order.comment.content || '';
        existingCommentId.value = props.order.comment.id;
    } else {
        currentRating.value = 0;
        commentContent.value = '';
        existingCommentId.value = null;
    }

    // 初始化食物點讚狀態
    foodLikes.value = {};
    if (props.order.orderDetails && props.order.orderDetails.length > 0) {
        props.order.orderDetails.forEach(detail => {
            if (detail.likeFood) {
                foodLikes.value[detail.id] = detail.likeFood.isLiked;
            } else {
                foodLikes.value[detail.id] = null; // 初始為未選擇
            }
        });
    }
};

// 監聽 order props 變化，當 order 更新時重新初始化數據 (例如，從 API 獲取新訂單數據)
watch(() => props.order, (newOrder) => {
    if (newOrder) {
        initializeModalData();
    }
}, { immediate: true }); // immediate: true 表示組件加載時立即執行一次

// Modal 開啟/關閉
const openModal = () => {
    if (ratingModalRef.value) {
        if (!bsModal) {
            bsModal = new Modal(ratingModalRef.value);
        }
        initializeModalData(); // 每次打開 Modal 都重新初始化數據
        bsModal.show();
    }
};

const closeModal = () => {
    if (bsModal) {
        bsModal.hide();
    }
};

// 星級評分操作
const setRating = (rating) => {
    currentRating.value = rating;
};

const setHoverRating = (rating) => {
    hoverRating.value = rating;
};

const resetHoverRating = () => {
    hoverRating.value = 0;
};

// 食物點讚/倒讚操作
const toggleLike = (orderDetailId, isLiked) => {
    foodLikes.value[orderDetailId] = isLiked;
};

// 提交評論和點讚狀態
const submitReview = async () => {
    if (currentUserId.value === null) {
        alert('請先登入才能進行評論和點讚。');
        return;
    }

    // 1. 提交或更新評論
    try {
        const commentData = {
            content: commentContent.value,
            score: currentRating.value,
            // reply 和 isHidden 可以在後端預設或不傳
            orderId: props.order.id,
            userId: currentUserId.value,
            storeId: props.order.store.id
        };

        let commentResponse;
        if (existingCommentId.value) {
            // 更新現有評論
            commentResponse = await axios.put(`/comment/${existingCommentId.value}`, commentData);
        } else {
            // 創建新評論
            commentResponse = await axios.post('/comment', commentData);
            existingCommentId.value = commentResponse.data.id; // 保存新創建的評論 ID
        }
        console.log('評論提交成功:', commentResponse.data);
        emit('commentSubmitted', commentResponse.data); // 通知父組件評論已提交
    } catch (error) {
        console.error('提交評論失敗:', error);
        alert('提交評論失敗！');
        return; // 如果評論失敗，則停止後續操作
    }

    // 2. 提交或更新食物點讚/倒讚狀態
    if (props.order.orderDetails) {
        for (const detail of props.order.orderDetails) {
            const likedStatus = foodLikes.value[detail.id];
            if (likedStatus !== null) { // 只有當用戶實際選擇了點讚/倒讚時才提交
                try {
                    const likedFoodData = {
                        userId: currentUserId.value,
                        foodId: detail.food.id,
                        orderDetailId: detail.id,
                        isLiked: likedStatus,
                        // updatedTime 會由後端處理
                    };
                    // 假設後端有一個 /liked-food 或 /liked-food/{orderDetailId} 的 endpoint
                    // 如果是新增或更新，需要判斷現有狀態。
                    // 這裡簡單處理為直接發送 PUT 請求，假設後端會處理 upsert 邏輯。
                    // 您可能需要根據實際的 LikedFoodController API 設計進行調整。
                    await axios.post('/liked-food', likedFoodData); // 或者 PUT /liked-food/{id}
                    console.log(`食物 ${detail.food.name} 點讚狀態提交成功.`);
                } catch (error) {
                    console.error(`提交食物 ${detail.food.name} 點讚狀態失敗:`, error);
                    // 即使單個食物點讚失敗，也不中斷整個提交過程
                }
            }
        }
    }

    alert('感謝您的評分和評論！');
    closeModal(); // 提交成功後關閉 Modal
};
</script>

<template>
    <div>
        <button class="btn btn-warning btn-sm rounded-pill px-3" @click="openModal">
            <i class="bi bi-star-fill me-1"></i> 評分
        </button>

        <div class="modal fade" id="ratingModal" tabindex="-1" aria-labelledby="ratingModalLabel" aria-hidden="true" ref="ratingModalRef">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="ratingModalLabel">評價訂單 - {{ order.store.name }}</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body p-4">
                        <div class="d-flex align-items-center mb-4">
                            <img :src="order.store.photo" alt="店家圖片" class="me-3 rounded-circle" style="width: 60px; height: 60px; object-fit: cover;">
                            <div>
                                <h6 class="mb-0">{{ order.store.name }}</h6>
                                <p class="text-muted small mb-0">訂單時間: {{ order.createTime }}</p>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label d-block mb-2 fw-bold">整體評分：</label>
                            <div class="star-rating">
                                <i v-for="star in 5" :key="star"
                                   class="bi"
                                   :class="{ 'bi-star-fill text-warning': star <= currentRating || star <= hoverRating, 'bi-star': star > currentRating && star > hoverRating }"
                                   @mouseover="setHoverRating(star)"
                                   @mouseleave="resetHoverRating"
                                   @click="setRating(star)">
                                </i>
                                <span class="ms-2 text-muted">({{ currentRating }} / 5)</span>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label d-block mb-2 fw-bold">食物評價：</label>
                            <div v-if="order.orderDetails && order.orderDetails.length > 0">
                                <div v-for="detail in order.orderDetails" :key="detail.id" class="d-flex align-items-center mb-3 p-2 border rounded">
                                    <img :src="detail.food.photo" alt="食物圖片" class="me-3 rounded" style="width: 50px; height: 50px; object-fit: cover;">
                                    <div class="flex-grow-1">
                                        <p class="mb-0 fw-medium">{{ detail.food.name }} <span class="text-muted small">x {{ detail.quantity }}</span></p>
                                    </div>
                                    <div class="btn-group" role="group">
                                        <button type="button"
                                                class="btn"
                                                :class="{ 'btn-success': foodLikes[detail.id] === true, 'btn-outline-success': foodLikes[detail.id] !== true }"
                                                @click="toggleLike(detail.id, true)">
                                            <i class="bi bi-hand-thumbs-up-fill"></i>
                                        </button>
                                        <button type="button"
                                                class="btn"
                                                :class="{ 'btn-danger': foodLikes[detail.id] === false, 'btn-outline-danger': foodLikes[detail.id] !== false }"
                                                @click="toggleLike(detail.id, false)">
                                            <i class="bi bi-hand-thumbs-down-fill"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <p v-else class="text-muted">此訂單沒有食物明細。</p>
                        </div>

                        <div class="mb-4">
                            <label for="commentText" class="form-label fw-bold">您的評論：</label>
                            <textarea class="form-control" id="commentText" rows="4" v-model="commentContent" placeholder="請輸入您的評論..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        <button type="button" class="btn btn-primary" @click="submitReview">提交評價</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 確保 Bootstrap Icons 載入 */
@import 'bootstrap-icons/font/bootstrap-icons.css';

.star-rating .bi {
    font-size: 1.8rem;
    cursor: pointer;
    margin-right: 5px;
    color: #ccc; /* 預設灰色 */
}

.star-rating .bi-star-fill.text-warning {
    color: #ffc107 !important; /* 黃色星星 */
}

</style>