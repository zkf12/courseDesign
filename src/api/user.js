import request from '@/utils/request'

/**
 * 获取用户列表
 * @param {string} username - 可选，用户名搜索
 * @param {number} pageNum - 页码，默认为1
 * @param {number} pageSize - 每页数量，默认为12
 * @returns {Promise} 包含用户列表的Promise
 */
export function getUserList(username, pageNum = 1, pageSize = 12) {
  return request({
    url: '/user/getUser',
    method: 'get',
    params: {
      username,
      pageNum,
      pageSize
    }
  })
}

/**
 * 批准商户申请
 * @param {string} username - 必需，要批准的用户名
 * @returns {Promise} 包含操作结果的Promise
 */
export function permitShop(username) {
  return request({
    url: '/shop/permit',
    method: 'get',
    params: {
      username
    }
  })
}

/**
 * 删除用户
 * @param {number} userId - 用户ID
 * @returns {Promise} 包含操作结果的Promise
 */
export function deleteUser(id) {
  return request({
    url: '/user/delete',
    method: 'get',
    params: {
      id
    }
  })
}

/**
 * 获取用户日志
 * @param {string} username - 可选，用户名
 * @param {number} pageNum - 页码，默认为1
 * @param {number} pageSize - 每页数量，默认为12
 * @returns {Promise} 包含日志数据的Promise
 */
export function getUserLog(username, pageNum = 1, pageSize = 12) {
  return request({
    url: '/user/infoSearch',
    method: 'get',
    params: {
      username,
      pageNum,
      pageSize
    }
  })
}

/**
 * 删除日志记录
 * @param {number} id - 日志ID
 * @returns {Promise} 包含操作结果的Promise
 */
export function deleteLog(id) {
  return request({
    url: '/user/logDelete',
    method: 'get',
    params: { id }
  })
}