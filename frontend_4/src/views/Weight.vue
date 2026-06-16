<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>记录体重</template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="体重(kg)">
              <el-input-number v-model="form.weight" :min="20" :max="300" :precision="1" style="width:100%" />
            </el-form-item>
            <el-form-item label="日期">
              <el-date-picker v-model="form.recordDate" type="date" style="width:100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="addWeight" style="width:100%">添加记录</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>体重变化趋势</template>
          <div ref="chartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:20px">
      <template #header>历史记录</template>
      <el-table :data="records" stripe>
        <el-table-column prop="recordDate" label="日期" width="180" />
        <el-table-column prop="weight" label="体重(kg)" width="180" />
        <el-table-column label="变化">
          <template #default="scope">
            <el-tag v-if="scope.row.change > 0" type="danger">+{{ scope.row.change }}kg</el-tag>
            <el-tag v-else-if="scope.row.change < 0" type="success">{{ scope.row.change }}kg</el-tag>
            <el-tag v-else type="info">-</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import * as echarts from 'echarts'

const form = reactive({ weight: 65, recordDate: new Date() })
const records = ref([])
const chartRef = ref(null)
let chart = null

const loadData = async () => {
  try {
    const res = await request.get('/weight/history')
    if (res.code === 200) {
      const list = res.data || []
      // 计算变化
      for (let i = 0; i < list.length; i++) {
        if (i < list.length - 1) {
          list[i].change = parseFloat((list[i].weight - list[i+1].weight).toFixed(1))
        } else {
          list[i].change = 0
        }
      }
      records.value = list
      nextTick(() => initChart(list))
    }
  } catch (e) {}
}

const initChart = (data) => {
  if (!chartRef.value) return
  if (chart) chart.dispose()
  chart = echarts.init(chartRef.value)
  const xData = data.map(d => d.recordDate).reverse()
  const yData = data.map(d => d.weight).reverse()
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: xData },
    yAxis: { type: 'value', name: 'kg' },
    series: [{
      data: yData,
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64,158,255,0.2)' },
      itemStyle: { color: '#409EFF' }
    }]
  })
}

const addWeight = async () => {
  try {
    const dateStr = form.recordDate.toISOString().split('T')[0]
    const res = await request.post('/weight', {
      weight: form.weight,
      recordDate: dateStr
    })
    if (res.code === 200) {
      ElMessage.success('记录成功')
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    ElMessage.error('记录失败')
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', () => chart?.resize())
})
</script>
