import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import Profile from '../views/Profile.vue'
import Plan from '../views/Plan.vue'
import Weight from '../views/Weight.vue'
import History from '../views/History.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  {
    path: '/app',
    component: Layout,
    children: [
      { path: 'profile', component: Profile },
      { path: 'plan', component: Plan },
      { path: 'weight', component: Weight },
      { path: 'history', component: History },
      { path: '', redirect: '/app/profile' }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
