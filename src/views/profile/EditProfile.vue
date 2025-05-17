<template>
  <el-card class="profile-card">
    <template #header>
      <div class="card-header">
        <span>完善个人信息</span>
      </div>
    </template>
    
    <el-form 
      ref="profileFormRef" 
      :model="form" 
      :rules="rules" 
      label-width="100px" 
      label-position="left"
    >
      <!-- 头像上传 -->
      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          :show-file-list="false"
          :auto-upload="false"
          :on-change="handleAvatarChange"
        >
          <img v-if="form.avatar" :src="form.avatar" class="avatar">
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      
      <!-- 用户名 -->
      <el-form-item label="用户名" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入用户名" clearable />
      </el-form-item>
      
      <!-- 邮箱 -->
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" clearable />
      </el-form-item>
      
      <!-- 电话 -->
      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入电话号码" clearable />
      </el-form-item>
      
      <!-- 提交按钮 -->
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存修改</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { updateProfile } from '@/api/upload'
import { usePersistStore } from '@/store/persist'

const profileFormRef = ref(null) // 修改了ref名称并确保声明
const avatarFile = ref(null)

// 表单数据
const form = reactive({
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  nickname: '',
  email: '',
  phone: ''
})

// 表单验证规则 - 确保prop与表单字段一致
const rules = reactive({
  nickname: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 16, message: '长度在 2 到 16 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

// 处理头像选择变化
const handleAvatarChange = (file) => {
  // 更全面的图片类型验证
  const validTypes = ['image/jpeg', 'image/png', 'image/x-png']
  const isImage = validTypes.includes(file.type)
  
  // 或者更宽松的验证方式 - 检查文件扩展名
  const fileExtension = file.name.split('.').pop().toLowerCase()
  const isValidExtension = ['jpg', 'jpeg', 'png'].includes(fileExtension)
  
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage && !isValidExtension) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    form.avatar = e.target.result
  }
  reader.readAsDataURL(file.raw)
  
  avatarFile.value = file.raw
  return false
}

// 提交表单
const submitForm = () => {
  profileFormRef.value?.validate(async (valid) => { // 使用可选链操作符
    if (valid) {
      try {
        const formData = new FormData()
        
        formData.append('nickname', form.nickname)
        formData.append('email', form.email)
        formData.append('phone', form.phone)
        
        if (avatarFile.value) {
          formData.append('avatarFile', avatarFile.value)
        }
        
        const response = await updateProfile(formData)
        
        if (response.code === 301) {
          ElMessage.success(response.msg || '修改成功')
          const persistStore = usePersistStore()
          persistStore.userInfo.picture = response.data.avatarUrl
          persistStore.userInfo.nickname = response.data.nickname
          resetForm()
        } else {
          ElMessage.error(response.msg || '修改失败')
        }
      } catch (error) {
        ElMessage.error('提交失败: ' + (error.message || '未知错误'))
      }
    } else {
      ElMessage.error('请填写正确的信息')
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  profileFormRef.value?.resetFields()
  form.avatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  avatarFile.value = null
}
</script>

<style scoped>
.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
  border-radius: 50%;
  border: 1px dashed var(--el-border-color);
}
</style>