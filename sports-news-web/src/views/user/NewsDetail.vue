<template>
  <div class="news-detail-container">
    <el-card v-loading="loading" class="news-content">
      <!-- 新闻标题 -->
      <h1 class="news-title">{{ news?.title }}</h1>
      
      <!-- 新闻元信息 -->
      <div class="news-meta">
        <div class="meta-left">
          <span class="category-tag">
            {{ getCategoryName(news?.categoryId) }}
          </span>
          <span class="publish-time">
            <el-icon><Timer /></el-icon>
            {{ formatDate(news?.createTime) }}
          </span>
        </div>
        <div class="meta-right">
          <span>
            <el-icon><View /></el-icon>
            {{ news?.viewCount }} 浏览
          </span>
          <span>
            <el-icon><ChatDotRound /></el-icon>
            {{ news?.commentCount }} 评论
          </span>
        </div>
      </div>

      <!-- 新闻封面图 -->
      <div v-if="news?.coverImage" class="news-cover">
        <el-image
          :src="news.coverImage"
          fit="cover"
          :preview-src-list="[news.coverImage]"
        />
      </div>

      <!-- 新闻内容 -->
      <div class="news-body" v-html="news?.content"></div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="comments-section">
      <template #header>
        <div class="comments-header">
          <h3>评论区 ({{ total }})</h3>
        </div>
      </template>

      <!-- 评论输入框 -->
      <div class="comment-input">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          :maxlength="500"
          show-word-limit
        />
        <div class="comment-actions">
          <el-button type="primary" @click="handleSubmitComment">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div v-loading="commentsLoading" class="comments-list">
        <template v-if="commentsList.length === 0">
          <el-empty description="暂无评论" />
        </template>
        <template v-else>
          <div v-for="comment in commentsList" :key="comment.id" class="comment-item">
            <div class="comment-user">
              <el-avatar :size="40" :src="comment.user.avatar">
                {{ comment.user.nickname?.charAt(0) }}
              </el-avatar>
              <div class="user-info">
                <span class="username">{{ comment.user.nickname }}</span>
                <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button
                v-if="comment.userId === userStore.userInfo?.id"
                type="danger"
                link
                @click="handleDeleteComment(comment)"
              >
                删除
              </el-button>
            </div>
          </div>
        </template>

        <!-- 评论分页 -->
        <div v-if="commentsList.length > 0" class="pagination">
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
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, ChatDotRound, Timer } from '@element-plus/icons-vue'
import { getNewsDetail } from '@/api/news'
import { getComments, addComment, deleteComment } from '@/api/comment'
import { getAllCategories } from '@/api/category'
import { useUserStore } from '@/stores/user'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const commentsLoading = ref(false)
const news = ref<any>(null)
const categories = ref([])
const commentsList = ref([])
const commentContent = ref('')
const total = ref(0)
const page = ref(1)
const size = ref(10)

const loadNewsDetail = async () => {
  loading.value = true
  try {
    news.value = await getNewsDetail(route.params.id as string)
  } catch (error: any) {
    ElMessage.error(error.message || '获取新闻详情失败')
    router.push('/news')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  categories.value = await getAllCategories()
}

const loadComments = async () => {
  commentsLoading.value = true
  try {
    const res = await getComments({
      newsId: route.params.id as string,
      page: page.value,
      size: size.value
    })
    commentsList.value = res.records
    total.value = res.total
  } finally {
    commentsLoading.value = false
  }
}

const getCategoryName = (categoryId: string) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

const handleSubmitComment = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await addComment({
      newsId: route.params.id as string,
      content: commentContent.value.trim()
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    loadComments()
  } catch (error: any) {
    ElMessage.error(error.message || '评论失败')
  }
}

const handleDeleteComment = async (comment: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      type: 'warning'
    })
    await deleteComment(comment.id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleSizeChange = (val: number) => {
  size.value = val
  page.value = 1
  loadComments()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  loadComments()
}

onMounted(async () => {
  await Promise.all([
    loadNewsDetail(),
    loadCategories(),
    loadComments()
  ])
})
</script>

<style scoped>
.news-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.news-content {
  margin-bottom: 20px;
}

.news-title {
  margin: 0 0 20px;
  font-size: 28px;
  line-height: 1.4;
  text-align: center;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
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

.publish-time,
.meta-right span {
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.news-cover {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.news-cover :deep(.el-image) {
  width: 100%;
  max-height: 400px;
}

.news-body {
  line-height: 1.8;
  font-size: 16px;
}

.comments-section {
  min-height: 400px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comments-header h3 {
  margin: 0;
}

.comment-input {
  margin-bottom: 30px;
}

.comment-actions {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  margin: 10px 0;
  line-height: 1.6;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .news-detail-container {
    padding: 10px;
  }

  .news-title {
    font-size: 24px;
  }

  .news-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style> 