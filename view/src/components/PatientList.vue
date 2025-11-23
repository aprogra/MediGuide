<template>
  <div class="patient-list">
    <el-table 
      :data="processedPatients" 
      style="width: 100%" 
      v-loading="loading"
      @row-click="selectPatient"
    >
      <el-table-column prop="id" label="患者ID" width="150"></el-table-column>
      <el-table-column prop="name" label="姓名" width="120"></el-table-column>
      <el-table-column prop="symptoms" label="症状描述" min-width="400">
        <template #default="scope">
          <span v-if="scope.row.symptoms">
            {{ Array.isArray(scope.row.symptoms) ? scope.row.symptoms.join(', ') : scope.row.symptoms }}
          </span>
          <span v-else style="color: #999;">暂无症状描述</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'PatientList',
  props: {
    patientsData: {
      type: Array,
      default: () => []
    }
  },
  emits: ['select-patient', 'close'],
  setup(props, { emit }) {
    const loading = ref(false)
    
    // 默认mock数据，确保组件始终有内容显示
    const defaultPatients = [
      { id: 'P000001', name: '测试患者1', symptoms: '头痛、发热，持续两天' },
      { id: 'P000002', name: '测试患者2', symptoms: '腹痛，腹泻，昨天开始' },
      { id: 'P000003', name: '测试患者3', symptoms: '胸闷，气短，活动后加重' }
    ]
    
    // 处理传入的患者数据
    const processedPatients = computed(() => {
      // 检查数据有效性并返回适当的结果
      if (!Array.isArray(props.patientsData) || props.patientsData.length === 0) {
        return defaultPatients;
      }
      
      // 处理并格式化患者数据，将后端字段映射到前端字段
      const result = props.patientsData.map(patient => ({
        id: patient.num || '未知ID',  // 映射后端的num字段到前端的id
        name: patient.name || '未知患者',
        symptoms: patient.descriptions || patient.description || '暂无症状描述'  // 映射后端的descriptions或description字段到前端的symptoms
      }));
      
      return result.length > 0 ? result : defaultPatients;
    })

    const selectPatient = (patient) => {
      emit('select-patient', patient)
    }

    return {
      loading,
      processedPatients,
      selectPatient
    }
  }
}
</script>

<style scoped>
.patient-list {
  padding: 20px;
}
</style>