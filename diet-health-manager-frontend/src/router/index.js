import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
    {
        path: '/',
        component: Layout,
        children: [
            { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
            { path: 'goal', name: 'Goal', component: () => import('../views/Goal.vue') },
            { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue') },
            // 👇 这是新加的食物库路由 👇
            { path: 'food', name: 'Food', component: () => import('../views/Food.vue') },
            { path: 'diet', name: 'Diet', component: () => import('../views/Diet.vue') }
        ]
    }
]

const router = createRouter({ history: createWebHistory(), routes })
export default router