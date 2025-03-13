import request from '../utils/request'

// 获取地址列表
export const getAddressList = () => {
  return request({
    url: '/address/list',
    method: 'get'
  })
}

// 添加地址
export const addAddress = (data: any) => {
  return request({
    url: '/address',
    method: 'post',
    data
  })
}

// 更新地址
export const updateAddress = (data: any) => {
  return request({
    url: '/address',
    method: 'put',
    data
  })
}

// 删除地址
export const deleteAddress = (id: number) => {
  return request({
    url: `/address/${id}`,
    method: 'delete'
  })
}

// 地址接口参数类型定义
export interface AddressForm {
  id?: number
  receiver: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: number
} 