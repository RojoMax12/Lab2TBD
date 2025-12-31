<template>
  <header class="admin-header">
    <div class="admin-left">

      <!-- Botón menú -->
      <button class="menu-btn" @click="showSidebar = true" aria-label="Abrir menú">
        <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
          <circle cx="14" cy="14" r="14" fill="#f7f7f7"/>
          <rect x="7" y="12" width="14" height="2" rx="1" fill="#5f6949"/>
          <rect x="7" y="16" width="14" height="2" rx="1" fill="#5f6949"/>
          <rect x="7" y="8" width="14" height="2" rx="1" fill="#5f6949"/>
        </svg>
      </button>

      <img src="/logo.png" alt="Admin" class="admin-avatar" />

      <!-- Muestra nombre y apellido -->
      <span class="admin-name">{{ fullName }}</span>
    </div>

    <button class="logout-btn" @click="cerrarSesion()">
      Cerrar sesión
    </button>
  </header>

  <!-- Sidebar -->
  <aside class="sidebar" v-if="showSidebar">
    <div class="sidebar-header">
      <span class="sidebar-title">Menú</span>
      <button class="close-btn" @click="showSidebar = false">&times;</button>
    </div>

    <nav class="sidebar-links">
      <a class="sidebar-link" @click="home()">Inicio</a>
      <a class="sidebar-link" @click="container()">Contenedores</a>
      <a class="sidebar-link" @click="users()">Conductores</a>
      <a class="sidebar-link" @click="admins()">Adminstradores</a>
      <a class="sidebar-link" @click="centrals()">Centrales</a>
      <a class="sidebar-link" @click="rutas()">Rutas</a>
    </nav>
  </aside>

  <div v-if="showSidebar" class="sidebar-backdrop" @click="showSidebar = false"></div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
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
      getAdminData(userEmail.value)   // <--- OBTENER NOMBRE Y APELLIDO
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

        // Asegúrate de que la respuesta tenga los campos correctos
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
  background-color: #f4e9da;
  position: relative;
}

/* Header */
.admin-header {
  --bg-start: #6a704a;
  --bg-end: #4e5336;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, var(--bg-start), var(--bg-end));
  color: white;
  padding: 0.75rem 1.25rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.12);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
  /* Make admin header fixed so it stays visible while scrolling */
  position: static;
  top: 0;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 1100;
  backdrop-filter: blur(4px);
}

.admin-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.menu-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.2rem;
  border-radius: 50%;
  transition: background 0.18s;
  display: flex;
  align-items: center;
}
.menu-btn:hover {
  background: #eaeaea;
}

.admin-avatar {
  width: 40px;
  height: 40px;
  object-fit: contain;
  box-shadow: 0 2px 6px rgba(0,0,0,0.10);
}

.admin-name {
  color: white;
  font-weight: bold;
  font-size: 1.2rem;
}

.logout-btn {
  background: linear-gradient(180deg,#ffffff,#f3f3f3);
  color: #5e6541;
  padding: 0.5rem 1.5rem;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  font-weight: 800;
  box-shadow: 0 2px 6px rgba(94,101,65,0.14);
  transition: transform .12s ease, box-shadow .12s ease, opacity .12s ease;
  font-size: 15px;
}
.logout-btn:hover {
    transform: translateY(-2px);
  box-shadow: 0 8px 18px rgba(94,101,65,0.18);
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
  background: linear-gradient(180deg, #5f6949, #4c553a);
  color: #fff;
  box-shadow: 2px 0 16px rgba(0,0,0,0.13);
  z-index: 1001;
  display: flex;
  flex-direction: column;
  padding: 2rem 1.2rem 1.2rem 1.2rem;
  animation: slideInSidebarLeft 0.2s;
}

@keyframes slideInSidebarLeft {
  from { left: -260px; opacity: 0; }
  to { left: 0; opacity: 1; }
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.sidebar-title {
  font-size: 1.2rem;
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
}

.sidebar-link:hover {
  background: rgba(255,255,255,0.12);
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
  background-color: #5f6949;
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
  background-color: #5f6949;
  border-radius: 50%;
  width: 350px;
  height: 350px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.calculate-btn {
  margin-top: 1.5rem;
  background-color: #5f6949;
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 25px;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.3s ease;
}
.calculate-btn:hover {
  background-color: #4c553a;
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