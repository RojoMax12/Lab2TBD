<template>
  <!-- Teleport para asegurarnos de que el modal esté fuera del header y otros stacking contexts -->
  <teleport to="body">
    <div class="overlay" @click.self="close">
      <div class="modal" role="dialog" aria-modal="true" aria-labelledby="login-title" ref="modalRef">
        <!-- Botón cerrar -->
        <button class="close-btn" @click="close" aria-label="Cerrar">←</button>

        <!-- Icono y título -->
        <img src="/logo.png" alt="icono login" class="modal-icon" />
        <h2 id="login-title" class="modal-title">Iniciar sesión</h2>

        <!-- Formulario -->
        <form @submit.prevent="handleLogin" novalidate>
          <div class="form-group">
            <label for="email" class="label">Usuario o correo</label>
            <input id="email" ref="emailInput" type="text" v-model="email" placeholder="usuario o correo" required aria-describedby="email-error" />
            <div v-if="emailError" id="email-error" class="error">{{ emailError }}</div>
          </div>

          <div class="form-group">
            <label for="password" class="label">Contraseña</label>
            <div class="password-container">
              <input id="password" :type="showPassword ? 'text' : 'password'" v-model="password" placeholder="Contraseña" required aria-describedby="password-error" />
              <button
                type="button"
                class="eye-btn"
                @click="togglePasswordVisibility"
                :aria-pressed="showPassword"
                :title="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
                :aria-label="showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
              >
                <!-- Eye (visible) -->
                <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z" stroke="#333" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke="#333" stroke-width="1.5"/>
                </svg>
                <!-- Eye Off (hidden) -->
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                  <path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a20.2 20.2 0 0 1 3.11-4.43" stroke="#333" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M1 1l22 22" stroke="#333" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
            <div v-if="passwordError" id="password-error" class="error">{{ passwordError }}</div>
          </div>

          <div class="row">
            <a href="#" class="forgot-password">¿Olvidaste tu contraseña?</a>
          </div>

          <div class="divider"></div>
          <button type="submit" class="login-btn">Iniciar sesión</button>
        </form>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useRouter } from 'vue-router'
import { jwtDecode } from "jwt-decode";

const emit = defineEmits(['close', 'logged-in'])
const router = useRouter()

const modalRef = ref(null)
const emailInput = ref(null)

const email = ref('')
const password = ref('')
const showPassword = ref(false)

const emailError = ref('')
const passwordError = ref('')

function validate() {
  emailError.value = ''
  passwordError.value = ''
  let ok = true
  if (!email.value) {
    emailError.value = 'Por favor ingresa tu usuario o correo.'
    ok = false
  } else {
    const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)
    const isUser = /^[A-Za-z0-9._-]{3,}$/.test(email.value)
    if (!isEmail && !isUser) {
      emailError.value = 'Ingresa un usuario válido (ej. admin) o un correo válido.'
      ok = false
    }
  }
  if (!password.value) {
    passwordError.value = 'Por favor ingresa tu contraseña.'
    ok = false
  }
  return ok
}

// Función para mostrar/ocultar la contraseña
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

