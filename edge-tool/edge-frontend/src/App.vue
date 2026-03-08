<template>
  <el-container class="app-container">
    <el-aside width="200px" class="aside">
      <div class="logo">批量配置工具</div>
      <el-menu :default-active="activeMenu" router class="menu">
        <el-menu-item index="/identify">
          <el-icon><Cpu /></el-icon>
          <span>协议自动识别</span>
        </el-menu-item>
        <el-menu-item index="/library">
          <el-icon><Collection /></el-icon>
          <span>识别库管理</span>
        </el-menu-item>
        <el-menu-item index="/verify-config">
          <el-icon><Setting /></el-icon>
          <span>校核配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">协议自动识别系统</span>
        <el-button type="primary" circle @click="showOmcConfig = true">
          <el-icon><Tools /></el-icon>
        </el-button>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
    <el-dialog v-model="showOmcConfig" title="OMC配置" width="450px">
      <el-form :model="omcForm" label-width="80px">
        <el-form-item label="IP地址"><el-input v-model="omcForm.ipAddress" placeholder="0.0.0.0" /></el-form-item>
        <el-form-item label="端口"><el-input-number v-model="omcForm.port" :min="0" :max="65535" /></el-form-item>
        <el-form-item label="用户名"><el-input v-model="omcForm.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="omcForm.password" :type="showPwd ? 'text' : 'password'">
          <template #suffix>
            <el-icon @click="showPwd = !showPwd" style="cursor:pointer"><View v-if="showPwd" /><Hide v-else /></el-icon>
          </template>
        </el-input></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showOmcConfig = false">取消</el-button>
        <el-button type="primary" @click="saveOmcConfig">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from './utils/request'

const route = useRoute()
const activeMenu = computed(() => '/' + route.path.split('/')[1])
const showOmcConfig = ref(false)
const showPwd = ref(false)
const omcForm = ref({ ipAddress: '', port: 9000, username: '', password: '' })

async function loadOmcConfig() {
  try {
    const res = await request({ url: '/omc-config', method: 'get' })
    if (res.data) { omcForm.value = res.data }
  } catch(e) {}
}

async function saveOmcConfig() {
  try {
    await request({ url: '/omc-config/save', method: 'post', data: omcForm.value })
    ElMessage.success('配置成功')
    showOmcConfig.value = false
  } catch(e) {
    ElMessage.error('配置失败')
  }
}

onMounted(() => { loadOmcConfig() })
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; }
.app-container { height: 100%; }
.aside { background: #1d2b36; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: bold; border-bottom: 1px solid #2d3e4f; }
.menu { border-right: none; background: #1d2b36; }
.menu .el-menu-item { color: #bfcbd9; }
.menu .el-menu-item:hover { background: #263445; }
.menu .el-menu-item.is-active { color: #67c23a; background: #263445; }
.header { display: flex; justify-content: space-between; align-items: center; background: #fff; border-bottom: 1px solid #e6e6e6; }
.title { font-size: 16px; font-weight: bold; }
.main { background: #f0f2f5; padding: 20px; }
</style>
