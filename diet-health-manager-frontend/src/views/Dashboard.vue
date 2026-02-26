<template>
  <div class="dashboard-container">
    <div class="header-control">
      <el-date-picker
        v-model="selectedDate"
        type="date"
        placeholder="选择日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        @change="fetchDashboardData"
        :clearable="false"
      />
      <div style="flex: 1; margin-left: 20px;">
        <el-alert 
          v-if="dashboardData.advice" 
          :title="dashboardData.advice" 
          :type="getAlertType(dashboardData.remainingCalories)" 
          show-icon 
          :closable="false"
        />
      </div>
    </div>

    <div v-if="hasGoal">
      <el-row :gutter="20" class="data-row">
        <el-col :span="6">
          <el-card shadow="hover" class="data-card">
            <template #header><div class="card-header">🔥 总热量 (kcal)</div></template>
            <div class="card-content">
              <h2>{{ dashboardData.actualCalories }} / {{ dashboardData.targetCalories }}</h2>
              <el-progress 
                :percentage="calcPercent(dashboardData.actualCalories, dashboardData.targetCalories)" 
                :color="customColors" 
                :stroke-width="12"
              />
              <p class="remaining">剩余: {{ dashboardData.remainingCalories }}</p>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover" class="data-card">
            <template #header><div class="card-header">🥩 蛋白质 (g)</div></template>
            <div class="card-content">
              <h2>{{ dashboardData.actualProtein }} / {{ dashboardData.targetProtein }}</h2>
              <el-progress :percentage="calcPercent(dashboardData.actualProtein, dashboardData.targetProtein)" color="#e6a23c" :stroke-width="12"/>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover" class="data-card">
            <template #header><div class="card-header">🍞 碳水化合物 (g)</div></template>
            <div class="card-content">
              <h2>{{ dashboardData.actualCarbohydrate }} / {{ dashboardData.targetCarbohydrate }}</h2>
              <el-progress :percentage="calcPercent(dashboardData.actualCarbohydrate, dashboardData.targetCarbohydrate)" color="#67c23a" :stroke-width="12"/>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card shadow="hover" class="data-card">
            <template #header><div class="card-header">🥑 脂肪 (g)</div></template>
            <div class="card-content">
              <h2>{{ dashboardData.actualFat }} / {{ dashboardData.targetFat }}</h2>
              <el-progress :percentage="calcPercent(dashboardData.actualFat, dashboardData.targetFat)" color="#f56c6c" :stroke-width="12"/>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="data-row">
        <el-col :span="8">
          <el-card shadow="hover" style="height: 100%;">
            <template #header><div class="card-header">❤️ 身体基础指标</div></template>
            <div class="health-data-box">
              <el-statistic title="基础代谢率 (BMR)" :value="dashboardData.bmr || 0" suffix="kcal" />
              <el-divider direction="vertical" style="height: 50px;" />
              <el-statistic title="体质指数 (BMI)" :value="dashboardData.bmi || 0" />
            </div>
          </el-card>
        </el-col>

        <el-col :span="16">
          <el-card shadow="hover" style="height: 100%;">
            <template #header><div class="card-header">🍽️ 餐别热量摄入</div></template>
            <div class="meal-distribution">
              <div class="meal-item">
                <span>早餐</span>
                <h3>{{ dashboardData.breakfastCalories || 0 }} kcal</h3>
              </div>
              <div class="meal-item">
                <span>午餐</span>
                <h3>{{ dashboardData.lunchCalories || 0 }} kcal</h3>
              </div>
              <div class="meal-item">
                <span>晚餐</span>
                <h3>{{ dashboardData.dinnerCalories || 0 }} kcal</h3>
              </div>
              <div class="meal-item">
                <span>加餐</span>
                <h3>{{ dashboardData.snackCalories || 0 }} kcal</h3>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-else description="您还未设置每日营养目标，无法生成数据看板">
      <el-button type="primary" size="large" @click="router.push('/goal')">去设置目标</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

// 获取今天的日期 YYYY-MM-DD
const getToday = () => {
  const d = new Date()
  const month = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  return `${d.getFullYear()}-${month}-${day}`
}

const selectedDate = ref(getToday())
const dashboardData = ref({})
const hasGoal = ref(true)

// 请求后端分析数据接口
const fetchDashboardData = () => {
  request.get(`/analysis/daily-board?date=${selectedDate.value}`).then(res => {
    dashboardData.value = res
    hasGoal.value = true
  }).catch(err => {
    // 如果后端抛出“请先设置目标”的异常，我们就显示空状态让用户去设置
    if (err && err.includes('目标')) {
      hasGoal.value = false
    }
  })
}

// 计算进度条百分比 (不能超过100%)
const calcPercent = (actual, target) => {
  if (!target || target === 0) return 0
  const percent = Math.round((actual / target) * 100)
  return percent > 100 ? 100 : percent
}

// 动态计算进度条颜色 (超过100%变红预警)
const customColors = [
  { color: '#5cb87a', percentage: 80 },
  { color: '#e6a23c', percentage: 100 },
  { color: '#f56c6c', percentage: 101 }
]

// 动态判断警告条类型
const getAlertType = (remaining) => {
  if (remaining < 0) return 'error'
  if (remaining < 200) return 'warning'
  return 'success'
}

// 页面加载时请求一次数据
onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.dashboard-container {
  height: 100%;
}

.header-control {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.data-row {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  color: #303133;
}

.card-content {
  text-align: center;
}

.card-content h2 {
  margin: 0 0 15px 0;
  color: #409EFF;
  font-size: 24px;
}

.remaining {
  font-size: 13px;
  color: #909399;
  margin-top: 10px;
}

.health-data-box {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px 0;
}

.meal-distribution {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
}

.meal-item {
  text-align: center;
  background: #f4f4f5;
  padding: 20px 30px;
  border-radius: 8px;
  min-width: 100px;
}

.meal-item span {
  color: #909399;
  font-size: 14px;
}

.meal-item h3 {
  margin: 10px 0 0 0;
  color: #303133;
}
</style>