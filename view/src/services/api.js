// 医疗辅助系统API服务
import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api', // 基础URL，与vue.config.js中的代理配置匹配
  timeout: 30000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    console.log('实际请求地址:', config.baseURL + config.url); // 关键：打印完整API地址
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('API请求错误:', error);
    return Promise.reject(error);
  }
);

// 医生登录接口
export const doctorLogin = async (username, id) => {
  try {
    console.log('尝试调用后端登录API验证用户名和ID');

    // 构建与后端DoctorController.login接口匹配的请求体
    // 确保id是数字类型，与后端要求一致
    const requestData = {
      username: username.trim(),  // 对应doctor表中的doc_name，去除前后空格
      id: parseInt(id)           // 确保id是整数类型
    };
    console.log("api为：")
    // 调用后端登录API验证用户名和ID
    const response = await api.post('/doctor/login', requestData);

    console.log('后端登录验证成功');
    return response;
  } catch (error) {
    console.error('医生登录API调用失败:', error);
    
    // 注释掉模拟数据部分，只保留错误抛出
    // 实际生产环境应始终使用后端API验证
    throw error;
  }
};

// AI对话接口 - 已修改为与后端GET方法匹配
export const aiChat = async (doctorId, prompt, chatId = null) => {
  try {
    // 如果没有提供chatId，则生成一个随机的聊天ID
    const finalChatId = chatId || `chat_${doctorId}_${Math.floor(Math.random() * 1000000)}`;
    
    // 后端使用GET方法，参数为msg和chatId
    const response = await api.get('/doctor/helper', {
      params: {
        msg: prompt,
        chatId: finalChatId
      }
    });
    return response;
  } catch (error) {
    console.error('AI对话请求失败:', error);
    throw error;
  }
};

// 获取医生的患者列表（联合查询）
export const getDoctorPatients = async (doctorId) => {
  try {
    console.log('getDoctorPatients被调用，doctorId:', doctorId);
    
    // 1. 参数验证和类型转换
    if (!doctorId) {
      throw new Error('医生ID不能为空');
    }
    
    // 确保doctorId是数字类型，与后端预期保持一致
    const numericDoctorId = parseInt(doctorId);
    if (isNaN(numericDoctorId)) {
      throw new Error('医生ID必须是有效的数字');
    }
    
    // 2. 使用正确的REST API路径格式 - 使用查询参数而不是直接拼接在URL中
    // 注意：后端路径是patientList（首字母小写）
    const response = await api.get('/doctor/patientList', {
      params: {
        doctorId: numericDoctorId
      }
    });
    
    // 3. 验证响应数据结构
    if (!response || !Array.isArray(response)) {
      console.warn('患者列表响应数据格式异常:', response);
      // 如果后端返回的是对象而非数组，尝试提取患者数组
      if (response && Array.isArray(response.patients)) {
        return response.patients;
      }
    }
    
    console.log('成功获取患者列表，数据条数:', Array.isArray(response) ? response.length : 0);
    return response;
  } catch (error) {
    console.error('获取患者列表失败:', error);
    
    // 4. 完善错误处理 - 在生产环境下，要么抛出错误让调用方处理，
    // 要么返回空数组作为默认值，避免界面渲染错误
    // 在开发阶段，可以考虑使用模拟数据作为备选
    
    // 方案A: 抛出错误让调用方处理
    throw error;
    
    // 方案B: 返回空数组作为默认值（如果调用方希望静默失败）
    // return [];
  }
};

