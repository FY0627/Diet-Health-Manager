<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover" class="profile-card">
          <template #header>
            <div class="card-header">
              <span>👤 基本资料</span>
            </div>
          </template>
          
          <el-form :model="userForm" label-width="100px">
            <el-form-item label="登录账号">
              <el-input v-model="userForm.username" disabled />
            </el-form-item>
            
            <el-form-item label="性别">
              <el-radio-group v-model="userForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="出生日期">
              <el-date-picker 
                v-model="userForm.birthday" 
                type="date" 
                placeholder="选择出生日期以计算年龄" 
                format="YYYY-MM-DD" 
                value-format="YYYY-MM-DD"
                style="width: 100%;" 
              />
            </el-form-item>
            
            <el-form-item label="重置密码">
              <el-input v-model="userForm.password" type="password" placeholder="如果不修改请留空" show-password />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveUserInfo" :loading="loadingUser">保存基本资料</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover" class="profile-card">
          <template #header>
            <div class="card-header">
              <span>⚕️ 身体指标录入</span>
            </div>
          </template>
          
          <el-alert 
            title="请先在左侧完善【性别】和【出生日期】，系统将自动为您计算精准的 BMI 和基础代谢率 (BMR)！" 
            type="success" 
            :closable="false" 
            style="margin-bottom: 25px;"
          />
          
          <el-form :model="healthForm" label-width="100px">
            <el-form-item label="身高 (cm)">
              <el-input-number v-model="healthForm.height" :min="50" :max="250" :step="0.1" size="large" />
            </el-form-item>
            
            <el-form-item label="体重 (kg)">
              <el-input-number v-model="healthForm.weight" :min="20" :max="300" :step="0.1" size="large" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="success" @click="saveHealthData" :loading="loadingHealth">
                录入并重新计算 BMR
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const loadingUser = ref(false)
const loadingHealth = ref(false)

// 基本资料表单
const userForm = reactive({
  username: '',
  gender: 1,
  birthday: '',
  password: ''
})

// 健康数据表单 (初始值为 null，等待后端覆盖)
const healthForm = reactive({
  height: null,
  weight: null
})

// 加载当前登录用户的信息
const fetchCurrentUser = () => {
  request.get('/user/current').then(res => {
    if (res) {
      userForm.username = res.username
      userForm.gender = res.gender
      userForm.birthday = res.birthday || ''
    }
  })
}

// 新增：加载用户的身高体重信息进行回显
const fetchHealthData = () => {
  request.get('/health/info').then(res => {
    // 如果后端查到了数据，就覆盖掉 healthForm 里的内容
    if (res) {
      healthForm.height = res.height
      healthForm.weight = res.weight
    }
  })
}

// 保存基本资料
const saveUserInfo = () => {
  loadingUser.value = true
  const payload = {
    gender: userForm.gender,
    birthday: userForm.birthday
  }
  if (userForm.password) {
    payload.password = userForm.password
  }

  request.post('/user/update', payload).then(res => {
    ElMessage.success('基本资料更新成功！')
    userForm.password = '' 
  }).finally(() => {
    loadingUser.value = false
  })
}

// 保存健康数据
const saveHealthData = () => {
  if (!userForm.birthday) {
    ElMessage.warning('请先在左侧保存您的出生日期！')
    return
  }
  if (!healthForm.height || !healthForm.weight) {
    ElMessage.warning('请输入有效的身高和体重！')
    return
  }
  
  loadingHealth.value = true
  request.post('/health/update', healthForm).then(res => {
    ElMessage.success('身体指标已记录，BMI与BMR计算完毕！')
    router.push('/dashboard')
  }).finally(() => {
    loadingHealth.value = false
  })
}

// 页面挂载时，同时请求基本资料和健康资料
onMounted(() => {
  fetchCurrentUser()
  fetchHealthData()
})
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
}

.profile-card {
  height: 100%;
  min-height: 400px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}
</style>