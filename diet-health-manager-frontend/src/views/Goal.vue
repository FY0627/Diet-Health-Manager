<template>
  <div class="goal-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>🎯 设置每日营养目标</span>
        </div>
      </template>

      <el-form 
        :model="goalForm" 
        :rules="rules" 
        ref="goalFormRef" 
        label-width="140px" 
        class="goal-form"
      >
        <el-alert 
          title="合理的营养目标是健康管理的第一步！如果您不知道怎么填，建议先去【个人健康档案】计算您的基础代谢率(BMR)。" 
          type="info" 
          show-icon 
          style="margin-bottom: 25px;"
          :closable="false"
        />

        <el-form-item label="🔥 总热量 (kcal)" prop="targetCalories">
          <el-input-number v-model="goalForm.targetCalories" :min="500" :max="5000" :step="100" size="large" />
          <span class="unit-text">千卡 / 天</span>
        </el-form-item>

        <el-form-item label="🥩 蛋白质 (g)" prop="targetProtein">
          <el-input-number v-model="goalForm.targetProtein" :min="10" :max="300" :step="5" size="large" />
          <span class="unit-text">克 / 天</span>
        </el-form-item>

        <el-form-item label="🍞 碳水化合物 (g)" prop="targetCarbohydrate">
          <el-input-number v-model="goalForm.targetCarbohydrate" :min="10" :max="500" :step="10" size="large" />
          <span class="unit-text">克 / 天</span>
        </el-form-item>

        <el-form-item label="🥑 脂肪 (g)" prop="targetFat">
          <el-input-number v-model="goalForm.targetFat" :min="10" :max="200" :step="5" size="large" />
          <span class="unit-text">克 / 天</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="submitGoal" :loading="loading" class="submit-btn">
            保存并生效
          </el-button>
          <el-button size="large" @click="router.push('/dashboard')">返回看板</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const goalFormRef = ref(null)
const loading = ref(false)

// 表单数据
const goalForm = reactive({
  targetCalories: 2000,
  targetProtein: 60,
  targetCarbohydrate: 250,
  targetFat: 60
})

// 简单的必填校验规则
const rules = reactive({
  targetCalories: [{ required: true, message: '请输入目标热量', trigger: 'blur' }],
  targetProtein: [{ required: true, message: '请输入目标蛋白质', trigger: 'blur' }],
  targetCarbohydrate: [{ required: true, message: '请输入目标碳水', trigger: 'blur' }],
  targetFat: [{ required: true, message: '请输入目标脂肪', trigger: 'blur' }]
})

// 页面加载时，尝试获取用户已经设置过的目标
const fetchExistingGoal = () => {
  request.get('/goal/info').then(res => {
    if (res) {
      // 如果后端有数据，就回显到表单上
      goalForm.targetCalories = res.targetCalories
      goalForm.targetProtein = res.targetProtein
      goalForm.targetCarbohydrate = res.targetCarbohydrate
      goalForm.targetFat = res.targetFat
    }
  }).catch(err => {
    // 这里会捕获到后端的 "尚未设置营养目标" 异常，我们不需要做特殊处理，直接让用户填默认值即可
    console.log("尚未设置目标，使用默认值")
  })
}

// 提交表单
const submitGoal = () => {
  goalFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      request.post('/goal/update', goalForm).then(res => {
        ElMessage.success('营养目标设置成功！')
        // 设置成功后，自动跳转回看板页查看效果
        router.push('/dashboard')
      }).finally(() => {
        loading.value = false
      })
    }
  })
}

onMounted(() => {
  fetchExistingGoal()
})
</script>

<style scoped>
.goal-container {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.goal-form {
  margin-top: 20px;
}

.unit-text {
  margin-left: 15px;
  color: #909399;
  font-size: 14px;
}

.submit-btn {
  width: 150px;
}
</style>