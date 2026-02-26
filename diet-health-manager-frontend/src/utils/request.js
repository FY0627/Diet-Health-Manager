import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
    baseURL: '/api',  // 配合刚才的 proxy 代理
    timeout: 5000,
    withCredentials: true // 【关键】允许携带 Cookie，保持 Session 登录状态
})

// 响应拦截器
request.interceptors.response.use(
    response => {
        let res = response.data
        // 如果后端返回 code 200，说明成功，直接返回具体数据
        if (res.code === 200) {
            return res.data
        } 
        // 如果是 401 未登录，跳转到登录页
        else if (res.code === 401) {
            ElMessage.warning("请先登录")
            router.push('/login')
            return Promise.reject('未登录')
        } 
        // 其他业务错误，直接弹出后端的 message
        else {
            ElMessage.error(res.message || '系统错误')
            return Promise.reject(res.message)
        }
    },
    error => {
        ElMessage.error('网络请求失败，请检查后端是否启动')
        return Promise.reject(error)
    }
)

export default request