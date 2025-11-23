<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2 class="login-title">医疗辅助系统登录</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            clearable
          />
        </el-form-item>
        <el-form-item label="ID" prop="id">
          <el-input
            v-model="loginForm.id"
            placeholder="请输入ID"
            prefix-icon="el-icon-document"
            clearable
            type="number"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="isLoading"
            @click="handleLogin"
            class="login-button"
            :disabled="isLoading"
          >
            {{ isLoading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <div v-if="errorMessage" class="error-message">
            <i class="el-icon-warning"></i> {{ errorMessage }}
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { doctorLogin } from '../services/api.js';

export default {
  name: 'LoginPage',
  setup() {
    const router = useRouter();
    const loginFormRef = ref(null);
    const isLoading = ref(false);
    const errorMessage = ref('');
    
    const loginForm = reactive({
      username: '',
      id: null  // 初始化为null而不是空字符串，更符合数字类型的语义
    });
    
    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      id: [
        { required: true, message: '请输入ID', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            // 尝试将值转换为数字
            const numValue = parseInt(value);
            if (isNaN(numValue)) {
              callback(new Error('ID必须是数字'));
            } else if (numValue < 1) {
              callback(new Error('ID不能小于1'));
            } else {
              callback();
            }
          },
          trigger: 'blur'
        }
      ]
    };
    
    const handleLogin = async () => {
      // 表单验证
      loginFormRef.value.validate(async (valid) => {
        if (valid) {
          isLoading.value = true;
          errorMessage.value = '';
          
          try {
            // 确保ID是数字类型
            const doctorId = parseInt(loginForm.id);
            // 调用登录接口
            console.log(doctorId);
            const response = await doctorLogin(loginForm.username, doctorId);

            
            if (response && response.success) {
                // 登录成功，保存登录状态和医生信息
                localStorage.setItem('isLoggedIn', 'true');
                localStorage.setItem('doctorId', response.data.doctorId.toString());
                localStorage.setItem('doctorName', response.data.doctorName);
                
                ElMessage.success('登录成功！');
                
                // 跳转到聊天页面，并传递doctor_id参数
                router.push({ 
                  path: '/chat', 
                  query: { doctor_id: response.data.doctorId } 
                });
              } else {
                // 登录失败
                errorMessage.value = response?.message || '登录失败，请检查用户名和ID是否匹配';
                ElMessage.error(errorMessage.value);
              }
          } catch (error) {
            console.error('登录错误:', error);
            errorMessage.value = '登录失败，请稍后重试';
          } finally {
            isLoading.value = false;
          }
        }
      });
    };
    
    return {
      loginFormRef,
      loginForm,
      rules,
      isLoading,
      errorMessage,
      handleLogin
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 32px;
  width: 100%;
  max-width: 400px;
  animation: fadeIn 0.5s ease-in-out;
}

.login-title {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.login-form {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  border-radius: 8px;
}

.error-message {
  color: #f56c6c;
  font-size: 14px;
  text-align: center;
  padding: 8px;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
  margin-top: 8px;
}

.error-message i {
  margin-right: 4px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #555;
}

:deep(.el-input) {
  border-radius: 6px;
}

:deep(.el-input__inner) {
  height: 40px;
  border-radius: 6px;
}
</style>