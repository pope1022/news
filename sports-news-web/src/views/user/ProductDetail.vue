<template>
  <div class="product-detail-container">
    <el-card v-loading="loading" class="product-info">
      <el-row :gutter="30">
        <!-- 商品图片 -->
        <el-col :span="12">
          <div class="product-image">
            <el-image
              :src="product?.coverImage"
              fit="cover"
              :preview-src-list="[product?.coverImage]"
            />
          </div>
        </el-col>

        <!-- 商品信息 -->
        <el-col :span="12">
          <div class="product-content">
            <h1 class="product-name">{{ product?.name }}</h1>
            
            <div class="product-meta">
              <div class="points">
                <span class="label">所需积分</span>
                <span class="value">{{ product?.points }}</span>
              </div>
              <div class="stock" :class="{ 'low-stock': product?.stock < 10 }">
                <span class="label">库存</span>
                <span class="value">{{ product?.stock }}</span>
              </div>
              <div class="sales">
                <span class="label">销量</span>
                <span class="value">{{ product?.salesCount }}</span>
              </div>
            </div>

            <div class="quantity-selector">
              <span class="label">数量</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product?.stock || 1"
                :disabled="product?.stock === 0"
              />
            </div>

            <div class="total-points">
              <span class="label">总积分</span>
              <span class="value">{{ totalPoints }}</span>
            </div>

            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                :disabled="!canPurchase"
                @click="handlePurchase"
              >
                立即购买
              </el-button>
              <el-button
                size="large"
                :disabled="!canPurchase"
                @click="handleAddToCart"
              >
                加入购物车
              </el-button>
            </div>

            <div class="user-points">
              <el-alert
                v-if="userStore.userInfo"
                :type="hasEnoughPoints ? 'info' : 'warning'"
                :closable="false"
                show-icon
              >
                {{ pointsMessage }}
              </el-alert>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 商品详情 -->
      <div class="product-description">
        <h2>商品详情</h2>
        <div v-html="product?.description"></div>
      </div>
    </el-card>

    <!-- 收货地址对话框 -->
    <el-dialog
      v-model="addressDialogVisible"
      title="选择收货地址"
      width="500px"
    >
      <template v-if="addressList.length === 0">
        <el-empty description="暂无收货地址">
          <el-button type="primary" @click="showAddAddressDialog">
            添加收货地址
          </el-button>
        </el-empty>
      </template>
      <template v-else>
        <div class="address-list">
          <div
            v-for="address in addressList"
            :key="address.id"
            class="address-item"
            :class="{ active: selectedAddressId === address.id }"
            @click="selectedAddressId = address.id"
          >
            <div class="address-content">
              <p class="receiver">
                {{ address.receiver }}
                <span class="phone">{{ address.phone }}</span>
              </p>
              <p class="address">{{ address.address }}</p>
            </div>
            <div class="address-actions">
              <el-button
                type="primary"
                link
                @click.stop="handleEditAddress(address)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                link
                @click.stop="handleDeleteAddress(address)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </template>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addressDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="showAddAddressDialog">
            添加新地址
          </el-button>
          <el-button
            type="primary"
            :disabled="!selectedAddressId"
            @click="handleConfirmPurchase"
          >
            确认购买
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
      v-model="addAddressDialogVisible"
      :title="editingAddress ? '编辑地址' : '添加地址'"
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
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input
            v-model="addressForm.address"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addAddressDialogVisible = false">取消</el-button>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getProductDetail } from '@/api/product'
import { getAddressList, addAddress, updateAddress, deleteAddress } from '@/api/address'
import { createOrder } from '@/api/order'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 商品相关
const loading = ref(false)
const product = ref<any>(null)
const quantity = ref(1)

// 地址相关
const addressDialogVisible = ref(false)
const addAddressDialogVisible = ref(false)
const addressList = ref([])
const selectedAddressId = ref('')
const editingAddress = ref<any>(null)
const addressFormRef = ref<FormInstance>()

const addressForm = ref({
  receiver: '',
  phone: '',
  address: '',
  isDefault: false
})

