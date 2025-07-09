import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useLocationStore = defineStore('location', () => {
  // 定義一個響應式的狀態來儲存座標
  // 初始值為 null，當取得座標後會更新為 { lat: number, lon: number }
  const coordinates = ref(null);

  // 定義一個函式來更新座標狀態
  function setCoordinates(newCoords) {
    coordinates.value = newCoords;
  }

  // 將狀態和方法回傳，供其他元件使用
  return {
    coordinates,
    setCoordinates,
  };
});