import request from '@/utils/request'

export interface CategoryQuery {
  page: number
  size: number
  keyword?: string
}

export const createCategory = (data: any) => {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

export const updateCategory = (data: any) => {
  return request({
    url: '/categories',
    method: 'put',
    data
  })
}

export const deleteCategory = (id: number) => {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}

export const getCategoryById = (id: number) => {
  return request({
    url: `/categories/${id}`,
    method: 'get'
  })
}

export const getAllCategories = () => {
  return request({
    url: '/categories/all',
    method: 'get'
  })
}

export const getCategories = (params: CategoryQuery) => {
  return request({
    url: '/categories/list',
    method: 'get',
    params
  })
} 