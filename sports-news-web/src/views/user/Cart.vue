<template>
  <div class="cart">
    <el-card class="cart-card">
      <template #header>
        <div class="cart-header">
          <h2>我的购物车</h2>
          <el-button
            type="danger"
            plain
            :disabled="selectedItems.length === 0"
            @click="handleClearSelected"
          >
            清空选中
          </el-button>
        </div>
      </template>

      <el-empty
        v-if="cartItems.length === 0"
        description="购物车还是空的"
      >
        <el-button type="primary" @click="router.push('/products')">
          去逛逛
        </el-button>
      </el-empty>

      <template v-else>
        <div class="cart-table">
          <el-table
            :data="cartItems"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column label="商品" min-width="400">
              <template #default="{ row }">
                <div class="product-info">
                  <el-image
                    :src="row.product.coverImage"
                    class="product-image"
                    fit="cover"
                  />
                  <div class="product-detail">
                    <div class="product-name">{{ row.product.name }}</div>
                    <div class="product-points">{{ row.product.points }} 积分</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                <span class="points">{{ row.product.points }}</span> 积分
              </template>
            </el-table-column>
            <el-table-column label="数量" width="150">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.quantity"
                  :min="1"
                  :max="row.product.stock"
                  :step="1"
                  @change="handleQuantityChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="points">{{ row.product.points * row.quantity }}</span> 积分
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button
                  type="danger"
                  link
                  @click="handleRemoveItem(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox
              v-model="selectAll"
              @change="handleSelectAllChange"
            >
              全选
            </el-checkbox>
            <span class="selected-count" v-if="selectedItems.length > 0">
              已选择 {{ selectedItems.length }} 件商品
            </span>
          </div>
          <div class="footer-right">
            <div class="total-info">
              <span class="label">合计：</span>
              <span class="points">{{ totalPoints }}</span>
              <span class="unit">积分</span>
            </div>
            <el-button
              type="primary"
              size="large"
              :disabled="selectedItems.length === 0"
              @click="handleCheckout"
            >
              结算
            </el-button>
          </div>
        </div>
      </template>
    </el-card>

    <el-dialog
      v-model="showAddressDialog"
      title="选择收货地址"
      width="500px"
    >
      <div class="address-list">
        <el-empty
          v-if="addresses.length === 0"
          description="暂无收货地址"
        >
          <el-button type="primary" @click="handleAddAddress">添加地址</el-button>
        </el-empty>
        <template v-else>
          <el-radio-group v-model="selectedAddress">
            <div
              v-for="address in addresses"
              :key="address.id"
              class="address-item"
            >
              <el-radio :label="address.id">
                <div class="address-content">
                  <div class="address-info">
                    <span class="receiver">{{ address.receiver }}</span>
                    <span class="phone">{{ address.phone }}</span>
                  </div>
                  <div class="address-detail">
                    {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
                  </div>
                </div>
              </el-radio>
            </div>
          </el-radio-group>
          <div class="dialog-footer">
            <el-button @click="showAddressDialog = false">取消</el-button>
            <el-button type="primary" @click="handleConfirmCheckout">
              确认结算
            </el-button>
          </div>
        </template>
      </div>
    </el-dialog>

    <el-dialog
      v-model="showAddressFormDialog"
      :title="editingAddress ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="addressForm.receiver" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="所在地区" required>
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择省/市/区"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            rows="2"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddressFormDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSaveAddress">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { createOrder } from '@/api/order'

const router = useRouter()
const userStore = useUserStore()

const cartItems = ref<any[]>([])
const selectedItems = ref<any[]>([])
const addresses = ref<any[]>([])
const selectedAddress = ref('')
const showAddressDialog = ref(false)
const showAddressFormDialog = ref(false)
const editingAddress = ref(false)
const addressFormRef = ref<FormInstance>()

const addressForm = ref({
  receiver: '',
  phone: '',
  region: [],
  detail: '',
  isDefault: false
})

const addressRules = {
  receiver: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 示例数据，实际项目中应该从后端获取
const regionOptions = [
  {
    value: '浙江',
    label: '浙江省',
    children: [
      {
        value: '杭州',
        label: '杭州市',
        children: [
          { value: '西湖', label: '西湖区' },
          { value: '余杭', label: '余杭区' }
        ]
      }
    ]
  }
]

const selectAll = computed({
  get: () => cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length,
  set: (value) => {
    // 在 handleSelectAllChange 中处理
  }
})

const totalPoints = computed(() => {
  return selectedItems.value.reduce((total, item) => {
    return total + item.product.points * item.quantity
  }, 0)
})

const loadCartItems = () => {
  // TODO: 从后端加载购物车数据
  cartItems.value = []
}

const handleSelectionChange = (items: any[]) => {
  selectedItems.value = items
}

const handleSelectAllChange = (value: boolean) => {
  const tableRef = document.querySelector('.el-table')
  if (tableRef) {
    const table = (tableRef as any).__vueParentComponent.proxy
    if (value) {
      cartItems.value.forEach(row => table.toggleRowSelection(row, true))
    } else {
      table.clearSelection()
    }
  }
}

const handleQuantityChange = async (item: any) => {
  try {
    // TODO: 更新购物车商品数量
    ElMessage.success('数量已更新')
  } catch (error: any) {
    ElMessage.error(error.message || '更新数量失败')
  }
}

const handleRemoveItem = async (item: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      type: 'warning'
    })
    // TODO: 从购物车中删除商品
    const index = cartItems.value.indexOf(item)
    if (index > -1) {
      cartItems.value.splice(index, 1)
    }
    ElMessage.success('商品已删除')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleClearSelected = async () => {
  try {
    await ElMessageBox.confirm('确定要清空选中的商品吗？', '提示', {
      type: 'warning'
    })
    // TODO: 批量删除选中的商品
    selectedItems.value.forEach(item => {
      const index = cartItems.value.indexOf(item)
      if (index > -1) {
        cartItems.value.splice(index, 1)
      }
    })
    selectedItems.value = []
    ElMessage.success('选中商品已清空')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '清空失败')
    }
  }
}

