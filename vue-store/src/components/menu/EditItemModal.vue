<script setup>
import { ref, watchEffect} from 'vue';

const props = defineProps({
    item: {
        type: Object,
        default: null, // 如果是新增，prop 會是 null
    },
    categories: { 
        type: Array, 
        required: true } // 在 props 中接收 categories
});

const emit = defineEmits(['close', 'save', 'delete']);

// 建立一個本地的響應式物件來處理表單資料
// 這樣才不會直接修改到 prop，這是 Vue 的一個重要實踐
const form = ref({});

// 使用 watchEffect 來監聽 prop 的變化，並更新本地 form
watchEffect(() => {
    if (props.item) {
        // 編輯模式：複製 prop item 的資料到 form
        form.value = { ...props.item };
    } else {
        // 新增模式：設定預設空值
        form.value = {
            name: '',
            price: 0,
            status: '供應中',
            stock: 0,
            categoryId: props.categories.length > 0 ? props.categories[0].id : null, // 設定預設類別為第一個類別
            description: '',
            img: ''
        };
    }
});

// const title = computed(() => props.item ? '編輯品項' : '新增品項');

const handleSave = () => {
    // 把表單資料透過 event 傳回給父組件
    emit('save', form.value);
};

const handleDelete = () => {
    // 把要刪除的 id 傳回去
    if (props.item?.id) { // 使用可選鏈 ?. 更安全
        emit('delete', props.item.id);
    }
};

// 這個 handleClose 主要是為了讓父組件能接收到關閉訊號，例如在儲存成功後觸發
const handleClose = () => {
    emit('close');
};

</script>


<template>
    <div>
        <!-- 表單的主體內容 -->
        <form @submit.prevent="handleSave">
            <!-- 品項內容 -->
            <h6 class="mb-3">品項內容</h6>
            <div class="text-center mb-3">
                <div class="border rounded d-flex justify-content-center align-items-center bg-light" style="width: 150px; height: 150px; margin: auto; cursor: pointer;">
                <span v-if="!form.img" class="fs-1 text-muted">+</span>
                <img v-else :src="form.img" class="img-fluid rounded">
                </div>
                <small class="form-text text-muted">請上傳建議尺寸的圖片</small>
            </div>

            <div class="mb-3">
                <label class="form-label">品項名稱*</label>
                <input type="text" class="form-control" v-model="form.name" required>
            </div>
            
            <div class="mb-3">
                <label class="form-label">品項類別*</label>
                <select class="form-select" v-model="form.categoryId" required>
                <option v-for="category in categories" :key="category.id" :value="category.id">
                    {{ category.name }}
                </option>
                </select>
            </div>
            
            <div class="row mb-3">
                <div class="col">
                <label class="form-label">價格*</label>
                <input type="number" class="form-control" v-model.number="form.price" min="0" required>
                </div>
                <div class="col">
                    <label class="form-label">供應狀態*</label>
                    <select class="form-select" v-model="form.status">
                        <option>供應中</option>
                        <option>暫停供應</option>
                    </select>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">庫存</label>
                <input type="number" class="form-control" v-model.number="form.stock" min="0">
            </div>

            <div class="mb-3">
                <label class="form-label">品項敘述*</label>
                <textarea class="form-control" rows="3" v-model="form.description"></textarea>
            </div>

            <hr class="my-4">

            <!-- 按鈕現在放在表單的末尾 -->
            <div class="d-flex justify-content-between">
                <button v-if="props.item" type="button" class="btn btn-outline-danger" @click="handleDelete">刪除品項</button>
                <div v-else></div><!-- 占位符，讓按鈕保持在右邊 -->
                <div class="d-flex gap-2">
                    <button type="button" class="btn btn-secondary" @click="handleClose">取消</button>
                    <button type="submit" class="btn btn-primary">確定提交</button>
                </div>
            </div>
        </form>
    </div>
</template>
