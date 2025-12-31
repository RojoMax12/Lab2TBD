<template>
  <header class="header" role="banner">
    <div class="profile-container">
      <button class="menu-btn" @click="showSidebar = true" aria-label="Abrir menú">
        <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
          <circle cx="14" cy="14" r="14" fill="#f7f7f7"/>
          <rect x="7" y="12" width="14" height="2" rx="1" fill="#5f6949"/>
          <rect x="7" y="16" width="14" height="2" rx="1" fill="#5f6949"/>
          <rect x="7" y="8" width="14" height="2" rx="1" fill="#5f6949"/>
        </svg>
      </button>
      <img src="/logo.png" alt="Logo empresa" class="logo" />
      <div class="profile-info">
        <h1 class="name-employeer">{{ fullName }}</h1>
        <span class="role">Conductor</span>
      </div>
    </div>
    <div class="header-actions">
      <button class="logout-btn" @click="logout" aria-label="Cerrar sesión">
        <span class="logout-text">Cerrar sesión</span>
      </button>
    </div>

    <!-- Barra lateral -->
    <aside class="sidebar" v-if="showSidebar">
      <div class="sidebar-header">
        <span class="sidebar-title">Menú</span>
        <button class="close-btn" @click="showSidebar = false" aria-label="Cerrar menú">
          &times;
        </button>
      </div>
      <nav class="sidebar-links">
        <a href="#" class="sidebar-link" @click="home">Inicio </a>
        <a href="#" class="sidebar-link" @click="routeAssigned"> Rutas asignadas</a>
        <a href="#" class="sidebar-link" @click="routeTaken"> Rutas realizadas</a>
      </nav>
    </aside>
    <div v-if="showSidebar" class="sidebar-backdrop" @click="showSidebar = false"></div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter} from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { jwtDecode } from "jwt-decode"
import DriverServices from '@/services/driverservices'

const router = useRouter()
const showSidebar = ref(false)
const name = ref('')
const lastname = ref('')
const userEmail = ref("")
const fullName = computed(() =>
  name.value && lastname.value
    ? `${name.value} ${lastname.value}` : null

)


onMounted(() => {
  const token = localStorage.getItem('jwt')

  if (!token) return

  try {
    const decoded = jwtDecode(token)

    userEmail.value = decoded.sub || decoded.email || null

    if (userEmail.value != null) {
      getDriverData(userEmail.value)   // <--- OBTENER NOMBRE Y APELLIDO
    }
  } catch (error) {
    console.error("Error al decodificar token:", error)
  }
})

async function getDriverData(email) {
    try {

        const response = await DriverServices.getDriverByEmail(email)

   
        // Asegúrate de que la respuesta tenga los campos correctos
        const driver = response.data
  

        name.value = driver.name
        lastname.value = driver.last_name
    } catch (err) {
        console.error("Error obteniendo admin:", err)
    }
}


function logout() {
  const authStore = useAuthStore()
  authStore.setToken(null)
  localStorage.removeItem('jwt')
  router.push('/')
}

function home() {
  router.push({ name: 'home-driver' })
}

function routeTaken() {
  router.push({ name: 'route-taken' })
}

function routeAssigned() {
  router.push({ name: 'route-assigned' })
}
</script>

<style scoped>
.header {
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
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(4px);
}

.profile-container {
  display: flex;
  align-items: center;
  gap: 1.1rem;
}

.logo {
  width: 54px;
  height: 54px;
  border-radius: 12px;
  object-fit: contain;
  box-shadow: 0 4px 12px rgba(0,0,0,0.10);
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 0.1rem;
}

.name-employeer {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
  color: #fff;
  letter-spacing: 0.5px;
}

.role {
  font-size: 0.98rem;
  color: #dcd6c8;
  font-weight: 500;
  margin-top: 2px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
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

.logout-btn {
  background: linear-gradient(180deg,#ffffff,#f3f3f3);
  color: #5e6541;
  padding: 0.5rem 1rem;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(94,101,65,0.14);
  transition: transform .12s ease, box-shadow .12s ease, opacity .12s ease;
}
.logout-btn:hover {
    transform: translateY(-2px);
  box-shadow: 0 8px 18px rgba(94,101,65,0.18);
}

.logout-btn:focus {
  outline: 3px solid rgba(255,255,255,0.12);
  outline-offset: 2px;
}

.logout-text {
  font-size: 1rem;
  font-weight: 600;
  color: #4d5d39;
}

/* Barra lateral */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 260px;
  height: 100vh;
  background: linear-gradient(180deg, #5e6541, #4d5d39);
  color: #fff;
  box-shadow: -2px 0 16px rgba(0,0,0,0.13);
  z-index: 1001;
  display: flex;
  flex-direction: column;
  padding: 2rem 1.2rem 1.2rem 1.2rem;
  animation: slideInSidebar 0.2s;
}

@keyframes slideInSidebar {
  from { right: -280px; opacity: 0; }
  to { right: 0; opacity: 1; }
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
</style>