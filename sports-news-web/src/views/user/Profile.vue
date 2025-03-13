<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="handleEditProfile">编辑</el-button>
            </div>
          </template>
          <div class="profile-info">
            <el-avatar :size="100" :src="userStore.userInfo?.avatar" />
            <div class="info-item">
              <label>用户名：</label>
              <span>{{ userStore.userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <label>昵称：</label>
              <span>{{ userStore.userInfo?.nickname }}</span>
            </div>
            <div class="info-item">
              <label>邮箱：</label>
              <span>{{ userStore.userInfo?.email }}</span>
            </div>
            <div class="info-item">
              <label>手机号：</label>
              <span>{{ userStore.userInfo?.phone }}</span>
            </div>
            <div class="info-item">
              <label>积分：</label>
              <span class="points">{{ userStore.userInfo?.points }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="address-card">
          <template #header>
            <div class="card-header">
              <span>收货地址</span>
              <el-button type="primary" @click="handleAddAddress">新增地址</el-button>
            </div>
          </template>
          <div class="address-list">
            <el-empty v-if="addressList.length === 0" description="暂无收货地址" />
            <template v-else>
              <div v-for="address in addressList" :key="address.id" class="address-item">
                <div class="address-content">
                  <div class="address-info">
                    <p class="receiver">
                      <span>{{ address.receiver }}</span>
                      <span>{{ address.phone }}</span>
                    </p>
                    <p class="address">
                      {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
                    </p>
                  </div>
                  <div class="address-actions">
                    <el-tag v-if="address.isDefault" type="success" size="small">默认地址</el-tag>
                    <el-button v-else type="primary" link @click="handleSetDefault(address.id)">
                      设为默认
                    </el-button>
                    <el-button type="primary" link @click="handleEditAddress(address)">编辑</el-button>
                    <el-button type="danger" link @click="handleDeleteAddress(address.id)">删除</el-button>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 个人信息编辑对话框 -->
    <el-dialog
      v-model="profileDialogVisible"
      title="编辑个人信息"
      width="500px"
    >
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="80px"
      >
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProfile" :loading="profileSubmitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 地址编辑对话框 -->
    <el-dialog
      v-model="addressDialogVisible"
      :title="addressForm.id ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="addressForm.region"
            :options="regionOptions"
            placeholder="请选择所在地区"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="addressForm.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addressDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAddress" :loading="addressSubmitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="500px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword" :loading="passwordSubmitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateProfile, changePassword } from '@/api/user'

const userStore = useUserStore()

// 个人信息相关
const profileDialogVisible = ref(false)
const profileFormRef = ref<FormInstance>()
const profileSubmitting = ref(false)

const profileForm = reactive({
  nickname: '',
  email: '',
  phone: ''
})

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 地址相关
const addressDialogVisible = ref(false)
const addressFormRef = ref<FormInstance>()
const addressSubmitting = ref(false)
const addressList = ref<any[]>([])

const addressForm = reactive({
  id: '',
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
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

// 密码相关
const passwordDialogVisible = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordSubmitting = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else {
    if (passwordForm.confirmPassword !== '') {
      if (passwordFormRef.value) {
        passwordFormRef.value.validateField('confirmPassword')
      }
    }
    callback()
  }
}

const validatePass2 = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

// 方法
const handleEditProfile = () => {
  profileForm.nickname = userStore.userInfo.nickname
  profileForm.email = userStore.userInfo.email
  profileForm.phone = userStore.userInfo.phone
  profileDialogVisible.value = true
}

const submitProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        profileSubmitting.value = true
        await updateProfile(profileForm)
        await userStore.getUserInfo()
        ElMessage.success('个人信息更新成功')
        profileDialogVisible.value = false
      } catch (error: any) {
        ElMessage.error(error.message || '更新失败')
      } finally {
        profileSubmitting.value = false
      }
    }
  })
}

const handleAddAddress = () => {
  addressForm.id = ''
  addressForm.receiver = ''
  addressForm.phone = ''
  addressForm.region = []
  addressForm.detail = ''
  addressForm.isDefault = false
  addressDialogVisible.value = true
}

const handleEditAddress = (address: any) => {
  addressForm.id = address.id
  addressForm.receiver = address.receiver
  addressForm.phone = address.phone
  addressForm.region = [address.province, address.city, address.district]
  addressForm.detail = address.detail
  addressForm.isDefault = address.isDefault === 1
  addressDialogVisible.value = true
}

const submitAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        addressSubmitting.value = true
        // TODO: 调用地址保存接口
        ElMessage.success(addressForm.id ? '地址修改成功' : '地址添加成功')
        addressDialogVisible.value = false
        loadAddressList()
      } catch (error: any) {
        ElMessage.error(error.message || '操作失败')
      } finally {
        addressSubmitting.value = false
      }
    }
  })
}

const handleDeleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      type: 'warning'
    })
    // TODO: 调用地址删除接口
    ElMessage.success('地址删除成功')
    loadAddressList()
  } catch (error) {
    // 用户取消删除
  }
}

const handleSetDefault = async (id: number) => {
  try {
    // TODO: 调用设置默认地址接口
    ElMessage.success('设置默认地址成功')
    loadAddressList()
  } catch (error: any) {
    ElMessage.error(error.message || '设置失败')
  }
}

const submitPassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        passwordSubmitting.value = true
        await changePassword(passwordForm.oldPassword, passwordForm.newPassword)
        ElMessage.success('密码修改成功')
        passwordDialogVisible.value = false
        // 清空表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error: any) {
        ElMessage.error(error.message || '修改失败')
      } finally {
        passwordSubmitting.value = false
      }
    }
  })
}

const loadAddressList = async () => {
  try {
    // TODO: 调用获取地址列表接口
    addressList.value = []
  } catch (error: any) {
    ElMessage.error(error.message || '获取地址列表失败')
  }
}

// 模拟地区数据
const regionOptions = [
  {
    value: '浙江省',
    label: '浙江省',
    children: [
      {
        value: '杭州市',
        label: '杭州市',
        children: [
          { value: '西湖区', label: '西湖区' },
          { value: '上城区', label: '上城区' }
        ]
      }
    ]
  }
]

onMounted(() => {
  loadAddressList()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.info-item {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points {
  color: #f56c6c;
  font-weight: bold;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
}

.address-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.address-info {
  flex: 1;
}

.receiver {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.receiver span:not(:last-child) {
  margin-right: 20px;
}

.address {
  margin: 10px 0 0;
  color: #666;
}

.address-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 