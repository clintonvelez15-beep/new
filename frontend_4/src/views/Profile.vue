<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>健康档案</span>
          <el-button type="primary" @click="saveProfile">保存档案</el-button>
        </div>
      </template>
      <el-form :model="profile" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年龄">
              <el-input-number v-model="profile.age" :min="1" :max="120" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="profile.gender" style="width:100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身高(cm)">
              <el-input-number v-model="profile.height" :min="50" :max="250" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重(kg)">
              <el-input-number v-model="profile.weight" :min="20" :max="300" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动水平">
              <el-select v-model="profile.activityLevel" style="width:100%">
                <el-option label="低（久坐少动）" value="低" />
                <el-option label="中（每周运动3-4次）" value="中" />
                <el-option label="高（每天运动或体力劳动）" value="高" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="饮食偏好">
              <el-select v-model="profile.dietPref" style="width:100%">
                <el-option label="素食" value="素食" />
                <el-option label="均衡饮食" value="均衡" />
                <el-option label="高蛋白" value="高蛋白" />
                <el-option label="低碳水" value="低碳水" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="健康目标">
          <el-select v-model="profile.goal" style="width:100%">
            <el-option label="减肥减脂" value="减肥" />
            <el-option label="增肌塑形" value="增肌" />
            <el-option label="保持健康" value="保持健康" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const profile = reactive({
  age: 20,
  gender: '男',
  height: 170,
  weight: 65,
  activityLevel: '中',
  dietPref: '均衡',
  goal: '保持健康'
})

onMounted(async () => {
  try {
    const res = await request.get('/profile')
    if (res.code === 200 && res.data) {
      Object.assign(profile, res.data)
    }
  } catch (e) {}
})

const saveProfile = async () => {
  try {
    const res = await request.post('/profile', profile)
    if (res.code === 200) {
      ElMessage.success('档案保存成功')
    } else {
      ElMessage.error(res.msg)
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
