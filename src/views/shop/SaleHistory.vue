<template>
  <div class="sale-history-container">
    <!-- 标题和搜索栏 -->
    <div class="header-section">
      <h2 class="page-title">销售历史记录</h2>
      <div class="search-section">
        <el-input
          v-model="searchParams.productName"
          placeholder="输入商品名称搜索"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button 
          type="primary" 
          @click="handleSearch"
          class="search-btn"
        >
          搜索
        </el-button>
      </div>
    </div>

    <!-- 销售历史表格 -->
    <div class="card-container">
      <el-card class="table-card">
        <div class="table-wrapper">
          <el-table
            :data="saleList"
            v-loading="loading"
            stripe
            style="width: 100%"
            height="calc(100vh - 280px)"
          >
            <el-table-column prop="id" label="订单ID" width="100" align="center" />
            <el-table-column prop="username" label="购买用户" width="120" align="center" />
            <el-table-column prop="productName" label="商品名称" min-width="180" />
            <el-table-column prop="quantity" label="数量" width="100" align="center">
              <template #default="{ row }">
                <el-tag>{{ row.quantity }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="总金额" width="140" align="right">
              <template #default="{ row }">
                <span class="price-text">¥{{ row.totalPrice.toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="tradeTime" label="交易时间" width="180" align="center">
              <template #default="{ row }">
                <div class="time-cell">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatTime(row.tradeTime) }}</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[9, 12, 18, 24]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Clock } from '@element-plus/icons-vue'
import { getSaleHistory } from '@/api/shop'
import { usePersistStore } from '@/store/persist'

const persistStore = usePersistStore()

// 搜索参数
const searchParams = reactive({
  productName: '',
  shop: persistStore.userInfo?.shop || ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 9,
  total: 0
})

// 销售历史列表
const saleList = ref([])
const loading = ref(false)

// 加载销售历史数据
const loadSaleData = async () => {
  try {
    loading.value = true
    const params = {
      ...searchParams,
      pageNum: pagination.page,
      pageSize: pagination.pageSize
    }
    
    const res = await getSaleHistory(params)
    if (res.code === 601) {
      saleList.value = res.data.list
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取销售历史失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadSaleData()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadSaleData()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.page = val
  loadSaleData()
}

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}

// 初始化加载数据
onMounted(() => {
  loadSaleData()
})
</script>

<style scoped>
.sale-history-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #303133;
}

.search-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 300px;
}

.search-btn {
  padding: 10px 20px;
}

.card-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.table-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.table-wrapper {
  flex: 1;
}

.pagination-wrapper {
  padding: 16px 0;
  display: flex;
  justify-content: flex-end;
}

.time-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.time-cell .el-icon {
  color: var(--el-color-primary);
}

.price-text {
  font-weight: 600;
  color: var(--el-color-primary);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-section {
    width: 100%;
  }
  
  .search-input {
    flex: 1;
  }
}

/* 表格行悬停效果 */
:deep(.el-table__body tr:hover>td) {
  background-color: #f5f7fa !important;
}
</style>