async function handleLogin() {
  if (!validate()) return
  const payload = {
    username: email.value,
    email: email.value,
    password: password.value
  }

  try {
    const res = await fetch('http://localhost:8080/public/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
    if (!res.ok) {
      const text = await res.text()
      throw new Error(`Error ${res.status}: ${text}`)
    }
    const data = await res.json()
    const token = data.token

    if (!token) throw new Error('No se recibió token')

    // Guarda el token en el store y en localStorage
    const auth = useAuthStore()
    auth.setToken(token)
    localStorage.setItem('jwt', token)

    const decoded = jwtDecode(token);
    const userTypeFromToken = decoded ? decoded.usertype : null;
    
    emit('logged-in', token)
    // Redirigir a la vista según el tipo de usuario
    if (userTypeFromToken === 'admin') {
      // Redirigir a la vista del administrador
      router.push({ name: 'home-admin' })
    } else if (userTypeFromToken === 'driver') {
      // Redirigir a la vista del conductor
      router.push({ name: 'home-driver' }) // Cambia 'home-driver' por la ruta correspondiente
    } else {
      throw new Error('Tipo de usuario desconocido')
    }

    emit('close')
    // Limpia campos y errores
    email.value = ''
    password.value = ''
    emailError.value = ''
    passwordError.value = ''
  } catch (err) {
    console.error('Error al iniciar sesión:', err)
    passwordError.value = 'Usuario o contraseña inválidos.'
  }
}


function close() {
  emit('close')
}

// focus trap y escape
function onKeydown(e) {
  if (e.key === 'Escape') {
    emit('close')
    return
  }
  if (e.key !== 'Tab') return
  const el = modalRef.value
  if (!el) return
  const focusable = Array.from(el.querySelectorAll('a[href], button:not([disabled]), input:not([disabled]), textarea:not([disabled]), select:not([disabled]), [tabindex]:not([tabindex="-1"])'))
  if (!focusable.length) return
  const first = focusable[0]
  const last = focusable[focusable.length - 1]
  if (e.shiftKey) {
    if (document.activeElement === first) {
      e.preventDefault(); last.focus()
    }
  } else {
    if (document.activeElement === last) {
      e.preventDefault(); first.focus()
    }
  }
}

onMounted(async () => {
  await nextTick()
  if (emailInput.value) emailInput.value.focus()
  document.addEventListener('keydown', onKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', onKeydown)
})
</script>

<style scoped>
/* Fondo oscuro */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  /* Centrar verticalmente el modal */
  align-items: center;
  /* Debe quedar por encima del header sticky (header usa z-index:100) */
  z-index: 1200;
}

/* Ventana modal */
.modal {
  background-color: #52563f;
  padding: 1.5rem 1.5rem;
  border-radius: 12px;
  position: relative;
  width: 92%;
  max-width: 420px;
  color: #222;
  text-align: left;
  box-shadow: 0 18px 40px rgba(0,0,0,0.28);
  /* Evitar que el modal sea más alto que la ventana */
  max-height: calc(100vh - 12vh);
  overflow-y: auto;
  transform: none;
}

/* Botón cerrar */
.close-btn {
  position: absolute;
  top: 0.6rem;
  right: 0.6rem;
  background: #f3f3f3;
  color: #000000;
  border: none;
  border-radius: 50%;
  font-size: 1.1rem;
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(0,0,0,0.12);
}

/* Icono */
.icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.modal-title {
  margin: 0.25rem 0 1rem 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
}

.form-group {
  margin-bottom: 0.75rem;
  display: flex;
  flex-direction: column;
  align-items: center; /* centra label y contenido */
}
.label {
  display: block;
  font-size: 0.85rem;
  color: #ffffff;
  margin-bottom: 0.35rem;
  text-align: center; /* centra el texto del label */
}

/* limitar ancho de los inputs dentro del form-group para que queden centrados */
.form-group input {
  max-width: 360px;
  width: 100%;
}

.password-container { width: 100%; max-width: 360px; }

input[type="email"], input[type="password"], .password-container input {
  width: 100%;
  padding: 0.65rem 0.8rem;
  border-radius: 10rem;
  border: 1px solid rgba(0,0,0,0.08);
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.04);
}

.password-container { display:flex; gap:0.5rem; align-items:center }
.eye-btn { background:transparent; border:none; cursor:pointer; font-size:1.1rem }

.error { color: #b43b3b; font-size:0.85rem; margin-top:0.35rem }

.row { display:flex; justify-content:flex-end; margin-bottom:0.75rem }
.forgot-password { font-size:0.9rem; color:#1a9f7a; text-decoration:none }

.divider { height:1px; background:rgba(0,0,0,0.04); margin:0.6rem 0 0.9rem }

/* Inputs */
input {
  width: 100%;
  padding: 0.8rem;
  margin: 0.5rem 0;
  border-radius: 2rem;
  border: none;
}

/* Contenedor de contraseña con botón ojo */
.password-container {
  /* make container a positioned box so the eye button can be absolutely positioned inside the input */
  position: relative;
  width: 100%;
  max-width: 360px;
}

.password-container input {
  width: 100%;
  padding-right: 3rem; /* room for the eye button inside the input */
  box-sizing: border-box;
}

.password-container .eye-btn {
  position: absolute;
  right: 0.6rem;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0.2rem;
  color: #333;
}

/* Botón login */
.login-btn {
  margin-top: 1rem;
  background: linear-gradient(180deg,#5e6541,#52563f);
  color: white;
  padding: 0.75rem 1.25rem;
  border-radius: 10rem;
  border: none;
  cursor: pointer;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(94,101,65,0.18);
  width: 100%;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(94,101,65,0.22);
  opacity: 0.98;
}

/* Animaciones */
.overlay { animation: overlayFade .16s ease; }
.modal { animation: modalPop .18s cubic-bezier(.2,.9,.3,1); }
@keyframes overlayFade { from { opacity:0 } to { opacity:1 } }
@keyframes modalPop { from { transform: translateY(6vh) scale(.99); opacity:0 } to { transform: translateY(0) scale(1); opacity:1 } }

.forgot-password {
  display: block;
  margin: 0.5rem auto 0;
  font-size: 0.875rem;
  color: #ffffff; /* mantener color legible */
  text-decoration: underline;
  text-align: center;
}

.divider {
  height: 1px;
  background: rgba(255,255,255,0.2);
  margin: 1.5rem 0;
}

.login-btn {
  margin-top: 1rem;
  background: linear-gradient(180deg,#5e6541,#52563f);
  color: #ffffff;
  padding: 0.75rem 1.25rem;
  border-radius: 10rem;
  border: 1px solid oldlace;          /* borde visible */
  cursor: pointer;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(0,0,0,0.18);
  width: 100%;
}
/* Responsive: centrar verticalmente en pantallas pequeñas */
@media (max-width: 480px), (max-height: 700px) {
  .overlay {
    /* En pantallas pequeñas centramos y quitamos el translate para que ocupe más espacio natural */
    align-items: center;
  }
  .modal {
    max-width: 92%;
    max-height: calc(100vh - 6vh);
    transform: none;
  }
}

.modal-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 1rem;
  display: block;
}
</style>
