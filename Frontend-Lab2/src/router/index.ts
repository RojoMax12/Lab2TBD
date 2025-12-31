import { createRouter, createWebHistory } from 'vue-router'


import HomeView from '../views/HomeView.vue'
import HomeDriverView from '../views/User/HomeDriverView.vue'
import HomeAdminView from '../views/AdminView/HomeAdminView.vue'
import RouteTakenView from '../views/User/RouteTakenView.vue'
import UsersView from '../views/AdminView/UsersView.vue'
import ContainersView from '../views/AdminView/ContainerView.vue'
import RouteView from '../views/AdminView/RouteView.vue'
import AdminsView from '../views/AdminView/AdminsView.vue'
import RouteAssignedView from '@/views/User/RouteAssignedView.vue'
import CentralView from '@/views/AdminView/CentralView.vue'
import ServicesView from '../views/ServicesView.vue'
import ContactView from '../views/ContactView.vue'
import WhoWeAreView from '@/views/WhoWeAreView.vue'
 


const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/driver', name: 'home-driver', component: HomeDriverView, meta: { requiresAuth: true } },
    { path: '/admin', name: 'home-admin', component: HomeAdminView, meta: { requiresAuth: true } },
    { path: '/route-taken', name: 'route-taken', component: RouteTakenView },
    { path: '/users', name: 'users', component: UsersView, meta: { requiresAuth: true } },
    { path: '/container', name: 'containers', component: ContainersView, meta: { requiresAuth: true } },
    { path: '/route', name: 'route', component: RouteView, meta: { requiresAuth: true } },
    { path: '/admins', name: 'admins', component: AdminsView, meta: { requiresAuth: true } },
    { path: '/route-assigned', name: 'route-assigned', component: RouteAssignedView },
    { path: '/centrals', name: 'centrals', component: CentralView, meta: { requiresAuth: true } },
    { path: '/services', name: 'services', component: ServicesView},
    { path: '/contact', name: 'contact', component: ContactView},
    { path: '/whoweare', name: 'whoweare', component: WhoWeAreView},

  ]
})

// navegacion global bloquea rutas que requieren auth cuando no hay JWT presente
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta && record.meta.requiresAuth)
  const token = (() => {
    try { return localStorage.getItem('jwt') } catch (e) { return null }
  })()
  // Helper to safely decode JWT payload (no external deps)
  function parseJwtPayload(t: string | null) {
    if (!t) return null
    try {
      const part = t.split('.')[1]
      if (!part) return null
      const decoded = decodeURIComponent(atob(part).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
      }).join(''))
      return JSON.parse(decoded)
    } catch (e) {
      return null
    }
  }

  // If route requires auth but there's no token, block access and send to public home
  if (requiresAuth && !token) {
    return next({ name: 'home' })
  }

  // If user is logged in (has token) and tries to navigate to public home, redirect
  if (to.name === 'home' && token) {
    const payload = parseJwtPayload(token)
    const userType = payload?.userType || payload?.role || payload?.rol || payload?.type
    if (userType && String(userType).toLowerCase().includes('driver')) {
      return next({ name: 'home-driver' })
    }
    // default to admin dashboard when role is not driver
    return next({ name: 'home-admin' })
  }

  return next()
})

export default router
