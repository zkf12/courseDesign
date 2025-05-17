<template>
  <div class="cart-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="请输入商品名称搜索"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button icon="Search" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 表格容器 -->
    <div class="table-container">
      <!-- 加载状态 -->
      <el-skeleton :rows="5" animated v-if="loading" />
      
      <!-- 空状态 -->
      <el-empty description="购物车为空" v-else-if="cartItems.length === 0" />
      
      <!-- 购物车表格 -->
      <el-table
        v-else
        :data="filteredCartItems"
        style="width: 100%"
        border
        stripe
        v-loading="loading"
      >
        <!-- 商品图片 -->
        <el-table-column label="商品图片" width="120">
          <template #default="{ row }">
            <el-image
              :src="row.image"
              :preview-src-list="[row.image]"
              fit="cover"
              style="width: 80px; height: 80px"
            />
          </template>
        </el-table-column>

        <!-- 商品名称 -->
        <el-table-column prop="name" label="商品名称" width="180">
          <template #default="{ row }">
            <el-button type="text" @click="showProductDetail(row)">
              {{ row.name }}
            </el-button>
          </template>
        </el-table-column>

        <!-- 商品价格 -->
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            ¥{{ row.price.toFixed(2) }}
          </template>
        </el-table-column>

        <!-- 数量选择 -->
        <el-table-column label="数量" width="180">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="0"
              @change="handleQuantityChange(row)"
            />
          </template>
        </el-table-column>

        <!-- 小计 -->
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="danger"
                size="small"
                icon="Delete"
                @click="removeItem(row.id)"
              >
                移出购物车
              </el-button>
              <el-button
                type="primary"
                size="small"
                @click="buyNow(row)"
                :disabled="row.quantity <= 0"
              >
                购买
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </div>

      
    <!-- 底部汇总 -->
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="total">
        总计: ¥{{ totalPrice.toFixed(2) }} (共{{ totalQuantity }}件商品)
      </div>
      <el-button 
        type="primary" 
        size="large" 
        @click="checkoutAll"
        :loading="loading"
      >
        结算全部
      </el-button>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="商品详情"
      width="50%"
    >
      <ProductDetail 
        :product="selectedProduct" 
        @add-to-cart="addToCart"
        @buy-now="buyNow"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCart, 
  deleteCart,
  purchase
} from '@/api/trade'
import ProductDetail from '@/components/ProductDetail.vue'

// 数据状态
const searchQuery = ref('')
const cartItems = ref([])
const selectedProduct = ref({})
const detailDialogVisible = ref(false)
const loading = ref(false)

// 加载购物车数据
const loadCartData = async () => {
  try {
    loading.value = true
    const res = await getCart()
    if (res.code === 403) {
      cartItems.value = res.data.map(item => ({
        ...item,
        id: item.id,
        image: '/api/' + item.image, // 更新 image 属性
        quantity: 0, // 默认数量为1
      }))
      console.log(cartItems.value)
    } else {
      ElMessage.error(res.msg || '获取购物车数据失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 初始化加载数据
onMounted(() => {
  loadCartData()
})

// 搜索功能
const filteredCartItems = computed(() => {
  if (!searchQuery.value) return cartItems.value
  return cartItems.value.filter(item =>
    item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

// 商品详情
const showProductDetail = (product) => {
  selectedProduct.value = product
  detailDialogVisible.value = true
}

// 数量变化
const handleQuantityChange = (item) => {
  console.log(`商品 ${item.name} 数量修改为: ${item.quantity}`)
  // 这里可以添加更新购物车数量的API调用
}

// 移除商品
const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定要移除该商品吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteCart(id)
    if (res.code === 502) {
      ElMessage.success(res.msg || '移除成功')
      loadCartData()
    } else {
      ElMessage.error(res.msg || '移除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 立即购买
const buyNow = async (item) => {
  try {
    await ElMessageBox.confirm(`确定要购买 ${item.name} 吗?`, '确认购买', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 构造符合后端要求的购买数据
    const purchaseData = [{
      productId: item.id,
      quantity: item.quantity || 0,
      price: item.price
    }]
    console.log(purchaseData)
    
    // 调用购买API
    const res = await purchase(purchaseData)
    if(res.code === 503) {
      ElMessage.success(res.msg || '购买成功')
      // 购买成功后询问是否移出购物车
      try {
        await ElMessageBox.confirm('购买成功，是否从购物车移出该商品?', '提示', {
          confirmButtonText: '移出',
          cancelButtonText: '保留',
          type: 'success'
        })
        // 用户选择移出
        await deleteCart(item.id)
        loadCartData()
      } catch (ignore) {
        // 用户选择保留，不做任何操作
      }
    } else {
      ElMessage.error(res.msg || '购买失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('购买失败: ' + (error.message || '未知错误'))
    }
  }
}

// 结算全部
const checkoutAll = async () => {
  // 过滤掉数量为0的商品
  const validItems = cartItems.value.filter(item => item.quantity > 0)
  
  if (validItems.length === 0) {
    ElMessage.warning('没有可结算的商品')
    return
  }
  
  try {
    await ElMessageBox.confirm(`确定要结算 ${validItems.length} 件商品吗?`, '确认结算', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 构造符合后端要求的结算数据
    const checkoutData = validItems.map(item => ({
      productId: item.id,
      quantity: item.quantity,
      price: item.price
    }))
    
    // 调用购买API
    const res = await purchase(checkoutData)
    if(res.code === 503) {
      ElMessage.success(res.msg || '结算成功')
      // 结算成功后询问是否移出购物车
      try {
        await ElMessageBox.confirm('结算成功，是否从购物车移出已购买商品?', '提示', {
          confirmButtonText: '移出',
          cancelButtonText: '保留',
          type: 'success'
        })
        // 用户选择移出
        await Promise.all(validItems.map(item => deleteCart(item.id)))
        loadCartData()
      } catch (ignore) {
        // 用户选择保留，不做任何操作
      }
    } else {
      ElMessage.error(res.msg || '结算失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('结算失败: ' + (error.message || '未知错误'))
    }
  }
}

// 加入购物车
const addToCart = () => {
  ElMessage.error("该物品已在购物车中!")
  detailDialogVisible.value = false
}

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.quantity > 0) // 过滤掉数量为 0 的商品
    .reduce((total, item) => total + (item.price * item.quantity), 0)
})

// 计算总数量
const totalQuantity = computed(() => {
  return cartItems.value
    .filter(item => item.quantity > 0) // 过滤掉数量为 0 的商品
    .reduce((total, item) => total + item.quantity, 0)
})
</script>

<style scoped>
.cart-container {
  padding: 20px;
  position: relative;
  min-height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}


.search-bar {
  margin-bottom: 20px;
  width: 300px;
}

.cart-footer {
  position: sticky;
  bottom: 20px;
  background: #fff;
  padding: 16px;
  border-radius: 4px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 10;
  margin-top: auto;
}

.table-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
}

.total {
  font-size: 18px;
  font-weight: bold;
}

.el-image {
  cursor: pointer;
  transition: transform 0.3s;
}

.el-image:hover {
  transform: scale(1.05);
}

.product-detail {
  padding: 10px;
}

.price {
  font-size: 24px;
  color: #f56c6c;
  margin: 10px 0;
}

.specs {
  margin-top: 20px;
}

h2, h3 {
  color: #333;
}

p {
  color: #666;
  line-height: 1.6;
}

.search-bar {
  margin-bottom: 20px;
  width: 300px;
}
</style>