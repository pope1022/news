<template>
  <div class="order-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索订单号/用户名"
              style="width: 300px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select
              v-model="searchForm.status"
              placeholder="订单状态"
              clearable
              style="width: 150px"
            >
              <el-option label="待支付" value="pending" />
              <el-option label="已支付" value="paid" />
              <el-option label="已发货" value="shipped" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="orderList"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="productName" label="商品名称" show-overflow-tooltip />
        <el-table-column prop="points" label="积分" width="100" align="center" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewDetail(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.status === 'paid'"
              link
              type="success"
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              link
              type="danger"
              @click="handleCancel(row)"
            >
              取消
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

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ detail.productName }}</el-descriptions-item>
        <el-descriptions-item label="商品图片">
          <el-image
            :src="detail.productImage"
            :preview-src-list="[detail.productImage]"
            fit="cover"
            class="product-image"
          />
        </el-descriptions-item>
        <el-descriptions-item label="积分">{{ detail.points }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detail.quantity }}</el-descriptions-item>
        <el-descriptions-item label="总积分">{{ detail.totalPoints }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ detail.recipientName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.recipientPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ detail.recipientAddress }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detail.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ detail.shipTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detail.completeTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getOrderStatusType(detail.status)">
            {{ getOrderStatusText(detail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="detail.status === 'shipped'" label="物流单号">
          {{ detail.trackingNo }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 发货对话框 -->
    <el-dialog
      v-model="shipDialogVisible"
      title="订单发货"
      width="400px"
    >
      <el-form
        ref="shipFormRef"
        :model="shipForm"
        :rules="shipRules"
        label-width="100px"
      >
        <el-form-item label="物流单号" prop="trackingNo">
          <el-input v-model="shipForm.trackingNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmShip">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orderList = ref([])
const dialogVisible = ref(false)
const shipDialogVisible = ref(false)
const shipFormRef = ref<FormInstance>()

const searchForm = reactive({
  keyword: '',
  status: ''
})

const detail = reactive({
  orderNo: '',
  username: '',
  productName: '',
  productImage: '',
  points: 0,
  quantity: 0,
  totalPoints: 0,
  recipientName: '',
  recipientPhone: '',
  recipientAddress: '',
  createTime: '',
  payTime: '',
  shipTime: '',
  completeTime: '',
  status: '',
  trackingNo: ''
})

const shipForm = reactive({
  orderId: '',
  trackingNo: ''
})

const shipRules = {
  trackingNo: [
    { required: true, message: '请输入物流单号', trigger: 'blur' },
    { min: 5, max: 50, message: '长度在 5 到 50 个字符', trigger: 'blur' }
  ]
}

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  const map: Record<string, string> = {
    'pending': 'warning',
    'paid': 'primary',
    'shipped': 'success',
    'completed': 'success',
    'cancelled': 'info'
  }
  return map[status] || 'info'
}

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  const map: Record<string, string> = {
    'pending': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return map[status] || status
}

// 加载订单列表
const loadOrderList = async () => {
  loading.value = true
  try {
    const response = await fetch(
      `/api/admin/orders?page=${currentPage.value}&pageSize=${pageSize.value}&keyword=${searchForm.keyword}&status=${searchForm.status}`
    )
    const data = await response.json()
    if (data.code === 200) {
      orderList.value = data.data.list
      total.value = data.data.total
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadOrderList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

// 查看订单详情
const handleViewDetail = async (row: any) => {
  try {
    const response = await fetch(`/api/admin/orders/${row.id}`)
    const data = await response.json()
    if (data.code === 200) {
      Object.assign(detail, data.data)
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 发货
const handleShip = (row: any) => {
  shipForm.orderId = row.id
  shipForm.trackingNo = ''
  shipDialogVisible.value = true
}

// 确认发货
const handleConfirmShip = async () => {
  if (!shipFormRef.value) return
  await shipFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await fetch(`/api/admin/orders/${shipForm.orderId}/ship`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ trackingNo: shipForm.trackingNo })
        })
        const data = await response.json()
        if (data.code === 200) {
          ElMessage.success('发货成功')
          shipDialogVisible.value = false
          loadOrderList()
        }
      } catch (error) {
        console.error('订单发货失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 取消订单
const handleCancel = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      type: 'warning'
    })
    const response = await fetch(`/api/admin/orders/${row.id}/cancel`, {
      method: 'PUT'
    })
    const data = await response.json()
    if (data.code === 200) {
      ElMessage.success('取消成功')
      loadOrderList()
    }
  } catch (error) {
    console.error('取消订单失败:', error)
  }
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadOrderList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadOrderList()
}

// 初始加载
loadOrderList()
</script>

<style scoped>
.order-management {
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

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}

:deep(.el-descriptions__cell) {
  padding: 12px 20px;
}
</style> 