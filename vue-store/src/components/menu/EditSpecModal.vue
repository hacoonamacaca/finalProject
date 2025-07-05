<template>
    <!-- Modal 的背景遮罩 -->
    <div class="modal-backdrop fade show"></div>
    <!-- Modal 主體 -->
    <div class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog modal-dialog-scrollable">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">{{ title }}</h5>
                    <button type="button" class="btn-close" @click="handleClose"></button>
                </div>
                <div class="modal-body">
                    <!-- 規格設定 -->
                    <h6 class="mb-3">客製化規格設定</h6>

                    <div class="mb-3">
                        <label class="form-label">規格名稱*</label>
                        <input type="text" class="form-control" v-model="form.name">
                    </div>

                    <div class="row mb-4">
                        <div class="col">
                            <label class="form-label">最少選擇*</label>
                            <input type="number" class="form-control" v-model.number="form.minSelection" min="0">
                            <!--最小值為0-->
                        </div>
                        <div class="col">
                            <label class="form-label">最多選擇</label>
                            <input type="number" class="form-control" v-model.number="form.maxSelection" min="1">
                            <!--最小值為1-->
                        </div>
                    </div>

                    <!-- 規格選項列表 -->
                    <h6 class="mb-3">客製化規格選項</h6>
                    <!-- 使用 v-for 遍歷 form.options 陣列 -->
                    <div v-for="(option, index) in form.options" :key="option.id || index"
                        class="row align-items-center mb-3">
                        <div class="col-4">
                            <label v-if="index === 0" class="form-label">選項名稱*</label>
                            <input type="text" class="form-control" placeholder="例如: 全糖" v-model="option.name">
                        </div>
                        <div class="col-3">
                            <label v-if="index === 0" class="form-label">加價*</label>
                            <input type="number" class="form-control" placeholder="0" v-model.number="option.price"
                                min="0">
                        </div>
                        <div class="col-4">
                            <label v-if="index === 0" class="form-label">供應狀態*</label>
                            <select class="form-select" v-model="option.status">
                                <option>供應中</option>
                                <option>暫停供應</option>
                            </select>
                        </div>
                        <div class="col-1 d-flex align-items-end">
                            <button class="btn btn-outline-danger btn-sm" @click="removeOption(index)">🗑️</button>
                        </div>
                    </div>
                    <!-- 新增選項按鈕 -->
                    <button class="btn btn-outline-primary btn-sm" @click="addOption">⊕ 新增客製化選項</button>
                </div>

                <div class="modal-footer justify-content-between">
                    <!-- 條件應該是 props.spec ，不是 spec -->
                    <button v-if="props.spec" type="button" class="btn btn-danger" @click="handleDelete">刪除規格</button>
                    <div v-else></div> <!-- 占位符，讓按鈕保持在右邊 -->
                    <div>
                        <button type="button" class="btn btn-secondary me-2" @click="handleClose">取消</button>
                        <button type="button" class="btn btn-primary" @click="handleSave">確定提交</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, watchEffect, computed } from 'vue';

// 1. 定義 Props：只接收 spec 物件
const props = defineProps({
    spec: {
        type: Object,
        default: null, // 新增時，prop 會是 null
    },
});

// 2. 定義 Emits：事件名稱保持一致
const emit = defineEmits(['close', 'save', 'delete']);

// 3. 建立一個本地的響應式物件來處理表單資料
// 這樣才不會直接修改到 prop，這是 Vue 的一個重要實踐
const form = ref({});

// 4. 使用 watchEffect 來監聽 prop 的變化，並更新本地 form
watchEffect(() => {
    if (props.spec) {
        // 編輯模式：複製傳入的 spec 資料
        // 使用深拷貝 JSON.parse(JSON.stringify(...)) 來確保 options 陣列也是新的
        form.value = JSON.parse(JSON.stringify(props.spec));
    } else {
        // 新增模式：設定規格的預設空值
        form.value = {
            name: '',
            minSelection: 0,
            maxSelection: 1,
            // 規格的選項應該是一個陣列
            options: [
                { id: `opt_${Date.now()}`, name: '', price: 0, status: '供應中' } // 預設給一個空選項
            ]
        };
    }
});

// 5. 動態標題
const title = computed(() => props.spec ? '編輯客製化規格' : '新增客製化規格');

// 6. 新增/刪除選項的方法
const addOption = () => {
    form.value.options.push({
        id: `opt_${Date.now()}`,
        name: '',
        price: 0,
        status: '供應中'
    });
};
const removeOption = (index) => {
    if (form.value.options.length > 1) { // 至少保留一個選項
        form.value.options.splice(index, 1);
    }
};

// 7. 事件處理函式
const handleSave = () => {
    // 把表單資料透過 event 傳回給父組件
    emit('save', form.value);
};

const handleDelete = () => {
    // 把要刪除的 id 傳回去
    // if (props.spec && props.spec.id) {
    if (props.spec?.id) {  // 使用可選鏈 ?. 更安全
        emit('delete', props.spec.id);
    }
};

const handleClose = () => {
    emit('close');
};
</script>