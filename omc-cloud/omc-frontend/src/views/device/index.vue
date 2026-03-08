<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="设备搜索" name="search">
        <div class="search-bar">
          <el-input v-model="searchFilter.ipAddress" placeholder="IP地址" clearable style="width:150px" />
          <el-input v-model="searchFilter.deviceCode" placeholder="设备编码" clearable style="width:150px" />
          <el-input v-model="searchFilter.siteName" placeholder="站点名称" clearable style="width:150px" />
          <el-button type="primary" @click="filterSearchResult">查询</el-button>
          <el-button type="success" @click="searchCurrent" :loading="searching">搜索当前网段</el-button>
          <el-button type="success" @click="showSubnetDialog = true">搜索其他网段</el-button>
          <el-button type="warning" @click="handleBatchAdd">加入设备列表</el-button>
          <el-popconfirm title="确认清空列表？" @confirm="clearSearchList">
            <template #reference><el-button type="danger">清空列表</el-button></template>
          </el-popconfirm>
        </div>
        <el-table :data="filteredSearchData" border stripe @selection-change="onSearchSelect">
          <el-table-column type="selection" width="50" />
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="ipAddress" label="IP地址" />
          <el-table-column prop="port" label="端口" width="80" />
          <el-table-column prop="deviceCode" label="设备编码" />
          <el-table-column prop="deviceName" label="设备名称" />
          <el-table-column prop="softwareVersion" label="软件版本" width="100" />
          <el-table-column prop="bInterfaceVersion" label="B接口版本" width="100" />
          <el-table-column prop="siteName" label="站点名称" />
          <el-table-column label="操作" width="140">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleAddOne(row)">加入设备列表</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="设备列表" name="list">
        <div class="search-bar">
          <el-input v-model="devQuery.ipAddress" placeholder="IP地址" clearable style="width:150px" @keyup.enter="searchDev" />
          <el-input v-model="devQuery.deviceCode" placeholder="设备编码" clearable style="width:150px" @keyup.enter="searchDev" />
          <el-input v-model="devQuery.siteName" placeholder="站点名称" clearable style="width:150px" @keyup.enter="searchDev" />
          <el-button type="primary" @click="searchDev">查询</el-button>
          <el-button type="success" @click="handleNewDevice">新增</el-button>
          <el-button type="danger" @click="handleBatchDeleteDev">删除</el-button>
          <el-upload action="/api/device/import" :show-file-list="false" :on-success="onDevImportSuccess" accept=".xlsx,.xls">
            <el-button type="success">导入</el-button>
          </el-upload>
          <el-button @click="handleExportDev">导出</el-button>
        </div>
        <el-table :data="devData" border stripe @selection-change="onDevSelect">
          <el-table-column type="selection" width="50" />
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="ipAddress" label="IP地址" />
          <el-table-column prop="port" label="端口" width="80" />
          <el-table-column prop="deviceCode" label="设备编码" />
          <el-table-column prop="deviceName" label="设备名称" />
          <el-table-column prop="softwareVersion" label="软件版本" width="100" />
          <el-table-column prop="bInterfaceVersion" label="B接口版本" width="100" />
          <el-table-column prop="siteName" label="站点名称" />
          <el-table-column label="操作" width="140">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleEditDevice(row)">编辑</el-button>
              <el-popconfirm title="确认删除本条数据？" @confirm="handleDeleteDevice(row.id)">
                <template #reference><el-button link type="danger">删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination v-model:current-page="devQuery.current" v-model:page-size="devQuery.size"
          :total="devTotal" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadDevList" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="showSubnetDialog" title="网段设置" width="500px">
      <div v-for="(item, idx) in ipRanges" :key="idx" style="display:flex;gap:8px;margin-bottom:8px;align-items:center">
        <span>网段{{ idx + 1 }}起始IP：</span>
        <el-input v-model="ipRanges[idx]" placeholder="起始IP-结束IP" style="flex:1" />
        <el-button v-if="ipRanges.length > 1" link type="danger" @click="ipRanges.splice(idx, 1)">删除</el-button>
      </div>
      <el-button @click="ipRanges.push('')">添加IP段</el-button>
      <template #footer>
        <el-button @click="showSubnetDialog = false">取消</el-button>
        <el-button type="primary" @click="doSearchOther" :loading="searching">检索</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="devDialogVisible" :title="isDevEdit ? '编辑' : '新增'" width="450px">
      <el-form :model="devForm" ref="devFormRef" label-width="80px">
        <el-form-item label="IP地址" prop="ipAddress" :rules="[{ required: true, message: '请输入IP地址' }]">
          <el-input v-model="devForm.ipAddress" placeholder="0.0.0.0" />
        </el-form-item>
        <el-form-item label="端口" prop="port" :rules="[{ required: true, message: '请输入端口' }]">
          <el-input-number v-model="devForm.port" :min="0" :max="65535" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="devDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitDevForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { searchCurrentSubnet, searchOtherSubnet, addToDeviceList, batchAddToDeviceList,
         pageDevice, addDevice, updateDevice, deleteDevice, batchDeleteDevice, exportDevice } from '../../api/device'

