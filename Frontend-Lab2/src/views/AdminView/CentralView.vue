<template>
  <div>
    <HomeAdminView/>
  </div>

  <div class="container-worker">
    <h1 class="title">Gestión de Centrales</h1>

    <!-- Botón agregar -->
    <div class="actions-top">
      <button class="btn-add" @click="abrirModalNuevo">
        Agregar Central
      </button>
    </div>

    <!-- MODAL -->
    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2>{{ editando ? 'Editar Central' : 'Nueva Central' }}</h2>

        <input v-model="nuevaCentral.name" placeholder="Nombre" />
        <input
          v-model="nuevaCentral.location"
          placeholder="Ubicación (WKT)"
        />

        <small class="hint">
          Ejemplo: POINT(-70.678 -33.456)
        </small>

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
      <div>Ubicación</div>
      <div>Acciones</div>
    </div>

    <div class="scrollable-table">
      <div class="grid-row" v-for="central in centrals" :key="central.id">
        <div>{{ central.id }}</div>
        <div>{{ central.name }}</div>
        <div v-html="formatLocation(central.location)"></div>
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
  components: { HomeAdminView },

  setup() {
    const mostrarModal = ref(false)
    const editando = ref(false)
    const centrals = ref([])

    const nuevaCentral = ref({
      id: null,
      name: '',
      location: ''
    })

    const cerrarModal = () => {
      mostrarModal.value = false
    }

    const obtenerCentrals = () => {
      CentralServices.getAllCentrals()
        .then(res => {
          centrals.value = res.data || res
        })
        .catch(err => {
          console.error('Error al obtener centrales:', err)
        })
    }

    onMounted(obtenerCentrals)

    const abrirModalNuevo = () => {
      editando.value = false
      mostrarModal.value = true
      nuevaCentral.value = {
        id: null,
        name: '',
        location: ''
      }
    }

    const abrirModalEditar = (central) => {
      editando.value = true
      mostrarModal.value = true
      nuevaCentral.value = { ...central }
    }

    const guardarCentral = () => {
      CentralServices.createCentral(nuevaCentral.value)
        .then(() => {
          obtenerCentrals()
          cerrarModal()
        })
        .catch(err => console.error('Error al crear central:', err))
    }

    const actualizarCentral = () => {
      CentralServices.updateCentral(
        nuevaCentral.value.id,
        nuevaCentral.value
      )
        .then(() => {
          obtenerCentrals()
          cerrarModal()
        })
        .catch(err => console.error('Error al actualizar central:', err))
    }

    const eliminarCentral = (id) => {
      if (!confirm('¿Eliminar esta central?')) return

      CentralServices.deleteCentral(id)
        .then(obtenerCentrals)
        .catch(err => console.error('Error al eliminar central:', err))
    }

    // Función para dar formato a coordenadas
    const formatLocation = (wkt) => {
      if (!wkt) return "Sin coordenadas";
      
      // Usamos una expresión regular para extraer los números
      const match = wkt.match(/\(([^)]+)\)/);
      if (match) {
        const [lon, lat] = match[1].split(' ');
        return `X= ${lon} <br> Y= ${lat}`;
      }
      return wkt;
    };

    return {
      mostrarModal,
      editando,
      centrals,
      nuevaCentral,
      abrirModalNuevo,
      abrirModalEditar,
      guardarCentral,
      actualizarCentral,
      eliminarCentral,
      cerrarModal,
      formatLocation
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
  color: #3E5C44;
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
  background: #4C7840;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.btn-add:hover {
  background: #3E5C44;
}

/* ========================== 
   TABLA
========================== */
.grid-header {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: #3E5C44;
  color: white;
  padding: 12px;
  border-radius: 6px;
  font-weight: bold;
  text-align: center;
}

.grid-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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
  color: #3E5C44;
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
