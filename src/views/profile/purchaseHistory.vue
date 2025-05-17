<template>
  <div class="purchase-history-container">
    <!-- 搜索栏 -->
    <div class="search-bar-optimized">
      <el-form 
        :model="searchForm" 
        class="optimized-search-form"
        label-position="top"
      >
        <div class="form-row">
          <el-form-item label="商品名称" class="form-item">
            <el-input
              v-model="searchForm.productName"
              placeholder="输入商品名称"
              clearable
              @clear="resetSearch"
              class="search-input"
            />
          </el-form-item>
          
          <el-form-item label="店铺名称" class="form-item">
            <el-input
              v-model="searchForm.shop"
              placeholder="输入店铺名称"
              clearable
              @clear="resetSearch"
              class="search-input"
            />
          </el-form-item>
          
          <div class="form-actions">
            <el-button 
              type="primary" 
              @click="handleSearch"
              class="search-btn"
            >
              <el-icon><Search /></el-icon>
              <span>搜索</span>
            </el-button>
            <el-button 
              @click="resetSearch"
              class="reset-btn"
            >
              重置
            </el-button>
          </div>
        </div>
      </el-form>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <el-table
        :data="orderList"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderId" label="订单号" width="120" />
        <el-table-column prop="totalPrice" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalPrice.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="tradeTime" label="交易时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.tradeTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              type="text"
              @click="showOrderDetail(row.orderId)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[6, 12, 18, 24]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="50%"
    >
      <el-table :data="orderDetail" border stripe>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="price" label="单价" width="120">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="shop" label="店铺" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPurchaseHistory, getHistoryDetail } from '@/api/trade'
import { usePersistStore } from '@/store/persist'

const persistStore = usePersistStore()

// 搜索表单
const searchForm = ref({
  productName: '',
  shop: ''
})

// 分页数据
const pagination = ref({
  page: 1,
  pageSize: 12,
  total: 0
})

// 订单列表
const orderList = ref([])
const loading = ref(false)

// 订单详情
const detailDialogVisible = ref(false)
const orderDetail = ref([])

// 加载订单数据
const loadOrderData = async () => {
  try {
    loading.value = true
    const params = {
      ...searchForm.value,
      pageNum: pagination.value.page,
      pageSize: pagination.value.pageSize
    }

    // 根据用户权限决定是否添加userId
    if (persistStore.userInfo?.permission < 4) {
      params.username = persistStore.userInfo?.username
    }
    console.log(params)
    const res = await getPurchaseHistory(params)
    if (res.code === 504) {
      orderList.value = res.data.list
      pagination.value.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取订单数据失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 查看订单详情
const showOrderDetail = async (orderId) => {
  try {
    const res = await getHistoryDetail(orderId)
    if (res.code === 505) {
      orderDetail.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.value.page = 1
  loadOrderData()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    productName: '',
    shop: ''
  }
  handleSearch()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  loadOrderData()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.value.page = val
  loadOrderData()
}

// 格式化时间
const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString()
}

// 初始化加载数据
onMounted(() => {
  loadOrderData()
})
</script>

<style scoped>
.purchase-history-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-bar-optimized {
  margin-bottom: 24px;
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.optimized-search-form {
  width: 100%;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

.form-item {
  flex: 1;
  min-width: 200px;
  margin-bottom: 0;
}

.form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  padding-bottom: 8px;
}

.search-input {
  width: 100%;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 22px;
}

.search-btn {
  padding: 10px 20px;
}

.reset-btn {
  padding: 10px 20px;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .form-item {
    width: 100%;
    min-width: unset;
  }
  
  .form-actions {
    width: 100%;
    justify-content: flex-end;
  }
}

.order-list {
  margin-top: 20px;
  background: #ffffff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  background: #ffffff;
  border-radius: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>