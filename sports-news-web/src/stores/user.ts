import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getCurrentUser } from '@/api/auth'
import type { LoginData } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)

  const login = async (loginData: LoginData) => {
    const res = await loginApi(loginData)
    token.value = res.token
    localStorage.setItem('token', res.token)
    localStorage.setItem('userRole', res.role.toString())
    await getUserInfo()
  }

  const getUserInfo = async () => {
    userInfo.value = await getCurrentUser()
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
  }

  return {
    token,
    userInfo,
    login,
    getUserInfo,
    logout
  }
}) 