<template>
  <div>
    <HomeAdminView/>
  </div>

  <div class="container-worker">
    <h1 class="title">Gestión de Administradores</h1>

    <!-- Botón agregar -->
    <div class="actions-top">
      <button class="btn-add" @click="abrirModalNuevo">
        Agregar administrador
      </button>
    </div>

    <!-- MODAL EMERGENTE -->
    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2>{{ editando ? 'Editar Administrador' : 'Nuevo Administrador' }}</h2>

        <!-- Campos según DriverEntity -->
        <input v-model="nuevoAdmin.name" placeholder="Nombre" />
        <input v-model="nuevoAdmin.last_name" placeholder="Apellido" />
        <input v-model="nuevoAdmin.email" placeholder="Email" type="email" />
        <input v-model="nuevoAdmin.password" placeholder="Contraseña" type="password" />

        <div class="modal-buttons">
          <button class="btn-save" @click="editando ? actualizarAdmin() : guardarAdmin()">
            {{ editando ? 'Actualizar' : 'Guardar' }}
          </button>
          <button class="btn-cancel" @click="cerrarModal">Cancelar</button>
        </div>
      </div>
    </div>

    <!-- TABLA -->
    <div class="grid-header">
      <div>ID</div>
      <div>Nombre</div>
      <div>Apellido</div>
      <div>Email</div>
      <div>Acciones</div>
    </div>

  <div class = "scrollable-table">
    <div class="grid-row" v-for="admin in admins" :key="admin.id">
      <div>{{ admin.id }}</div>
      <div>{{ admin.name }}</div>
      <div>{{ admin.last_name }}</div>
      <div>{{ admin.email }}</div>

      <div class="row-actions">
        <button class="btn-edit" @click="abrirModalEditar(admin)">Editar</button>
        <button class="btn-delete" @click="eliminarAdmin(admin.id)">Eliminar</button>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import { onMounted, ref } from 'vue'
import HomeAdminView from '@/components/Admin/HeaderAdmin.vue'
import AdminServices from '@/services/adminservices'

export default {
  name: 'AdminsView',
  components: {
    HomeAdminView
  },
  setup() {
    const mostrarModal = ref(false)
    const editando = ref(false)
    const admins = ref([])
    const nuevoAdmin = ref({
      name: '',
      last_name: '',
      email: '',
      password: ''
    })
    const saving = ref(false)
    const errorMsg = ref('')

    // Cerrar modal
    const cerrarModal = () => {
      mostrarModal.value = false
      errorMsg.value = ''
    }

    // Obtener los administradores desde el backend
    const obtenerAdmins = () => {
      AdminServices.getAllAdmins()
        .then(response => {
          admins.value = response.data
        })
        .catch(error => {
          console.error('Error al obtener administradores:', error)
        })
    }

    onMounted(obtenerAdmins)
    // Abrir modal para agregar un nuevo administrador
    const abrirModalNuevo = () => {
      mostrarModal.value = true
      editando.value = false
      nuevoAdmin.value = {  // Resetea los valores del administrador
        name: '',
        last_name: '',
        email: '',
        password: ''
      }
    }

    // Abrir modal para editar un administrador
    const abrirModalEditar = (admin) => {
      mostrarModal.value = true
      editando.value = true  // Establece que estamos editando
      nuevoAdmin.value = { ...admin }  // Carga los datos del administrador seleccionado
    }

    // Guardar un nuevo administrador
    const guardarAdmin = () => {
      AdminServices.createAdmin(nuevoAdmin.value)
        .then(() => {
          obtenerAdmins()
          cerrarModal()
        })
        .catch((error) => {
          console.error('Error al guardar administrador:', error)
          errorMsg.value = 'Error al guardar administrador. Inténtalo de nuevo.'
        })
        .finally(() => {
          saving.value = false
        })
    }

    // Actualizar un administrador existente
    const actualizarAdmin = () => {
      saving.value = true
      AdminServices.updateAdmin(nuevoAdmin.value.id, nuevoAdmin.value)
        .then(() => {
          obtenerAdmins()
          cerrarModal()
        })
        .catch((error) => {
          console.error('Error al actualizar administrador:', error)
          errorMsg.value = 'Error al actualizar administrador. Inténtalo de nuevo.'
        })
        .finally(() => {
          saving.value = false
        })
    }

    // Eliminar administrador (con confirmación)
    const eliminarAdmin = (id) => {
      if (!id) return
      const confirmMsg = '¿Confirma que desea eliminar al administrador #' + id + '? Esta acción no se puede deshacer.'
      if (!confirm(confirmMsg)) return

      AdminServices.deleteAdmin(id)
        .then(() => {
          obtenerAdmins()
          alert('Administrador eliminado correctamente')
        })
        .catch((error) => {
          console.error('Error al eliminar administrador:', error)
          alert('No se pudo eliminar el administrador. Revisa la consola para más detalles.')
        })
    }

    return {
      mostrarModal,
      editando,
      saving,
      errorMsg,
      admins,
      nuevoAdmin,
      cerrarModal,
      abrirModalNuevo,
      guardarAdmin,
      actualizarAdmin,
      eliminarAdmin,
      abrirModalEditar
    }
  }
}
</script>

<style scoped>
/* ========================== 
   CONTENEDOR PRINCIPAL
========================== */
.container-worker {
  background-color: #f9f9f9;
  padding: 25px;
  border-radius: 12px;
  width: 1000px;
  margin: 40px auto;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 25px;
  color: #4a4f37;
}

/* ========================== 
   BOTÓN AGREGAR
========================== */
.actions-top {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.btn-add {
  padding: 10px 18px;
  background: #4a4f37;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-add:hover {
  background: #393d2b;
}

/* ========================== 
   TABLA
========================== */
.grid-header {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  background: #52563f;
  color: white;
  padding: 12px;
  border-radius: 6px;
  font-weight: bold;
  text-align: center;
}

.grid-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  padding: 12px;
  align-items: center;
  border-bottom: 1px solid #ddd;
  text-align: center;
  background: white;
  color: #333;
}

.row-actions {
  display: flex;
  justify-content: center;
  gap: 6px;
}

.btn-edit {
  background: #ecd674;
  border: none;
  padding: 6px 10px;
  border-radius: 5px;
  cursor: pointer;
}

.btn-delete {
  background: #d1655d;
  border: none;
  padding: 6px 10px;
  border-radius: 5px;
  color: white;
  cursor: pointer;
}

/* ========================== 
   MODAL EMERGENTE
========================== */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.40);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal {
  background: white;
  padding: 25px;
  width: 380px;
  border-radius: 12px;
  box-shadow: 0px 8px 25px rgba(0, 0, 0, 0.3);
  animation: fadeInScale 0.25s ease-out;
}

.modal h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #4a4f37;
}

@keyframes fadeInScale {
  from { opacity: 0; transform: scale(0.85); }
  to   { opacity: 1; transform: scale(1); }
}

.modal input {
  width: 100%;
  padding: 10px;
  margin-bottom: 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.btn-save {
  background: #4a4f37;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
}

.btn-cancel {
  background: #d1655d;
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
}

.scrollable-table {
  max-height: 300px; /* Ajusta la altura máxima según lo que necesites */
  overflow-y: auto;  /* Muestra el scroll cuando la tabla sea más alta que el contenedor */
}
</style>
