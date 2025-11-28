<template>
  <div class="chat-page">
    <!-- 头部导航 -->
    <header class="chat-header">
      <div class="header-left">
        <h1 class="logo">MediGuide</h1>
      </div>
      <div class="header-center">
        <h2 class="current-patient">当前患者: {{ currentPatientName || '未选择患者' }}</h2>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          plain 
          @click="showPatientList = true"
          icon="el-icon-user-solid"
        >
          患者列表
        </el-button>
        <el-button 
          type="success" 
          plain 
          @click="refreshPatientList"
          icon="el-icon-refresh"
          style="margin-left: 10px"
        >
          刷新列表
        </el-button>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="chat-container">
      <!-- 左侧边栏 - 对话列表 -->
      <aside class="chat-sidebar">
        <div class="sidebar-header">
          <el-button 
            type="primary" 
            plain 
            size="small" 
            class="new-chat-btn"
            @click="newChat"
          >
            <i class="el-icon-plus"></i> 新建对话
          </el-button>
        </div>
        <div class="chat-history-list">
          <div 
            v-for="(chat, index) in chatHistory" 
            :key="index"
            :class="['chat-history-item', { active: currentChatIndex === index }]"
            @click="switchChat(index)"
          >
            <div class="chat-preview">
              <p class="chat-title">{{ chat.title || '新对话' }}</p>
              <p class="chat-summary">{{ getChatSummary(chat) }}</p>
            </div>
            <div class="chat-actions">
              <el-icon class="action-icon reset-icon" @click.stop="resetChat(index)" title="重置对话"><RefreshRight /></el-icon>
              <el-icon class="action-icon delete-icon" @click.stop="deleteChat(index)" title="删除对话"><Delete /></el-icon>
            </div>
          </div>
        </div>
        <div class="sidebar-footer">
          <div class="action-buttons">
            <!-- 导入导出功能暂时移除 -->
          </div>
        </div>
      </aside>

      <!-- 中间聊天区域 -->
      <div class="chat-content">
        <!-- 聊天消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-if="currentChat.messages.length === 0" class="empty-chat">
            <p>开始与AI助手对话</p>
          </div>
          <div 
            v-for="(message, index) in currentChat.messages" 
            :key="index"
            :class="['message-wrapper', message.role]"
          >
            <div class="message-avatar">
              <el-icon v-if="message.role === 'user'"><User /></el-icon>
            <el-icon v-else><ChatDotRound /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div v-if="message.role === 'assistant'" class="message-actions">
              <el-icon class="action-icon regenerate-icon" @click="regenerateResponse(index)" title="重新输出"><RefreshLeft /></el-icon>
                <el-icon class="action-icon delete-icon" @click="deleteMessage(index)" title="删除记录"><Delete /></el-icon>
            <el-icon class="action-icon copy-icon" @click="copyMessage(index)" title="复制消息"><DocumentCopy /></el-icon>
            </div>
            <div v-if="message.role === 'user'" class="message-actions">
              <el-icon class="action-icon delete-icon" @click="deleteMessage(index)" title="删除记录"><Delete /></el-icon>
              <el-icon class="action-icon copy-icon" @click="copyMessage(index)" title="复制消息"><DocumentCopy /></el-icon>
            </div>
            </div>
          </div>
          <div v-if="isGenerating" class="message-wrapper assistant">
            <div class="message-avatar">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
                <span class="typing-dot"></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-container">
          <div class="input-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="showDetailedForm = true"
              icon="el-icon-edit-outline"
            >
              详细输入
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="handleQuickMedicineClick"
              icon="el-icon-medkit"
              style="margin-left: 10px"
            >
              快速配药
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="callNacosApi"
              icon="el-icon-s-operation"
              style="margin-left: 10px"
            >
              调用nacos
            </el-button>
          </div>
          <el-input
            v-model="messageInput"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题..."
            :maxlength="2000"
            show-word-limit
            @keydown.enter.exact="sendMessage"
          />
          <div class="input-footer">
            <span class="input-tips">按 Enter 发送</span>
            <el-button 
              type="primary" 
              @click="sendMessage"
              :disabled="!messageInput.trim() || isGenerating"
            >
              发送
            </el-button>
          </div>
        </div>
      </div>
    </main>

    <!-- 患者列表弹窗 -->
    <el-dialog
      v-model="showPatientList"
      title="患者列表"
      width="800px"
    >
      <patient-list
        ref="patientListRef"
        :patients-data="patientListData"
        @select-patient="selectPatient"
        @close="showPatientList = false"
      />
    </el-dialog>
    
    <!-- 详细输入弹窗 -->
    <DetailedReplyDialog
      v-model="showDetailedForm"
      :patient-id="currentPatientId"
      :initial-symptoms="initialSymptomsForDialog"
      @submit="handleDetailedFormSubmit"
      @close="handleDialogClose"
    />
    
    <!-- 快速配药弹窗 -->
    <QuickMedicineDialog
      v-model="showQuickMedicine"
      :patient-id="currentPatientId"
      :predefined-medicines="extractedMedicines"
      @submit="handleQuickMedicineSubmit"
      @close="handleDialogClose"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import DetailedReplyDialog from '../components/DetailedReplyDialog.vue'
import PatientList from '../components/PatientList.vue'
import QuickMedicineDialog from '../components/QuickMedicineDialog.vue'
import { aiChat, getDoctorPatients, receivePatientDataFromNacos } from '../services/api.js';

