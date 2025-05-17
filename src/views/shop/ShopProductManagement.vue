<template>
  <div class="product-management-container">
    <!-- 搜索和操作栏 -->
    <div class="action-bar">
      <div class="search-box">
        <el-input
          v-model="searchParams.productName"
          placeholder="输入商品名称搜索"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        添加商品
      </el-button>
    </div>

    <!-- 商品列表 -->
    <el-table
      :data="productList"
      v-loading="loading"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品图片" width="120">
        <template #default="{ row }">
          <el-image
            :src="row.image"
            fit="cover"
            style="width: 80px; height: 80px"
            :preview-src-list="[row.image]"
            hide-on-click-modal
          />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" width="180" />
      <el-table-column label="价格" width="120">
        <template #default="{ row }">
          ¥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '上架' ? 'success' : 'danger'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(row.id)"
          >
            删除
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
        :page-sizes="[9, 18, 27, 36]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="50%"
    >
      <el-form
        ref="productForm"
        :model="currentProduct"
        :rules="productRules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="currentProduct.name" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="currentProduct.price" :min="0" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleImageChange"
          >
            <img v-if="imagePreview" :src="imagePreview" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="currentProduct.description"
            type="textarea"
            :rows="3"
          />
            </el-form-item>
            <el-form-item label="分类" prop="category">
            <el-select
              v-model="currentProduct.category"
              placeholder="请选择分类"
              filterable
              clearable
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.name"
              />
          </el-select>
        </el-form-item>
        <el-form-item label="店铺" prop="shop">
          <el-input v-model="currentProduct.shop" disabled />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="currentProduct.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="currentProduct.status">
            <el-option label="上架" value="上架" />
            <el-option label="下架" value="下架" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getProductList, 
  insertProduct, 
  updateProduct,
  updateProductImage,
  deleteProduct
} from '@/api/shop'
import  {getCategories as fetchCategories } from '@/api/product'
import { usePersistStore } from '@/store/persist'

const persistStore = usePersistStore()

const categories = ref([])

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

// 商品列表
const productList = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const currentProduct = ref({
  id: null,
  name: '',
  price: 0,
  image: '',
  description: '',
  category: '',
  shop: persistStore.userInfo?.shop || '',
  stock: 0,
  status: '上架'
})

// 图片上传相关
const imageFile = ref(null)
const imagePreview = ref('')

// 表单验证规则
const productRules = reactive({
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
})

// 加载商品数据
const loadProductData = async () => {
  try {
    loading.value = true
    const params = {
      ...searchParams,
      pageNum: pagination.page,
      pageSize: pagination.pageSize
    }
    
    const res = await getProductList(params)
    if (res.code === 601) {
      productList.value = res.data.list.map(product => ({
        ...product,
        image: product.image ? '/api/' + product.image : ''
      }))
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.msg || '获取商品数据失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理图片选择
const handleImageChange = (file) => {
  const isImage = file.raw.type.includes('image/')
  const isLt2M = file.raw.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB!')
    return false
  }

  imageFile.value = file.raw
  imagePreview.value = URL.createObjectURL(file.raw)
  return true
}

// 上传图片
const uploadProductImage = async (productId) => {
  if (!imageFile.value) return null

  const formData = new FormData()
  formData.append('avatarFile', imageFile.value)
  formData.append('productId', productId)

  try {
    const res = await updateProductImage(formData)
    if (res.code === 406) {
      return res.data // 返回新的图片路径
    }
    return null
  } catch (error) {
    ElMessage.error('图片上传失败')
    return null
  }
}

// 搜索商品
const handleSearch = () => {
  pagination.page = 1
  loadProductData()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadProductData()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.page = val
  loadProductData()
}

// 显示添加对话框
const showAddDialog = () => {
  dialogTitle.value = '添加商品'
  currentProduct.value = {
    id: null,
    name: '',
    price: 0,
    image: '',
    description: '',
    category: '',
    shop: persistStore.userInfo?.shop || '',
    stock: 0,
    status: '上架'
  }
  imagePreview.value = ''
  imageFile.value = null
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (product) => {
  dialogTitle.value = '编辑商品'
  currentProduct.value = { ...product }
  imagePreview.value = product.image
  imageFile.value = null
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  try {
    // 如果是编辑且有新图片，先上传图片
    if (currentProduct.value.id && imageFile.value) {
      const newImage = await uploadProductImage(currentProduct.value.id)
    }
    // 更新或添加商品信息
    if (currentProduct.value.id) {
      await updateProduct(currentProduct.value)
      ElMessage.success('商品更新成功')
    } else {
      // 如果是新增且有图片，先添加商品再上传图片
      const res = await insertProduct(currentProduct.value)
      if (res.code === 405 && imageFile.value) {
        const newImage = await uploadProductImage(res.data.id)
      }
      ElMessage.success('商品添加成功')
    }
    
    dialogVisible.value = false
    loadProductData()
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  }
}

// 删除商品
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await deleteProduct(id)
    ElMessage.success(res.msg)
    loadProductData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 加载分类数据
const loadCategories = async () => {
  try {
    const res = await fetchCategories()
    if (res.code === 401) {
      categories.value = res.data.categories.map(item => ({
        id: item.id,
        name: item.name
      }))
    } else {
      ElMessage.error(res.msg || '获取分类失败')
    }
  } catch (error) {
    ElMessage.error('加载分类数据失败')
  }
}

// 初始化加载数据
onMounted(async () => {
  await loadCategories()
  loadProductData()
})
</script>

<style scoped>
.product-management-container {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.el-image {
  cursor: pointer;
  transition: transform 0.3s;
}

.el-image:hover {
  transform: scale(1.05);
}

/* 图片上传样式 */
.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 150px;
  height: 150px;
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  text-align: center;
  line-height: 150px;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>