<template>
  <div class="news-container">
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索新闻标题"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.sort" placeholder="排序方式">
            <el-option label="最新发布" value="createTime" />
            <el-option label="最多浏览" value="viewCount" />
            <el-option label="最多评论" value="commentCount" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 新闻列表 -->
    <el-card v-loading="loading" class="news-list">
      <template v-if="newsList.length === 0">
        <el-empty description="暂无新闻" />
      </template>
      <template v-else>
        <div
          v-for="news in newsList"
          :key="news.id"
          class="news-item"
          @click="router.push(`/news/${news.id}`)"
        >
          <div class="news-image">
            <el-image
              :src="news.coverImage"
              fit="cover"
              :preview-src-list="[news.coverImage]"
            />
          </div>
          <div class="news-content">
            <h3 class="news-title">{{ news.title }}</h3>
            <p class="news-summary">{{ news.content.substring(0, 200) }}...</p>
            <div class="news-meta">
              <div class="meta-left">
                <span class="category-tag">
                  {{ getCategoryName(news.categoryId) }}
                </span>
                <span class="publish-time">
                  <el-icon><Timer /></el-icon>
                  {{ formatDate(news.createTime) }}
                </span>
              </div>
              <div class="meta-right">
                <span>
                  <el-icon><View /></el-icon>
                  {{ news.viewCount }} 浏览
                </span>
                <span>
                  <el-icon><ChatDotRound /></el-icon>
                  {{ news.commentCount }} 评论
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, ChatDotRound, Timer } from '@element-plus/icons-vue'
import { getNewsList } from '@/api/news'
import { getAllCategories } from '@/api/category'
import { formatDate } from '@/utils/format'

const router = useRouter()
const loading = ref(false)
const categories = ref([])
const newsList = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const searchForm = reactive({
  keyword: '',
  categoryId: '',
  sort: 'createTime'
})

const loadCategories = async () => {
  categories.value = await getAllCategories()
}

const loadNews = async () => {
  loading.value = true
  try {
    const res = await getNewsList({
      page: page.value,
      size: size.value,
      ...searchForm
    })
    newsList.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

const getCategoryName = (categoryId: string) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

const handleSearch = () => {
  page.value = 1
  loadNews()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = ''
  searchForm.sort = 'createTime'
  handleSearch()
}

const handleSizeChange = (val: number) => {
  size.value = val
  page.value = 1
  loadNews()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  loadNews()
}

onMounted(async () => {
  await Promise.all([
    loadCategories(),
    loadNews()
  ])
})
</script>

<style scoped>
.news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.news-list {
  min-height: 400px;
}

.news-item {
  display: flex;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: transform 0.3s;
}

.news-item:hover {
  transform: translateX(10px);
}

.news-image {
  width: 240px;
  height: 160px;
  margin-right: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.news-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-title {
  margin: 0 0 15px;
  font-size: 20px;
  line-height: 1.4;
}

.news-summary {
  flex: 1;
  margin: 0 0 15px;
  color: #666;
  line-height: 1.6;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.meta-left,
.meta-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.category-tag {
  padding: 2px 8px;
  background-color: #ecf5ff;
  color: #409eff;
  border-radius: 4px;
}

.publish-time {
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-right span {
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .news-container {
    padding: 10px;
  }

  .news-item {
    flex-direction: column;
  }

  .news-image {
    width: 100%;
    height: 200px;
    margin: 0 0 15px;
  }

  .news-title {
    font-size: 18px;
  }

  .news-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 