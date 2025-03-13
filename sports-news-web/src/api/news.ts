import request from '../utils/request'

export interface NewsQuery {
  page: number
  size: number
  keyword?: string
  categoryId?: number
}

export const getNewsList = (params: NewsQuery) => {
  return request({
    url: '/news/list',
    method: 'get',
    params
  })
}

export const getNewsById = (id: number) => {
  return request({
    url: `/news/${id}`,
    method: 'get'
  })
}

export const createNews = (data: any) => {
  return request({
    url: '/news',
    method: 'post',
    data
  })
}

export const updateNews = (data: any) => {
  return request({
    url: '/news',
    method: 'put',
    data
  })
}

export const deleteNews = (id: number) => {
  return request({
    url: `/news/${id}`,
    method: 'delete'
  })
}

export const publishNews = (id: number) => {
  return request({
    url: `/news/${id}/publish`,
    method: 'put'
  })
}

export const unpublishNews = (id: number) => {
  return request({
    url: `/news/${id}/unpublish`,
    method: 'put'
  })
}

export const likeNews = (id: number) => {
  return request({
    url: `/news/${id}/like`,
    method: 'post'
  })
}

export const getHotNews = (params: { page: number; size: number }) => {
  return request({
    url: '/news/hot',
    method: 'get',
    params
  })
}

export const getLatestNews = (params: { page: number; size: number }) => {
  return request({
    url: '/news/latest',
    method: 'get',
    params
  })
} 