const handleCheckout = () => {
  if (userStore.userInfo.points < totalPoints.value) {
    ElMessage.warning('积分不足，无法结算')
    return
  }

  showAddressDialog.value = true
}

const handleAddAddress = () => {
  editingAddress.value = false
  addressForm.value = {
    receiver: '',
    phone: '',
    region: [],
    detail: '',
    isDefault: false
  }
  showAddressFormDialog.value = true
}

const handleSaveAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      // TODO: 实现保存地址功能
      showAddressFormDialog.value = false
      // 重新加载地址列表
    }
  })
}

const handleConfirmCheckout = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  try {
    // TODO: 创建订单
    await createOrder({
      items: selectedItems.value.map(item => ({
        productId: item.product.id,
        quantity: item.quantity
      })),
      addressId: selectedAddress.value,
      payType: 1 // 使用积分支付
    })
    ElMessage.success('下单成功')
    router.push('/orders')
  } catch (error: any) {
    ElMessage.error(error.message || '下单失败')
  }
}

onMounted(() => {
  loadCartItems()
  // TODO: 加载用户地址列表
})
</script>

<style scoped>
.cart {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-header h2 {
  margin: 0;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
}

.product-detail {
  flex: 1;
}

.product-name {
  font-size: 16px;
  margin-bottom: 8px;
}

.product-points {
  color: #f56c6c;
}

.points {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.selected-count {
  color: #666;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-info {
  font-size: 16px;
}

.total-info .points {
  font-size: 24px;
  margin: 0 4px;
}

.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.address-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.address-item:last-child {
  border-bottom: none;
}

.address-content {
  margin-left: 10px;
}

.address-info {
  margin-bottom: 5px;
}

.address-info .receiver {
  font-weight: bold;
  margin-right: 10px;
}

.address-info .phone {
  color: #666;
}

.address-detail {
  color: #666;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style> 