// 解析AI响应中的患者信息（暂时注释掉，因为目前不使用）
/*
const parsePatientsFromAIResponse = (response) => {
  // 这是一个示例解析函数，需要根据实际的AI返回格式进行调整
  const patients = [];
  
  try {
    // 如果响应是字符串，尝试解析为患者列表
    if (typeof response === 'string') {
      // 简单的正则匹配示例，实际情况可能需要更复杂的解析
      const patientMatches = response.match(/患者编号：(\d+)\n\s*患者姓名：([^\n]+)\n\s*病情描述：([^\n]+)/g);
      
      if (patientMatches) {
        patientMatches.forEach(match => {
          const numMatch = match.match(/患者编号：(\d+)/);
          const nameMatch = match.match(/患者姓名：([^\n]+)/);
          const descMatch = match.match(/病情描述：([^\n]+)/);
          
          if (numMatch && nameMatch && descMatch) {
            patients.push({
              id: parseInt(numMatch[1]),
              name: nameMatch[1].trim(),
              description: descMatch[1].trim()
            });
          }
        });
      }
    }
    // 如果响应已经是结构化数据
    else if (Array.isArray(response)) {
      return response;
    }
    // 如果响应是对象且包含患者列表
    else if (typeof response === 'object' && response.patients) {
      return response.patients;
    }
  } catch (error) {
    console.error('解析患者信息失败:', error);
  }
  
  // 如果解析失败，返回模拟数据（仅用于开发测试）
  if (patients.length === 0) {
    return generateMockPatients();
  }
  
  return patients;
};
*/

// 生成模拟患者数据（用于开发和测试）- 仅返回三个必要字段
/*const generateMockPatients = () => {
  const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十', '郑十一', '王十二'];
  const symptoms = [
    '头痛、发热，持续两天，伴有轻微咳嗽',
    '腹痛，腹泻，昨天开始，可能与饮食有关',
    '胸闷，气短，活动后加重，有高血压病史',
    '皮肤瘙痒，出现红色皮疹，可能是过敏反应',
    '关节疼痛，晨起僵硬，疑似类风湿',
    '头晕乏力，食欲不振，可能贫血',
    '咳嗽咳痰，夜间加重，有吸烟史',
    '心悸胸痛，劳累后发作，需排除冠心病',
    '恶心呕吐，右上腹疼痛，可能胆囊炎',
    '尿频尿急，排尿困难，泌尿系感染可能'
  ];
  
  const mockPatients = [];
  for (let i = 1; i <= 20; i++) {
    const randomIndex = Math.floor(Math.random() * names.length);
    const symptomIndex = Math.floor(Math.random() * symptoms.length);
    
    // 只返回三个必要字段：id、name和symptoms
    mockPatients.push({
      id: `P${String(i).padStart(6, '0')}`,
      name: names[randomIndex] + (Math.floor(Math.random() * 100)),
      symptoms: symptoms[symptomIndex]
    });
  }
  
  return mockPatients;
};*/

// 保存诊断结果
export const saveDiagnosis = async (patientId, doctorId, doctorName, diagnosis) => {
  try {
    const prompt = `保存诊断结果：患者${patientId}，医生${doctorName}（ID：${doctorId}），诊断为${diagnosis}`;
    return await aiChat(doctorId, prompt);
  } catch (error) {
    console.error('保存诊断结果失败:', error);
    throw error;
  }
};

// 查询药品信息
export const queryDrugs = async (doctorId, category) => {
  try {
    const prompt = `查询${category}类药品信息`;
    return await aiChat(doctorId, prompt);
  } catch (error) {
    console.error('查询药品信息失败:', error);
    throw error;
  }
};

// 保存配药结果
export const saveMedicine = async (patientId, doctorId, doctorName, medicines, instructions) => {
  try {
    const prompt = `保存配药结果：患者${patientId}，医生${doctorName}（ID：${doctorId}），药物：${medicines}，用药说明：${instructions}`;
    return await aiChat(doctorId, prompt);
  } catch (error) {
    console.error('保存配药结果失败:', error);
    throw error;
  }
};

// 结束当前患者看诊
export const finishCurrentPatient = async (doctorId, currentPatientId) => {
  try {
    const prompt = `结束当前患者${currentPatientId}的看诊，并获取下一位患者信息`;
    return await aiChat(doctorId, prompt);
  } catch (error) {
    console.error('结束患者看诊失败:', error);
    throw error;
  }
};

export default {
  doctorLogin,
  aiChat,
  getDoctorPatients,
  saveDiagnosis,
  queryDrugs,
  saveMedicine,
  finishCurrentPatient
};