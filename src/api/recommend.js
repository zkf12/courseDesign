import request from '@/utils/request'
import { usePersistStore } from '@/store/persist'

const persistStore = usePersistStore()
// 获取用户推荐
export function getRecommendations() {
  return request({
    url: '/product/for-user',
    method: 'get',
    params: {
      limit: 12
    }
  })
}