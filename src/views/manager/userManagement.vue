<template>
  <div class="user-management-container">
    <!-- 搜索栏和筛选 -->
    <div class="search-filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="输入用户名搜索"
        clearable
        style="width: 300px"
        @keyup.enter="fetchUserData"
      />
      <el-button type="primary" @click="fetchUserData">搜索</el-button>
    </div>

    <!-- 用户表格 -->
    <el-table
      :data="userList"
      border
      stripe
      v-loading="loading"
      style="width: 100%"
    >
      <!-- 用户头像 -->
      <el-table-column label="头像" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.picture"
            :src="getFullImagePath(row.picture)"
            :preview-src-list="[getFullImagePath(row.picture)]"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 50%"
          />
          <el-avatar v-else :size="60">{{ row.nickname?.charAt(0) }}</el-avatar>
        </template>
      </el-table-column>

      <!-- 基本信息列 -->
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="nickname" label="昵称" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="phone" label="电话" width="150" />
      
      <!-- 权限列 -->
      <el-table-column label="权限" width="120">
        <template #default="{ row }">
          <el-tag :type="getPermissionTagType(row.permission)">
            {{ getPermissionText(row.permission) }}
          </el-tag>
        </template>
      </el-table-column>
      
      <!-- 商户信息 -->
      <el-table-column prop="shop" label="店铺" width="150" />
      
      <!-- 时间信息 -->
      <el-table-column label="注册时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="更新时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.updateTime) }}
        </template>
      </el-table-column>
      
      <!-- 操作列 -->
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.permission === 2"
            type="success"
            size="small"
            @click="handleApprove(row.username)"
          >
            批准
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(row.id)"
            v-if="row.permission < 3"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchUserData"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, permitShop, deleteUser } from '@/api/user'

// 数据状态
const searchQuery = ref('')
const userList = ref([])
const loading = ref(false)

// 分页信息
const pagination = ref({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

// 获取用户数据
const fetchUserData = async () => {
  try {
    loading.value = true
    const res = await getUserList(
      searchQuery.value,
      pagination.value.pageNum,
      pagination.value.pageSize
    )
    
    if (res.code === 204) {
      userList.value = res.data.list
      pagination.value.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理图片路径
const getFullImagePath = (image) => {
  if (!image) return ''
  return `/api/${image}`
}

// 权限文本转换
const getPermissionText = (permission) => {
  const map = {
    0: '普通用户',
    1: '商户',
    2: '审核中',
  }
  return permission > 3 ? '管理员' : map[permission] || '未知'
}

// 权限标签类型
const getPermissionTagType = (permission) => {
  const map = {
    0: '',
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return permission > 3 ? 'danger' : map[permission] || ''
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString()
}

// 批准商户申请
const handleApprove = async (username) => {
  try {
    await ElMessageBox.confirm(`确定要批准用户 ${username} 的商户申请吗?`, '确认批准', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await permitShop(username)
    if (res.code === 603) {
      ElMessage.success(res.msg || '批准成功')
      fetchUserData()
    } else {
      ElMessage.error(res.msg || '批准失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 删除用户
const handleDelete = async (userId) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗?', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteUser(userId)
    if (res.code === 303) {
      ElMessage.success(res.msg || '删除成功')
      // 重新加载数据时考虑当前分页
      if (userList.value.length === 1 && pagination.value.pageNum > 1) {
        pagination.value.pageNum-- // 如果删除的是最后一页的最后一条，则返回上一页
      }
      fetchUserData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }
}

// 初始化加载数据
onMounted(() => {
  fetchUserData()
})
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.search-filter-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-tag {
  margin-right: 5px;
}
</style>