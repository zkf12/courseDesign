import request from '@/utils/request'

// 获取分类列表
export function getCategories() {
  return request({
    url: '/product/categories',
    method: 'get'
  })
}

// 获取商品列表
export function getProducts(params) {
  return request({
    url: '/product/getProduct',
    method: 'get',
    params
  })
}

export function recordViewDuration(data) {
  return request({
    url: '/product/recordView',
    method: 'post',
    data
  })
}

/**
 * 获取浏览记录
 * @param {Object} params - 包含page, pageSize, username的参数对象
 * @returns {Promise} 包含浏览记录的Promise
 */
export function getViewTime(params) {
  return request({
    url: '/product/getView',
    method: 'get',
    params
  })
}
