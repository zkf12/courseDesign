<template>
  <el-container style="height: 100vh;">
    <!-- 侧边栏 -->
    <el-aside width="200px" style="background-color: #545c64;">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
        :collapse="false"
        router
      >
        <!-- 首页菜单项 -->
        <el-sub-menu index='1'>
          <template #title>
            <el-icon><home-filled /></el-icon>
            <span>首页</span>
          </template>
          <el-menu-item 
            index="/home"
          >
            首页
          </el-menu-item>
          <el-menu-item 
            index="/recommend"
            v-if="persistStore.userInfo?.permission < 3"
          >
            猜你喜欢
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 商铺菜单项 -->
        <el-sub-menu index="2" v-if="persistStore.userInfo?.permission < 3">
          <template #title>
            <el-icon><shop /></el-icon>
            <span>商铺</span>
          </template>
          <el-menu-item 
            index="/shop/sub1"
            @click="handleShopMenuClick('/shop/sub1')"
          >
            我的商铺
          </el-menu-item>
          <el-menu-item 
            index="/shop/sub2"
            @click="handleShopMenuClick('/shop/sub2')"
          >
            销售历史
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 个人中心菜单项 -->
        <el-sub-menu index="3">
          <template #title>
            <el-icon><user /></el-icon>
            <span>个人中心</span>
          </template>
          <el-menu-item index="/profile/cart" v-if="persistStore.userInfo?.permission < 3">购物车</el-menu-item>
          <el-menu-item index="/profile/purchaseHistory">购买历史</el-menu-item>
          <el-menu-item index="/profile/sub5">浏览记录</el-menu-item>
          <el-menu-item index="/profile/edit">完善个人信息</el-menu-item>
          <el-menu-item index="/profile/changePassword">修改密码</el-menu-item>
          <el-menu-item index="/userManagement" v-if="persistStore.userInfo?.permission > 3">用户管理</el-menu-item>
          <el-menu-item index="/userlog" v-if="persistStore.userInfo?.permission > 3">用户登陆信息</el-menu-item>
          <el-menu-item index="/showData" v-if="persistStore.userInfo?.permission > 3">销售图表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header style="background-color: #ffffff; border-bottom: 1px solid #eee;">
        <div style="display: flex; justify-content: flex-end; align-items: center; height: 100%;">
          <el-dropdown>
            <span class="el-dropdown-link">
              <!-- 头像绑定 -->
              <el-avatar 
                :size="40" 
                :src="persistStore.userAvatarUrl"
                :fit="'cover'"
              />
              <span style="margin-left: 10px;">{{ persistStore.userInfo?.nickname || '未登录用户' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="persistStore.userInfo" @click="Logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区域 -->
      <el-main style="background-color: #f5f7fa;">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePersistStore } from '@/store/persist'
import {
  HomeFilled,
  Shop,
  User,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/login_register'

const route = useRoute()
const router = useRouter()
const persistStore = usePersistStore()

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

// 退出登录
const Logout = async () => {
  const res = await logout()
  if(res.code === 203){
    persistStore.removeToken()
    persistStore.removeUserInfo()
    router.push('/login')
    ElMessage.info('注销成功')
  }
}

const handleShopMenuClick = (path) => {
  const permission = persistStore.userInfo?.permission || 0
  console.log(permission)
  
  if (permission === 0) {
    // 未申请商铺，跳转到申请页面
    router.push('/shop/apply')
    ElMessage.info('请先申请开通商铺')
  } else if (permission === 2) {
    // 申请中，跳转到等待页面
    router.push('/shop/waiting')
    ElMessage.warning('您的商铺申请正在审核中，请耐心等待')
  } else if (permission === 1) {
    // 已通过，正常跳转
    router.push(path)
  } else {
    // 其他情况跳回首页
    router.push('/home')
    ElMessage.warning('您没有访问此页面的权限')
  }
}
</script>

<style scoped>
.el-header {
  padding: 0 20px;
}
.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}
</style>