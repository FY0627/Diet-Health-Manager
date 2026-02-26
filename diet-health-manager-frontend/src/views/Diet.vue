<template>
  <div class="diet-container">
    <el-card shadow="hover">
      <div class="header-action">
        <div class="left">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="切换日期查看"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="fetchDailyRecords"
            :clearable="false"
          />
          <span class="date-tip">共查到 {{ tableData.length }} 条记录</span>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAddDialog">记一笔饮食</el-button>
      </div>

      <el-table :data="tableData" border stripe style="width: 100%; margin-top: 20px;">
        <el-table-column prop="recordDate" label="日期" width="120" align="center" />
        <el-table-column label="餐别" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getMealTag(scope.row.mealType)">
              {{ getMealName(scope.row.mealType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="foodNameSnapshot" label="食物名称" min-width="150" />
        <el-table-column prop="quantity" label="摄入量 (g/ml)" width="120" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="scope">
            <el-popconfirm title="确定要删除这条记录吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button type="danger" :icon="Delete" circle size="small" />
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="🍽️ 记录今日饮食" width="500px">
      <el-form :model="recordForm" :rules="rules" ref="recordFormRef" label-width="100px">
        <el-form-item label="选择食物" prop="foodId">
          <el-select
            v-model="recordForm.foodId"
            filterable
            remote
            reserve-keyword
            placeholder="输入食物名称搜索"
            :remote-method="remoteSearchFood"
            :loading="searchLoading"
            style="width: 100%"
          >
            <el-option
              v-for="item in foodOptions"
              :key="item.id"
              :label="item.foodName"
              :value="item.id"
            >
              <span style="float: left">{{ item.foodName }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                {{ item.calories }} kcal/100g
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="摄入量(g)" prop="quantity">
          <el-input-number v-model="recordForm.quantity" :min="1" :max="5000" style="width: 100%" />
        </el-form-item>

        <el-form-item label="餐别" prop="mealType">
          <el-select v-model="recordForm.mealType" placeholder="请选择" style="width: 100%">
            <el-option label="早餐" :value="0" />
            <el-option label="午餐" :value="1" />
            <el-option label="晚餐" :value="2" />
            <el-option label="加餐" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="日期" prop="recordDate">
          <el-date-picker
            v-model="recordForm.recordDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecord" :loading="submitting">保存记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

// 获取今天日期
const getToday = () => new Date().toISOString().split('T')[0]

const selectedDate = ref(getToday())
const tableData = ref([])
const dialogVisible = ref(false)
const submitting = ref(false)
const searchLoading = ref(false)
const foodOptions = ref([])
const recordFormRef = ref(null)

const recordForm = reactive({
  foodId: null,
  quantity: 100,
  mealType: 0,
  recordDate: getToday()
})

const rules = {
  foodId: [{ required: true, message: '请选择食物', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入摄入量', trigger: 'blur' }],
  mealType: [{ required: true, message: '请选择餐别', trigger: 'change' }]
}

// 1. 获取每日记录
const fetchDailyRecords = () => {
  request.get(`/diet/daily?date=${selectedDate.value}`).then(res => {
    tableData.value = res || []
  })
}

// 2. 远程搜索食物 (对接后端的 /food/list 接口)
const remoteSearchFood = (query) => {
  if (query) {
    searchLoading.value = true
    request.post('/food/list', { keyword: query, current: 1, size: 20 }).then(res => {
      foodOptions.value = res.records
    }).finally(() => {
      searchLoading.value = false
    })
  } else {
    foodOptions.value = []
  }
}

// 3. 提交记录
const submitRecord = () => {
  recordFormRef.value.validate(valid => {
    if (valid) {
      submitting.value = true
      request.post('/diet/add', recordForm).then(() => {
        ElMessage.success('记录成功！')
        dialogVisible.value = false
        // 如果记录的是选中的日期，刷新列表
        if (recordForm.recordDate === selectedDate.value) {
          fetchDailyRecords()
        }
      }).finally(() => {
        submitting.value = false
      })
    }
  })
}

// 4. 删除记录
const handleDelete = (id) => {
  request.delete(`/diet/delete/${id}`).then(() => {
    ElMessage.success('已删除')
    fetchDailyRecords()
  })
}

// 辅助函数：餐别翻译
const getMealName = (type) => ['早餐', '午餐', '晚餐', '加餐'][type]
const getMealTag = (type) => ['', 'success', 'warning', 'info'][type]

const openAddDialog = () => {
  dialogVisible.value = true
  // 重置表单，日期默认为当前选择的日期
  recordForm.recordDate = selectedDate.value
}

onMounted(fetchDailyRecords)
</script>

<style scoped>
.header-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.date-tip {
  margin-left: 15px;
  font-size: 14px;
  color: #909399;
}
</style>