export default {
  name: 'ChatPage',
  components: {
    PatientList,
    DetailedReplyDialog,
    QuickMedicineDialog
  },
  props: {
    patientId: {
      type: String,
      default: null
    }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    // 获取路由中的patientId参数，优先级高于props
    const currentPatientId = computed(() => route.params.patientId || props.patientId)
    // 从localStorage加载聊天历史
  const loadChatHistory = () => {
    // 内联生成随机ID以避免引用问题
    const createRandomId = () => {
      return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    };
    
    try {
      const savedHistory = localStorage.getItem('mediGuideChatHistory');
      return savedHistory ? JSON.parse(savedHistory) : [
        {
          id: createRandomId(),
          title: '新对话',
          messages: [],
          createdAt: new Date().toISOString()
        }
      ];
    } catch (error) {
      console.error('Failed to load chat history from localStorage:', error);
      return [
        {
          id: createRandomId(),
          title: '新对话',
          messages: [],
          createdAt: new Date().toISOString()
        }
      ];
    }
  };

    // 从localStorage加载当前聊天索引
  const loadCurrentChatIndex = () => {
    try {
      const savedIndex = localStorage.getItem('currentChatIndex');
      return savedIndex !== null ? parseInt(savedIndex, 10) : 0;
    } catch (error) {
      console.error('Failed to load current chat index from localStorage:', error);
      return 0;
    }
  };

  // 聊天相关状态
  const chatHistory = ref(loadChatHistory())
  const currentChatIndex = ref(loadCurrentChatIndex())
    const messageInput = ref('')
    const isGenerating = ref(false)
    const messagesContainer = ref(null)
    
    // 界面状态
    const showDetailedForm = ref(false)
    const showPatientList = ref(false)
    const showQuickMedicine = ref(false)
    const currentPatientName = ref('')
    const patientListRef = ref(null)
    const patientListData = ref([])
    
    // 用于详细回复组件的初始症状
    const initialSymptomsForDialog = ref('')
    
    // 监听patientId变化，更新患者信息
    watch(() => currentPatientId.value, (newPatientId) => {
      if (newPatientId) {
        // 模拟根据患者ID获取患者信息
        // 在实际应用中，这里应该从API或状态管理中获取
        const mockPatient = {
          id: newPatientId,
          name: `患者${newPatientId.slice(-4)}`,
          // 其他患者信息...
        }
        currentPatientName.value = mockPatient.name
        // 更新对话标题为当前患者
        if (currentChat.value.messages.length === 0) {
          currentChat.value.title = `与${mockPatient.name}的对话`
        }
      }
    }, { immediate: true })
    
    // 计算属性
    const currentChat = computed(() => {
      return chatHistory.value[currentChatIndex.value] || { messages: [] }
    })
    
    // 方法
    const newChat = () => {
      chatHistory.value.unshift({
        title: '新对话',
        messages: []
      })
      currentChatIndex.value = 0
      // 生成新的随机聊天ID并更新URL
      currentChatId.value = generateRandomChatId();
      router.push({ query: { ...route.query, chatId: currentChatId.value } });
    }
    
    // 获取医生ID（优先从路由参数，其次从localStorage）
    const getDoctorId = () => {
      // 从路由查询参数获取
      const routeDoctorId = route.query.doctor_id;
      if (routeDoctorId) return parseInt(routeDoctorId);
      
      // 从localStorage获取
      const storedDoctorId = localStorage.getItem('doctorId');
      if (storedDoctorId) return parseInt(storedDoctorId);
      
      // 默认值（仅作为后备）
      return 1;
    };
    
    // 生成随机聊天ID
    const generateRandomChatId = () => {
      const doctorId = getDoctorId();
      return `chat_${doctorId}_${Math.floor(Math.random() * 1000000)}`;
    };
    
    // 存储当前聊天ID
    const currentChatId = ref(generateRandomChatId());
    
    // 加载医生患者列表
    const loadPatients = async () => {
      try {
        // 获取医生ID
        const doctorId = getDoctorId();
        console.log('当前医生ID:', doctorId);
        
        // 调用getDoctorPatients获取患者数据
        console.log('开始获取患者列表...');
        const patients = await getDoctorPatients(doctorId);
        console.log('获取到的原始患者数据:', patients);
        
        // 处理和转换患者数据，正确映射字段
        const processedPatients = Array.isArray(patients) 
          ? patients.map(patient => ({
              // 正确映射后端字段到前端需要的字段
              id: patient.num !== undefined ? String(patient.num) : (patient.id || '未知ID'),
              name: patient.name || '未知患者',
              symptoms: patient.description || patient.descriptions || patient.symptoms || '暂无症状描述'
            }))
          : [];
        
        console.log('处理后的患者数据:', processedPatients);
        // 使用响应式方式更新数据
        patientListData.value = processedPatients;
        console.log('patientListData已成功更新:', patientListData.value.length, '条记录');
        
        // 实现默认选择第一个患者的功能
        // 检查是否有患者，并且当前是否没有选中患者
        if (processedPatients.length > 0 && (!currentPatientId.value || currentPatientId.value === 'undefined')) {
          console.log('自动选择第一个患者:', processedPatients[0].name);
          // 调用selectPatient函数选择第一个患者
          selectPatient(processedPatients[0]);
        }
        
        return processedPatients;
      } catch (error) {
        console.error('加载患者列表错误:', error);
        ElMessage.error('加载患者列表失败，请重试');
        // 出错时设置为空数组，确保组件不会显示错误数据
        patientListData.value = [];
        return [];
      }
    }
    
    // 刷新患者列表
    const refreshPatientList = async () => {
      try {
        ElMessage.info('正在刷新患者列表...');
        // loadPatients方法已经会更新patientListData，不需要重复赋值
        await loadPatients();
        console.log('患者列表已刷新，组件应该自动响应数据变化');
        ElMessage.success('患者列表已刷新');
      } catch (error) {
        console.error('刷新患者列表失败:', error);
        ElMessage.error('刷新失败，请稍后重试');
      }
    }
    
    // 调用nacos接口
    const callNacosApi = async () => {
      try {
        ElMessage.info('正在调用nacos接口...');
        // 调用后端接口接收患者数据
        const result = await receivePatientDataFromNacos();
        
        // 在控制台打印接收到的内容
        console.log('从nacos接收到的数据:', result);
        
        // 显示成功消息
        ElMessage.success('调用nacos接口成功');
        
        // 刷新患者列表，以便显示新添加的患者
        await refreshPatientList();
      } catch (error) {
        console.error('调用nacos接口失败:', error);
        ElMessage.error('调用失败，请检查后端服务是否正常');
      }
    }
    
    // 处理详细回复弹窗发送的内容
    const handleDetailedReply = async (replyData) => {
      showDetailedForm.value = false
      // 构造消息内容
      let messageContent = replyData.mainSymptoms || ''
      if (replyData.details) {
        messageContent += '\n' + replyData.details
      }
      
      // 直接调用sendMessage发送消息，而不是设置输入框
      if (messageContent.trim()) {
        messageInput.value = messageContent;
        await sendMessage();
      }
    }
    
    // 处理快速配药弹窗提交的数据
    const handleQuickMedicineSubmit = async (data) => {
      showQuickMedicine.value = false
      // 这里可以根据需要处理配药数据
      console.log('快速配药数据:', data);
      
      // 将生成的提示词设置到输入框
      if (data && data.prompt) {
        messageInput.value = data.prompt;
        
        // 自动聚焦到输入框
        nextTick(() => {
          const inputElement = document.querySelector('.input-container textarea');
          if (inputElement) {
            inputElement.focus();
          }
        });
        
        ElMessage.success('提示词已生成并设置到输入框');
      }
    }
    
    // 患者选择功能在下面的selectPatient函数中实现
    
    const switchChat = (index) => {
      currentChatIndex.value = index
      // 切换对话时滚动到底部
      scrollToBottom()
      // 生成新的随机聊天ID并更新URL
      currentChatId.value = generateRandomChatId();
      router.push({ query: { ...route.query, chatId: currentChatId.value } });
      // 可以在这里添加保存当前对话状态的逻辑
    }
    
    const deleteChat = (index) => {
      ElMessageBox.confirm('确定要删除这个对话吗？此操作不可恢复。', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        chatHistory.value.splice(index, 1)
        if (currentChatIndex.value >= chatHistory.value.length) {
          currentChatIndex.value = Math.max(0, chatHistory.value.length - 1)
        }
        // 显式调用saveChatHistory函数确保本地存储更新
        saveChatHistory()
        ElMessage.success('对话已删除')
      }).catch(() => {
        // 取消删除
      })
    }
    
    // 聊天历史导入导出功能将在接入后端后实现
    
    const sendMessage = async () => {
      if (!messageInput.value.trim() || isGenerating.value) return
      
      const userMessage = messageInput.value.trim()
      messageInput.value = ''
      
      // 添加用户消息
      currentChat.value.messages.push({
        role: 'user',
        content: userMessage
      })
      
      // 滚动到底部
      scrollToBottom()
      
      // 显示AI正在生成回复
      isGenerating.value = true
      try {
        // 获取医生ID
        const doctorId = getDoctorId();
        // 调用真实API获取AI回复，使用当前聊天ID
        const aiResponse = await aiChat(doctorId, userMessage, currentChatId.value);
        
        // 解析响应（确保是字符串格式）
        const responseContent = typeof aiResponse === 'string' ? aiResponse : JSON.stringify(aiResponse);
        
        currentChat.value.messages.push({
          role: 'assistant',
          content: responseContent
        })
        
        // 更新对话标题
        if (!currentChat.value.title || currentChat.value.title === '新对话') {
          currentChat.value.title = userMessage.substring(0, 30) + (userMessage.length > 30 ? '...' : '')
        }
      } catch (error) {
        console.error('获取AI回复失败:', error);
        ElMessage.error('生成回复失败，请重试');
        
        // 发生错误时，使用模拟回复作为备选
       /* const aiReply = getMockAIReply();
        currentChat.value.messages.push({
          role: 'assistant',
          content: aiReply
        });*/
      } finally {
        isGenerating.value = false
        scrollToBottom()
      }
    }
    
    const regenerateResponse = async (messageIndex) => {
      if (isGenerating.value) return
      
      // 找到对应的用户消息
      let userMessageIndex = -1
      for (let i = messageIndex - 1; i >= 0; i--) {
        if (currentChat.value.messages[i].role === 'user') {
          userMessageIndex = i
          break
        }
      }
      
      if (userMessageIndex === -1) {
        ElMessage.warning('找不到对应的用户消息')
        return
      }
      
      const userMessage = currentChat.value.messages[userMessageIndex].content;
      
      // 删除原有的AI回复
      currentChat.value.messages.splice(messageIndex)
      
      // 重新生成回复
      isGenerating.value = true
      ElMessage.info('正在重新生成回复...')
      try {
        // 获取医生ID
        const doctorId = getDoctorId();
        // 调用API重新生成回复，使用当前聊天ID
        const aiResponse = await aiChat(doctorId, `请重新生成回复：${userMessage}`, currentChatId.value);
        
        const responseContent = typeof aiResponse === 'string' ? aiResponse : JSON.stringify(aiResponse);
        
        currentChat.value.messages.push({
          role: 'assistant',
          content: responseContent,
          timestamp: new Date().toISOString()
        })
        
        ElMessage.success('重新生成成功')
      } catch (error) {
        console.error('重新生成回复失败:', error);
        ElMessage.error('重新生成失败，请重试')
        
        // 如果API调用失败，使用模拟回复
        /*const aiReply = getMockAIReply();
        currentChat.value.messages.push({
          role: 'assistant',
          content: aiReply,
          timestamp: new Date().toISOString()
        })*/
      } finally {
        isGenerating.value = false
        scrollToBottom()
      }
    }
    
    const deleteMessage = (messageIndex) => {
      // 显示确认对话框
      ElMessageBox.confirm('确定要删除这条消息吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const message = currentChat.value.messages[messageIndex]
        
        // 如果是用户消息，同时删除对应的AI回复
        if (message.role === 'user') {
          const nextMessage = currentChat.value.messages[messageIndex + 1]
          if (nextMessage && nextMessage.role === 'assistant') {
            currentChat.value.messages.splice(messageIndex, 2)
            ElMessage.success('消息及回复已删除')
          } else {
            currentChat.value.messages.splice(messageIndex, 1)
            ElMessage.success('消息已删除')
          }
        } else {
          currentChat.value.messages.splice(messageIndex, 1)
          ElMessage.success('回复已删除')
        }
      }).catch(() => {
        // 取消删除
      })
    }
    
    const handleDetailedForm = () => {
      // 从最近的AI回复中提取病情信息
      const lastAssistantMessage = currentChat.value.messages
        .slice()
        .reverse()
        .find(msg => msg.role === 'assistant')
      
      if (lastAssistantMessage) {
        initialSymptomsForDialog.value = extractSymptomsFromAIReply(lastAssistantMessage.content)
      } else {
        initialSymptomsForDialog.value = ''
      }
      
      showDetailedForm.value = true
    }
    
    // 重置对话功能
    const resetChat = (chatIndex) => {
      ElMessageBox.confirm('确定要重置这个对话吗？这将清空所有消息但保留对话标题。', '确认重置', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const chat = chatHistory.value[chatIndex]
        // 清空消息数组
        chat.messages = []
        // 如果是当前对话，确保更新
        if (currentChatIndex.value === chatIndex) {
          scrollToBottom()
        }
        // 显式调用saveChatHistory函数确保本地存储更新
        saveChatHistory()
        ElMessage.success('对话已重置')
      }).catch(() => {
        // 取消重置
      })
    }
    
    // 复制消息功能
    const copyMessage = (messageIndex) => {
      const message = currentChat.value.messages[messageIndex]
      const textToCopy = message.content
      
      // 使用现代的Clipboard API
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(textToCopy)
          .then(() => {
            ElMessage.success('复制成功')
          })
          .catch(err => {
            console.error('复制失败:', err)
            ElMessage.error('复制失败，请重试')
          })
      } else {
        // 降级方案：使用传统的方法
        const textArea = document.createElement('textarea')
        textArea.value = textToCopy
        document.body.appendChild(textArea)
        textArea.select()
        
        try {
          document.execCommand('copy')
          ElMessage.success('复制成功')
        } catch (err) {
          console.error('复制失败:', err)
          ElMessage.error('复制失败，请重试')
        } finally {
          document.body.removeChild(textArea)
        }
      }
    }
    
    const handleDetailedFormSubmit = (data) => {
      // 将构建的提示词设置到输入框
      messageInput.value = data.prompt
      
      // 自动聚焦到输入框
      nextTick(() => {
        const inputElement = document.querySelector('.message-input textarea')
        if (inputElement) {
          inputElement.focus()
        }
      })
    }
    
    const handleDialogClose = () => {
      // 设置所有对话框为关闭状态
      showQuickMedicine.value = false
      showDetailedForm.value = false
    }
    
    const selectPatient = (patient) => {
      currentPatientName.value = patient.name
      showPatientList.value = false
      // 设置当前患者ID，触发watch监听
      router.push({ 
        name: 'ChatPage', 
        params: { patientId: patient.id },
        query: { ...route.query }
      });
      
      // 创建新对话
      newChat()
      
      // 更新对话标题为当前患者
      if (currentChat.value.messages.length === 0) {
        currentChat.value.title = `诊疗${patient.name}的医疗助手`
      }
    }
    
    // 文件处理相关功能已移至DetailedReplyDialog组件中
    
    // 识别并处理药物推荐表格格式
    const isMedicineRecommendation = (content) => {
      // 检查内容是否包含药物推荐表格的关键词和表格格式
      const keywords = ['药品名称', '药品类别', '药品简介', '适用症状', '治疗优势', '副作用'];
      const hasKeywords = keywords.some(keyword => content.includes(keyword));
      const hasTableFormat = content.includes('|') && content.includes('-');
      return hasKeywords && hasTableFormat;
    }
    
    // 从AI回复中提取药品信息
    const extractMedicineInfo = (content) => {
      const medicines = [];
      
      try {
        // 解析表格格式的药品信息
        const lines = content.split('\n').filter(line => line.trim());
        
        // 查找表头和分隔行
        let headerIndex = -1;
        let separatorIndex = -1;
        for (let i = 0; i < lines.length; i++) {
          if (lines[i].includes('药品名称') && lines[i].includes('|')) {
            headerIndex = i;
            // 分隔行通常在表头行之后
            if (i + 1 < lines.length && lines[i + 1].includes('-')) {
              separatorIndex = i + 1;
            }
            break;
          }
        }
        
        if (headerIndex >= 0 && separatorIndex >= 0) {
          // 解析表头，找到各列的索引
          const headerCells = lines[headerIndex].split('|').map(h => h.trim()).filter(h => h);
          const nameIndex = headerCells.findIndex(h => h.includes('药品名称'));
          const typeIndex = headerCells.findIndex(h => h.includes('药品类别'));
          const effectIndex = headerCells.findIndex(h => h.includes('药品简介') || h.includes('适用症状') || h.includes('功效'));
          
          // 解析数据行
          for (let i = separatorIndex + 1; i < lines.length; i++) {
            if (lines[i].includes('|')) {
              const cells = lines[i].split('|').map(cell => cell.trim()).filter(cell => cell);
              if (cells.length > 0) {
                const medicine = {
                  name: cells[nameIndex >= 0 ? nameIndex : 0] || '',
                  type: cells[typeIndex >= 0 ? typeIndex : (cells.length > 1 ? 1 : 0)] || '',
                  effect: cells[effectIndex >= 0 ? effectIndex : (cells.length > 2 ? 2 : 0)] || '',
                  days: 7, // 默认值
                  timesPerDay: 3 // 默认值
                };
                
                // 只添加有效的药品信息
                if (medicine.name.trim()) {
                  medicines.push(medicine);
                }
              }
            }
          }
        }
        
        // 如果没有从表格中提取到药品信息，尝试从其他格式中提取
        if (medicines.length === 0 && content.includes('药品名称') && content.includes('感冒药')) {
          // 尝试提取可能的药品信息（简单实现）
          // 这里可以根据实际需求扩展
          console.log('未从表格中提取到药品信息，尝试其他格式');
        }
      } catch (error) {
        console.error('提取药品信息时出错:', error);
      }
      
      return medicines;
    }

    // 格式化药物推荐表格
    const formatMedicineTable = (content) => {
      // 分离表头和数据
      const lines = content.split('\n').filter(line => line.trim());
      let tableHTML = '<table class="medicine-recommendation-table">';
      
      // 查找表头行和分隔行
      let headerIndex = -1;
      for (let i = 0; i < lines.length; i++) {
        if (lines[i].includes('药品名称') && lines[i].includes('药品类别')) {
          headerIndex = i;
          break;
        }
      }
      
      if (headerIndex >= 0) {
        // 添加表头
        tableHTML += '<thead><tr>';
        const headers = lines[headerIndex].split('|').map(h => h.trim()).filter(h => h);
        headers.forEach(header => {
          tableHTML += `<th>${header}</th>`;
        });
        tableHTML += '</tr></thead>';
        
        // 添加表格数据（从分隔行之后开始）
        tableHTML += '<tbody>';
        for (let i = headerIndex + 2; i < lines.length; i++) {
          if (lines[i].includes('|')) {
            tableHTML += '<tr>';
            const dataCells = lines[i].split('|').map(cell => cell.trim()).filter(cell => cell);
            dataCells.forEach(cell => {
              tableHTML += `<td>${cell}</td>`;
            });
            tableHTML += '</tr>';
          }
        }
        tableHTML += '</tbody>';
      } else {
        // 如果没有找到标准表格格式，尝试解析其他格式的药物信息
        tableHTML = parseAlternativeMedicineFormat(content);
      }
      
      tableHTML += '</table>';
      return tableHTML;
    }

    // 解析替代格式的药物信息
    const parseAlternativeMedicineFormat = (content) => {
      const lines = content.split('\n').filter(line => line.trim());
      let tableHTML = '<table class="medicine-recommendation-table"><thead><tr>';
      const headers = ['药品名称', '药品类别', '药品简介', '适用症状', '治疗优势', '副作用'];
      headers.forEach(header => {
        tableHTML += `<th>${header}</th>`;
      });
      tableHTML += '</tr></thead><tbody>';
      
      // 尝试从文本中提取药物信息（简单实现，实际可能需要更复杂的解析）
      let currentMedicine = {};
      lines.forEach(line => {
        headers.forEach(header => {
          if (line.includes(header + '：') || line.includes(header + ':')) {
            const match = line.match(new RegExp(`${header}[：:](.+)`));
            if (match) {
              currentMedicine[header] = match[1].trim();
            }
          }
        });
        
        // 当收集到足够的信息时，添加一行表格数据
        if (Object.keys(currentMedicine).length >= 3) {
          tableHTML += '<tr>';
          headers.forEach(header => {
            tableHTML += `<td>${currentMedicine[header] || '-'}</td>`;
          });
          tableHTML += '</tr>';
          currentMedicine = {};
        }
      });
      
      tableHTML += '</tbody></table>';
      return tableHTML;
    }

    const formatMessage = (content) => {
      // 检查是否为药物推荐表格格式
      if (isMedicineRecommendation(content)) {
        return formatMedicineTable(content);
      }
      
      // 普通消息格式化，支持换行
      return content.replace(/\n/g, '<br>');
    }
    
    const getChatSummary = (chat) => {
      if (chat.messages.length === 0) return '无消息'
      const firstMessage = chat.messages[0]
      return firstMessage.content.substring(0, 50) + (firstMessage.content.length > 50 ? '...' : '')
    }
    
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }
    
    // 检测最新聊天记录中的药物信息
    const checkLatestMedicineInfo = () => {
      // 获取当前对话中的所有AI助手消息
      const assistantMessages = currentChat.value.messages.filter(
        msg => msg.role === 'assistant'
      );
      
      // 如果没有AI助手消息，返回false
      if (assistantMessages.length === 0) {
        return false;
      }
      
      // 获取最新的AI助手消息
      const latestMessage = assistantMessages[assistantMessages.length - 1];
      
      // 使用现有的isMedicineRecommendation函数检测是否包含药物信息
      return isMedicineRecommendation(latestMessage.content);
    }
    
    // 存储提取的药品信息
    const extractedMedicines = ref([]);
    
    // 处理快速配药按钮点击事件
    const handleQuickMedicineClick = () => {
      if (checkLatestMedicineInfo()) {
        // 获取最新的AI助手消息
        const assistantMessages = currentChat.value.messages.filter(
          msg => msg.role === 'assistant'
        );
        const latestMessage = assistantMessages[assistantMessages.length - 1];
        
        // 提取药品信息
        const medicines = extractMedicineInfo(latestMessage.content);
        
        console.log('提取到的药品信息:', medicines);
        
        // 简化数据处理，使用原始JavaScript对象而不是复杂的响应式处理
        if (Array.isArray(medicines) && medicines.length > 0) {
          // 创建一个简单的普通JavaScript数组
          const simpleMedicines = [];
          
          // 逐个处理药品信息，确保数据类型正确
          for (let i = 0; i < medicines.length; i++) {
            const med = medicines[i];
            const simpleMed = {
              name: String(med.name || '未知药品'),
              type: String(med.type || '处方药'),
              effect: String(med.effect || '治疗效果'),
              days: Number(med.days || 7),
              timesPerDay: Number(med.timesPerDay || 3)
            };
            simpleMedicines.push(simpleMed);
          }
          
          console.log('简化后的药品数组:', simpleMedicines);
          
          // 使用一个空数组赋值然后再设置实际数据，确保触发响应式更新
          extractedMedicines.value = [];
          
          // 使用一个小的延迟确保响应式系统能够正确处理数据变化
          setTimeout(() => {
            // 创建一个完全新的数组实例，确保没有任何引用问题
            const freshMedicines = [...simpleMedicines];
            extractedMedicines.value = freshMedicines;
            console.log('设置后的extractedMedicines:', extractedMedicines.value);
            
            // 确保弹窗在数据完全设置后再打开
            showQuickMedicine.value = true;
          }, 50);
        } else {
          // 如果没有提取到药品，清空数组并打开弹窗
          extractedMedicines.value = [];
          console.log('无药品信息，extractedMedicines已清空');
          showQuickMedicine.value = true;
        }
      } else {
        // 如果未检测到药物信息，显示提示
        ElMessage.warning('最新回复未检测到药品信息');
      }
    }
    
    // 模拟AI回复函数
    /*const getMockAIReply = () => {
      const replies = [
        '根据您提供的信息，患者可能患有上呼吸道感染。建议多喝水，多休息，避免受凉。',
        '从症状来看，这可能是普通感冒引起的。如果症状持续加重，请及时就医。',
        '结合症状和年龄因素，建议进行血常规检查以进一步明确诊断。',
        '您的描述符合典型的急性胃肠炎表现。请注意饮食清淡，必要时补充电解质。',
        '这种情况需要密切观察，如果出现发热或疼痛加剧，建议尽快到医院就诊。'
      ]
      
      // 随机返回一个回复
      return replies[Math.floor(Math.random() * replies.length)]
    }*/
    
    // 从AI回复中提取症状信息
    const extractSymptomsFromAIReply = (reply) => {
      // 简单的提取逻辑，实际应用中可能需要更复杂的处理
      return reply
    }
    
    // 监听patientId变化
    watch(currentPatientId, (newPatientId) => {
      if (newPatientId) {
        // 模拟根据patientId获取患者信息
        // 实际项目中这里会调用API获取患者详情
        /*const mockPatientNames = {
          'P202405001': '张三',
          'P202405002': '李四',
          'P202405003': '王五',
          'P202405004': '赵六',
          'P202405005': '钱七',
          'P202405006': '孙八',
          'P202405007': '周九',
          'P202405008': '吴十'
        }*/
        
        const patientName =  `患者${newPatientId}`
        currentPatientName.value = patientName
        
        // 为新患者创建新对话
        newChat()
        
        ElMessage.success(`已选择患者：${patientName}`)
      }
    }, { immediate: true })
    
    // 监听路由变化，当切换到/chat页面时生成新的随机聊天ID
    watch(() => route.fullPath, () => {
      // 如果路由中有chatId参数，则使用它，否则生成新的随机聊天ID
      if (route.query.chatId) {
        currentChatId.value = route.query.chatId;
      } else {
        currentChatId.value = generateRandomChatId();
        // 更新URL以显示chatId
        router.push({ query: { ...route.query, chatId: currentChatId.value } });
      }
    })
    
    // 初始化
    // 保存聊天历史到localStorage
  const saveChatHistory = () => {
    try {
      localStorage.setItem('mediGuideChatHistory', JSON.stringify(chatHistory.value));
      localStorage.setItem('currentChatIndex', currentChatIndex.value.toString());
    } catch (e) {
      console.error('保存聊天历史失败:', e);
    }
  };

  // 监听聊天历史变化并保存
  watch(chatHistory, saveChatHistory, { deep: true });

  // 监听当前聊天索引变化并保存
  watch(currentChatIndex, saveChatHistory);

    onMounted(async () => {
      // 如果路由中有chatId参数，则使用它，否则生成新的随机聊天ID
      if (route.query.chatId) {
        currentChatId.value = route.query.chatId;
      } else {
        currentChatId.value = generateRandomChatId();
        // 更新URL以显示chatId
        router.push({ query: { ...route.query, chatId: currentChatId.value } });
      }

      // 暂时禁用自动发送初始提示词给AI（减少AI消耗）
      
     // console.log('已禁用自动发送初始验证消息，避免token消耗')

      // 加载患者列表
      try {
        console.log('组件挂载时开始加载患者列表');
        await loadPatients();
        console.log('患者列表初始化加载完成');
      } catch (error) {
        console.error('初始化加载患者列表失败:', error);
        ElMessage.error('加载患者列表失败，请检查网络连接或稍后重试');
      }
      const doctorId = getDoctorId();
      if (doctorId) {
        const initialPrompt = `医生ID: ${doctorId}, 聊天ID: ${currentChatId.value}。请根据这些信息初始化对话上下文。`;
        try {  
           // 设置消息输入框的值
          messageInput.value = initialPrompt;   
          // 发送消息给AI
          await sendMessage();
         } catch (error) {
          console.error('发送初始提示词失败:', error);
         }
      } 
    })
    
    return {
      chatHistory,
      currentChatIndex,
      currentChat,
      currentChatId,
      messageInput,
      isGenerating,
      messagesContainer,
      showDetailedForm,
      showPatientList,
      showQuickMedicine,
      currentPatientName,
      initialSymptomsForDialog,
      currentPatientId,
      patientListRef,
      patientListData,
      extractedMedicines,
      newChat,
      switchChat,
      deleteChat,
      sendMessage,
      regenerateResponse,
      deleteMessage,
      copyMessage,
      resetChat,
      handleDetailedForm,
      handleDetailedFormSubmit,
      handleDialogClose,
      selectPatient,
      handleDetailedReply,
      handleQuickMedicineSubmit,
      checkLatestMedicineInfo,
      handleQuickMedicineClick,
      formatMessage,
      getChatSummary,
      scrollToBottom,
      refreshPatientList,
      callNacosApi
    }
  }
}
</script>

