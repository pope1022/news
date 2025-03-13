<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索分类名称"
              style="width: 300px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="categoryList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="newsCount" label="新闻数量" width="100" align="center" />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
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

    <!-- 新增/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'edit' ? '编辑分类' : '新增分类'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
          />
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
import { ref, reactive } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const categoryList = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref<FormInstance>()

const searchForm = reactive({
  keyword: ''
})

const form = reactive({
  id: '',
  name: '',
  description: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过 200 个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

// 加载分类列表
const loadCategoryList = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `/api/admin/categories?page=${currentPage.value}&pageSize=${pageSize.value}&keyword=${searchForm.keyword}`
    )
    const data = await response.json()
    if (data.code === 200) {
      categoryList.value = data.data.records
      total.value = data.data.total
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadCategoryList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  handleSearch()
}

// 新增分类
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  Object.assign(form, {
    id: '',
    name: '',
    description: '',
    sort: 0,
    status: 1
  })
}

// 编辑分类
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 删除分类
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？删除后该分类下的新闻将无法显示。', '提示', {
      type: 'warning'
    })
    const response = await fetch(`/api/admin/categories/${row.id}`, {
      method: 'DELETE'
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('删除成功')
      loadCategoryList()
    }
  } catch (error) {
    console.error('删除分类失败:', error)
  }
}

// 修改分类状态
const handleStatusChange = async (row: any) => {
  try {
    const response = await fetch(`/api/admin/categories/${row.id}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ status: row.status })
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('状态修改成功')
    }
  } catch (error) {
    console.error('修改分类状态失败:', error)
    row.status = row.status === 1 ? 0 : 1
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const url = dialogType.value === 'edit' 
          ? `/api/admin/categories/${form.id}`
          : '/api/admin/categories'
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
          loadCategoryList()
        }
      } catch (error) {
        console.error('保存分类失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadCategoryList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadCategoryList()
}

// 初始加载
loadCategoryList()
</script>

<style scoped>
.category-management {
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

:deep(.el-dialog__body) {
  padding-top: 20px;
}
</style> 