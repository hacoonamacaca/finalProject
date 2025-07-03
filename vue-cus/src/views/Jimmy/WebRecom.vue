<template>
<div class="container mt-4">
    <h2>站方推薦標籤管理</h2>
    <Message v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</Message>

    <!-- 新增標籤表單 -->
    <div class="card p-4 mb-4">
    <h3>{{ editMode ? '編輯標籤' : '新增標籤' }}</h3>
    <div class="p-fluid">
        <div class="field">
        <label for="tag">標籤名稱</label>
        <InputText id="tag" v-model="newTag.tag" placeholder="輸入標籤名稱" />
        </div>
        <Button :label="editMode ? '更新' : '新增'" icon="pi pi-check" class="p-button-success mt-2" @click="saveTag" :disabled="!newTag.tag" />
        <Button v-if="editMode" label="取消" icon="pi pi-times" class="p-button-secondary mt-2 ml-2" @click="cancelEdit" />
    </div>
    </div>

    <!-- 標籤列表 -->
    <div class="card">
    <h3>標籤列表</h3>
    <table class="table table-striped">
        <thead>
        <tr>           
            <th>標籤名稱</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="tag in tags" :key="tag.id">            
            <td>{{ tag.tag }}</td>
            <td>
            <Button icon="pi pi-pencil" class="p-button-rounded p-button-warning mr-2" @click="editTag(tag)" />
            <Button icon="pi pi-trash" class="p-button-rounded p-button-danger" @click="deleteTag(tag.id)" />
            </td>
        </tr>
        </tbody>
    </table>
    </div>
</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import Message from 'primevue/message';

// API 基礎 URL
const API_URL = 'http://localhost:8080/api/web-recom';

// 狀態管理
const tags = ref([]);
const newTag = ref({ tag: '' });
const editMode = ref(false);
const errorMessage = ref('');

// 獲取所有標籤
const fetchTags = async () => {
try {
    const response = await axios.get(API_URL);
    tags.value = response.data;
} catch (error) {
    errorMessage.value = '無法獲取標籤列表：' + error.message;
}
};

// 新增或更新標籤
const saveTag = async () => {
try {
    if (editMode.value) {
    // 更新標籤
    await axios.put(`${API_URL}/${newTag.value.id}`, newTag.value);
    } else {
    // 新增標籤
    await axios.post(API_URL, newTag.value);
    }
    newTag.value = { tag: '' };
    editMode.value = false;
    await fetchTags();
    errorMessage.value = '';
} catch (error) {
    errorMessage.value = `操作失敗：${error.response?.data?.message || error.message}`;
}
};

// 編輯標籤
const editTag = (tag) => {
newTag.value = { ...tag };
editMode.value = true;
};

// 取消編輯
const cancelEdit = () => {
newTag.value = { tag: '' };
editMode.value = false;
errorMessage.value = '';
};

// 刪除標籤
const deleteTag = async (id) => {
if (confirm('確定要刪除此標籤嗎？')) {
    try {
    await axios.delete(`${API_URL}/${id}`);
    await fetchTags();
    errorMessage.value = '';
    } catch (error) {
    errorMessage.value = `刪除失敗：${error.response?.data?.message || error.message}`;
    }
}
};

// 頁面加載時獲取標籤
onMounted(fetchTags);
</script>

<style scoped>
.table {
width: 100%;
}
.field {
margin-bottom: 1rem;
}
.ml-2 {
margin-left: 0.5rem;
}
</style>