<template>
  <div class="view-records-container">
    <!-- 搜索栏（仅管理员可见） -->
    <div class="search-header" v-if="isAdmin">
      <el-input
        v-model="searchQuery"
        placeholder="输入用户名搜索"
        clearable
        style="width: 300px"
        @clear="resetSearch"
        @keyup.enter="fetchViewRecords"
      />
      <el-button type="primary" @click="fetchViewRecords" :icon="Search">
        搜索
      </el-button>
    </div>

    <!-- 浏览记录表格 -->
    <el-table
      :data="viewRecords"
      border
      stripe
      v-loading="loading"
      style="width: 100%"
      empty-text="暂无浏览记录"
    >
      <el-table-column prop="id" label="记录ID" width="100" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="productName" label="商品名称" width="200" />
      
      <el-table-column label="浏览时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.viewTime) }}
        </template>
      </el-table-column>
      
      <el-table-column label="停留时长" width="120">
        <template #default="{ row }">
          {{ row.time || '--' }}
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchViewRecords"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getViewTime } from '@/api/product'
import { usePersistStore } from '@/store/persist'

const persistStore = usePersistStore()

// 数据状态
const searchQuery = ref('')
const viewRecords = ref([])
const loading = ref(false)

// 分页信息
const pagination = ref({
  page: 1,
  pageSize: 12,
  total: 0
})

// 检查当前用户是否是管理员
const isAdmin = computed(() => {
  return persistStore.userInfo?.permission > 3
})

// 获取浏览记录
const fetchViewRecords = async () => {
  try {
    loading.value = true
    
    // 构建请求参数
    const params = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize
    }
    
    // 非管理员自动添加当前用户名作为参数
    if (!isAdmin.value && persistStore.userInfo?.username) {
      params.username = persistStore.userInfo.username
    } else if (searchQuery.value) {
      // 管理员且有搜索词时使用搜索词
      params.username = searchQuery.value
    }
    
    const res = await getViewTime(params)
    
    if (res.code === 408) {
      viewRecords.value = res.data.list
      pagination.value = {
        page: res.data.page,
        pageSize: res.data.pageSize,
        total: res.data.total
      }
    } else {
      ElMessage.error(res.msg || '获取浏览记录失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (datetime) => {
  if (!datetime) return '--'
  return new Date(datetime).toLocaleString()
}

// 清空搜索
const resetSearch = () => {
  searchQuery.value = ''
  pagination.value.page = 1
  fetchViewRecords()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.value.pageSize = size
  pagination.value.page = 1
  fetchViewRecords()
}

// 初始化加载数据
onMounted(() => {
  fetchViewRecords()
})
</script>

<style scoped>
.view-records-container {
  padding: 20px;
}

.search-header {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>