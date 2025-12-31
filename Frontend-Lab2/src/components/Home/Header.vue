<template>
  <header class="header" role="banner">
    <div class="left">
      <div class="logo-container">
        <img src="/logoact.png" alt="Logo Trash TBD" class="logo" />
        <h1 class="site-title">CleanOps</h1>
      </div>

      <nav class="nav" aria-label="Main navigation">
        <a href="#" class="nav-link" @click="Home">Inicio</a>
        <router-link to="/services" class="nav-link">Servicios</router-link>
        <a href="#" class="nav-link" @click="Contact">Contacto</a>
      </nav>
    </div>

    <div class="actions">
      <!-- Si está autenticado mostrar logout, si no mostrar iniciar sesión -->
      <template v-if="auth.token">
        <button class="login-btn" @click="onLogout">Cerrar sesión</button>
      </template>
      <template v-else>
        <button @click="showLogin = true" class="login-btn" aria-haspopup="dialog">Iniciar sesión</button>
      </template>
    </div>

    <!-- Modal -->
    <LoginModal v-if="showLogin" @close="showLogin = false" @logged-in="onLoggedIn" />
  </header>
</template>

<script setup>
import { ref } from "vue"
import LoginModal from "./LoginModal.vue" // ajusta la ruta si está en otra carpeta
import { useAuthStore } from '../../stores/auth'
import { useRouter } from 'vue-router'

const showLogin = ref(false)
const auth = useAuthStore()
const router = useRouter()
auth.initFromStorage()

function onLoggedIn(token) {
  auth.setToken(token)
}

function onLogout() {
  auth.clear()
}

function Home() {
  router.push({name: 'home'})

}

function Contact() {
  router.push({name: 'contact'})
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

.left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.logo {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 10px;
}

.site-title {
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: 0.4px;
  margin: 0;
}

.nav {
  display: flex;
  gap: 0.75rem;
  margin-left: 0.5rem;
}

.nav-link {
  color: rgba(255,255,255,0.92);
  text-decoration: none;
  font-weight: 500;
  padding: 0.4rem 0.6rem;
  border-radius: 6px;
  transition: background-color .12s ease, color .12s ease, transform .12s ease;
}

.nav-link:hover,
.nav-link:focus {
  background: rgba(255,255,255,0.06);
  color: #fff;
  transform: translateY(-1px);
}

.actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.login-btn {
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

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 18px rgba(94,101,65,0.18);
}

.login-btn:focus {
  outline: 3px solid rgba(255,255,255,0.12);
  outline-offset: 2px;
}

@media (max-width: 700px) {
  .nav { display: none; }
  .site-title { font-size: 1rem; }
  .logo { width: 48px; height: 48px; }
  .header { padding: 0.5rem 0.75rem; }
}
</style>
