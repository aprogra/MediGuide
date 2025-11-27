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
import { ref, watch, nextTick } from 'vue'
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
    
    // 初始化药品数据，如果有预设数据则使用预设数据
    const initializeMedicines = () => {
      console.log('QuickMedicineDialog初始化，接收到的预设药品:', props.predefinedMedicines);
      
      // 深拷贝预设数据，确保响应性
      if (props.predefinedMedicines && Array.isArray(props.predefinedMedicines) && props.predefinedMedicines.length > 0) {
        console.log('有预设药品数据，长度为:', props.predefinedMedicines.length);
        
        // 完全重新构建对象，避免Proxy嵌套问题
        const processedMedicines = JSON.parse(JSON.stringify(props.predefinedMedicines)).map(med => {
          // 确保所有必需字段都有值
          return {
            name: med.name || '未知药品',
            type: med.type || '处方药',
            effect: med.effect || '治疗效果',
            days: med.days || 7,
            timesPerDay: med.timesPerDay || 3
          }
        });
        
        console.log('处理后的药品数据:', processedMedicines);
        return processedMedicines;
      } else {
        // 默认数据
        const defaultMedicines = [
          {
            name: '',
            type: '',
            effect: '',
            days: 7,
            timesPerDay: 3
          }
        ];
        console.log('使用默认药品数据');
        return defaultMedicines;
      }
    }
    
    const medicines = ref(initializeMedicines())

    // 监听外部visible变化
    watch(() => props.visible, (newVal) => {
      dialogVisible.value = newVal
      // 当弹窗打开时，重新初始化药品数据
      if (newVal) {
        // 使用nextTick确保DOM更新后再设置数据
        nextTick(() => {
          medicines.value = initializeMedicines()
        })
      }
    })
    
    // 监听预设药品数据变化
    watch(() => props.predefinedMedicines, (newVal, oldVal) => {
      // 检查是否有实际的数据变化，并且弹窗是可见的
      const hasDataChanged = JSON.stringify(newVal) !== JSON.stringify(oldVal);
      if (dialogVisible.value && hasDataChanged) {
        // 确保newVal是有效的数组
        if (newVal && Array.isArray(newVal)) {
          console.log('检测到预设药品数据变化:', newVal);
          nextTick(() => {
            medicines.value = initializeMedicines();
            console.log('药品数据已更新:', medicines.value);
          });
        }
      }
    }, { deep: true, immediate: true });

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
        prompt += `【${medicine.name || ''}】【${medicine.type || ''}】【${medicine.effect || ''}】【'使用天数：'+${medicine.days || 7}】【'每天次数：'+${medicine.timesPerDay || 3}】`
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
