<template>
  <div>
    <HomeAdminView />
  </div>

  <div class="container-worker">
    <h1 class="title">Gestión de Conductores</h1>

    <!-- Botón agregar -->
    <div class="actions-top">
      <button class="btn-add" @click="abrirModalNuevo">
        Agregar conductor
      </button>
    </div>

    <!-- MODAL EMERGENTE -->
    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2>{{ editando ? 'Editar Conductor' : 'Nuevo Conductor' }}</h2>

        <!-- Campos según DriverEntity -->
        <input v-model="nuevoConductor.name" placeholder="Nombre" />
        <input v-model="nuevoConductor.last_name" placeholder="Apellido" />
        <input v-model="nuevoConductor.email" placeholder="Email" type="email" />
        <input v-model="nuevoConductor.password" placeholder="Contraseña" type="password" />

        <div class="modal-buttons">
          <button class="btn-save" @click="editando ? actualizarConductor() : guardarConductor()">
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
    <div class="grid-row" v-for="driver in drivers" :key="driver.id">
      <div>{{ driver.id }}</div>
      <div>{{ driver.name }}</div>
      <div>{{ driver.last_name }}</div>
      <div>{{ driver.email }}</div>

      <div class="row-actions">
        <button class="btn-edit" @click="abrirModalEditar(driver)">Editar</button>
        <button class="btn-delete" @click="eliminarConductor(driver.id)">Eliminar</button>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import { onMounted, ref } from 'vue'
import HomeAdminView from '@/components/Admin/HeaderAdmin.vue'
import DriverServices from '@/services/driverservices'

export default {
  name: 'UsersView',
  components: {
    HomeAdminView
  },
  setup() {
    const mostrarModal = ref(false)
    const editando = ref(false)
    const drivers = ref([])
    const nuevoConductor = ref({
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

    // Obtener los conductores desde el backend
    const obtenerConductores = () => {
      DriverServices.getAllDrivers()
        .then(response => {
          drivers.value = response.data
        })
        .catch(error => {
          console.error('Error al obtener conductores:', error)
        })
    }

    onMounted(obtenerConductores)

    // Abrir modal para agregar un nuevo conductor
    const abrirModalNuevo = () => {
      mostrarModal.value = true
      editando.value = false
      nuevoConductor.value = {  // Resetea los valores del conductor
        name: '',
        last_name: '',
        email: '',
        password: ''
      }
    }

    // Abrir modal para editar un conductor
    const abrirModalEditar = (driver) => {
      mostrarModal.value = true
      editando.value = true  // Establece que estamos editando
      nuevoConductor.value = { ...driver }  // Carga los datos del conductor seleccionado
    }

    // Guardar un nuevo conductor
    const guardarConductor = () => {
      DriverServices.createDriver(nuevoConductor.value)
        .then(() => {
          obtenerConductores()
          cerrarModal()
        })
        .catch((error) => {
          console.error('Error al guardar conductor:', error)
          errorMsg.value = 'Error al guardar conductor. Inténtalo de nuevo.'
        })
        .finally(() => {
          saving.value = false
        })
    }

    // Actualizar un conductor existente
    const actualizarConductor = () => {
      saving.value = true
      DriverServices.updateDriver(nuevoConductor.value.id, nuevoConductor.value)
        .then(() => {
          obtenerConductores()
          cerrarModal()
        })
        .catch((error) => {
          console.error('Error al actualizar conductor:', error)
          errorMsg.value = 'Error al actualizar conductor. Inténtalo de nuevo.'
        })
        .finally(() => {
          saving.value = false
        })
    }

    // Eliminar conductor (con confirmación)
    const eliminarConductor = (id) => {
      if (!id) return
      const confirmMsg = '¿Confirma que desea eliminar al conductor #' + id + '? Esta acción no se puede deshacer.'
      if (!confirm(confirmMsg)) return

      DriverServices.deleteDriver(id)
        .then(() => {
          obtenerConductores()
          alert('Conductor eliminado correctamente')
        })
        .catch((error) => {
          console.error('Error al eliminar conductor:', error)
          alert('No se pudo eliminar el conductor. Revisa la consola para más detalles.')
        })
    }

    return {
      mostrarModal,
      editando,
      saving,
      errorMsg,
      drivers,
      nuevoConductor,
      cerrarModal,
      abrirModalNuevo,
      guardarConductor,
      actualizarConductor,
      eliminarConductor,
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