const activeTab = ref('search')
const searching = ref(false)
const searchData = ref([])
const searchSelected = ref([])
const searchFilter = ref({ ipAddress: '', deviceCode: '', siteName: '' })

const filteredSearchData = computed(() => {
  return searchData.value.filter(d => {
    if (searchFilter.value.ipAddress && !d.ipAddress?.includes(searchFilter.value.ipAddress)) { return false }
    if (searchFilter.value.deviceCode && !d.deviceCode?.includes(searchFilter.value.deviceCode)) { return false }
    if (searchFilter.value.siteName && !d.siteName?.includes(searchFilter.value.siteName)) { return false }
    return true
  })
})

async function searchCurrent() {
  searching.value = true
  try {
    const res = await searchCurrentSubnet()
    searchData.value = [...searchData.value, ...res.data]
  } finally { searching.value = false }
}

const showSubnetDialog = ref(false)
const ipRanges = ref([''])

async function doSearchOther() {
  searching.value = true
  showSubnetDialog.value = false
  try {
    const res = await searchOtherSubnet({ ipRanges: ipRanges.value.filter(r => r) })
    searchData.value = [...searchData.value, ...res.data]
  } finally { searching.value = false }
}

function clearSearchList() { searchData.value = [] }
function filterSearchResult() {}
function onSearchSelect(rows) { searchSelected.value = rows }

async function handleAddOne(row) {
  await addToDeviceList(row)
  ElMessage.success('已加入设备列表')
  searchData.value = searchData.value.filter(d => d.ipAddress !== row.ipAddress)
}

async function handleBatchAdd() {
  if (searchSelected.value.length === 0) {
    ElMessage.warning('请选择设备')
    return
  }
  await batchAddToDeviceList(searchSelected.value)
  ElMessage.success('已加入设备列表')
  const ips = new Set(searchSelected.value.map(d => d.ipAddress))
  searchData.value = searchData.value.filter(d => !ips.has(d.ipAddress))
}

const devQuery = ref({ ipAddress: '', deviceCode: '', siteName: '', current: 1, size: 10 })
const devData = ref([])
const devTotal = ref(0)
const devSelected = ref([])
const devDialogVisible = ref(false)
const isDevEdit = ref(false)
const devFormRef = ref(null)
const devForm = ref({ ipAddress: '', port: 8080 })

async function loadDevList() {
  const res = await pageDevice(devQuery.value)
  devData.value = res.data.records
  devTotal.value = res.data.total
}
function searchDev() { devQuery.value.current = 1; loadDevList() }
function onDevSelect(rows) { devSelected.value = rows }

function handleNewDevice() {
  isDevEdit.value = false
  devForm.value = { ipAddress: '', port: 8080 }
  devDialogVisible.value = true
}

function handleEditDevice(row) {
  isDevEdit.value = true
  devForm.value = { id: row.id, ipAddress: row.ipAddress, port: row.port }
  devDialogVisible.value = true
}

async function submitDevForm() {
  await devFormRef.value.validate()
  if (isDevEdit.value) {
    await updateDevice(devForm.value)
    ElMessage.success('编辑成功')
  } else {
    await addDevice(devForm.value)
    ElMessage.success('新增成功')
  }
  devDialogVisible.value = false
  loadDevList()
}

async function handleDeleteDevice(id) {
  await deleteDevice(id)
  ElMessage.success('删除成功')
  loadDevList()
}

async function handleBatchDeleteDev() {
  if (devSelected.value.length === 0) {
    ElMessage.warning('请先选择数据')
    return
  }
  await ElMessageBox.confirm('确认删除选中数据？', '提示')
  await batchDeleteDevice(devSelected.value.map(r => r.id))
  ElMessage.success('删除成功')
  loadDevList()
}

function handleExportDev() {
  exportDevice(devQuery.value).then(res => {
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a')
    a.href = url; a.download = '设备列表.xlsx'; a.click()
  })
}

function onDevImportSuccess() { ElMessage.success('导入成功'); loadDevList() }

onMounted(() => { loadDevList() })
</script>

<style scoped>
.page-container { background: #fff; padding: 20px; border-radius: 4px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
