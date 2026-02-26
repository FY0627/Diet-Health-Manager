<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <div class="card-header">
        <h2>{{ isLogin ? '饮食健康管理系统' : '注册新账号' }}</h2>
        <p class="subtitle">{{ isLogin ? '登录以管理您的健康目标' : '开启您的健康自律之旅' }}</p>
      </div>

      <el-form v-if="isLogin" :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password :prefix-icon="Lock" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" size="large" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
        <div class="toggle-text">
          没有账号？ <el-link type="primary" @click="isLogin = false">立即注册</el-link>
        </div>
      </el-form>

      <el-form v-else :model="registerForm" :rules="rules" ref="registerFormRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入账号 (最少4位)" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码 (最少6位)" show-password :prefix-icon="Lock" size="large" />
        </el-form-item>
        <el-form-item prop="checkPassword">
          <el-input v-model="registerForm.checkPassword" type="password" placeholder="请再次确认密码" show-password :prefix-icon="Lock" size="large" />
        </el-form-item>
        <el-form-item prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="success" class="submit-btn" size="large" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form-item>
        <div class="toggle-text">
          已有账号？ <el-link type="primary" @click="isLogin = true">返回登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '../utils/request' // 引入咱们封装好的 axios

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)

const loginFormRef = ref(null)
const registerFormRef = ref(null)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  checkPassword: '',
  gender: 1
})

// 表单校验规则
const rules = reactive({
  username: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { min: 4, message: '账号长度不能小于 4 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ],
  checkPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
})

// 登录逻辑
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 发送请求给 Spring Boot 后端
      request.post('/user/login', loginForm).then(res => {
        ElMessage.success('登录成功！欢迎回来')
        // 登录成功后跳转到看板页
        router.push('/dashboard')
      }).finally(() => {
        loading.value = false
      })
    }
  })
}

// 注册逻辑
const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      request.post('/user/register', registerForm).then(res => {
        ElMessage.success('注册成功！请登录')
        // 注册成功后自动切换回登录页面，并清空表单
        isLogin.value = true
        registerForm.password = ''
        registerForm.checkPassword = ''
      }).finally(() => {
        loading.value = false
      })
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 随便找个好看的渐变色背景 */
  background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%); 
}

.login-card {
  width: 400px;
  border-radius: 12px;
  padding: 20px;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.subtitle {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}

.submit-btn {
  width: 100%;
  border-radius: 8px;
}

.toggle-text {
  text-align: center;
  font-size: 14px;
  color: #606266;
  margin-top: 15px;
}
</style>