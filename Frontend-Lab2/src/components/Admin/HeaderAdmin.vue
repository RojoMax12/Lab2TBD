<template>
  <header class="admin-header">
    <div class="admin-left">

      <button class="menu-btn" @click="showSidebar = true" aria-label="Abrir menú">
        <svg width="36" height="36" viewBox="0 0 28 28" fill="none">
          <rect x="7" y="12" width="14" height="2" rx="1" fill="white"/>
          <rect x="7" y="16" width="14" height="2" rx="1" fill="white"/>
          <rect x="7" y="8" width="14" height="2" rx="1" fill="white"/>
        </svg>
      </button>

      <img src="/cleanops.png" alt="Admin" class="admin-avatar" />
      <span class="header-title">CleanOps</span>
    </div>

    <div class="admin-right">
      <BxSolidUserCircle class="user-circle-icon" />
      <span class="admin-name">{{ fullName }}</span>
      <button class="logout-btn" @click="cerrarSesion()" title="Cerrar Sesión">
        <FlFilledArrowExit />
      </button>
    </div>
  </header>

  <!-- Sidebar -->
  <Transition name="sidebar-slide">
    <aside class="sidebar" v-if="showSidebar">
      <div class="sidebar-header">
        <span class="sidebar-title">MENÚ</span>
        <button class="close-btn" @click="showSidebar = false">&times;</button>
      </div>
      <div class="sidebar-divider"></div>

      <nav class="sidebar-links">
        <a class="sidebar-link" @click="home()">● Inicio</a>
        <a class="sidebar-link" @click="container()">● Contenedores</a>
        <a class="sidebar-link" @click="users()">● Conductores</a>
        <a class="sidebar-link" @click="admins()">● Adminstradores</a>
        <a class="sidebar-link" @click="centrals()">● Centrales</a>
        <a class="sidebar-link" @click="rutas()">● Rutas</a>
        <a class="sidebar-link" @click="collectionzones()">● Zonas de Recolección</a>
      </nav>
    </aside>
  </Transition>
      <a class="sidebar-link" @click="outOfZone()">Contenedores fuera de zona</a>

  <div v-if="showSidebar" class="sidebar-backdrop" @click="showSidebar = false"></div>
</template>

<script setup>
import { FlFilledArrowExit, BxSolidUserCircle } from '@kalimahapps/vue-icons';
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth' // Asegúrate que esta ruta sea correcta en tu proyecto
import { jwtDecode } from "jwt-decode"
import AdminServices from '@/services/adminservices'

const router = useRouter()

const showSidebar = ref(false)
const userEmail = ref(null)

const name = ref('')
const lastname = ref('')

/* ============================
      DECODIFICAR TOKEN
============================ */
onMounted(() => {
  const token = localStorage.getItem('jwt')

  if (!token) return

  try {
    const decoded = jwtDecode(token)
    userEmail.value = decoded.sub || decoded.email || null

    if (userEmail.value != null) {
      getAdminData(userEmail.value)
    }
  } catch (error) {
    console.error("Error al decodificar token:", error)
  }
})

/* ============================
    OBTENER ADMIN POR EMAIL
============================ */
async function getAdminData(email) {
    try {
        const response = await AdminServices.getAdminByEmail(email)
        const admin = response.data


        name.value = admin.name
        lastname.value = admin.last_name
    } catch (err) {
        console.error("Error obteniendo admin:", err)
    }
}

/* ============================
    NOMBRE COMPLETO COMPUTADO
============================ */
const fullName = computed(() =>
  name.value && lastname.value
    ? `${name.value} ${lastname.value}`
    : userEmail.value || "Administrador"
)

/* ============================
        NAVEGACIÓN
============================ */
function cerrarSesion() {
  const auth = useAuthStore()
  auth.setToken(null)
  localStorage.removeItem('jwt')
  router.push('/')
}

function home() {
  showSidebar.value = false
  router.push({ name: 'home-admin' })
}

