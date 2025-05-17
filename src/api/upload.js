// src/api/upload.js
import request from '@/utils/request'

export function updateProfile(formData) {
  return request({
    url: '/user/changeUserInfo',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

//修改密码
export function changePassword(formData){
  return request({
    url: '/user/changePassword',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}