<template>
  <div class="shop-apply-container">
    <el-card class="apply-card">
      <template #header>
        <div class="card-header">
          <span>商铺申请</span>
        </div>
      </template>
      
      <el-form :model="form" label-width="120px">
        <el-form-item label="商铺名称" required>
          <el-input v-model="form.shopName" placeholder="请输入商铺名称" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitApplication">提交申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { usePersistStore } from '@/store/persist'
import { ElMessage } from 'element-plus'
import { applyForShop } from '@/api/shop'
import { useRouter } from 'vue-router'

const router = useRouter()
const persistStore = usePersistStore()
const form = ref({
  username: persistStore.userInfo.username,
  shop: ''
})

const submitApplication = async () => {
  if (!form.value.shopName.trim()) {
    ElMessage.error('请输入商铺名称')
    return
  }
  
  try {
    // 调用API提交申请
    const res = await applyForShop(form.value)
    if(res.code === 602){
      ElMessage.success('申请提交成功，请等待管理员审核')
      // 更新用户权限状态为2（审核中）
      persistStore.userInfo.permission = 2
    }else{
      ElMessage.error(res.msg)
    }
    // 跳转到等待页面
    router.push('/shop/waiting')
  } catch (error) {
    ElMessage.error('提交申请失败：' + error.message)
  }
}
</script>

<style scoped>
.shop-apply-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.apply-card {
  width: 600px;
}
</style>