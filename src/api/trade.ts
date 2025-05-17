import request from '@/utils/request'

// 获取购物车列表
export function getCart() {
  return request({
    url: '/trade/getCart',
    method: 'post'
  })
}

// 删除购物车商品
export function deleteCart(productId: number) {
  return request({
    url: '/trade/deleteCart',
    method: 'post',
    params: { product_id: productId }
  })
}

// 加入购物车
export function addInCart(productId: number) {
  return request({
    url: '/trade/add',
    method: 'post',
    params: { product_id: productId },
  })
}

//结算
export function purchase(productList) {
  return request({
    url: '/trade/purchase',
    method: 'post',
    data: productList
  })
}

export function getPurchaseHistory(params) {
  return request({
    url: '/trade/history',
    method: 'post',
    params: params
  })
}

export function getHistoryDetail(orderId) {
  return request({
    url: '/trade/detail',
    method: 'post',
    params: { order_id: orderId }
  })
}

import service from '@/utils/request'

// 获取销售额排行
export function getMostPrices(pageNum = 1, pageSize = 5) {
  return service.get('/trade/mostPrices', {
    params: {
      pageNum,
      pageSize
    }
  })
}

// 获取销量排行
export function getMostQuantity(pageNum = 1, pageSize = 5) {
  return service.get('/trade/mostQuantity', {
    params: {
      pageNum,
      pageSize
    }
  })
}