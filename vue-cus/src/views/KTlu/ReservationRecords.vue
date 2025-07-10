<template>
    <div class="container my-5">
        <h2>訂位紀錄</h2>
        <table class="table">
            <thead>
                <tr>
                    <th>訂位日期</th>
                    <th>時間</th>
                    <th>人數</th>
                    <th>狀態</th>
                    <th>內容</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="record in records" :key="record.id">
                    <td>{{ record.reserved_date }}</td>
                    <td>{{ record.reserved_time }}</td>
                    <td>{{ record.guests }}</td>
                    <td>{{ record.status }}</td>
                    <td>{{ record.content }}</td>
                    <td>
                        <button v-if="canEdit(record)" class="btn btn-sm btn-primary"
                            @click="editRecord(record)">修改</button>
                        <button v-if="canEdit(record)" class="btn btn-sm btn-danger"
                            @click="cancelRecord(record)">取消</button>
                        <span v-if="!canEdit(record)" class="text-muted">已過期</span>
                    </td>
                </tr>
            </tbody>
        </table>
        <!-- 這裡可以加上編輯的 modal 或表單 -->
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// 這裡請改成實際 API 取得資料
const records = ref([])

onMounted(async () => {
    // TODO: 改成呼叫 API 取得目前登入者的訂位紀錄
    records.value = [
        {
            id: 1,
            reserved_date: '2024-07-01',
            reserved_time: '18:00',
            guests: 4,
            status: '已預約',
            content: '靠窗',
            created_at: '2024-06-20',
            updated_at: '2024-06-20'
        },
        {
            id: 2,
            reserved_date: '2024-06-01',
            reserved_time: '12:00',
            guests: 2,
            status: '已完成',
            content: '',
            created_at: '2024-05-20',
            updated_at: '2024-05-20'
        }
    ]
})

function canEdit(record) {
    // 只允許未到期且狀態為已預約的訂位可編輯/取消
    const now = new Date()
    const reservedDate = new Date(record.reserved_date + 'T' + record.reserved_time)
    return reservedDate > now && record.status === '已預約'
}

function editRecord(record) {
    // 彈出 modal 或跳轉到編輯頁
    alert('編輯功能開發中')
}

function cancelRecord(record) {
    // 呼叫 API 取消訂位
    alert('取消功能開發中')
}
</script>