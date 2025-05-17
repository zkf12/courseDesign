<template>
  <div class="waiting-container">
    <el-result
      icon="info"
      title="审核中"
      sub-title="您的商铺申请正在审核中，请耐心等待管理员处理"
    >
      <template #extra>
        <el-button type="primary" @click="goToHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToHome = () => {
  router.push('/home')
}
</script>

<style scoped>
.waiting-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>