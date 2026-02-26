<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <h2>🍏 饮食健康管理</h2>
      </div>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#304156"
        text-color="#bfcbd9"
        :default-active="route.path"
        router
        class="menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>综合数据看板</span>
        </el-menu-item>
        
        <el-menu-item index="/diet">
          <el-icon><Notebook /></el-icon>
          <span>我的饮食记录</span>
        </el-menu-item>

        <el-menu-item index="/food">
          <el-icon><Apple /></el-icon>
          <span>公共食物库</span>
        </el-menu-item>

        <el-menu-item index="/goal">
          <el-icon><Aim /></el-icon>
          <span>营养目标设置</span>
        </el-menu-item>

        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人健康档案</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="welcome-text">欢迎回来！开始今天的健康自律之旅吧。</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link user-dropdown">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span class="username">我的账户</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 处理右上角下拉菜单点击事件
const handleCommand = (command) => {
  if (command === 'logout') {
    // 简单的退出逻辑：提示并跳回登录页
    ElMessage.success('已安全退出')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3649;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  color: #fff;
}

.menu {
  flex: 1;
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left .welcome-text {
  font-size: 14px;
  color: #606266;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>