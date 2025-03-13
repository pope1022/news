import request from '../utils/request'

export interface ProductQuery {
  page: number
  size: number
  keyword?: string
}

export const getProductList = (params: ProductQuery) => {
  return request({
    url: '/products/list',
    method: 'get',
    params
  })
}

export const getProductById = (id: number) => {
  return request({
    url: `/products/${id}`,
    method: 'get'
  })
}

export const createProduct = (data: any) => {
  return request({
    url: '/products',
    method: 'post',
    data
  })
}

export const updateProduct = (data: any) => {
  return request({
    url: '/products',
    method: 'put',
    data
  })
}

export const deleteProduct = (id: number) => {
  return request({
    url: `/products/${id}`,
    method: 'delete'
  })
}

export const updateStock = (id: number, stock: number) => {
  return request({
    url: `/products/${id}/stock`,
    method: 'put',
    params: { stock }
  })
}

export const putOnSale = (id: number) => {
  return request({
    url: `/products/${id}/on-sale`,
    method: 'put'
  })
}

export const takeOffSale = (id: number) => {
  return request({
    url: `/products/${id}/off-sale`,
    method: 'put'
  })
}

export const getHotProducts = (params: { page: number; size: number }) => {
  return request({
    url: '/products/hot',
    method: 'get',
    params
  })
} 