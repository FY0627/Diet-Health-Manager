<template>
  <div class="food-container">
    <el-card shadow="hover">
      <div class="header-action">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="食物名称">
            <el-input 
              v-model="queryParams.keyword" 
              placeholder="请输入关键字搜索" 
              clearable 
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        
        <el-button type="success" :icon="Plus" @click="openAddDialog">
          添加自定义食物
        </el-button>
      </div>

      <el-table 
        v-loading="loading" 
        :data="tableData" 
        border 
        stripe 
        style="width: 100%; margin-top: 15px;"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="foodName" label="食物名称" min-width="150" />
        <el-table-column prop="calories" label="热量 (kcal/100g)" width="150" align="center" />
        <el-table-column prop="protein" label="蛋白质 (g/100g)" width="150" align="center" />
        <el-table-column prop="carbohydrate" label="碳水 (g/100g)" width="150" align="center" />
        <el-table-column prop="fat" label="脂肪 (g/100g)" width="150" align="center" />
      </el-table>

      <div class="pagination-box">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog 
      v-model="dialogVisible" 
      title="🍏 添加自定义食物 (每100克含量)" 
      width="500px"
      @close="resetForm"
    >
      <el-form :model="foodForm" :rules="rules" ref="foodFormRef" label-width="120px">
        <el-form-item label="食物名称" prop="foodName">
          <el-input v-model="foodForm.foodName" placeholder="例如：妈妈做的红烧肉" />
        </el-form-item>
        <el-form-item label="热量 (kcal)" prop="calories">
          <el-input-number v-model="foodForm.calories" :min="0" :step="10" />
        </el-form-item>
        <el-form-item label="蛋白质 (g)" prop="protein">
          <el-input-number v-model="foodForm.protein" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="碳水化合物 (g)" prop="carbohydrate">
          <el-input-number v-model="foodForm.carbohydrate" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="脂肪 (g)" prop="fat">
          <el-input-number v-model="foodForm.fat" :min="0" :step="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddFood" :loading="submitLoading">确定添加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

// ========== 表格与分页逻辑 ==========
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  keyword: '',
  current: 1,
  size: 10
})

// 获取食物列表数据
const fetchFoodList = () => {
  loading.value = true
  request.post('/food/list', queryParams).then(res => {
    // MyBatis-Plus 分页插件返回的结构里有 records 和 total
    tableData.value = res.records || []
    total.value = res.total || 0
  }).finally(() => {
    loading.value = false
  })
}

// 搜索按钮
const handleSearch = () => {
  queryParams.current = 1 // 搜索时重置为第一页
  fetchFoodList()
}

// 重置按钮
const resetSearch = () => {
  queryParams.keyword = ''
  handleSearch()
}

// 切换每页显示条数
const handleSizeChange = (newSize) => {
  queryParams.size = newSize
  fetchFoodList()
}

// 切换页码
const handleCurrentChange = (newPage) => {
  queryParams.current = newPage
  fetchFoodList()
}

// ========== 添加食物弹窗逻辑 ==========
const dialogVisible = ref(false)
const submitLoading = ref(false)
const foodFormRef = ref(null)

const foodForm = reactive({
  foodName: '',
  calories: 0,
  protein: 0,
  carbohydrate: 0,
  fat: 0
})

const rules = reactive({
  foodName: [{ required: true, message: '请输入食物名称', trigger: 'blur' }],
  calories: [{ required: true, message: '请输入热量', trigger: 'blur' }]
})

// 打开弹窗
const openAddDialog = () => {
  dialogVisible.value = true
}

// 关闭弹窗时重置表单
const resetForm = () => {
  if (foodFormRef.value) {
    foodFormRef.value.resetFields()
  }
}

// 提交添加食物
const submitAddFood = () => {
  foodFormRef.value.validate((valid) => {
    if (valid) {
      submitLoading.value = true
      request.post('/food/add', foodForm).then(res => {
        ElMessage.success('自定义食物添加成功！')
        dialogVisible.value = false
        handleSearch() // 刷新列表，显示最新添加的食物
      }).finally(() => {
        submitLoading.value = false
      })
    }
  })
}

// 页面加载时自动查询一次
onMounted(() => {
  fetchFoodList()
})
</script>

<style scoped>
.header-action {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>