<template>
  <div class="orders-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="status" @change="handleStatusChange">
            <el-radio-button label="">全部订单</el-radio-button>
            <el-radio-button label="0">待支付</el-radio-button>
            <el-radio-button label="1">已支付</el-radio-button>
            <el-radio-button label="2">已发货</el-radio-button>
            <el-radio-button label="3">已完成</el-radio-button>
            <el-radio-button label="4">已取消</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="orderList"
        style="width: 100%"
      >
        <el-table-column label="订单信息" min-width="400">
          <template #default="{ row }">
            <div class="order-info">
              <div class="product-info">
                <el-image
                  :src="row.product.coverImage"
                  :preview-src-list="[row.product.coverImage]"
                  class="product-image"
                />
                <div class="product-detail">
                  <p class="product-name">{{ row.product.name }}</p>
                  <p class="product-price">{{ row.product.points }} 积分</p>
                </div>
              </div>
              <p class="quantity">x {{ row.quantity }}</p>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货信息" min-width="200">
          <template #default="{ row }">
            <p>{{ row.receiver }}</p>
            <p>{{ row.phone }}</p>
            <p>{{ row.address }}</p>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="120" align="center">
          <template #default="{ row }">
            <span class="total-points">{{ row.quantity * row.product.points }} 积分</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <div class="order-actions">
              <el-button
                v-if="row.status === 0"
                type="primary"
                size="small"
                @click="handlePay(row)"
              >
                立即支付
              </el-button>
              <el-button
                v-if="row.status === 2"
                type="success"
                size="small"
                @click="handleConfirmReceive(row)"
              >
                确认收货
              </el-button>
              <el-button
                v-if="row.status === 0"
                type="danger"
                size="small"
                @click="handleCancel(row)"
              >
                取消订单
              </el-button>
              <el-button
                type="primary"
                link
                @click="handleViewDetail(row)"
              >
                查看详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <template v-if="currentOrder">
        <div class="order-detail">
          <div class="detail-section">
            <h3>订单信息</h3>
            <p>订单编号：{{ currentOrder.orderNo }}</p>
            <p>创建时间：{{ formatDate(currentOrder.createTime) }}</p>
            <p>支付时间：{{ currentOrder.payTime ? formatDate(currentOrder.payTime) : '-' }}</p>
            <p>发货时间：{{ currentOrder.deliveryTime ? formatDate(currentOrder.deliveryTime) : '-' }}</p>
          </div>
          <div class="detail-section">
            <h3>商品信息</h3>
            <div class="product-info">
              <el-image
                :src="currentOrder.product.coverImage"
                :preview-src-list="[currentOrder.product.coverImage]"
                class="product-image"
              />
              <div class="product-detail">
                <p class="product-name">{{ currentOrder.product.name }}</p>
                <p class="product-price">{{ currentOrder.product.points }} 积分</p>
                <p class="quantity">数量：{{ currentOrder.quantity }}</p>
                <p class="total">总积分：{{ currentOrder.quantity * currentOrder.product.points }}</p>
              </div>
            </div>
          </div>
          <div class="detail-section">
            <h3>收货信息</h3>
            <p>收货人：{{ currentOrder.receiver }}</p>
            <p>联系电话：{{ currentOrder.phone }}</p>
            <p>收货地址：{{ currentOrder.address }}</p>
          </div>
          <div class="detail-section" v-if="currentOrder.status >= 2">
            <h3>物流信息</h3>
            <p>快递公司：{{ currentOrder.expressCompany }}</p>
            <p>快递单号：{{ currentOrder.expressNo }}</p>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, payOrder, cancelOrder, completeOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 列表相关
const loading = ref(false)
const orderList = ref<any[]>([])
const status = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 详情相关
const detailDialogVisible = ref(false)
const currentOrder = ref<any>(null)

// 获取订单列表
const loadOrders = async () => {
  try {
    loading.value = true
    const res = await getUserOrders({
      userId: userStore.userInfo.id,
      page: page.value,
      size: size.value
    })
    orderList.value = res.records
    total.value = res.total
  } catch (error: any) {
    ElMessage.error(error.message || '获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 订单状态相关
const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

// 订单操作
const handlePay = async (order: any) => {
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？', '提示', {
      type: 'warning'
    })
    await payOrder(order.orderNo)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '支付失败')
    }
  }
}

const handleConfirmReceive = async (order: any) => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '提示', {
      type: 'warning'
    })
    await completeOrder(order.orderNo)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '确认收货失败')
    }
  }
}

const handleCancel = async (order: any) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      type: 'warning'
    })
    await cancelOrder(order.orderNo)
    ElMessage.success('订单取消成功')
    loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

const handleViewDetail = (order: any) => {
  currentOrder.value = order
  detailDialogVisible.value = true
}

// 分页相关
const handleSizeChange = (val: number) => {
  size.value = val
  page.value = 1
  loadOrders()
}

const handleCurrentChange = (val: number) => {
  page.value = val
  loadOrders()
}

const handleStatusChange = () => {
  page.value = 1
  loadOrders()
}

// 工具方法
const formatDate = (date: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-detail {
  flex: 1;
}

.product-name {
  margin: 0 0 5px;
  font-weight: bold;
}

.product-price {
  margin: 0;
  color: #f56c6c;
}

.quantity {
  margin: 0;
  color: #666;
}

.total-points {
  color: #f56c6c;
  font-weight: bold;
}

.order-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 订单详情样式 */
.order-detail {
  padding: 20px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  margin: 0 0 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.detail-section p {
  margin: 10px 0;
  color: #666;
}

.detail-section .total {
  color: #f56c6c;
  font-weight: bold;
}
</style> 