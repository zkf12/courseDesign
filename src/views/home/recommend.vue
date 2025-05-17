<template>
  <div class="recommend-container">
    <!-- 猜你喜欢 -->
    <div class="guess-like">
      <div class="section-header">
        <h2>猜你喜欢</h2>
        <el-button 
          type="primary" 
          link 
          @click="refreshRecommendations"
          :loading="loadingRecommendations"
        >
          换一批
        </el-button>
      </div>
      
      <div v-if="loadingRecommendations" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <el-row v-else :gutter="20">
        <el-col
          v-for="product in recommendations"
          :key="product.id"
          :xs="12"
          :sm="8"
          :md="6"
          :lg="6"
          :xl="4"
        >
          <ProductCard 
            :product="product" 
            @click="showProductDetail(product)"
            @add-to-cart="addToCart"
            @buy-now="buyNow"
          />
        </el-col>
      </el-row>
      
      <div v-if="recommendations.length === 0 && !loadingRecommendations" class="empty-tip">
        <el-empty description="暂无推荐商品" />
      </div>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProductCard from '@/components/ProductCard.vue'
import ProductDetail from '@/components/ProductDetail.vue'
import { getRecommendations } from '@/api/recommend'
import { addInCart, purchase } from '@/api/trade'

// 数据状态
const recommendations = ref([])
const selectedProduct = ref({})
const detailDialogVisible = ref(false)

// 加载状态
const loadingRecommendations = ref(false)

// 初始化加载推荐
onMounted(() => {
  loadRecommendations()
})

// 加载猜你喜欢推荐
const loadRecommendations = async () => {
  loadingRecommendations.value = true
  try {
    const res = await getRecommendations()
    recommendations.value = res.data.map(p => ({
      ...p,
      image: '/api/' + p.image
    }))
  } catch (error) {
    ElMessage.error('加载推荐失败: ' + error.message)
  } finally {
    loadingRecommendations.value = false
  }
}

// 刷新推荐
const refreshRecommendations = () => {
  loadRecommendations()
}

// 显示商品详情
const showProductDetail = (product) => {
  selectedProduct.value = product
  detailDialogVisible.value = true
}

// 加入购物车
const addToCart = async (product) => {
  try {
    const res = await addInCart(product.id)
    ElMessage.success(res.msg)
    detailDialogVisible.value = false
  } catch (error) {
    ElMessage.error('添加购物车失败: ' + error.message)
  }
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
</script>

<style scoped>
.recommend-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.guess-like {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.loading-container {
  padding: 20px;
}

.empty-tip {
  padding: 40px 0;
}
</style>