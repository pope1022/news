import request from '@/utils/request'

export interface LoginData {
  username: string
  password: string
}

export interface RegisterData {
  username: string
  password: string
  nickname: string
  email: string
  phone: string
}

export const login = (data: LoginData) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export const register = (data: RegisterData) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export const getCurrentUser = () => {
  return request({
    url: '/auth/current',
    method: 'get'
  })
} 