<template>
  <div>
    <h1>测试页面</h1>
    <el-button @click="testApi">测试API</el-button>
    <el-button @click="testComponent">测试组件</el-button>
    <div>
      <h2>API返回结果:</h2>
      <pre>{{ apiResult }}</pre>
    </div>
    <div>
      <h2>组件测试:</h2>
      <patient-list 
        :patients-data="testPatients" 
        @select-patient="handleSelectPatient"
      />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { getDoctorPatients } from '../services/api.js'
import PatientList from '../components/PatientList.vue'

export default {
  name: 'TestPage',
  components: {
    PatientList
  },
  setup() {
    const apiResult = ref('')
    // 初始化testPatients为默认数据，用于立即测试组件显示
    const testPatients = ref([
      { id: 'P000001', name: '初始测试患者1', symptoms: '头痛、发热，持续两天' },
      { id: 'P000002', name: '初始测试患者2', symptoms: '腹痛，腹泻，昨天开始' },
      { id: 'P000003', name: '初始测试患者3', symptoms: '胸闷，气短，活动后加重' }
    ])

    // 测试特定医生ID（20236734）的患者列表API
    const DOCTOR_ID = 20236734; // 测试目标医生ID

    const testApi = async () => {
      try {
        console.log(`测试医生ID: ${DOCTOR_ID} 的患者列表API`);
        const result = await getDoctorPatients(DOCTOR_ID)
        apiResult.value = JSON.stringify(result, null, 2)
        console.log('API测试结果:', result)
      } catch (error) {
        const errorMsg = `Error: ${error.message}\n详细信息: ${error.response?.data || ''}`;
        apiResult.value = errorMsg
        console.error('API测试错误:', error)
      }
    }

    const testComponent = async () => {
      try {
        console.log(`testComponent方法开始执行，测试医生ID: ${DOCTOR_ID}`);
        const patients = await getDoctorPatients(DOCTOR_ID)
        console.log('getDoctorPatients返回数据:', patients, '类型:', typeof patients, '是否数组:', Array.isArray(patients));
        
        // 处理不同格式的响应数据
        if (Array.isArray(patients)) {
          testPatients.value = patients;
        } else if (patients && Array.isArray(patients.patients)) {
          testPatients.value = patients.patients; // 假设后端可能返回{patients: []}格式
        } else {
          testPatients.value = [];
          console.warn('返回的数据不是预期的数组格式:', patients);
        }
        
        console.log('testPatients更新后的值:', testPatients.value.length, '条记录');
        console.log('testPatients第一条数据:', testPatients.value.length > 0 ? JSON.stringify(testPatients.value[0]) : '无数据');
      } catch (error) {
        console.error('组件测试错误:', error)
        testPatients.value = [] // 出错时设置为空数组
        // 显示错误信息
        apiResult.value = `组件测试错误: ${error.message}`;
      }
    }

    const handleSelectPatient = (patient) => {
      console.log('选中患者:', patient)
    }

    return {
      apiResult,
      testPatients,
      testApi,
      testComponent,
      handleSelectPatient
    }
  }
}
</script>