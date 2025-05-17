<template>
  <div class="product-card" @click="$emit('click', product)">
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
          @click.stop="$emit('buy-now', product)"
        >
          立即购买
        </el-button>
        <el-button 
          size="small" 
          type="success" 
          @click.stop="$emit('add-to-cart', product)"
        >
          加入购物车
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Shop } from '@element-plus/icons-vue'

defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['click', 'add-to-cart', 'buy-now'])
</script>

<style scoped>
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

.product-actions {
  margin-top: 10px;
  display: flex;
  gap: 5px;
}
</style>