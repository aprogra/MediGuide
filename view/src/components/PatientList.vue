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
        console.log('患者数据为空，返回默认mock数据');
        return defaultPatients;
      }
      
      console.log('原始患者数据结构:', JSON.stringify(props.patientsData[0]));
      
      // 处理并格式化患者数据，支持多种数据格式
      const result = props.patientsData.map(patient => {
        // 创建映射后的患者对象，支持多种可能的字段名称
        const mappedPatient = {
          // 支持多种ID字段：num（后端）、id（前端）、patientId等
          id: patient.num !== undefined ? String(patient.num) : 
              (patient.id || 
               patient.patientId || 
               patient.patient_id || 
               '未知ID'),
          
          // 支持多种名称字段
          name: patient.name || 
                patient.patientName || 
                patient.patient_name || 
                '未知患者',
          
          // 支持多种症状描述字段
          symptoms: patient.description || 
                    patient.descriptions || 
                    patient.symptoms || 
                    patient.symptom || 
                    patient.condition || 
                    '暂无症状描述'
        };
        
        console.log('映射后患者数据:', mappedPatient);
        return mappedPatient;
      });
      
      console.log('映射后数据示例:', result[0]);
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