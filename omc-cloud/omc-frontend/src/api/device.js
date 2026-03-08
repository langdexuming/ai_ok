import request from '../utils/request'

export function searchCurrentSubnet() {
  return request({ url: '/device/search-current', method: 'post' })
}
export function searchOtherSubnet(data) {
  return request({ url: '/device/search-other', method: 'post', data })
}
export function addToDeviceList(data) {
  return request({ url: '/device/add-to-list', method: 'post', data })
}
export function batchAddToDeviceList(data) {
  return request({ url: '/device/batch-add-to-list', method: 'post', data })
}
export function pageDevice(params) {
  return request({ url: '/device/page', method: 'get', params })
}
export function addDevice(data) {
  return request({ url: '/device/add', method: 'post', data })
}
export function updateDevice(data) {
  return request({ url: '/device/update', method: 'put', data })
}
export function deleteDevice(id) {
  return request({ url: '/device/delete', method: 'delete', params: { id } })
}
export function batchDeleteDevice(ids) {
  return request({ url: '/device/batch-delete', method: 'delete', data: ids })
}
export function exportDevice(params) {
  return request({ url: '/device/export', method: 'get', params, responseType: 'blob' })
}
export function listDeviceTypes() {
  return request({ url: '/device/device-type/list', method: 'get' })
}
export function listDeviceSubtypes(typeId) {
  return request({ url: '/device/device-subtype/list', method: 'get', params: { typeId } })
}