const addressRules = {
  receiver: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入收货地址', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const totalPoints = computed(() => {
  if (!product.value) return 0
  return product.value.points * quantity.value
})

const hasEnoughPoints = computed(() => {
  if (!userStore.userInfo) return false
  return userStore.userInfo.points >= totalPoints.value
})

const canPurchase = computed(() => {
  if (!product.value) return false
  return product.value.stock > 0 && hasEnoughPoints.value
})

const pointsMessage = computed(() => {
  if (!userStore.userInfo) return ''
  return `当前积分：${userStore.userInfo.points}${
    hasEnoughPoints.value ? '' : '，积分不足'
  }`
})

// 方法
const loadProduct = async () => {
  loading.value = true
  try {
    product.value = await getProductDetail(route.params.id as string)
  } catch (error: any) {
    ElMessage.error(error.message || '获取商品详情失败')
    router.push('/products')
  } finally {
    loading.value = false
  }
}

const loadAddresses = async () => {
  try {
    const res = await getAddressList()
    addressList.value = res
    if (res.length > 0) {
      const defaultAddress = res.find((a: any) => a.isDefault)
      selectedAddressId.value = defaultAddress?.id || res[0].id
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取地址列表失败')
  }
}

const handlePurchase = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  addressDialogVisible.value = true
  loadAddresses()
}

const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart({
      productId: product.value.id,
      quantity: quantity.value
    })
    ElMessage.success('已加入购物车')
  } catch (error: any) {
    ElMessage.error(error.message || '加入购物车失败')
  }
}

const showAddAddressDialog = () => {
  editingAddress.value = null
  addressForm.value = {
    receiver: '',
    phone: '',
    address: '',
    isDefault: false
  }
  addAddressDialogVisible.value = true
}

const handleEditAddress = (address: any) => {
  editingAddress.value = address
  addressForm.value = {
    receiver: address.receiver,
    phone: address.phone,
    address: address.address,
    isDefault: address.isDefault
  }
  addAddressDialogVisible.value = true
}

const handleDeleteAddress = async (address: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这个地址吗？', '提示', {
      type: 'warning'
    })
    await deleteAddress(address.id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleSaveAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (editingAddress.value) {
          await updateAddress({
            id: editingAddress.value.id,
            ...addressForm.value
          })
        } else {
          await addAddress(addressForm.value)
        }
        ElMessage.success('保存成功')
        addAddressDialogVisible.value = false
        loadAddresses()
      } catch (error: any) {
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

const handleConfirmPurchase = async () => {
  try {
    await createOrder({
      productId: product.value.id,
      quantity: quantity.value,
      addressId: selectedAddressId.value
    })
    ElMessage.success('购买成功')
    addressDialogVisible.value = false
    router.push('/orders')
  } catch (error: any) {
    ElMessage.error(error.message || '购买失败')
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
}

.product-image :deep(.el-image) {
  width: 100%;
  height: 100%;
}

.product-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-name {
  margin: 0;
  font-size: 24px;
  line-height: 1.4;
}

.product-meta {
  display: flex;
  gap: 30px;
}

.product-meta > div {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.label {
  color: #666;
  font-size: 14px;
}

.points .value {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}

.stock .value,
.sales .value {
  font-size: 20px;
  color: #333;
}

.low-stock .value {
  color: #f56c6c;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-points {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-points .value {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  gap: 20px;
}

.user-points {
  margin-top: auto;
}

.product-description {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.product-description h2 {
  margin: 0 0 20px;
  font-size: 20px;
}

.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.address-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
}

.address-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.address-content {
  margin-bottom: 10px;
}

.receiver {
  margin: 0 0 5px;
  font-weight: bold;
}

.phone {
  margin-left: 10px;
  color: #666;
}

.address {
  margin: 0;
  color: #666;
}

.address-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .product-detail-container {
    padding: 10px;
  }

  .product-image {
    height: 300px;
    margin-bottom: 20px;
  }

  .product-meta {
    flex-wrap: wrap;
    gap: 20px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style> 