    <template>
        <div class="modal-mask">
        <div class="modal-dialog-custom">
            <button class="modal-close" @click="$emit('close')">×</button>
            <div class="text-center p-4">
            <h4>變更大頭貼</h4>
            <!-- 頭像預覽 -->
            <div>
                <img
                :src="previewUrl || avatarUrl"
                alt="預覽"
                class="rounded-circle mb-3"
                style="width: 96px; height: 96px; object-fit: cover; border: 2px solid #ffba20"
                />
            </div>
            <input type="file" accept="image/*" @change="onFileChange" class="form-control mb-3" />
            <button class="btn btn-primary" @click="updateAvatar" :disabled="!previewUrl">儲存</button>
            </div>
        </div>
        </div>
    </template>
    
    <script setup>
    import { ref, watch } from 'vue'
    
    const props = defineProps(['avatarUrl'])
    const emit = defineEmits(['update-avatar', 'close'])
    const previewUrl = ref(null)
    const fileRaw = ref(null)
    
    function onFileChange(e) {
        const file = e.target.files[0]
        if (file) {
        previewUrl.value = URL.createObjectURL(file)
        fileRaw.value = file
        }
    }
    function updateAvatar() {
        if (previewUrl.value) {
        // 回傳預覽用的本地圖片 URL
        emit('update-avatar', previewUrl.value)
        emit('close')
        }
    }
    </script>
    
    <style scoped>
    /* 可複製登入modal的樣式 */
    .modal-mask { position: fixed; inset: 0; background: rgba(0,0,0,0.12); display: flex; align-items: center; justify-content: center; z-index: 3000;}
    .modal-dialog-custom { background: #fff; border-radius: 14px; min-width: 340px; box-shadow: 0 4px 32px 2px rgba(0,0,0,0.11); position: relative; }
    .modal-close { position: absolute; top: 14px; right: 20px; border: none; background: transparent; font-size: 2rem; color: #888; cursor: pointer;}
    </style>
    