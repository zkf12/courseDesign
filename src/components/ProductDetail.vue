<template>
  <div class="product-detail">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-image
          :src="product?.image || ''"
          fit="contain"
          style="width: 100%; height: 300px"
        />
      </el-col>
      <el-col :span="12">
        <h2>{{ product?.name || '未知商品' }}</h2>
        <p class="price">¥{{ formatPrice(product?.price) }}</p>
        <p class="shop-info">
          <el-icon><Shop /></el-icon>
          {{ product?.shop || '未知店铺' }}
        </p>
        
        <el-divider />
        
        <h3>商品描述</h3>
        <p>{{ product?.description || '暂无描述' }}</p>
        
        <el-divider />
        
        <div class="specs" v-if="product?.specs?.length">
          <h3>规格参数</h3>
          <el-table :data="product.specs" border>
            <el-table-column prop="name" label="规格" width="120" />
            <el-table-column prop="value" label="参数" />
          </el-table>
        </div>
        
        <div class="actions">
          <el-button type="primary" size="large" @click="$emit('add-to-cart', product)">
            加入购物车
          </el-button>
          <el-button type="danger" size="large" @click="$emit('buy-now', product)">
            立即购买
          </el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>


<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Shop } from '@element-plus/icons-vue'
import { recordViewDuration } from '@/api/product'

const props = defineProps({
  product: {
    type: Object,
    default: () => ({
      id: null,
      name: '未知商品',
      price: 0,
      image: '',
      shop: '未知店铺',
      description: '暂无描述',
      specs: []
    })
  }
})

const emit = defineEmits(['add-to-cart', 'buy-now'])

const startTime = ref(null)
const duration = ref(0)

// 安全格式化价格
const formatPrice = (price) => {
  return (price || 0).toFixed(2)
}

// 进入详情时开始计时
onMounted(() => {
  console.log('商品详情加载:', props.product.id)
  startTime.value = Date.now() // 使用时间戳更精确
})

// 退出详情时结束计时并发送数据
onUnmounted(async () => {
  if (!startTime.value) return
  
  duration.value = Math.floor((Date.now() - startTime.value) / 1000)
  console.log('商品浏览时长:', {
    id: props.product.id,
    name: props.product.name,
    duration: duration.value + '秒'
  })

  if (!props.product.id) {
    console.warn('无有效商品ID，跳过记录')
    return
  }

  try {
    const res = await recordViewDuration({
      productName: props.product.name,
      duration: duration.value
    })
    console.log('浏览记录提交成功:', res)
  } catch (error) {
    console.error('浏览记录提交失败:', error)
  }
})
</script>


<style scoped>
.product-detail {
  padding: 10px;
}

.price {
  font-size: 24px;
  color: var(--el-color-primary);
  margin: 10px 0;
}

.shop-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  margin-bottom: 15px;
}

.specs {
  margin-top: 20px;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

h2, h3 {
  color: #333;
}

p {
  color: #666;
  line-height: 1.6;
}
</style>