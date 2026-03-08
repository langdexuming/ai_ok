<template>
  <div class="page-container">
    <el-card class="connect-card">
      <div class="connect-section">
        <el-form :inline="true">
          <el-form-item label="IP地址">
            <el-input v-model="connectForm.ipAddress" placeholder="0.0.0.0" style="width:160px" />
          </el-form-item>
          <el-form-item label="端口">
            <el-input-number v-model="connectForm.port" :min="0" :max="65535" style="width:120px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleConnect">连接设备</el-button>
          </el-form-item>
        </el-form>
        <div class="connect-status">
          <el-tag v-if="connected === true" type="success">已连接</el-tag>
          <el-tag v-else-if="connected === false" type="danger">连接失败</el-tag>
          <el-tag v-else type="info">未连接</el-tag>
        </div>
      </div>
    </el-card>

    <el-card class="status-card">
      <div class="status-section">
        <div class="status-item">
          <span class="label">状态：</span>
          <el-tag :type="statusTagType">{{ statusText }}</el-tag>
        </div>
        <div class="status-item">
          <span class="label">耗时：</span>
          <span class="value">{{ elapsedTime }}</span>
        </div>
        <div class="status-item">
          <span class="label">识别成功设备数：</span>
          <span class="value">{{ status === 'idle' ? '--' : successCount }}</span>
        </div>
        <div class="status-item">
          <span class="label">识别协议个数：</span>
          <span class="value">{{ status === 'idle' ? '--' : protocolCount }}</span>
        </div>
        <div class="status-actions">
          <el-button type="success" @click="handleStart" :disabled="status === 'running'">开始协议自动识别</el-button>
          <el-button type="danger" @click="handleStop" :disabled="status !== 'running'">停止协议自动识别</el-button>
          <el-button @click="handleExport">导出实时数据</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="log-card">
      <template #header>
        <div class="log-header">
          <span>结果打印</span>
          <div>
            <el-select v-model="currentSerial" style="width:120px;margin-right:8px">
              <el-option v-for="i in 32" :key="i" :label="'串口' + i" :value="i" />
            </el-select>
            <el-button @click="downloadLog">下载到本地</el-button>
            <el-button type="danger" @click="clearLogs">清除日志</el-button>
          </div>
        </div>
      </template>
      <div class="log-content" ref="logContainer">
        <pre>{{ currentLog }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { connectDevice, startIdentify, stopIdentify, getIdentifyStatus, getIdentifyLog, exportRealtimeData } from '../../api/identify'

const connectForm = ref({ ipAddress: '', port: 8080 })
const connected = ref(null)
const status = ref('idle')
const successCount = ref(0)
const protocolCount = ref(0)
const startTime = ref(null)
const elapsedMs = ref(0)
const currentSerial = ref(1)
const logs = ref({})
let timer = null
let statusTimer = null

const statusText = computed(() => {
  if (status.value === 'idle') { return '待开始' }
  if (status.value === 'running') { return '协议识别中' }
  return '协议识别完成'
})

const statusTagType = computed(() => {
  if (status.value === 'idle') { return 'info' }
  if (status.value === 'running') { return 'warning' }
  return 'success'
})

const elapsedTime = computed(() => {
  const ms = elapsedMs.value
  const h = Math.floor(ms / 3600000)
  const m = Math.floor((ms % 3600000) / 60000)
  const s = Math.floor((ms % 60000) / 1000)
  const milli = ms % 1000
  return `${String(h).padStart(2, '0')}小时${String(m).padStart(2, '0')}分钟${String(s).padStart(2, '0')}秒${String(milli).padStart(3, '0')}毫秒`
})

const currentLog = computed(() => logs.value[currentSerial.value] || '暂无日志')

async function handleConnect() {
  try {
    await connectDevice(connectForm.value)
    connected.value = true
    ElMessage.success('连接成功')
  } catch(e) {
    connected.value = false
  }
}

async function handleStart() {
  if (connected.value !== true) {
    ElMessage.warning('请先成功连接设备')
    return
  }
  if (status.value === 'running') {
    ElMessage.warning('当前已经在协议自动识别中，请勿重复操作')
    return
  }
  try {
    await startIdentify()
    status.value = 'running'
    startTime.value = Date.now()
    startTimer()
    startStatusPolling()
  } catch(e) {}
}

async function handleStop() {
  try {
    await stopIdentify()
    status.value = 'done'
    stopTimer()
    stopStatusPolling()
  } catch(e) {}
}

function startTimer() {
  timer = setInterval(() => {
    if (startTime.value) {
      elapsedMs.value = Date.now() - startTime.value
    }
  }, 100)
}

function stopTimer() { if (timer) { clearInterval(timer); timer = null } }

function startStatusPolling() {
  statusTimer = setInterval(async () => {
    try {
      const res = await getIdentifyStatus()
      if (res.data) {
        successCount.value = res.data.successCount || 0
        protocolCount.value = res.data.protocolCount || 0
        if (res.data.status === 'done') {
          status.value = 'done'
          stopTimer()
          stopStatusPolling()
        }
      }
      const logRes = await getIdentifyLog(currentSerial.value)
      if (logRes.data) {
        logs.value[currentSerial.value] = logRes.data
      }
    } catch(e) {}
  }, 2000)
}

function stopStatusPolling() { if (statusTimer) { clearInterval(statusTimer); statusTimer = null } }

function handleExport() {
  exportRealtimeData().then(res => {
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a')
    a.href = url
    a.download = '实时数据.xlsx'
    a.click()
  })
}

function downloadLog() {
  const content = logs.value[currentSerial.value] || ''
  const now = new Date()
  const ts = now.getFullYear() + String(now.getMonth()+1).padStart(2,'0') + String(now.getDate()).padStart(2,'0') +
    String(now.getHours()).padStart(2,'0') + String(now.getMinutes()).padStart(2,'0') + String(now.getSeconds()).padStart(2,'0')
  const blob = new Blob([content], { type: 'text/plain' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `COM${currentSerial.value}-${ts}.txt`
  a.click()
}

function clearLogs() { logs.value = {} }

onUnmounted(() => { stopTimer(); stopStatusPolling() })
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.connect-section { display: flex; align-items: center; gap: 16px; }
.connect-status { margin-left: 16px; }
.status-section { display: flex; flex-wrap: wrap; gap: 20px; align-items: center; }
.status-item { display: flex; align-items: center; }
.status-item .label { font-weight: bold; margin-right: 4px; }
.status-item .value { font-family: monospace; font-size: 16px; }
.status-actions { margin-left: auto; display: flex; gap: 8px; }
.log-header { display: flex; justify-content: space-between; align-items: center; }
.log-content { background: #1e1e1e; color: #d4d4d4; padding: 16px; border-radius: 4px; min-height: 300px; max-height: 500px; overflow: auto; font-family: monospace; font-size: 13px; }
</style>
