<template>
  <div class="products-container">
    <!-- 搜索和排序 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索商品名称"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.sort" placeholder="排序方式">
            <el-option label="默认排序" value="" />
            <el-option label="积分从低到高" value="points" />
            <el-option label="积分从高到低" value="-points" />
            <el-option label="销量从高到低" value="-salesCount" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商品列表 -->
    <el-card v-loading="loading" class="products-list">
      <template v-if="productsList.length === 0">
        <el-empty description="暂无商品" />
      </template>
      <template v-else>
        <el-row :gutter="20">
          <el-col
            v-for="product in productsList"
            :key="product.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <el-card
              class="product-card"
              :body-style="{ padding: '0px' }"
              @click="router.push(`/products/${product.id}`)"
            >
              <div class="product-image">
                <el-image
                  :src="product.coverImage"
                  fit="cover"
                  :preview-src-list="[product.coverImage]"
                />
                <div
                  v-if="product.stock < 10"
                  class="stock-warning"
                >
                  库存紧张
                </div>
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <div class="product-meta">
                  <span class="points">{{ product.points }} 积分</span>
                  <span class="stock" :class="{ 'low-stock': product.stock < 10 }">
                    库存: {{ product.stock }}
                  </span>
                </div>
                <div class="product-footer">
                  <span class="sales">已售 {{ product.salesCount }}</span>
                  <el-button
                    type="primary"
                    size="small"
                    :disabled="product.stock === 0"
                  >
                    {{ product.stock === 0 ? '已售罄' : '立即兑换' }}
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[12, 24, 36, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'

const router = useRouter()
const loading = ref(false)
const productsList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(12)

const searchForm = reactive({
  keyword: '',
  sort: ''
})

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await getProductList({
      page: page.value,
      size: size.value,
      ...searchForm
    })
    productsList.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadProducts()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.sort = ''
  handleSearch()
}

const handleSizeChange = (val: number) => {
  size.value = val
  page.value = 1
  loadProducts()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  loadProducts()
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.products-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.products-list {
  min-height: 400px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  position: relative;
  height: 200px;
}

.product-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.stock-warning {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 2px 8px;
  background-color: rgba(245, 108, 108, 0.9);
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 15px;
}

.product-name {
  margin: 0 0 10px;
  font-size: 16px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.points {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.stock {
  font-size: 14px;
  color: #666;
}

.low-stock {
  color: #f56c6c;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sales {
  font-size: 14px;
  color: #999;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .products-container {
    padding: 10px;
  }

  .product-image {
    height: 160px;
  }
}
</style> 