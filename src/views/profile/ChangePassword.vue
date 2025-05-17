<template>
  <el-card class="password-card">
    <template #header>
      <div class="card-header">
        <span>修改密码</span>
      </div>
    </template>
    
    <el-form 
      ref="passwordForm" 
      :model="form" 
      :rules="rules" 
      label-width="120px"
      label-position="left"
      status-icon
    >
      <!-- 原密码 -->
      <el-form-item label="原密码" prop="oldPassword">
        <el-input 
          v-model="form.oldPassword" 
          type="password" 
          placeholder="请输入当前密码"
          show-password
          clearable
        />
      </el-form-item>
      
      <!-- 新密码 -->
      <el-form-item label="新密码" prop="password">
        <el-input 
          v-model="form.password" 
          type="password" 
          placeholder="请输入新密码"
          show-password
          clearable
        />
        <div class="password-strength" :class="passwordStrengthClass">
          密码强度: {{ passwordStrengthText }}
        </div>
      </el-form-item>
      
      <!-- 确认新密码 -->
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input 
          v-model="form.confirmPassword" 
          type="password" 
          placeholder="请再次输入新密码"
          show-password
          clearable
        />
      </el-form-item>
      
      <!-- 提交按钮 -->
      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="loading">
          确认修改
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { changePassword } from '@/api/upload'

const passwordForm = ref(null)
const loading = ref(false)

// 表单数据
const form = reactive({
  oldPassword: '',
  password: '',
  confirmPassword: ''
})

// 密码强度计算
const passwordStrength = computed(() => {
  if (!form.password) return 0
  
  let strength = 0
  // 长度大于8
  if (form.password.length >= 8) strength += 1
  // 包含数字
  if (/\d/.test(form.password)) strength += 1
  // 包含小写字母
  if (/[a-z]/.test(form.password)) strength += 1
  // 包含大写字母
  if (/[A-Z]/.test(form.password)) strength += 1
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(form.password)) strength += 1
  
  return strength
})

// 密码强度文本和样式
const passwordStrengthText = computed(() => {
  const texts = ['非常弱', '弱', '中等', '强', '非常强']
  return texts[passwordStrength.value] || ''
})

const passwordStrengthClass = computed(() => {
  const classes = ['weak', 'weak', 'medium', 'strong', 'very-strong']
  return classes[passwordStrength.value] || ''
})

// 表单验证规则
const validateOldPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入原密码'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8位'))
  } else if (value === form.oldPassword) {
    callback(new Error('新密码不能与原密码相同'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = reactive({
  oldPassword: [
    { validator: validateOldPassword, trigger: 'blur' }
  ],
  password: [ // 注意这里改为password而不是newPassword
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 提交表单
const submitForm = () => {
  passwordForm.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 创建FormData对象
        const formData = new FormData()
        formData.append('oldPassword', form.oldPassword)
        formData.append('password', form.password)
        formData.append('confirmPassword', form.confirmPassword)
        console.log("22222222")
        const response = await changePassword(formData)
        if(response.code === 302){
          ElMessage.success(response.msg)
        }else {
          ElMessage.error(response.msg)
        }
        resetForm()
      } catch (error) {
        console.log("11111")
        ElMessage.error(error.message || '密码修改失败')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.error('请填写正确的信息')
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  passwordForm.value.resetFields()
}
</script>

<style scoped>
.password-card {
  max-width: 600px;
  margin: 0 auto;
}

.password-strength {
  margin-top: 8px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.password-strength.weak {
  background-color: #ffebee;
  color: #f44336;
}

.password-strength.medium {
  background-color: #fff8e1;
  color: #ffa000;
}

.password-strength.strong {
  background-color: #e8f5e9;
  color: #4caf50;
}

.password-strength.very-strong {
  background-color: #e3f2fd;
  color: #2196f3;
}
</style>