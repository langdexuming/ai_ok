import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/library' },
  { path: '/library', name: 'Library', component: () => import('../views/library/index.vue') },
  { path: '/verify-config', name: 'VerifyConfig', component: () => import('../views/verify-config/index.vue') },
  { path: '/device', name: 'Device', component: () => import('../views/device/index.vue') },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
