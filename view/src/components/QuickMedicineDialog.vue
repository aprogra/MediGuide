<template>
  <el-dialog
    v-model="dialogVisible"
    title="快速配药"
    width="700px"
    @close="handleClose"
  >
    <el-table :data="medicines" border style="width: 100%">
      <el-table-column prop="name" label="药品名称" width="180">
        <template #default="{ row }">
          <el-input
            v-model="row.name"
            placeholder="请输入药品名称"
            clearable
            style="width: 150px"
          />
        </template>
      </el-table-column>

      <el-table-column prop="type" label="药品类型" width="150">
        <template #default="{ row }">
          <el-input
            v-model="row.type"
            placeholder="请输入药品类型"
            clearable
            style="width: 120px"
          />
        </template>
      </el-table-column>

      <el-table-column prop="effect" label="药品效果">
        <template #default="{ row }">
          <el-input
            v-model="row.effect"
            placeholder="请输入药品效果"
            clearable
          />
        </template>
      </el-table-column>

      <el-table-column prop="days" label="使用天数" width="150">
        <template #default="{ row }">
          <div class="number-input-group">
            <el-button
              type="text"
              size="small"
              @click="decreaseDays(row)"
              :disabled="row.days <= 1"
            >
              -
            </el-button>
            <el-input
              v-model.number="row.days"
              type="number"
              min="1"
              max="30"
              placeholder="天数"
              style="width: 80px; text-align: center"
              @change="() => syncTimesPerDay(row)"
            />
            <el-button
              type="text"
              size="small"
              @click="increaseDays(row)"
              :disabled="row.days >= 30"
            >
              +
            </el-button>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="timesPerDay" label="每天次数" width="150">
        <template #default="{ row }">
          <div class="number-input-group">
            <el-button
              type="text"
              size="small"
              @click="decreaseTimes(row)"
              :disabled="row.timesPerDay <= 1"
            >
              -
            </el-button>
            <el-input
              v-model.number="row.timesPerDay"
              type="number"
              min="1"
              max="6"
              placeholder="次数"
              style="width: 80px; text-align: center"
            />
            <el-button
              type="text"
              size="small"
              @click="increaseTimes(row)"
              :disabled="row.timesPerDay >= 6"
            >
              +
            </el-button>
          </div>
        </template>
      </el-table-column>

      <el-table-column fixed="right" label="操作" width="80">
        <template #default="{ $index }">
          <el-button
            type="danger"
            text
            size="small"
            @click="removeMedicine($index)"
            :disabled="medicines.length <= 1"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px; text-align: right">
      <el-button @click="addMedicine">添加药品</el-button>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'QuickMedicineDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    patientId: {
      type: String,
      default: ''
    },
    predefinedMedicines: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:visible', 'close', 'submit'],
  setup(props, { emit }) {
    const dialogVisible = ref(props.visible)
    
    // 完全重新设计药品数据处理方式，使用原始的JavaScript对象
    const medicines = ref([])
    
    // 直接设置药品数据的函数
    const setMedicinesData = (presetData) => {
      console.log('设置药品数据，接收到的预设数据:', presetData);
      
      // 创建一个全新的数组，避免任何Proxy嵌套问题
      const newMedicines = []
      
      // 检查是否有有效的预设数据
      if (presetData && Array.isArray(presetData) && presetData.length > 0) {
        console.log('处理预设药品数据，数量:', presetData.length)
        
        // 逐个处理每个药品，创建全新的普通对象
        for (let i = 0; i < presetData.length; i++) {
          const med = presetData[i]
          
          // 创建完全新的对象，确保没有任何Proxy
          const newMed = {
            name: typeof med.name === 'string' ? med.name : '未知药品',
            type: typeof med.type === 'string' ? med.type : '处方药',
            effect: typeof med.effect === 'string' ? med.effect : '治疗效果',
            days: typeof med.days === 'number' ? med.days : 7,
            timesPerDay: typeof med.timesPerDay === 'number' ? med.timesPerDay : 3
          }
          
          newMedicines.push(newMed)
        }
      } else {
        // 如果没有预设数据，使用默认数据
        console.log('使用默认药品数据')
        newMedicines.push({
          name: '',
          type: '',
          effect: '',
          days: 7,
          timesPerDay: 3
        })
      }
      
      console.log('最终设置的药品数据:', newMedicines)
      
      // 完全替换整个数组，确保响应式更新
      medicines.value = newMedicines
    }
    
    // 监听外部visible变化
    watch(() => props.visible, (newVal) => {
      dialogVisible.value = newVal
      // 当弹窗打开时，重新初始化药品数据
      if (newVal) {
        // 直接设置数据，不再使用nextTick，避免延迟问题
        console.log('弹窗打开，准备设置药品数据')
        setMedicinesData(props.predefinedMedicines)
      }
    })
    
    // 添加一个立即执行的watch，确保组件初始化时就能正确获取预设药品数据
    watch(() => props.predefinedMedicines, (newVal) => {
      console.log('监听到预设药品数据变化:', newVal, '弹窗可见状态:', dialogVisible.value)
      // 无论弹窗是否可见，都更新数据，但只在弹窗可见时记录日志
      setMedicinesData(newVal)
    }, { immediate: true }); // 立即执行，确保组件初始化时就能获取数据

    // 监听内部visible变化
    watch(dialogVisible, (newVal) => {
      emit('update:visible', newVal)
    })

    // 处理关闭
    const handleClose = () => {
      dialogVisible.value = false
      // 触发close事件通知父组件
      emit('close')
    }

    // 添加药品
    const addMedicine = () => {
      medicines.value.push({
        name: '',
        type: '',
        effect: '',
        days: 7,
        timesPerDay: 3
      })
    }

    // 删除药品
    const removeMedicine = (index) => {
      if (medicines.value.length <= 1) {
        ElMessage.warning('至少保留一种药品')
        return
      }
      medicines.value.splice(index, 1)
    }

    // 减少天数
    const decreaseDays = (medicine) => {
      if (medicine.days > 1) {
        medicine.days--
        syncTimesPerDay(medicine)
      }
    }

    // 增加天数
    const increaseDays = (medicine) => {
      if (medicine.days < 30) {
        medicine.days++
        syncTimesPerDay(medicine)
      }
    }

    // 减少次数
    const decreaseTimes = (medicine) => {
      if (medicine.timesPerDay > 1) {
        medicine.timesPerDay--
      }
    }

    // 增加次数
    const increaseTimes = (medicine) => {
      if (medicine.timesPerDay < 6) {
        medicine.timesPerDay++
      }
    }

    // 同步每天次数（根据要求，确保每天次数和使用天数相同）
    const syncTimesPerDay = (medicine) => {
      // 确保每天次数不超过6次的限制
      if (medicine.days > 6) {
        medicine.timesPerDay = 6
      } else {
        medicine.timesPerDay = medicine.days
      }
    }

    // 格式化药品数据函数已移除，直接在handleSubmit中处理

    // 提交
    const handleSubmit = () => {
      // 验证所有药品信息
      for (let i = 0; i < medicines.value.length; i++) {
        const med = medicines.value[i]
        if (!med.name) {
          ElMessage.error(`第${i + 1}行药品名称不能为空`)
          return
        }
        if (!med.type) {
          ElMessage.error(`第${i + 1}行药品类型不能为空`)
          return
        }
        if (!med.effect) {
          ElMessage.error(`第${i + 1}行药品效果不能为空`)
          return
        }
      }

      // 按照指定格式生成提示词：【药品名称】【药品类型】【药品效果】【使用天数】【每天次数】
      let prompt = ''
      medicines.value.forEach((medicine, index) => {
        if (index > 0) {
          prompt += '\n'
        }
        prompt += `【${medicine.name || ''}】【${medicine.type || ''}】【${medicine.effect || ''}】【使用天数：${medicine.days || 7}】【每天次数：${medicine.timesPerDay || 3}】`
      })
      
      console.log('生成的提示词:', prompt)
      
      // 提交数据
      const submitData = {
        patientId: props.patientId,
        prompt: prompt,
        medicines: medicines.value
      }

      emit('submit', submitData)
      dialogVisible.value = false
      ElMessage.success('配药信息已生成提示词')
    }

    return {
      dialogVisible,
      medicines,
      handleClose,
      addMedicine,
      removeMedicine,
      decreaseDays,
      increaseDays,
      decreaseTimes,
      increaseTimes,
      syncTimesPerDay,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.number-input-group {
  display: flex;
  align-items: center;
  justify-content: center;
}

.number-input-group .el-input {
  margin: 0 8px;
}
</style>
