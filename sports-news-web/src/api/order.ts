import request from '../utils/request'

export interface OrderQuery {
  page: number
  size: number
  keyword?: string
}

export const createOrder = (data: CreateOrderParams) => {
  return request({
    url: '/api/orders',
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
    url: `/api/orders/no/${orderNo}`,
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
    url: `/api/orders/${orderNo}/pay`,
    method: 'post'
  })
}

export const cancelOrder = (orderNo: string) => {
  return request({
    url: `/api/orders/${orderNo}/cancel`,
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
    url: `/api/orders/${orderNo}/complete`,
    method: 'post'
  })
}

export const refundOrder = (orderNo: string) => {
  return request({
    url: `/orders/${orderNo}/refund`,
    method: 'post'
  })
}

export interface CreateOrderParams {
  productId: number
  quantity: number
  addressId: number
}

export interface OrderQueryParams {
  page?: number
  size?: number
  status?: number
  keyword?: string
} 