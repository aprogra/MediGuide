<template>
  <el-dialog
    v-model="dialogVisible"
    title="详细病情输入"
    width="700px"
    :before-close="handleClose"
  >
    <el-form 
      :model="formData" 
      :rules="rules" 
      ref="formRef"
      label-width="120px"
      class="detailed-form"
    >
      <el-form-item label="详细病情症状" prop="symptoms">
        <el-input 
          v-model="formData.symptoms" 
          type="textarea" 
          :rows="4" 
          placeholder="请详细描述患者症状，包括症状出现时间、程度、频率等"
          show-word-limit
          :maxlength="1000"
        />
      </el-form-item>
      <el-form-item label="过往病史" prop="medicalHistory">
        <el-input 
          v-model="formData.medicalHistory" 
          type="textarea" 
          :rows="3" 
          placeholder="请输入患者过往病史，包括慢性疾病、手术史等"
          show-word-limit
          :maxlength="500"
        />
      </el-form-item>
      <el-form-item label="过敏情况" prop="allergies">
        <el-input 
          v-model="formData.allergies" 
          type="textarea" 
          :rows="2" 
          placeholder="请输入患者对药物、食物或其他物质的过敏情况"
          show-word-limit
          :maxlength="300"
        />
      </el-form-item>
      <el-form-item label="其他相关因素" prop="otherFactors">
        <el-input 
          v-model="formData.otherFactors" 
          type="textarea" 
          :rows="2" 
          placeholder="其他需要说明的因素，如生活习惯、环境因素等"
          show-word-limit
          :maxlength="500"
        />
        
        <div class="upload-section">
          <div class="upload-title">相关文件上传：</div>
          <el-upload
            class="upload-demo"
            drag
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="5"
            :on-exceed="handleExceed"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
            <div class="el-upload__tip">
              <span style="color: #f56c6c; font-weight: 500;">提示：暂不支持图片处理功能</span>
              <span class="upload-limit">（最多上传5个文件，单个文件不超过10MB）</span>
            </div>
          </template>
          </el-upload>
          <div v-if="uploadedFiles.length > 0" class="uploaded-files">
            <div 
              v-for="(file, index) in uploadedFiles" 
              :key="index"
              class="file-item"
            >
              <i class="el-icon-document"></i>
              <span class="file-name" :title="file.name">{{ truncateFileName(file.name) }}</span>
              <i 
                class="el-icon-delete file-delete"
                @click="removeFile(index)"
                title="删除文件"
              ></i>
            </div>
          </div>
        </div>
      </el-form-item>
      <!-- 患者基本信息已删除 -->
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSubmit"
          :loading="submitting"
        >
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'DetailedReplyDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    initialSymptoms: {
      type: String,
      default: ''
    }
  },
  emits: ['update:modelValue', 'submit', 'close'],
  setup(props, { emit }) {
    // 本地对话框可见状态
    const dialogVisible = ref(props.modelValue);
    
    // 监听modelValue变化
    watch(
      () => props.modelValue,
      (newVal) => {
        dialogVisible.value = newVal;
      }
    );
    
    // 监听dialogVisible变化，更新父组件
    watch(dialogVisible, (newVal) => {
      emit('update:modelValue', newVal);
    });
    
    // 表单数据
    const formData = reactive({
      symptoms: '',
      medicalHistory: '',
      allergies: '',
      otherFactors: '',
      patientAge: '',
      patientGender: '',
      patientOccupation: ''
    })
    
    // 表单验证规则
    const rules = {
      symptoms: [
        {
          required: true,
          message: '请输入详细病情症状',
          trigger: 'blur'
        },
        {
          min: 10,
          message: '症状描述至少需要10个字符',
          trigger: 'blur'
        }
      ]
    }
    
    // 状态管理
    const formRef = ref(null)
    const submitted = ref(false)
    const submitting = ref(false)
    const uploadedFiles = ref([])
    
    // 监听初始症状变化
    watch(
      () => props.initialSymptoms,
      (newVal) => {
        if (newVal && !submitted.value) {
          formData.symptoms = newVal
        }
      },
      { immediate: true }
    )
    
    // 监听对话框显示状态
    watch(
      () => props.visible,
      (newVal) => {
        if (newVal) {
        // 对话框打开时重置状态
        submitted.value = false
        formRef.value?.resetFields()
        if (props.initialSymptoms) {
          formData.symptoms = props.initialSymptoms
        }
      }
      }
    )
    
    // 处理文件变化
    const handleFileChange = (file) => { // fileList参数未使用
      // 检查文件大小
      if (file.size > 10 * 1024 * 1024) {
        ElMessage.error('文件大小不能超过10MB')
        return false
      }
      
      // 检查是否为图片文件
      if (file.type.startsWith('image/')) {
        ElMessage.warning('暂不支持图片处理功能')
      }
      
      uploadedFiles.value.push(file)
      return false // 阻止默认上传
    }
    
    // 处理文件超出限制
    const handleExceed = () => {
      ElMessage.error('最多只能上传5个文件')
    }
    
    // 移除文件
    const removeFile = (index) => {
      uploadedFiles.value.splice(index, 1)
    }
    
    // 截断文件名
    const truncateFileName = (fileName) => {
      if (fileName.length > 20) {
        return fileName.substring(0, 15) + '...' + fileName.substring(fileName.lastIndexOf('.'))
      }
      return fileName
    }
    
    // 关闭对话框
    const handleClose = () => {
      // 如果有未保存的数据，显示确认提示
      if (hasUnsavedChanges() && !submitted.value) {
        ElMessageBox.confirm('确定要关闭对话框吗？未保存的数据将丢失。', '确认关闭', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          resetForm()
          dialogVisible.value = false
          emit('close')
        }).catch(() => {
          // 取消关闭
        })
      } else {
        resetForm()
        dialogVisible.value = false
        emit('close')
      }
    }
    
    // 检查是否有未保存的更改
    const hasUnsavedChanges = () => {
      return formData.symptoms || 
             formData.medicalHistory || 
             formData.allergies || 
             formData.otherFactors ||
             uploadedFiles.value.length > 0
    }
    
    // 重置表单
    const resetForm = () => {
      formData.symptoms = ''
      formData.medicalHistory = ''
      formData.allergies = ''
      formData.otherFactors = ''
      formData.patientAge = ''
      formData.patientGender = ''
      formData.patientOccupation = ''
      uploadedFiles.value = []
      submitted.value = false
      submitting.value = false
    }
    
    // 提交表单
    const handleSubmit = async () => {
      try {
        // 验证表单
        await formRef.value.validate()
        
        submitting.value = true
        
        // 构建格式化的提示词
        let prompt = ''
        prompt += `【详细病情症状】: ${formData.symptoms}\n`
        prompt += `【过往病史】: ${formData.medicalHistory || '无'}\n`
        prompt += `【过敏情况】: ${formData.allergies || '无'}\n`
        prompt += `【其他相关因素】: ${formData.otherFactors || '无'}`
        
        // 如果有患者基本信息，也添加到提示词中
        if (formData.patientAge || formData.patientGender || formData.patientOccupation) {
          const patientInfo = []
          if (formData.patientAge) patientInfo.push(`年龄: ${formData.patientAge}`)
          if (formData.patientGender) patientInfo.push(`性别: ${formData.patientGender === 'male' ? '男' : '女'}`)
          if (formData.patientOccupation) patientInfo.push(`职业: ${formData.patientOccupation}`)
          prompt += `\n【患者基本信息】: ${patientInfo.join('，')}`
        }
        
        // 如果有上传的文件，添加文件信息到提示词中
        if (uploadedFiles.value.length > 0) {
          const fileNames = uploadedFiles.value.map(file => file.name).join('，')
          prompt += `\n【上传文件】: ${fileNames}`
        }
        
        // 延迟模拟处理
        await new Promise(resolve => setTimeout(resolve, 500))
        
        // 标记为已提交
        submitted.value = true
        
        // 发送提交事件
        emit('submit', {
          prompt,
          formData: { ...formData },
          files: [...uploadedFiles.value]
        })
        
        ElMessage.success('表单提交成功')
        
        // 关闭对话框
        setTimeout(() => {
          dialogVisible.value = false
          resetForm()
        }, 500)
        
      } catch (error) {
        if (error !== false) { // 不是表单验证失败的情况
          console.error('提交失败:', error)
          ElMessage.error('提交失败，请重试')
        }
      } finally {
        submitting.value = false
      }
    }
    
    return {
      formData,
      rules,
      formRef,
      submitting,
      uploadedFiles,
      dialogVisible,
      handleFileChange,
      handleExceed,
      removeFile,
      truncateFileName,
      handleClose,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.detailed-form {
  max-height: 500px;
  overflow-y: auto;
}

.upload-section {
  margin-top: 16px;
}

.upload-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.upload-limit {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}

.uploaded-files {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  background-color: #f5f7fa;
  padding: 6px 10px;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  max-width: 100%;
  border: 1px solid #ebeef5;
}

.file-item i.el-icon-document {
  margin-right: 6px;
  color: #409eff;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.file-delete {
  color: #909399;
  cursor: pointer;
  padding: 2px;
  transition: color 0.3s ease;
}

.file-delete:hover {
  color: #f56c6c;
}

.basic-info-grid {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

/* 表单滚动条样式 */
.detailed-form::-webkit-scrollbar {
  width: 6px;
}

.detailed-form::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.detailed-form::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.detailed-form::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .basic-info-grid {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .basic-info-grid .el-input,
  .basic-info-grid .el-select {
    width: 100% !important;
    margin-left: 0 !important;
  }
  
  .uploaded-files {
    flex-direction: column;
    gap: 6px;
  }
  
  .file-item {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .detailed-form {
    max-height: 400px;
  }
  
  .el-form-item__label {
    font-size: 14px;
  }
  
  .el-input__inner {
    font-size: 14px;
  }
}
</style>