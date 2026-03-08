import request from '../utils/request'
export function listDeviceTypes() { return request({ url: '/device-type/list', method: 'get' }) }
export function listDeviceSubtypes(typeId) { return request({ url: '/device-subtype/list', method: 'get', params: { typeId } }) }