function users() {
  showSidebar.value = false
  router.push({ name: 'users' })
}

function container() {
  showSidebar.value = false
  router.push({ name: 'containers' })
}

function rutas() {
  showSidebar.value = false
  router.push({ name: 'route' })
}

function admins() {
  showSidebar.value = false
  router.push({ name: 'admins' })
}

function centrals() {
  showSidebar.value = false
  router.push({ name: 'centrals' })
}

</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  background-color: #F0F3E7;
  position: relative;
}

/* Header */
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #32533D;
  color: white;
  padding: 0rem 2rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: static;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 1100;
}

.admin-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.menu-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
  transition: background 0.2s ease;
  display: flex;
  align-items: center;
}
.menu-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}
.admin-avatar {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

.header-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-left: -0.5rem;
  font-family: 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.admin-name {
  color: white;
  font-weight: 500;
  font-size: 1.1rem;
  margin-left: -0.5rem;
}

.admin-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-circle-icon {
  font-size: 2rem;
  color: white;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  padding: 0.5rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background 0.2s ease;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.logout-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.logout-btn:focus {
  outline: 3px solid rgba(255,255,255,0.12);
  outline-offset: 2px;
}

/* Barra lateral */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 240px;
  height: 100vh;
  background: linear-gradient(180deg, #32533D, #3E5C44);
  color: #fff;
  box-shadow: 2px 0 16px rgba(0,0,0,0.13);
  z-index: 1001;
  display: flex;
  flex-direction: column;
  padding: 2rem 1.2rem 1.2rem 1.2rem;
}

.sidebar-slide-enter-active {
  animation: slideInSidebarLeft 0.3s ease-out;
}

.sidebar-slide-leave-active {
  animation: slideOutSidebarLeft 0.3s ease-in;
}

@keyframes slideInSidebarLeft {
  from { left: -260px; }
  to { left: 0; }
}

@keyframes slideOutSidebarLeft {
  from { left: 0; }
  to { left: -260px; }
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  cursor: default;
}

.sidebar-title {
  font-size: 1.4rem;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  color: #fff;
  cursor: pointer;
  line-height: 1;
}

.sidebar-links {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.sidebar-link {
  color: #fff;
  text-decoration: none;
  font-size: 1.08rem;
  font-weight: 500;
  padding: 0.6rem 1rem;
  border-radius: 8px;
  transition: background 0.18s;
  cursor: pointer;
}

.sidebar-link:hover {
  background: rgba(255,255,255,0.12);
}

.sidebar-divider {
  height: 1px;
  background-color: rgba(255, 255, 255, 0.15);
  margin: 1.5rem 0;
  width: 100%;
}

.sidebar-backdrop {
  position: fixed;
  top: 0; left: 0;
  width: 100vw; height: 100vh;
  background: rgba(0,0,0,0.18);
  z-index: 1000;
}

/* Main */
.admin-main {
  /* add top padding to avoid content being hidden behind the fixed header */
  padding: calc(2rem + 64px) 2rem 2rem 2rem;
}

.admin-title {
  color: #000000;
  text-align: center;
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 2.5rem;
}

.admin-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  align-items: center;
}

.admin-box {
  background-color: #4C7840;
  border-radius: 20px;
  width: 100%;
  height: 350px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.admin-circle-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.admin-circle {
  background-color: #4C7840;
  border-radius: 50%;
  width: 350px;
  height: 350px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.calculate-btn {
  margin-top: 1.5rem;
  background-color: #4C7840;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 25px;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s ease;
}
.calculate-btn:hover {
  background-color: #3E5C44;
}

@media (max-width: 900px) {
  .admin-main {
    padding: 1rem;
  }
  .admin-grid {
    grid-template-columns: 1fr;
    gap: 1.2rem;
  }
  .admin-box, .admin-circle {
    height: 220px;
    width: 100%;
    max-width: 320px;
    margin: 0 auto;
  }
  .sidebar {
    width: 80vw;
    min-width: 180px;
  }
}
</style>
