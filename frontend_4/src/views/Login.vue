<template>
  <div class="login-container">
    <!-- 动态背景粒子 -->
    <div class="particles">
      <span v-for="n in 20" :key="n" :style="particleStyle(n)"></span>
    </div>

    <!-- 左侧品牌区 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
        </div>
        <h1>智能健康助手</h1>
        <p>AI驱动的个性化饮食与运动规划</p>
        <div class="features">
          <div class="feature-item">
            <el-icon><Apple /></el-icon>
            <span>智能饮食</span>
          </div>
          <div class="feature-item">
            <el-icon><Basketball /></el-icon>
            <span>运动规划</span>
          </div>
          <div class="feature-item">
            <el-icon><TrendCharts /></el-icon>
            <span>体重追踪</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="form-section">
      <el-card class="login-card" shadow="never">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>登录您的健康账户</p>
        </div>

        <el-form :model="form" class="login-form">
          <el-form-item>
            <div class="input-wrapper">
              <el-icon class="input-icon"><UserFilled /></el-icon>
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名"
                size="large"
                clearable
              />
            </div>
          </el-form-item>

          <el-form-item>
            <div class="input-wrapper">
              <el-icon class="input-icon"><Lock /></el-icon>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </div>
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              size="large"
              :loading="loading"
              @click="handleLogin" 
              class="login-btn"
            >
              立即登录
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>还没有账号？</span>
            <el-link type="primary" @click="$router.push('/register')" :underline="'never'">
              立即注册
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Apple, Basketball, TrendCharts, UserFilled, Lock } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)

const particleStyle = (n) => {
  const size = Math.random() * 6 + 2
  const left = Math.random() * 100
  const delay = Math.random() * 15
  const duration = Math.random() * 10 + 10
  return {
    width: size + 'px',
    height: size + 'px',
    left: left + '%',
    animationDelay: delay + 's',
    animationDuration: duration + 's'
  }
}

const handleLogin = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/user/login', form)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      ElMessage.success('登录成功')
      router.push('/app')
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查网络')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.particles span {
  position: absolute;
  background: rgba(64, 158, 255, 0.3);
  border-radius: 50%;
  animation: float-up linear infinite;
}

@keyframes float-up {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-10vh) scale(1);
    opacity: 0;
  }
}

.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 1;
}

.brand-content {
  color: #fff;
  max-width: 480px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  box-shadow: 0 10px 40px rgba(64, 158, 255, 0.3);
}

.logo-icon svg {
  width: 40px;
  height: 40px;
  color: #fff;
}

.brand-content h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fff, #a8d8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-content > p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 50px;
  line-height: 1.6;
}

.features {
  display: flex;
  gap: 30px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.feature-item .el-icon {
  font-size: 28px;
  color: #409EFF;
  background: rgba(64, 158, 255, 0.15);
  padding: 12px;
  border-radius: 12px;
}

.form-section {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: none;
}

:deep(.el-card__body) {
  padding: 40px;
}

.form-header {
  text-align: center;
  margin-bottom: 35px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.form-header p {
  color: #909399;
  font-size: 14px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f7fa;
  border-radius: 12px;
  padding: 0 16px;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.input-wrapper:focus-within {
  background: #fff;
  border-color: #409EFF;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1);
}

.input-icon {
  font-size: 20px;
  color: #909399;
  margin-right: 12px;
  flex-shrink: 0;
}

:deep(.el-input__wrapper) {
  background: transparent !important;
  box-shadow: none !important;
  padding: 0 !important;
}

:deep(.el-input__inner) {
  height: 48px;
  font-size: 15px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.4);
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #606266;
  font-size: 14px;
}

.form-footer .el-link {
  font-weight: 600;
  margin-left: 4px;
}

@media (max-width: 900px) {
  .brand-section {
    display: none;
  }
  .form-section {
    flex: 1;
  }
}
</style>
