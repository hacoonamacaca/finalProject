<template>
    <div class="comment-wrapper">
        <button class="comment-btn" @click="showCommentPopout = true">
            查看評論
        </button>
        <div class="comment-popout" v-if="showCommentPopout">
            <div class="comment-popout-content">
                <button class="close-btn" @click="showCommentPopout = false">✕</button>
                <h3>餐廳評論</h3>
                <div class="comment-list">
                    <div class="comment-item" v-for="comment in comments" :key="comment.id" v-show="!comment.is_hidden">
                        <div class="comment-header">
                            <span class="user-id">{{ comment.user_id }}</span>
                            <span class="comment-score">評分: {{ comment.score }} ★</span>
                            <span class="comment-time">{{ formatDate(comment.create_time) }}</span>
                            <Report :report="report" />
                        </div>
                        <p class="comment-content">{{ comment.content }}</p>
                        <div class="comment-images" v-if="comment.comment_img && comment.comment_img.length > 0">
                            <img v-for="(img, index) in comment.comment_img" :key="index" :src="img.img"
                                :alt="'評論圖片 ' + (index + 1)" class="comment-img" />
                        </div>
                        <div class="comment-reply" v-if="comment.reply">
                            <p><strong>店家回覆:</strong> {{ comment.reply }}</p>
                            <span class="reply-time">{{ formatDate(comment.reply_update_time) }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import Report from '@/components/Report.vue';

defineProps({
    comments: {
        type: Array,
        required: true,
    },
});

const showCommentPopout = ref(false);

const report = ref([
    { header: "偏離主題", content: "評論內容與商家無關" },
    { header: "垃圾內容", content: "評論出自機器人或包含廣告與宣傳內容" },
    { header: "利益衝突", content: "評論出自競爭商家或相關人士" },
    { header: "不雅用語", content: "評論內容包含不雅字眼" },
    { header: "歧視或仇恨", content: "評論內容包含與個人或團體身分認同相關的有害言語" },
    { header: "個人資訊", content: "評論內含地址或電話號碼等個人資訊" },
])





// 格式化日期
const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
    });
};
</script>

<style scoped>
.comment-wrapper {
    display: inline-block;
}

.comment-btn {
    background-color: #ffba20;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
    transition: background-color 0.2s;
}

.comment-btn:hover {
    background-color: #eca300;
}

.comment-popout {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2000;
}

.comment-popout-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 600px;
    max-height: 80vh;
    overflow-y: auto;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    position: relative;
}

.close-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    background: none;
    border: none;
    font-size: 18px;
    color: #999;
    cursor: pointer;
}

.close-btn:hover {
    color: #ffba20;
}

.comment-list {
    margin-top: 20px;
}

.comment-item {
    border-bottom: 1px solid #eee;
    padding: 15px 0;
}

.comment-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.user-id {
    font-weight: bold;
    color: #333;
}

.comment-score {
    color: #ffba20;
}

.comment-time,
.reply-time {
    font-size: 12px;
    color: #999;
}

.comment-content {
    font-size: 14px;
    color: #333;
    margin-bottom: 10px;
}

.comment-images {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    margin-bottom: 10px;
}

.comment-img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
}

.comment-reply {
    background-color: #f7f7f7;
    padding: 10px;
    border-radius: 4px;
    margin-top: 10px;
}

.comment-reply p {
    font-size: 14px;
    color: #333;
    margin-bottom: 5px;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .comment-popout-content {
        width: 95%;
        padding: 15px;
    }

    .comment-btn {
        font-size: 10px;
        padding: 4px 8px;
    }

    .comment-img {
        width: 80px;
        height: 80px;
    }
}
</style>