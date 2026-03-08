<template>
  <el-container class="app-container">
    <el-aside width="200px" class="aside">
      <div class="logo">OMC管理工具</div>
      <el-menu :default-active="activeMenu" router class="menu">
        <el-menu-item index="/library">
          <el-icon><Collection /></el-icon>
          <span>识别库管理</span>
        </el-menu-item>
        <el-menu-item index="/verify-config">
          <el-icon><Setting /></el-icon>
          <span>校核配置</span>
        </el-menu-item>
        <el-menu-item index="/device">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => '/' + route.path.split('/')[1])
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; }
.app-container { height: 100%; }
.aside { background: #304156; }
.logo {
  height: 60px; line-height: 60px; text-align: center;
  color: #fff; font-size: 18px; font-weight: bold;
  border-bottom: 1px solid #3d4e5f;
}
.menu { border-right: none; background: #304156; }
.menu .el-menu-item { color: #bfcbd9; }
.menu .el-menu-item:hover { background: #263445; }
.menu .el-menu-item.is-active { color: #409eff; background: #263445; }
.main { background: #f0f2f5; padding: 20px; }
</style>
