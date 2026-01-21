<template>
  <teleport to="body">
    <div class="overlay" @click.self="close">
      <div class="modal" role="dialog" aria-modal="true" aria-labelledby="login-title" ref="modalRef">

        <div class="modal-header">
          <button class="close-btn" @click="close" aria-label="Cerrar">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>

          <img src="/cleanops.png" alt="CleanOps Logo" class="modal-icon" />
          <h2 id="login-title" class="modal-title">Iniciar Sesión</h2>
        </div>

        <div class="modal-body">
          <form @submit.prevent="handleLogin" novalidate>

            <div class="form-group">
              <label for="email" class="label">Usuario o correo</label>
              <input
                id="email"
                ref="emailInput"
                type="text"
                v-model="email"
                placeholder="Ej. admin@cleanops.com"
                required
                :class="{ 'input-error': emailError }"
              />
              <div v-if="emailError" class="error-msg">{{ emailError }}</div>
            </div>

            <div class="form-group">
              <label for="password" class="label">Contraseña</label>
              <div class="password-wrapper">
                <input
                  id="password"
                  :type="showPassword ? 'text' : 'password'"
                  v-model="password"
                  placeholder="Ingresa tu contraseña"
                  required
                  :class="{ 'input-error': passwordError }"
                />
                <button
                  type="button"
                  class="eye-btn"
                  @click="togglePasswordVisibility"
                  :title="showPassword ? 'Ocultar' : 'Mostrar'"
                >
                  <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                  <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a20.2 20.2 0 0 1 3.11-4.43"></path>
                    <path d="M1 1l22 22"></path>
                  </svg>
                </button>
              </div>
              <div v-if="passwordError" class="error-msg">{{ passwordError }}</div>
            </div>

            <div class="form-actions">
              <a href="#" class="forgot-link">¿Olvidaste tu contraseña?</a>
            </div>

            <button type="submit" class="login-btn">
              Iniciar Sesión
            </button>
          </form>
        </div>

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
    emailError.value = 'El usuario es requerido.'
    ok = false
  } else {
    if (email.value.length < 3) {
      emailError.value = 'Ingresa un usuario válido.'
      ok = false
    }
  }
  if (!password.value) {
    passwordError.value = 'La contraseña es requerida.'
    ok = false
  }
  return ok
}

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
    const res = await fetch('http://localhost:8090/public/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })

    if (!res.ok) {
      throw new Error('Credenciales inválidas')
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
      router.push({ name: 'home-driver' })
    } else {
      throw new Error('Tipo de usuario desconocido')
    }

    close()
    email.value = ''
    password.value = ''

  } catch (err) {
    console.error(err)
    passwordError.value = 'Usuario o contraseña incorrectos.'
  }
}

function close() {
  emit('close')
}

function onKeydown(e) {
  if (e.key === 'Escape') {
    close()
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
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(50, 83, 61, 0.5);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1200;
}

.modal {
  width: 90%;
  max-width: 400px;
  padding: 0;
  border-radius: 16px;
  box-shadow:
    0 5px 15px rgba(0, 0, 0, 0.35),
    0 30px 60px -10px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  position: relative;
  text-align: center;
  background-color: transparent;
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

/* --- HEADER --- */
.modal-header {
  background-color: #4C7840;
  padding: 2.5rem 2rem 2rem 2rem;
  position: relative;
  color: #F7FFE6;
}

.modal-icon {
  width: 64px;
  height: auto;
  margin-bottom: 1rem;
  filter: brightness(0) invert(1);
  opacity: 0.9;
}

.modal-title {
  color: #F7FFE6;
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0;
}

.modal-subtitle {
  color: rgba(247, 255, 230, 0.7);
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: #F7FFE6;
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

/* --- BODY --- */
.modal-body {
  background-color: #F0F3E7;
  padding: 2rem;
}

.form-group {
  margin-bottom: 1.25rem;
  text-align: left;
}

.label {
  display: block;
  font-size: 0.85rem;
  color: #32533D;
  font-weight: 600;
  margin-bottom: 0.5rem;
  margin-left: 0.25rem;
}

input {
  width: 100%;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  border: 1px solid #c0cbb8;
  background-color: #FFFFFF;
  color: #32533D;
  font-size: 0.95rem;
  transition: all 0.2s;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #4C7840;
  box-shadow: 0 0 0 3px rgba(76, 120, 64, 0.15);
}

.input-error {
  border-color: #e74c3c;
}

.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 2.5rem;
}

.eye-btn {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #3E5C44;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
}

.error-msg {
  color: #c0392b;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1.5rem;
}

.forgot-link {
  color: #4C7840;
  font-size: 0.85rem;
  text-decoration: none;
  font-weight: 500;
}

.forgot-link:hover {
  text-decoration: underline;
  color: #32533D;
}

/* Botón de acción principal */
.login-btn {
  width: 100%;
  background-color: #32533D;
  color: #F7FFE6;
  padding: 0.85rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.1s, background-color 0.2s;
  box-shadow: 0 4px 6px rgba(50, 83, 61, 0.2);
}

.login-btn:hover {
  background-color: #26402e;
  transform: translateY(-1px);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
</style>
