import axios from "axios";

const instance = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
});
// 加入 response interceptor：處理錯誤
instance.interceptors.response.use(
    function (response) {
        return response;
    },
    function (error) {
        if (error.response && error.response.status && error.response.status === 403) {
            window.location.href = "/403";
        }
        return Promise.reject(error);
    }
);

export default instance;