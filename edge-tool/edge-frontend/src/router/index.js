import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/identify' },
  { path: '/identify', name: 'Identify', component: () => import('../views/identify/index.vue') },
  { path: '/library', name: 'Library', component: () => import('../views/library/index.vue') },
  { path: '/verify-config', name: 'VerifyConfig', component: () => import('../views/verify-config/index.vue') },
]

export default createRouter({ history: createWebHistory(), routes })
