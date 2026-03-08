<template>
  <div class="page-container">
    <div class="search-bar">
      <el-input v-model="query.deviceTypeName" placeholder="设备类型" clearable style="width:160px" @keyup.enter="search" />
      <el-input v-model="query.deviceSubtypeName" placeholder="设备子类" clearable style="width:160px" @keyup.enter="search" />
      <el-input v-model="query.channelName" placeholder="信号名称" clearable style="width:160px" @keyup.enter="search" />
      <el-button type="primary" @click="search">查询</el-button>
      <el-button type="success" @click="handleAdd">新增</el-button>
      <el-button type="danger" @click="handleBatchDelete">删除</el-button>
      <el-upload action="/api/verify-config/import" :show-file-list="false" :on-success="onImportSuccess" accept=".xlsx,.xls">
        <el-button type="success">导入</el-button>
      </el-upload>
      <el-button @click="handleExport">导出</el-button>
    </div>

    <el-table :data="tableData" border stripe @selection-change="onSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="deviceTypeName" label="设备类型" />
      <el-table-column prop="deviceSubtypeName" label="设备子类" />
      <el-table-column prop="channelName" label="信号名称" />
      <el-table-column prop="channelCode" label="信号ID" />
      <el-table-column prop="upperLimit" label="上限" width="120" />
      <el-table-column prop="lowerLimit" label="下限" width="120" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确认删除本条数据？" @confirm="handleDelete(row.id)">
            <template #reference><el-button link type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="query.current" v-model:page-size="query.size"
      :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="loadData" />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑' : '新增'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="设备类型" prop="deviceTypeId">
          <el-select v-model="form.deviceTypeId" filterable placeholder="请选择" @change="onTypeChange">
            <el-option v-for="t in typeList" :key="t.typeId" :label="t.typeName" :value="t.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备子类" prop="deviceSubtypeId">
          <el-select v-model="form.deviceSubtypeId" filterable placeholder="请选择">
            <el-option v-for="s in subtypeList" :key="s.subtypeId" :label="s.subtypeName" :value="s.subtypeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="信号名称" prop="channelName">
          <el-input v-model="form.channelName" maxlength="50" />
        </el-form-item>
        <el-form-item label="信号ID" prop="channelCode">
          <el-input v-model="form.channelCode" maxlength="50" />
        </el-form-item>
        <el-form-item label="上限" prop="upperLimit">
          <el-input-number v-model="form.upperLimit" :precision="4" :min="-999999999" :max="999999999" />
        </el-form-item>
        <el-form-item label="下限" prop="lowerLimit">
          <el-input-number v-model="form.lowerLimit" :precision="4" :min="-999999999" :max="999999999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageVerifyConfig, addVerifyConfig, updateVerifyConfig, deleteVerifyConfig, batchDeleteVerifyConfig, exportVerifyConfig } from '../../api/verifyConfig'
import { listDeviceTypes, listDeviceSubtypes } from '../../api/device'

const query = ref({ deviceTypeName: '', deviceSubtypeName: '', channelName: '', current: 1, size: 10 })
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const typeList = ref([])
const subtypeList = ref([])
const form = ref({ deviceTypeId: '', deviceTypeName: '', deviceSubtypeId: '', deviceSubtypeName: '', channelName: '', channelCode: '', upperLimit: 0, lowerLimit: 0 })
const rules = {
  deviceTypeId: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  deviceSubtypeId: [{ required: true, message: '请选择设备子类', trigger: 'change' }],
  channelName: [{ required: true, message: '请输入信号名称', trigger: 'blur' }],
  channelCode: [{ required: true, message: '请输入信号ID', trigger: 'blur' }],
  upperLimit: [{ required: true, message: '请输入上限', trigger: 'blur' }],
  lowerLimit: [{ required: true, message: '请输入下限', trigger: 'blur' }],
}

async function loadData() {
  const res = await pageVerifyConfig(query.value)
  tableData.value = res.data.records
  total.value = res.data.total
}
function search() { query.value.current = 1; loadData() }

async function loadTypes() {
  const res = await listDeviceTypes()
  typeList.value = res.data
}

async function onTypeChange(val) {
  form.value.deviceSubtypeId = ''
  const type = typeList.value.find(t => t.typeId === val)
  if (type) { form.value.deviceTypeName = type.typeName }
  const res = await listDeviceSubtypes(val)
  subtypeList.value = res.data
}

function handleAdd() {
  isEdit.value = false
  form.value = { deviceTypeId: '', deviceTypeName: '', deviceSubtypeId: '', deviceSubtypeName: '', channelName: '', channelCode: '', upperLimit: 0, lowerLimit: 0 }
  dialogVisible.value = true
}

async function handleEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  await onTypeChange(row.deviceTypeId)
  form.value.deviceSubtypeId = row.deviceSubtypeId
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  const subtype = subtypeList.value.find(s => s.subtypeId === form.value.deviceSubtypeId)
  if (subtype) { form.value.deviceSubtypeName = subtype.subtypeName }
  if (isEdit.value) {
    await updateVerifyConfig(form.value)
    ElMessage.success('编辑成功')
  } else {
    await addVerifyConfig(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(id) {
  await deleteVerifyConfig(id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择数据')
    return
  }
  await ElMessageBox.confirm('确认删除选中数据？', '提示')
  await batchDeleteVerifyConfig(selectedIds.value)
  ElMessage.success('删除成功')
  loadData()
}

function onSelectionChange(rows) { selectedIds.value = rows.map(r => r.id) }

function handleExport() {
  exportVerifyConfig(query.value).then(res => {
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a')
    a.href = url; a.download = '校核配置.xlsx'; a.click()
  })
}

function onImportSuccess() { ElMessage.success('导入成功'); loadData() }

onMounted(() => { loadData(); loadTypes() })
</script>

<style scoped>
.page-container { background: #fff; padding: 20px; border-radius: 4px; }
.search-bar { display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
</style>
