import request from '../utils/request'

export function pageLibrary(params) {
  return request({ url: '/library/page', method: 'get', params })
}
export function exportLibrary(params) {
  return request({ url: '/library/export', method: 'get', params, responseType: 'blob' })
}
export function downloadLibraryFile(id) {
  return request({ url: `/library/download/${id}`, method: 'get', responseType: 'blob' })
}
export function viewLibraryConfig(id) {
  return request({ url: `/library/view/${id}`, method: 'get' })
}
export function pageTotalFrequency(params) {
  return request({ url: '/frequency/total/page', method: 'get', params })
}
export function exportTotalFrequency(params) {
  return request({ url: '/frequency/total/export', method: 'get', params, responseType: 'blob' })
}
export function pageSerialFrequency(params) {
  return request({ url: '/frequency/serial/page', method: 'get', params })
}
export function exportSerialFrequency(params) {
  return request({ url: '/frequency/serial/export', method: 'get', params, responseType: 'blob' })
}
