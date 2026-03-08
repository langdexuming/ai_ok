import request from '../utils/request'

export function pageVerifyConfig(params) {
  return request({ url: '/verify-config/page', method: 'get', params })
}
export function addVerifyConfig(data) {
  return request({ url: '/verify-config/add', method: 'post', data })
}
export function updateVerifyConfig(data) {
  return request({ url: '/verify-config/update', method: 'put', data })
}
export function deleteVerifyConfig(id) {
  return request({ url: '/verify-config/delete', method: 'delete', params: { id } })
}
export function batchDeleteVerifyConfig(ids) {
  return request({ url: '/verify-config/batch-delete', method: 'delete', data: ids })
}
export function exportVerifyConfig(params) {
  return request({ url: '/verify-config/export', method: 'get', params, responseType: 'blob' })
}
