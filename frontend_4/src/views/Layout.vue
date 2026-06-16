<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background: #304156">
      <div class="logo">智能健康助手</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        style="border: none"
      >
        <el-menu-item index="/app/profile">
          <el-icon><User /></el-icon>
          <span>健康档案</span>
        </el-menu-item>
        <el-menu-item index="/app/plan">
          <el-icon><Document /></el-icon>
          <span>AI计划生成</span>
        </el-menu-item>
        <el-menu-item index="/app/weight">
          <el-icon><TrendCharts /></el-icon>
          <span>体重追踪</span>
        </el-menu-item>
        <el-menu-item index="/app/history">
          <el-icon><Clock /></el-icon>
          <span>历史记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #eee">
        <span style="font-size:18px;font-weight:bold">智能健康助手</span>
        <div>
          <span style="margin-right:15px">{{ username }}</span>
          <el-button type="danger" size="small" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '用户')

const logout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
</style>
