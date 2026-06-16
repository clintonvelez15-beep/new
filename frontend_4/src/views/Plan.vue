<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>AI 健康计划生成</span>
        </div>
      </template>

      <!-- 本周计划偏好设置 -->
      <el-card shadow="never" style="margin-bottom: 20px; background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%); border: 1px solid #d9ecff;">
        <template #header>
          <div style="display: flex; align-items: center; gap: 8px; color: #409EFF;">
            <el-icon><Setting /></el-icon>
            <span style="font-weight: 600;">本周计划偏好（可选）</span>
          </div>
        </template>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="8">
            <el-form-item label="运动强度">
              <el-select 
                v-model="preferences.intensity" 
                placeholder="默认适中" 
                clearable 
                style="width: 100%"
              >
                <el-option label="🔥 提高强度" value="high">
                  <span style="color: #f56c6c;">🔥 提高强度</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">增加挑战性训练</span>
                </el-option>
                <el-option label="🌿 降低强度" value="low">
                  <span style="color: #67c23a;">🌿 降低强度</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">恢复性训练为主</span>
                </el-option>
                <el-option label="⚖️ 保持适中" value="normal">
                  <span style="color: #409eff;">⚖️ 保持适中</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">标准训练方案</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="运动时长">
              <el-select 
                v-model="preferences.duration" 
                placeholder="默认正常" 
                clearable 
                style="width: 100%"
              >
                <el-option label="⏱️ 延长时长" value="long">
                  <span style="color: #e6a23c;">⏱️ 延长时长</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">每天+15-30分钟</span>
                </el-option>
                <el-option label="⚡ 缩短时长" value="short">
                  <span style="color: #409eff;">⚡ 缩短时长</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">控制在30分钟内</span>
                </el-option>
                <el-option label="⏰ 保持正常" value="normal">
                  <span style="color: #67c23a;">⏰ 保持正常</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">45-60分钟</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="8">
            <el-form-item label="饮食倾向">
              <el-select 
                v-model="preferences.diet" 
                placeholder="默认均衡" 
                clearable 
                style="width: 100%"
              >
                <el-option label="🥗 增加素食" value="more_vege">
                  <span style="color: #67c23a;">🥗 增加素食</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">多吃蔬果豆类</span>
                </el-option>
                <el-option label="🥩 增加肉食" value="more_meat">
                  <span style="color: #f56c6c;">🥩 增加肉食</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">高蛋白增肌</span>
                </el-option>
                <el-option label="🍽️ 保持均衡" value="normal">
                  <span style="color: #409eff;">🍽️ 保持均衡</span>
                  <span style="color: #909399; font-size: 12px; margin-left: 8px;">荤素搭配合理</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-alert 
          v-if="hasPreferences" 
          :title="preferenceSummary" 
          type="info" 
          :closable="false"
          style="margin-top: 10px;"
        />
      </el-card>

      <div style="text-align: center; margin: 30px 0;">
        <el-button 
          type="primary" 
          size="large"
          :loading="loading" 
          :disabled="loading || countdown > 0"
          @click="generatePlan"
          style="width: 220px; height: 50px; font-size: 16px; border-radius: 25px;"
        >
          <el-icon style="margin-right: 8px;"><MagicStick /></el-icon>
          {{ buttonText }}
        </el-button>
        <p v-if="loading" style="color: #909399; margin-top: 15px; font-size: 14px;">
          <el-icon class="is-loading"><Loading /></el-icon>
          正在分析您的健康档案，生成个性化计划...
        </p>
      </div>

      <!-- 当前计划显示区域（保留上次生成的计划） -->
      <div v-if="currentPlan" class="current-plan-section">
        <el-divider content-position="left">
          <el-tag type="success" effect="dark">当前计划</el-tag>
        </el-divider>
        <div class="plan-report" v-html="renderMarkdown(currentPlan)"></div>
      </div>

      <div v-if="!currentPlan && !loading" class="empty-tip">
        <el-icon :size="60" color="#dcdfe6"><Document /></el-icon>
        <p style="color: #606266; margin-top: 20px; font-size: 16px;">点击上方按钮，AI将为您生成个性化健康计划</p>
        <p style="color:#909399; font-size: 14px; margin-top: 10px;">包含核心分析、饮食计划、运动建议和调整方案</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { marked } from 'marked'

const currentPlan = ref('')  // 保留当前显示的计划
const loading = ref(false)
const countdown = ref(0)     // 冷却倒计时

