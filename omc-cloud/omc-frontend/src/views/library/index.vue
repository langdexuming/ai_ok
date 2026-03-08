<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="动态库" name="library">
        <div class="search-bar">
          <el-input v-model="libQuery.deviceTypeName" placeholder="设备类型" clearable style="width:160px" @keyup.enter="searchLib" />
          <el-input v-model="libQuery.manufacturer" placeholder="设备厂家" clearable style="width:160px" @keyup.enter="searchLib" />
          <el-input v-model="libQuery.libraryName" placeholder="动态库名称" clearable style="width:160px" @keyup.enter="searchLib" />
          <el-button type="primary" @click="searchLib">查询</el-button>
          <el-upload action="/api/library/import-list" :show-file-list="false" :on-success="onImportListSuccess" accept=".csv">
            <el-button type="success">导入动态库列表</el-button>
          </el-upload>
          <el-upload action="/api/library/import-file" :show-file-list="false" :on-success="onImportFileSuccess" multiple>
            <el-button type="success">导入动态库</el-button>
          </el-upload>
          <el-button @click="handleExportLib">导出</el-button>
        </div>
        <el-table :data="libData" border stripe>
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="deviceTypeName" label="设备类型" />
          <el-table-column prop="manufacturer" label="设备厂家" />
          <el-table-column prop="deviceModel" label="设备型号" />
          <el-table-column prop="libraryName" label="动态库名称" />
          <el-table-column prop="version" label="版本号" width="100" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
              <el-button link type="primary" @click="handleView(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="libQuery.current" v-model:page-size="libQuery.size"
          :total="libTotal" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadLib" />
      </el-tab-pane>

      <el-tab-pane label="总使用频率" name="totalFreq">
        <div class="search-bar">
          <el-input v-model="freqQuery.deviceType" placeholder="设备类型" clearable style="width:160px" @keyup.enter="searchFreq" />
          <el-input v-model="freqQuery.deviceSubtype" placeholder="设备子类" clearable style="width:160px" @keyup.enter="searchFreq" />
          <el-input v-model="freqQuery.libraryName" placeholder="动态库名称" clearable style="width:160px" @keyup.enter="searchFreq" />
          <el-button type="primary" @click="searchFreq">查询</el-button>
          <el-upload action="/api/frequency/total/import" :show-file-list="false" :on-success="onImportFreqSuccess" accept=".xlsx,.xls">
            <el-button type="success">导入</el-button>
          </el-upload>
          <el-button @click="handleExportFreq">导出</el-button>
        </div>
        <el-table :data="freqData" border stripe>
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="deviceType" label="设备类型" />
          <el-table-column prop="deviceSubtype" label="设备子类" />
          <el-table-column prop="deviceModel" label="设备型号" />
          <el-table-column prop="libraryName" label="动态库名称" />
          <el-table-column prop="frequency" label="概率" width="120">
            <template #default="{ row }">{{ (row.frequency * 100).toFixed(2) }}%</template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="freqQuery.current" v-model:page-size="freqQuery.size"
          :total="freqTotal" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadFreq" />
      </el-tab-pane>

      <el-tab-pane label="串口使用频率" name="serialFreq">
        <div class="search-bar">
          <el-input v-model.number="serialQuery.serialNum" placeholder="串口号" clearable style="width:100px" @keyup.enter="searchSerial" />
          <el-input v-model.number="serialQuery.deviceAddress" placeholder="地址位" clearable style="width:100px" @keyup.enter="searchSerial" />
          <el-input v-model="serialQuery.deviceType" placeholder="设备类型" clearable style="width:140px" @keyup.enter="searchSerial" />
          <el-input v-model="serialQuery.deviceSubtype" placeholder="设备子类" clearable style="width:140px" @keyup.enter="searchSerial" />
          <el-input v-model="serialQuery.libraryName" placeholder="动态库名称" clearable style="width:140px" @keyup.enter="searchSerial" />
          <el-button type="primary" @click="searchSerial">查询</el-button>
          <el-upload action="/api/frequency/serial/import" :show-file-list="false" :on-success="onImportSerialSuccess" accept=".xlsx,.xls">
            <el-button type="success">导入</el-button>
          </el-upload>
          <el-button @click="handleExportSerial">导出</el-button>
        </div>
        <el-table :data="serialData" border stripe>
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="serialNum" label="串口号" width="80" />
          <el-table-column prop="deviceAddress" label="地址位" width="80" />
          <el-table-column prop="deviceType" label="设备类型" />
          <el-table-column prop="deviceSubtype" label="设备子类" />
          <el-table-column prop="deviceModel" label="设备型号" />
          <el-table-column prop="libraryName" label="动态库名称" />
          <el-table-column prop="frequency" label="概率" width="120">
            <template #default="{ row }">{{ (row.frequency * 100).toFixed(2) }}%</template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="serialQuery.current" v-model:page-size="serialQuery.size"
          :total="serialTotal" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadSerial" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="viewDialogVisible" title="配置内容" width="60%">
      <pre class="config-content">{{ viewContent }}</pre>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageLibrary, exportLibrary, downloadLibraryFile, viewLibraryConfig,
         pageTotalFrequency, exportTotalFrequency,
         pageSerialFrequency, exportSerialFrequency } from '../../api/library'