<style scoped>
/* 药物推荐表格样式 */
:deep(.medicine-recommendation-table) {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
  font-size: 14px;
  background-color: #ffffff;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.medicine-recommendation-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
  text-align: left;
  padding: 12px 15px;
  border-bottom: 2px solid #ebeef5;
}

:deep(.medicine-recommendation-table td) {
  padding: 12px 15px;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
  vertical-align: top;
}

:deep(.medicine-recommendation-table tbody tr:last-child td) {
  border-bottom: none;
}

:deep(.medicine-recommendation-table tbody tr:hover) {
  background-color: #fafafa;
}

/* 响应式表格处理 */
@media (max-width: 768px) {
  :deep(.medicine-recommendation-table) {
    font-size: 12px;
  }
  
  :deep(.medicine-recommendation-table th),
  :deep(.medicine-recommendation-table td) {
    padding: 8px 10px;
  }
}

.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 头部样式 */
.chat-header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin: 0;
}

.current-patient {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

/* 主要内容区域 */
.chat-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏样式 */
.chat-sidebar {
  width: 280px;
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.new-chat-btn {
  width: 100%;
}

.chat-history-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.chat-history-item {
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-history-item:hover {
  background-color: #f5f7fa;
}

.chat-history-item.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409eff;
}

.chat-preview {
  flex: 1;
  min-width: 0;
}

.chat-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-summary {
  font-size: 12px;
  color: #909399;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-actions {
  margin-left: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.chat-history-item:hover .chat-actions {
  opacity: 1;
}

.chat-actions i {
  font-size: 14px;
  color: #909399;
  cursor: pointer;
  padding: 4px;
}

.chat-actions i:hover {
  color: #f56c6c;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e4e7ed;
}

.action-buttons {
  display: flex;
  justify-content: space-around;
}

.action-buttons i {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
  padding: 8px;
  transition: color 0.3s ease;
}

.action-buttons i:hover {
  color: #409eff;
}

/* 聊天内容区域 */
.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.empty-chat {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.message-wrapper {
  display: flex;
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease-in-out;
}

.message-wrapper.user {
  flex-direction: row-reverse;
  justify-content: flex-start;
  /* 确保消息容器与输入区域对齐 */
  padding-right: 0;
}

.message-avatar {
  width: 40px;
  height: 40px;
  background-color: #ecf5ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  /* 添加边框和阴影效果 */
  border: 2px solid #409eff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  /* 调整间距，使div贴近头像 */
  margin: 0;
  margin-right: 8px; /* 系统消息与头像的间距 */
}

/* 用户头像样式 - 简单头像 */
.message-wrapper.user .message-avatar {
  background-color: #f0f9ff;
  /* 用户头像特定样式 */
  border-color: #409eff;
  background-image: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  /* 用户头像左侧间距 */
  margin-right: 0;
  margin-left: 8px;
}

.message-avatar i {
  font-size: 20px;
  color: #409eff;
  font-weight: bold;
}

/* 机器人头像特定样式 */
.message-wrapper.assistant .message-avatar {
  background-color: #f5f5f5;
  border-color: #909399;
  background-image: linear-gradient(135deg, #f5f5f5 0%, #e5e7eb 100%);
}

.message-wrapper.assistant .message-avatar i {
  color: #909399;
}

.message-content {
  /* 移除flex: 1和max-width限制，让内容根据字符长度自动调节 */
  width: auto;
}

.message-wrapper.user .message-content {
  /* 移除自动右对齐和额外的宽度计算，让div贴近头像 */
  margin-left: 0;
  margin-right: 0;
  width: auto;
}

/* 统一文字靠左对齐 */
.message-wrapper.user .message-content,
.message-wrapper.assistant .message-content {
  text-align: left;
}

/* 确保系统输出对话栏靠左对齐 */
.message-wrapper.assistant {
  justify-content: flex-start;
}

/* 修改消息文本样式，使其宽度根据内容自动调整 */
.message-text {
  background-color: #ecf5ff;
  padding: 12px 16px;
  border-radius: 8px;
  word-wrap: break-word;
  line-height: 1.6;
  display: inline-block;
  min-width: 40px; /* 确保即使只有一个字符也有基本宽度 */
  /* 移除max-width限制，让长度根据字符长度自动调节 */
}

.message-wrapper.user .message-text {
  background-color: #409eff;
  color: #fff;
  /* 确保消息气泡全部靠左显示 */
  display: inline-block;
  text-align: left;
  margin-right: 0;
  margin-left: 0;
  /* 移除width: 100%，让长度根据字符长度自动调节 */
}

.message-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-start;
  gap: 16px;
}

.message-wrapper.user .message-actions {
  justify-content: flex-end;
}

.message-actions i {
  font-size: 14px;
  color: #909399;
  cursor: pointer;
  padding: 6px;
  transition: all 0.3s ease;
  /* 按钮样式改进 */
  border-radius: 4px;
  background-color: transparent;
}

.message-actions i:hover {
  background-color: #f0f0f0;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-actions i:hover {
  color: #409eff;
}

/* 按钮样式 - 确保文字居中 */
.el-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.el-button i {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 输入区域 */
.input-container {
  background-color: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 16px 24px;
  /* 确保内部元素可以正确对齐 */
  position: relative;
}

.input-actions {
  margin-bottom: 12px;
}

.input-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 8px;
  /* 确保右侧对齐 */
  width: 100%;
}

.input-tips {
  font-size: 12px;
  color: #909399;
  margin-right: auto;
  /* 确保提示文字在左侧，按钮在右侧 */
}

/* 打字动画 */
.typing-indicator {
  display: inline-flex;
  align-items: center;
}

.typing-dot {
  width: 8px;
  height: 8px;
  background-color: #909399;
  border-radius: 50%;
  margin: 0 2px;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dot:nth-child(2) {
  animation-delay: -0.16s;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-sidebar {
    width: 240px;
  }
  
  .message-content {
    max-width: 80%;
  }
  
  .chat-header {
    padding: 0 12px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .current-patient {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .chat-sidebar {
    width: 200px;
  }
  
  .messages-container {
    padding: 16px 12px;
  }
  
  .input-container {
    padding: 12px;
  }
}
    
    /* 操作图标样式 - 明显版 */
    .action-icon {
      font-size: 20px; /* 更大的图标 */
      padding: 6px;    /* 更大的点击区域 */
      margin: 0 4px;   /* 适当的间距 */
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      background-color: #f0f2f5;
      color: #606266;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 32px;
      min-height: 32px;
    }
    
    /* 基本悬停效果 */
    .action-icon:hover {
      transform: scale(1.1); /* 轻微放大效果 */
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    }
    
    /* 特定图标颜色 */
    .delete-icon {
      color: #f56c6c;
    }
    
    .delete-icon:hover {
      background-color: #fef0f0;
      color: #e64340;
    }
    
    .reset-icon {
      color: #67c23a;
    }
    
    .reset-icon:hover {
      background-color: #f0f9eb;
      color: #5daf34;
    }
    
    .copy-icon {
      color: #409eff;
    }
    
    .copy-icon:hover {
      background-color: #ecf5ff;
      color: #66b1ff;
    }
    
    .regenerate-icon {
      color: #e6a23c;
    }
    
    .regenerate-icon:hover {
      background-color: #fdf6ec;
      color: #ebb563;
    }
    
    /* 图标容器样式 */
    .message-actions {
      display: flex;
      gap: 4px;
    }
    
    .chat-actions {
      display: flex;
      gap: 8px;
    }
  </style>