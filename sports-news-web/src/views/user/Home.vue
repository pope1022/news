<template>
  <div class="home">
    <!-- 新闻轮播 -->
    <el-card class="section">
      <template #header>
        <div class="section-header">
          <h3>热门新闻</h3>
          <el-button text @click="router.push('/news')">查看更多</el-button>
        </div>
      </template>
      <el-carousel height="400px" :interval="5000">
        <el-carousel-item v-for="news in hotNews" :key="news.id">
          <div class="carousel-item" @click="router.push(`/news/${news.id}`)">
            <img :src="news.coverImage" :alt="news.title">
            <div class="carousel-content">
              <h4>{{ news.title }}</h4>
              <p>{{ news.content.substring(0, 100) }}...</p>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-card>

    <!-- 最新新闻 -->
    <el-card class="section">
      <template #header>
        <div class="section-header">
          <h3>最新新闻</h3>
          <div class="category-filter">
            <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
              <el-radio-button label="">全部</el-radio-button>
              <el-radio-button v-for="category in categories" :key="category.id" :label="category.id">
                {{ category.name }}
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col v-for="news in latestNews" :key="news.id" :xs="24" :sm="12" :md="8">
          <el-card class="news-card" @click="router.push(`/news/${news.id}`)">
            <img :src="news.coverImage" class="news-image" :alt="news.title">
            <div class="news-info">
              <h4>{{ news.title }}</h4>
              <p class="news-summary">{{ news.content.substring(0, 50) }}...</p>
              <div class="news-meta">
                <span>
                  <el-icon><View /></el-icon>
                  {{ news.viewCount }}
                </span>
                <span>
                  <el-icon><ChatDotRound /></el-icon>
                  {{ news.commentCount }}
                </span>
                <span>
                  <el-icon><Timer /></el-icon>
                  {{ formatDate(news.createTime) }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="load-more">
        <el-button :loading="loading" @click="loadMore">加载更多</el-button>
      </div>
    </el-card>

    <!-- 热门商品 -->
    <el-card class="section">
      <template #header>
        <div class="section-header">
          <h3>热门商品</h3>
          <el-button text @click="router.push('/products')">查看更多</el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col v-for="product in hotProducts" :key="product.id" :xs="24" :sm="12" :md="6">
          <el-card class="product-card" @click="router.push(`/products/${product.id}`)">
            <img :src="product.coverImage" class="product-image" :alt="product.name">
            <div class="product-info">
              <h4>{{ product.name }}</h4>
              <p class="product-points">{{ product.points }} 积分</p>
              <div class="product-meta">
                <span class="stock" :class="{ 'low-stock': product.stock < 10 }">
                  库存: {{ product.stock }}
                </span>
                <span class="sales">
                  销量: {{ product.salesCount }}
                </span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { View, ChatDotRound, Timer } from '@element-plus/icons-vue'
import { getHotNews, getLatestNews } from '@/api/news'
import { getAllCategories } from '@/api/category'
import { getHotProducts } from '@/api/product'
import { formatDate } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const page = ref(1)
const selectedCategory = ref('')

const hotNews = ref([])
const latestNews = ref([])
const categories = ref([])
const hotProducts = ref([])

const loadHotNews = async () => {
  const res = await getHotNews({ page: 1, size: 5 })
  hotNews.value = res.records
}

const loadLatestNews = async () => {
  const res = await getLatestNews({ 
    page: page.value, 
    size: 9,
    categoryId: selectedCategory.value || undefined 
  })
  if (page.value === 1) {
    latestNews.value = res.records
  } else {
    latestNews.value.push(...res.records)
  }
}

const loadCategories = async () => {
  categories.value = await getAllCategories()
}

const loadHotProducts = async () => {
  const res = await getHotProducts({ page: 1, size: 8 })
  hotProducts.value = res.records
}

const handleCategoryChange = () => {
  page.value = 1
  loadLatestNews()
}

const loadMore = async () => {
  if (loading.value) return
  loading.value = true
  try {
    page.value++
    await loadLatestNews()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    loadHotNews(),
    loadLatestNews(),
    loadCategories(),
    loadHotProducts()
  ])
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h3 {
  margin: 0;
}

.carousel-item {
  height: 100%;
  position: relative;
  cursor: pointer;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: #fff;
}

.carousel-content h4 {
  margin: 0 0 10px;
  font-size: 24px;
}

.news-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.news-card:hover {
  transform: translateY(-5px);
}

.news-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.news-info {
  padding: 15px;
}

.news-info h4 {
  margin: 0 0 10px;
  font-size: 18px;
}

.news-summary {
  color: #666;
  margin-bottom: 10px;
}

.news-meta {
  display: flex;
  gap: 15px;
  color: #999;
  font-size: 14px;
}

.news-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
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
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-info h4 {
  margin: 0 0 10px;
  font-size: 16px;
}

.product-points {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin: 10px 0;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  color: #999;
  font-size: 14px;
}

.low-stock {
  color: #f56c6c;
}

.category-filter {
  margin: 10px 0;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .home {
    padding: 10px;
  }

  .carousel-content h4 {
    font-size: 20px;
  }

  .news-image,
  .product-image {
    height: 150px;
  }
}
</style> 