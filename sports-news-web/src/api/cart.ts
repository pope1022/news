import request from '../utils/request'

// 添加到购物车
export const addToCart = (data: AddToCartParams) => {
  return request({
    url: '/api/cart',
    method: 'post',
    data
  })
}

// 获取购物车列表
export const getCartList = () => {
  return request({
    url: '/api/cart/list',
    method: 'get'
  })
}

// 更新购物车商品数量
export const updateCartQuantity = (data: UpdateCartParams) => {
  return request({
    url: '/api/cart',
    method: 'put',
    data
  })
}

// 删除购物车商品
export const removeFromCart = (id: number) => {
  return request({
    url: `/api/cart/${id}`,
    method: 'delete'
  })
}

// 清空购物车
export const clearCart = () => {
  return request({
    url: '/api/cart/clear',
    method: 'delete'
  })
}

// 类型定义
export interface AddToCartParams {
  productId: number
  quantity: number
}

export interface UpdateCartParams {
  id: number
  quantity: number
} 