const preferences = reactive({
  intensity: '',
  duration: '',
  diet: ''
})

const hasPreferences = computed(() => {
  return preferences.intensity || preferences.duration || preferences.diet
})

const preferenceSummary = computed(() => {
  const parts = []
  if (preferences.intensity) {
    const map = { high: '提高强度', low: '降低强度', normal: '适中强度' }
    parts.push(`运动强度：${map[preferences.intensity]}`)
  }
  if (preferences.duration) {
    const map = { long: '延长时长', short: '缩短时长', normal: '正常时长' }
    parts.push(`运动时长：${map[preferences.duration]}`)
  }
  if (preferences.diet) {
    const map = { more_vege: '增加素食', more_meat: '增加肉食', normal: '均衡饮食' }
    parts.push(`饮食倾向：${map[preferences.diet]}`)
  }
  return '本周偏好：' + parts.join(' | ')
})

const buttonText = computed(() => {
  if (loading.value) return '生成中...'
  if (countdown.value > 0) return `等待 ${countdown.value} 秒`
  return '生成健康计划'
})

const renderMarkdown = (text) => {
  return text ? marked.parse(text) : ''
}

const generatePlan = async () => {
  if (loading.value || countdown.value > 0) return

  loading.value = true
  try {
    const params = new URLSearchParams()
    if (preferences.intensity) params.append('intensity', preferences.intensity)
    if (preferences.duration) params.append('duration', preferences.duration)
    if (preferences.diet) params.append('diet', preferences.diet)

    const queryString = params.toString() ? '?' + params.toString() : ''
    const res = await request.get(`/plan/generate${queryString}`)

    if (res.code === 200) {
      // 检查是否频率限制
      if (res.data && res.data.startsWith('ERROR:FREQUENCY_LIMIT:')) {
        const minutes = res.data.split(':')[2]
        ElMessage.warning(minutes)
        // 启动倒计时
        countdown.value = parseInt(minutes) * 60
        const timer = setInterval(() => {
          countdown.value--
          if (countdown.value <= 0) clearInterval(timer)
        }, 1000)
      } else {
        currentPlan.value = res.data
        ElMessage.success('健康计划生成成功！')
      }
    } else {
      ElMessage.error(res.msg || '生成失败')
    }
  } catch (e) {
    ElMessage.error('服务异常，请检查网络或稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header span {
  font-size: 18px;
  font-weight: 600;
}
.empty-tip {
  text-align: center;
  padding: 60px 0;
}

/* 当前计划区域 */
.current-plan-section {
  margin-top: 20px;
}

/* 报告样式优化 */
.plan-report {
  padding: 25px;
  background: #fff;
  border-radius: 12px;
  line-height: 1.8;
  border: 1px solid #e4e7ed;
}

.plan-report :deep(h1) { 
  color: #1a1a2e; 
  font-size: 26px;
  font-weight: 700;
  border-bottom: 3px solid #409EFF; 
  padding-bottom: 15px; 
  margin-bottom: 25px;
}

.plan-report :deep(h2) { 
  color: #409EFF; 
  font-size: 20px;
  font-weight: 600;
  margin-top: 30px; 
  margin-bottom: 15px;
  border-left: 4px solid #409EFF;
  padding-left: 15px;
}

.plan-report :deep(h3) { 
  color: #67C23A; 
  font-size: 17px;
  font-weight: 600;
  margin-top: 20px;
  margin-bottom: 12px;
}

.plan-report :deep(strong) { 
  color: #303133; 
  font-weight: 600;
}

/* 表格样式优化 */
.plan-report :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
  font-size: 14px;
}

.plan-report :deep(th) {
  background: linear-gradient(135deg, #409EFF, #67C23A);
  color: #fff;
  padding: 12px 15px;
  text-align: left;
  font-weight: 600;
  border: 1px solid #dcdfe6;
}

.plan-report :deep(td) {
  padding: 10px 15px;
  border: 1px solid #e4e7ed;
  color: #606266;
  line-height: 1.6;
}

.plan-report :deep(tr:nth-child(even)) {
  background: #f5f7fa;
}

.plan-report :deep(tr:hover) {
  background: #ecf5ff;
}

.plan-report :deep(ul) {
  padding-left: 20px;
  margin: 10px 0;
}

.plan-report :deep(li) {
  margin: 8px 0;
  line-height: 1.8;
}

.plan-report :deep(p) {
  margin: 12px 0;
  color: #606266;
}
</style>
