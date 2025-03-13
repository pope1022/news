import request from '@/utils/request'

export interface OrderQuery {
  page: number
  size: number
  keyword?: string
}

export const createOrder = (data: any) => {
  return request({
    url: '/orders',
    method: 'post',
    data
  })
}

export const getOrderById = (id: number) => {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

export const getOrderByOrderNo = (orderNo: string) => {
  return request({
    url: `/orders/no/${orderNo}`,
    method: 'get'
  })
}

export const getUserOrders = (params: { userId: number; page: number; size: number }) => {
  return request({
    url: '/orders/user',
    method: 'get',
    params
  })
}

export const getAllOrders = (params: OrderQuery) => {
  return request({
    url: '/orders/list',
    method: 'get',
    params
  })
}

export const payOrder = (orderNo: string) => {
  return request({
    url: `/orders/${orderNo}/pay`,
    method: 'post'
  })
}

export const cancelOrder = (orderNo: string) => {
  return request({
    url: `/orders/${orderNo}/cancel`,
    method: 'post'
  })
}

export const deliverOrder = (orderNo: string, expressNo: string, expressCompany: string) => {
  return request({
    url: `/orders/${orderNo}/deliver`,
    method: 'post',
    params: { expressNo, expressCompany }
  })
}

export const completeOrder = (orderNo: string) => {
  return request({
    url: `/orders/${orderNo}/complete`,
    method: 'post'
  })
}

export const refundOrder = (orderNo: string) => {
  return request({
    url: `/orders/${orderNo}/refund`,
    method: 'post'
  })
} 