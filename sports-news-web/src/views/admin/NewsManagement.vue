<template>
  <div class="news-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索新闻标题"
              style="width: 300px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select
              v-model="searchForm.categoryId"
              placeholder="选择分类"
              clearable
              style="width: 200px"
            >
              <el-option
                v-for="item in categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增新闻
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="newsList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="{ row }">
            <span>{{ getCategoryName(row?.categoryId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" align="center" />
        <el-table-column prop="commentCount" label="评论数" width="100" align="center" />
        <el-table-column prop="updateTime" label="发布时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'error' : row.status === 1 ? 'success' : 'info'">
              {{ row.status === 2 ? '已下架' : row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 0 || row.status === 2"
              link
              type="success"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-else
              link
              type="warning"
              @click="handleUnpublish(row)"
            >
              下架
            </el-button>
            <el-button
              link
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑新闻对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'edit' ? '编辑新闻' : '新增新闻'"
      width="800px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-upload
            class="cover-uploader"
            action="/api/admin/upload"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
          <img v-if="form.coverImage" :src="form.coverImage" class="cover" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div class="editor-container">
            <!-- 这里可以集成富文本编辑器，如 TinyMCE、CKEditor 等 -->
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="10"
              placeholder="请输入新闻内容"
            />
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadProps } from 'element-plus'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const newsList = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref<FormInstance>()
const userStore = useUserStore()

const searchForm = reactive({
  keyword: '',
  categoryId: ''
})

const form = reactive({
  id: '',
  title: '',
  categoryId: '',
  coverImage: '',
  content: '',
  status: 0
})

const rules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 3, max: 100, message: '长度在 3 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' }
  ]
}


// 加载新闻列表
const loadNewsList = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `/api/admin/news?page=${currentPage.value}&pageSize=${pageSize.value}&keyword=${searchForm.keyword}&categoryId=${searchForm.categoryId}`
    )
    const data = await response.json()
    if (data.code === 200) {
      newsList.value = data.data.records
      total.value = data.data.total
    }
  } catch (error) {
    console.error('获取新闻列表失败:', error)
    ElMessage.error('获取新闻列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadNewsList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.categoryId = ''
  handleSearch()
}

// 新增新闻
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(form, {
    id: '',
    title: '',
    categoryId: '',
    coverImage: '',
    content: '',
    status: 0,
    authorId: userStore.userInfo?.id || 0
  })
}

// 编辑新闻
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 删除新闻
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该新闻吗？', '提示', {
      type: 'warning'
    })
    const response = await fetch(`/api/admin/news/${row.id}`, {
      method: 'DELETE'
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('删除成功')
      loadNewsList()
    }
  } catch (error) {
    console.error('删除新闻失败:', error)
  }
}

// 发布新闻
const handlePublish = async (row: any) => {
  try {
    const response = await fetch(`/api/admin/news/${row.id}/publish`, {
      method: 'PUT'
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('发布成功')
      loadNewsList()
    }
  } catch (error) {
    console.error('发布新闻失败:', error)
  }
}

// 下架新闻
const handleUnpublish = async (row: any) => {
  try {
    const response = await fetch(`/api/admin/news/${row.id}/unpublish`, {
      method: 'PUT'
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('下架成功')
      loadNewsList()
    }
  } catch (error) {
    console.error('下架新闻失败:', error)
  }
}

// 上传封面图片
const handleCoverSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile
) => {
  form.coverImage = response.data.url
}

const beforeCoverUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = dialogType.value === 'edit' 
          ? `/api/admin/news/${form.id}`
          : '/api/admin/news'
        const method = dialogType.value === 'edit' ? 'PUT' : 'POST'
        
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(form)
        })
        const data = await response.json()
        if (data.code === 200) {
          ElMessage.success(dialogType.value === 'edit' ? '编辑成功' : '新增成功')
          dialogVisible.value = false
          loadNewsList()
        }
      } catch (error) {
        console.error('保存新闻失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadNewsList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadNewsList()
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await fetch('/api/admin/categories')
    const data = await response.json()
    if (data.code === 200) {
      categories.value = data.data.records
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 根据 categoryId 获取对应的分类名称
const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId);
  return category ? category.name : '未知分类';
};

onMounted(() => {
  loadCategories()
  loadNewsList()
})
</script>

<style scoped>
.news-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.cover-uploader:hover {
  border-color: #409EFF;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.cover {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}
</style> 