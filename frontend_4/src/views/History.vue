<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>历史计划记录</span>
        </div>
      </template>
      <el-empty v-if="!plans.length" description="暂无历史计划" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="plan in plans"
          :key="plan.id"
          :timestamp="plan.generatedAt"
          placement="top"
        >
          <el-card>
            <div class="plan-preview" v-html="renderMarkdown(getPreview(plan.content))" />
            <el-button type="primary" text @click="showDetail(plan)">查看完整计划</el-button>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="dialogVisible" title="计划详情" width="70%" top="5vh">
      <div class="plan-detail" v-html="renderMarkdown(currentPlan.content)" />
      <div style="margin-top:20px;padding:15px;background:#f5f7fa;border-radius:8px">
        <h4>生成时的体重上下文</h4>
        <p style="color:#606266;font-size:14px">{{ currentPlan.weightContext || '无记录' }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'
import { marked } from 'marked'

const plans = ref([])
const dialogVisible = ref(false)
const currentPlan = ref({})

const loadPlans = async () => {
  try {
    const res = await request.get('/plan/history')
    if (res.code === 200) {
      plans.value = res.data || []
    }
  } catch (e) {}
}

const showDetail = (plan) => {
  currentPlan.value = plan
  dialogVisible.value = true
}

const getPreview = (content) => {
  if (!content) return ''
  if (content.length <= 200) return content
  return content.substring(0, 200) + '...'
}

const renderMarkdown = (text) => {
  if (!text) return ''
  return marked.parse(text)
}

onMounted(() => {
  loadPlans()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.plan-preview {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  max-height: 120px;
  overflow: hidden;
}
.plan-detail {
  line-height: 1.8;
  max-height: 70vh;
  overflow-y: auto;
}

/* Markdown 渲染样式 */
.plan-preview :deep(h1),
.plan-detail :deep(h1) { 
  color: #409EFF; 
  border-bottom: 2px solid #409EFF; 
  padding-bottom: 10px; 
  font-size: 20px;
}

.plan-preview :deep(h2),
.plan-detail :deep(h2) { 
  color: #67C23A; 
  margin-top: 20px; 
  font-size: 18px;
}

.plan-preview :deep(h3),
.plan-detail :deep(h3) { 
  color: #E6A23C; 
  font-size: 16px;
}

.plan-preview :deep(strong),
.plan-detail :deep(strong) { 
  color: #303133; 
}

.plan-preview :deep(table),
.plan-detail :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 10px 0;
  font-size: 13px;
}

.plan-preview :deep(th),
.plan-detail :deep(th) {
  background: linear-gradient(135deg, #409EFF, #67C23A);
  color: #fff;
  padding: 8px 10px;
  text-align: left;
  border: 1px solid #dcdfe6;
}

.plan-preview :deep(td),
.plan-detail :deep(td) {
  padding: 6px 10px;
  border: 1px solid #e4e7ed;
  color: #606266;
}

.plan-preview :deep(tr:nth-child(even)),
.plan-detail :deep(tr:nth-child(even)) {
  background: #f5f7fa;
}

.plan-preview :deep(ul),
.plan-detail :deep(ul) {
  padding-left: 20px;
  margin: 8px 0;
}

.plan-preview :deep(li),
.plan-detail :deep(li) {
  margin: 6px 0;
}
</style>
