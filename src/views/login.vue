<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-form">
        <h2 class="title">{{ isLogin ? '用户登录' : '用户注册' }}</h2>
        
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
          label-position="top"
        >
          <!-- 用户名 -->
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              clearable
            />
          </el-form-item>
          
          <!-- 密码 -->
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              placeholder="请输入密码"
              show-password
              clearable
            />
          </el-form-item>
          
          <!-- 确认密码（仅注册时显示） -->
          <el-form-item v-if="!isLogin" label="确认密码" prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              placeholder="请再次输入密码"
              show-password
              clearable
            />
          </el-form-item>
          
          <!-- 验证码 -->
          <el-form-item label="验证码" prop="captcha">
            <div class="captcha-container">
              <el-input
                v-model="form.captcha"
                placeholder="请输入验证码"
                clearable
                style="width: 60%"
              />
              <div class="captcha-image" @click="refreshCaptcha">
                <img :src="captchaImage" alt="验证码" />
              </div>
            </div>
          </el-form-item>
          
          <!-- 记住我（仅登录时显示） -->
          <el-form-item v-if="isLogin" prop="remember">
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
          </el-form-item>
          
          <!-- 提交按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              @click="handleSubmit"
              :loading="loading"
              style="width: 100%"
            >
              {{ isLogin ? '登录' : '注册' }}
            </el-button>
          </el-form-item>
          
          <!-- 切换登录/注册 -->
          <div class="toggle-text">
            <span @click="toggleForm">
              {{ isLogin ? '没有账号？立即注册' : '已有账号？立即登录' }}
            </span>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login, register } from '../api/login_register';
import { usePersistStore } from '../store/persist';

// 模拟验证码图片（实际项目中应该从后端获取）
const captchaImage = ref('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCAxMDAgNDAiPgogIDxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iNDAiIGZpbGw9IiNlZWVlZWUiLz4KICA8dGV4dCB4PSI1MCIgeT0iMjUiIGZvbnQtc2l6ZT0iMTYiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSI+MTIzNDwvdGV4dD4KPC9zdmc+')

const router = useRouter()
const isLogin = ref(true) // 当前是否为登录状态
const loading = ref(false) // 加载状态
const formRef = ref(null) // 表单引用

// 表单数据
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  captcha: '',
  remember: false
})

// 表单验证规则
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为4位', trigger: 'blur' }
  ]
})

// 切换登录/注册表单
const toggleForm = () => {
  isLogin.value = !isLogin.value
  // 重置表单
  formRef.value?.resetFields()
}

// 刷新验证码
const refreshCaptcha = () => {
  // 实际项目中应该调用后端API获取新的验证码
  captchaImage.value = `data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxMDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCAxMDAgNDAiPgogIDxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iNDAiIGZpbGw9IiNlZWVlZWUiLz4KICA8dGV4dCB4PSI1MCIgeT0iMjUiIGZvbnQtc2l6ZT0iMTYiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZmlsbD0iIzMzMyIgdGV4dC1hbmNob3I9Im1pZGRsZSI+${Math.floor(Math.random() * 9000 + 1000)}</dGV4dD4KPC9zdmc+`
}

// 提交表单
const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      
      // 模拟API请求
      setTimeout(() => {
        if (isLogin.value) {
          // 登录逻辑
          loginSubmit(form.username, form.password)
        } else {
          // 注册逻辑
          registerSubmit(form.username, form.password, form.confirmPassword)
        }
        loading.value = false
      }, 1000)
    } else {
      return false
    }
  })
}

const loginSubmit = async (username, password) => {
  try {
    const response = await login(username, password);

    if (response.code === 201) {
      // 从响应数据中获取jwt令牌
      const jwtToken = response.data.jwt; // 假设jwt令牌在data中的token字段

      if(jwtToken && jwtToken.trim() !== ''){
        // 存储到持久化存储中
        const persistStore = usePersistStore();
        persistStore.setToken(jwtToken);
        const userInfo = reactive({
          username: response.data.username,
          permission: response.data.permission,
          nickname: response.data.nickname,
          picture: response.data.picture,
          shop: response.data.shop
        })
        persistStore.setUserInfo(userInfo)
        ElMessage.success('登录成功')
        router.push('/')
      }
      else {
        ElMessage.error(response.msg)
      }
    } else {
      // 处理登录失败的情况
      ElMessage.error(response.msg);
    }
  } catch (error) {
    ElMessage.error('登录失败:', error);
  }
};

const registerSubmit = async (username, password, confirmPassword) => {
  try {
    const response = await register(username, password, confirmPassword);

    if (response.code == 2002) {
      // 处理注册失败的情况
      ElMessage.error('注册失败:',response.msg);
    }else {
      ElMessage.success('注册成功，请登录')
      toggleForm()
      router.push('/login')
    }
  } catch (error) {
    ElMessage.error('注册失败?', error);
  }
};

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  background-image: url('https://img.freepik.com/free-vector/gradient-blur-pink-blue-abstract-background_53876-117324.jpg');
  background-size: cover;
  background-position: center;
}

.login-box {
  width: 420px;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.title {
  margin-bottom: 30px;
  text-align: center;
  color: #333;
}

.captcha-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-image {
  width: 40%;
  height: 40px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.toggle-text {
  margin-top: 15px;
  text-align: center;
  color: #666;
  cursor: pointer;
}

.toggle-text:hover {
  color: #409eff;
  text-decoration: underline;
}
</style>

