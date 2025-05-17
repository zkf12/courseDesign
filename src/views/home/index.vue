<template>
  <div class="home-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索商品名称"
        clearable
        @clear="resetSearch"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button icon="Search" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 分类导航 -->
    <div class="category-nav">
      <el-scrollbar>
        <div class="category-list">
          <div
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            :class="{ active: activeCategory === category.id }"
            @click="changeCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>
      </el-scrollbar>
    </div>

    <!-- 商品展示 -->
    <div class="product-list">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <!-- 商品列表 -->
      <template v-else>
        <el-row :gutter="20">
          <el-col
            v-for="product in products"
            :key="product.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
            :xl="4"
          >
            <div class="product-card" @click="showProductDetail(product)">
              <el-image
                :src="product.image"
                fit="cover"
                class="product-image"
                :preview-src-list="[product.image]"
              />
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <div class="product-price">¥{{ product.price.toFixed(2) }}</div>
                <div class="product-shop">
                  <el-icon><Shop /></el-icon>
                  <span>{{ product.shop }}</span>
                </div>
                <div class="product-actions">
                  <el-button 
                    size="small" 
                    type="primary" 
                    @click.stop="buyNow(product, 1)"
                  >
                    立即购买
                  </el-button>
                  <el-button 
                    size="small" 
                    type="success" 
                    @click.stop="addToCart(product)"
                  >
                    加入购物车
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 分页加载 -->
        <div class="pagination-container">
          <el-button
            v-if="hasMore"
            :loading="loadingMore"
            type="text"
            @click="loadMore"
          >
            加载更多
          </el-button>
          <div v-else class="no-more">没有更多商品了</div>
        </div>
      </template>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
    v-model="detailDialogVisible"
    title="商品详情"
    width="50%"
    :destroy-on-close="true"
  >
    <ProductDetail 
      v-if="detailDialogVisible"  
      :key="selectedProduct.id"
      :product="selectedProduct" 
      @add-to-cart="addToCart"
      @buy-now="buyNow"
    />
  </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { Shop } from '@element-plus/icons-vue'
import ProductDetail from '@/components/ProductDetail.vue'
import { getCategories, getProducts } from '@/api/product'
import { addInCart } from '@/api/trade'
import { ElMessage, ElMessageBox } from 'element-plus'
import { purchase } from '@/api/trade'

// 数据状态
const searchQuery = ref('')
const activeCategory = ref('all')
const categories = ref([])
const products = ref([])
const selectedProduct = ref({})
const detailDialogVisible = ref(false)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0) 
const loading = ref(false)
const loadingMore = ref(false)

// 初始化加载
onMounted(() => {
  loadCategories()
  loadProducts()
})

// 加载分类数据
const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = [{ id: 'all', name: '全部' }, ...res.data.categories]
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载商品数据
const loadProducts = async (reset = true) => {
  if (reset) {
    currentPage.value = 1
    loading.value = true
  } else {
    currentPage.value++
    loadingMore.value = true
  }

  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      category: activeCategory.value,
      keyword: searchQuery.value || undefined
    }

    const res = await getProducts(params)

    // 检查状态码
    // 处理无商品情况
    if (res.code === 4003) {
      products.value = []
      total.value = 0
      return
    }

    // 成功情况
    if (res.code === 402) {
      const pageInfo = res.data
      
      // 更新分页信息
      currentPage.value = pageInfo.page
      pageSize.value = pageInfo.pageSize
      total.value = pageInfo.total
      pageInfo.list = pageInfo.list.map(product => {
        return {
          ...product, // 保留其他属性不变
          image: '/api/' + product.image, // 更新 image 属性
        };
      });

      // 更新商品列表
      if (reset) {
        products.value = pageInfo.list
      } else {
        products.value = [...products.value, ...pageInfo.list]
      }
      console.log(products.value)
    }
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error(error.message)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 判断是否有更多数据（根据后端返回的total计算）
const hasMore = computed(() => {
  return products.value.length < total.value
})

// 切换分类
const changeCategory = (categoryId) => {
  if (activeCategory.value === categoryId) return
  
  activeCategory.value = categoryId
  loadProducts()
}

// 搜索功能
const handleSearch = () => {
  loadProducts()
}

const resetSearch = () => {
  searchQuery.value = ''
  loadProducts()
}

// 加载更多
const loadMore = () => {
  loadProducts(false)
}

// 显示商品详情
const showProductDetail = (product) => {
  selectedProduct.value = product
  detailDialogVisible.value = true
  console.log('打开对话框，product.id:', product.id) // 调试用
}

watch(detailDialogVisible, (newVal) => {
  console.log('对话框状态变化:', newVal) // 调试用
  if (!newVal) {
    selectedProduct.value = {}
  }
})

// 加入购物车
const addToCart = async (product) => {
  try {
    const res = await addInCart(product.id)
    ElMessage.success(res.msg)
  } catch (error) {
    ElMessage.error('添加购物车失败', error)
  }
  detailDialogVisible.value = false
}

// 立即购买
const buyNow = async (product, quantity = 1) => {
  try {
    // 确认购买对话框
    await ElMessageBox.confirm(`确定要购买 ${product.name} 吗?`, '确认购买', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 构造购买数据
    const purchaseData = [{
      productId: product.id,
      quantity: quantity,
      price: product.price
    }]
    console.log(purchaseData)
    
    // 调用购买API
    loading.value = true
    const res = await purchase(purchaseData)
    
    if(res.code === 503) {
      ElMessage.success(res.msg || '购买成功')
      // 关闭商品详情对话框（如果是从详情页调用）
      detailDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '购买失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('购买失败: ' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}

// 商品详情组件中修改购买按钮
// 在ProductDetail.vue中添加数量选择器并修改购买逻辑
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-bar {
  margin-bottom: 20px;
  max-width: 500px;
}

.category-nav {
  margin-bottom: 20px;
}

.category-list {
  display: flex;
  gap: 10px;
  padding-bottom: 10px;
}

.category-item {
  padding: 8px 16px;
  border-radius: 16px;
  background-color: #f5f5f5;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
}

.category-item:hover {
  background-color: #e0e0e0;
}

.category-item.active {
  background-color: var(--el-color-primary);
  color: white;
}

.product-actions {
  margin-top: 10px;
  display: flex;
  gap: 5px;
}

.product-list {
  margin-top: 20px;
  min-height: 300px;
}

.loading-container {
  padding: 20px;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 160px;
  display: block;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  color: var(--el-color-primary);
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}

.product-shop {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
  padding: 20px 0;
}

.no-more {
  color: #999;
  padding: 20px 0;
  text-align: center;
}
</style>