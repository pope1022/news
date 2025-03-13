import request from '../utils/request'

export interface UserQuery {
  page: number
  size: number
  keyword?: string
}

export const updateProfile = (data: any) => {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

export const changePassword = (oldPassword: string, newPassword: string) => {
  return request({
    url: '/users/password',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}

export const getUsers = (params: UserQuery) => {
  return request({
    url: '/users/list',
    method: 'get',
    params
  })
}

export const enableUser = (id: number) => {
  return request({
    url: `/users/${id}/enable`,
    method: 'put'
  })
}

export const disableUser = (id: number) => {
  return request({
    url: `/users/${id}/disable`,
    method: 'put'
  })
}

export const deleteUser = (id: number) => {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
} 