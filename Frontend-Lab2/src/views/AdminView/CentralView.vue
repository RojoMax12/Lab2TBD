<template>
  <div>
    <HomeAdminView/>
  </div>

  <div class="container-worker">
    <h1 class="title">Gestión de Centrales </h1>

    <!-- Botón agregar -->
    <div class="actions-top">
      <button class="btn-add" @click="abrirModalNuevo">
        Agregar Central
      </button>
    </div>

    <!-- MODAL EMERGENTE -->
    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2>{{ editando ? 'Editar Central' : 'Nueva Central' }}</h2>

        <!-- Campos según DriverEntity -->
        <input v-model="nuevaCentral.name" placeholder="Nombre" />
        <input v-model="nuevaCentral.coord_x" placeholder="Coordenada X" />
        <input v-model="nuevaCentral.coord_y" placeholder="Coordenada Y" />

        <div class="modal-buttons">
          <button class="btn-save" @click="editando ? actualizarCentral() : guardarCentral()">
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
      <div>Coordenada X</div>
      <div>Coordenada Y</div>
      <div>Acciones</div>
    </div>

  <div class = "scrollable-table">
    <div class="grid-row" v-for="central in centrals" :key="central.id">
      <div>{{ central.id }}</div>
      <div>{{ central.name }}</div>
      <div>{{ central.coord_x }}</div>
      <div>{{ central.coord_y }}</div>
      <div class="row-actions">
        <button class="btn-edit" @click="abrirModalEditar(central)">Editar</button>
        <button class="btn-delete" @click="eliminarCentral(central.id)">Eliminar</button>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import { onMounted, ref } from 'vue'
import HomeAdminView from '@/components/Admin/HeaderAdmin.vue'
import CentralServices from '@/services/centralservices'

export default {
  name: 'CentralsView',
  components: {
    HomeAdminView
  },
  setup() {
    const mostrarModal = ref(false)
    const editando = ref(false)
    const centrals = ref([])
    const nuevaCentral = ref({
      name: '',
      coord_x: '',
      coord_y: ''
    })
    const saving = ref(false)
    const errorMsg = ref('')

    // Cerrar modal
    const cerrarModal = () => {
      mostrarModal.value = false
      errorMsg.value = ''
    }

    // Obtener las centrales desde el backend
    const obtenerCentrals = () => {
      CentralServices.getAllCentrals()
        .then(response => {
          centrals.value = response.data
        })
        .catch(error => {
          console.error('Error al obtener centrales:', error)
        })
    }

    onMounted(obtenerCentrals)
    // Abrir modal para agregar un nuevo administrador
    const abrirModalNuevo = () => {
      mostrarModal.value = true
      editando.value = false
      nuevaCentral.value = {  // Resetea los valores del administrador
        name: '',
        coord_x: '',
        coord_y: ''
      }
    }

    // Abrir modal para editar un administrador
    const abrirModalEditar = (central) => {
      mostrarModal.value = true
      editando.value = true  // Establece que estamos editando
      nuevaCentral.value = { ...central }  // Carga los datos del administrador seleccionado
    }

    // Guardar un nuevo administrador
    const guardarCentral = () => {
      CentralServices.createCentral(nuevaCentral.value)
        .then(() => {
          obtenerCentrals()
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
    const actualizarCentral = () => {
      saving.value = true
      CentralServices.updateCentral(nuevaCentral.value.id, nuevaCentral.value)
        .then(() => {
          obtenerCentrals()
          cerrarModal()
        })
        .catch((error) => {
          console.error('Error al actualizar central:', error)
          errorMsg.value = 'Error al actualizar central. Inténtalo de nuevo.'
        })
        .finally(() => {
          saving.value = false
        })
    }

    // Eliminar central
    const eliminarCentral = (id) => {
      CentralServices.deleteCentral(id)
        .then(() => {
          obtenerCentrals()
        })
        .catch((error) => {
          console.error('Error al eliminar central:', error)
        })
    }

    return {
      mostrarModal,
      editando,
      saving,
      errorMsg,
      centrals,
      nuevaCentral,
      cerrarModal,
      abrirModalNuevo,
      guardarCentral,
      actualizarCentral,
      eliminarCentral,
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
