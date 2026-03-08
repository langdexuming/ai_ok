import request from '../utils/request'

export function connectDevice(data) { return request({ url: '/identify/connect', method: 'post', data }) }
export function startIdentify() { return request({ url: '/identify/start', method: 'post' }) }
export function stopIdentify() { return request({ url: '/identify/stop', method: 'post' }) }
export function getIdentifyStatus() { return request({ url: '/identify/status', method: 'get' }) }
export function getIdentifyLog(serialNum) { return request({ url: `/identify/log/${serialNum}`, method: 'get' }) }
export function exportRealtimeData() { return request({ url: '/identify/export', method: 'get', responseType: 'blob' }) }
