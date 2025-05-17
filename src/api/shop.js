import request from '@/utils/request'

export function getProductList(params) {
  return request({
    url: '/shop/getList',
    method: 'post',
    params: params
  })
}

export function insertProduct(data) {
  return request({
    url: '/shop/insert',
    method: 'post',
    data
  })
}

export function updateProduct(data) {
  return request({
    url: '/shop/update',
    method: 'post',
    data
  })
}

export function deleteProduct(id) {
  return request({
    url: '/shop/delete',
    method: 'post',
    params: {id}
  })
}

export function updateProductImage(data) {
  return request({
    url: '/shop/updateImage',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getSaleHistory(params) {
  return request({
    url: '/shop/history',
    method: 'post',
    params: params
  })
}

export function applyForShop(params) {
  return request({
    url: '/shop/apply',
    method: 'get',
    params: params
  })
}