const activeTab = ref('library')
const libQuery = ref({ deviceTypeName: '', manufacturer: '', libraryName: '', current: 1, size: 10 })
const libData = ref([])
const libTotal = ref(0)
const freqQuery = ref({ deviceType: '', deviceSubtype: '', libraryName: '', current: 1, size: 10 })
const freqData = ref([])
const freqTotal = ref(0)
const serialQuery = ref({ serialNum: null, deviceAddress: null, deviceType: '', deviceSubtype: '', libraryName: '', current: 1, size: 10 })
const serialData = ref([])
const serialTotal = ref(0)
const viewDialogVisible = ref(false)
const viewContent = ref('')

async function loadLib() {
  const res = await pageLibrary(libQuery.value)
  libData.value = res.data.records
  libTotal.value = res.data.total
}
function searchLib() { libQuery.value.current = 1; loadLib() }

async function loadFreq() {
  const res = await pageTotalFrequency(freqQuery.value)
  freqData.value = res.data.records
  freqTotal.value = res.data.total
}
function searchFreq() { freqQuery.value.current = 1; loadFreq() }

async function loadSerial() {
  const res = await pageSerialFrequency(serialQuery.value)
  serialData.value = res.data.records
  serialTotal.value = res.data.total
}
function searchSerial() { serialQuery.value.current = 1; loadSerial() }

async function handleDownload(row) {
  const res = await downloadLibraryFile(row.id)
  const url = window.URL.createObjectURL(new Blob([res.data]))
  const a = document.createElement('a')
  a.href = url
  a.download = row.libraryName
  a.click()
}

async function handleView(row) {
  const res = await viewLibraryConfig(row.id)
  viewContent.value = res.data
  viewDialogVisible.value = true
}

function handleExportLib() { exportLibrary(libQuery.value).then(downloadBlob('动态库列表.csv')) }
function handleExportFreq() { exportTotalFrequency(freqQuery.value).then(downloadBlob('总使用频率.xlsx')) }
function handleExportSerial() { exportSerialFrequency(serialQuery.value).then(downloadBlob('串口使用频率.xlsx')) }

function downloadBlob(filename) {
  return (res) => {
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    a.click()
  }
}

function onImportListSuccess() { ElMessage.success('导入成功'); loadLib() }
function onImportFileSuccess() { ElMessage.success('导入成功'); loadLib() }
function onImportFreqSuccess() { ElMessage.success('导入成功'); loadFreq() }
function onImportSerialSuccess() { ElMessage.success('导入成功'); loadSerial() }

onMounted(() => { loadLib() })
</script>

<style scoped>
.page-container { background: #fff; padding: 20px; border-radius: 4px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.config-content { white-space: pre-wrap; word-break: break-all; max-height: 500px; overflow: auto; background: #f5f5f5; padding: 16px; border-radius: 4px; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
