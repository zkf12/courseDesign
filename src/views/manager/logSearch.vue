<template>
  <div class="log-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="输入用户名搜索"
        clearable
        style="width: 300px"
        @keyup.enter="fetchLogData"
        @clear="handleClearSearch"
      />
      <el-button type="primary" @click="fetchLogData" :icon="Search">搜索</el-button>
    </div>

    <!-- 日志表格 -->
    <el-table
      :data="logList"
      border
      stripe
      v-loading="loading"
      style="width: 100%"
      empty-text="暂无日志数据"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      
      <el-table-column label="时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.time) }}
        </template>
      </el-table-column>
      
      <el-table-column label="IP地址" width="180">
        <template #default="{ row }">
          <el-tag v-if="row.ip === '0:0:0:0:0:0:0:1'">本地访问</el-tag>
          <span v-else>{{ row.ip }}</span>
        </template>
      </el-table-column>
      
      <el-table-column label="操作类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getOperationType(row.loginOrLogout)">
            {{ getOperationText(row.loginOrLogout) }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            @click="handleDeleteLog(row.id)"
          >
            删除记录
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
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchLogData"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserLog, deleteLog } from '@/api/user'

// 数据状态
const searchQuery = ref('')
const logList = ref([])
const loading = ref(false)
const userPermission = ref(0) // 从store或localStorage获取当前用户权限

// 分页信息
const pagination = ref({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

// 获取日志数据
const fetchLogData = async () => {
  try {
    loading.value = true
    const res = await getUserLog(
      searchQuery.value,
      pagination.value.pageNum,
      pagination.value.pageSize
    )
    
    if (res.code === 205) {
      logList.value = res.data.list
      pagination.value.total = res.data.total
    } else if (res.code === 403) {
      ElMessage.warning(res.msg || '权限不足')
    } else {
      ElMessage.error(res.msg || '获取日志失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '未知时间'
  return new Date(datetime).toLocaleString()
}

// 获取操作类型文本
const getOperationText = (type) => {
  const map = {
    'login': '登录',
    'logout': '登出',
    null: '未知操作'
  }
  return map[type] || type
}

// 获取操作类型标签样式
const getOperationType = (type) => {
  const map = {
    'login': 'success',
    'logout': 'info',
    null: ''
  }
  return map[type] || ''
}

// 清空搜索
const handleClearSearch = () => {
  searchQuery.value = ''
  pagination.value.pageNum = 1
  fetchLogData()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.pageNum = 1
  fetchLogData()
}

// 删除日志记录
const handleDeleteLog = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条日志记录吗?', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteLog(id)
    if (res.code === 304) {
      ElMessage.success(res.msg || '删除成功')
      // 如果删除的是最后一页的最后一条，返回上一页
      if (logList.value.length === 1 && pagination.value.pageNum > 1) {
        pagination.value.pageNum--
      }
      fetchLogData()
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
  // 从store或localStorage获取当前用户权限
  // userPermission.value = getUserPermission()
  fetchLogData()
})
</script>

<style scoped>
.log-container {
  padding: 20px;
}

.search-bar {
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