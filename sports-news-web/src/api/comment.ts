import request from '@/utils/request'

export interface CommentQuery {
  page: number
  size: number
  keyword?: string
}

export const createComment = (data: any) => {
  return request({
    url: '/comments',
    method: 'post',
    data
  })
}

export const updateComment = (data: any) => {
  return request({
    url: '/comments',
    method: 'put',
    data
  })
}

export const deleteComment = (id: number) => {
  return request({
    url: `/comments/${id}`,
    method: 'delete'
  })
}

export const getCommentById = (id: number) => {
  return request({
    url: `/comments/${id}`,
    method: 'get'
  })
}

export const getCommentsByNewsId = (newsId: number) => {
  return request({
    url: `/comments/news/${newsId}`,
    method: 'get'
  })
}

export const getComments = (params: CommentQuery) => {
  return request({
    url: '/comments/list',
    method: 'get',
    params
  })
}

export const likeComment = (id: number) => {
  return request({
    url: `/comments/${id}/like`,
    method: 'post'
  })
} 