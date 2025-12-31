<template>
  <div>
    <HeaderAdmin />
  </div>

  <div class="container-wrapper">
    <h1 class="title">Información de Contenedores</h1>

    <div class="actions-top">
      <button class="btn-add" @click="abrirModalNuevo">Agregar contenedor</button>
      <button class="btn-mass-update" @click="abrirModalMasivo">Actualización masiva de pesos</button>
    </div>

    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2>{{ editando ? 'Editar Contenedor' : 'Nuevo Contenedor' }}</h2>
        
        <div class="form-group">
          <label for="waste-type">Tipo de residuo</label>
          <select v-model="nuevoContenedor.id_waste" id="waste-type">
            <option value="" disabled>Seleccione un tipo de residuo</option>
            <option 
                v-for="waste in wasteTypes"
                :key="waste.id"
                :value="waste.id"
            >
                {{ waste.waste_type || waste.name || `ID ${waste.id}` }} 
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="coord-x">Coordenada X</label>
          <input v-model="nuevoContenedor.coord_x" type="number" id="coord-x" placeholder="Ej: -33.456" />
        </div>

        <div class="form-group">
          <label for="coord-y">Coordenada Y</label>
          <input v-model="nuevoContenedor.coord_y" type="number" id="coord-y" placeholder="Ej: -70.678" />
        </div>

        <div class="form-group">
          <label for="weight">Peso (kg)</label>
          <input v-model="nuevoContenedor.weight" type="number" id="weight" placeholder="Ej: 500" />
        </div>

        <div class="form-group">
          <label for="status">Estado del Contenedor</label>
          <select v-model="nuevoContenedor.status" id="status">
            <option value="" disabled>Seleccione estado</option>
            <option value="Disponible">Disponible</option>
            <option value="Lleno">Lleno</option>
            <option value="En mantenimiento">En mantenimiento</option>
          </select>
        </div>

        <div class="modal-buttons">
          <button class="btn-save" @click="editando ? actualizarContenedor() : guardarContenedor()">
            {{ editando ? 'Actualizar' : 'Guardar' }}
          </button>
          <button class="btn-cancel" @click="cerrarModal">Cancelar</button>
        </div>
      </div>
    </div>

    <div v-if="mostrarModalMasivo" class="modal-overlay" @click.self="cerrarModalMasivo">
      <div class="modal">
        <h2>Actualizar peso de contenedores</h2>

        <label>Seleccionar ruta:</label>
        <select v-model="selectedRoute" class="input">
          <option disabled value="">Selecciona una ruta</option>
          <option v-for="r in rutas" :key="r.id" :value="r.id">
            Ruta #{{ r.id }}
          </option>
        </select>

        <div v-if="contenedoresDeRuta.length > 0" class="contenedores-info">
          <p class="contenedores-title">Contenedores asociados:</p>
          <div class="contenedores-list">
            <span v-for="cid in contenedoresDeRuta" :key="cid" class="contenedor-chip">
              #{{ cid }}
            </span>
          </div>
        </div>

        <div v-else-if="selectedRoute">
          <p class="contenedores-empty">No hay contenedores asociados a esta ruta.</p>
        </div>


        <label>Nuevo peso (kg):</label>
        <input type="number" v-model="nuevoPeso" class="input" />

        <div class="modal-buttons">
          <button class="btn-save" @click="actualizarPesoMasivo">Actualizar</button>
          <button class="btn-cancel" @click="cerrarModalMasivo">Cancelar</button>
        </div>
      </div>
    </div>


    <div class="grid-header">
      <div>ID</div>
      <div>Tipo de Residuo</div> <div>Coord X</div>
      <div>Coord Y</div>
      <div>Peso</div>
      <div>Estado</div>
      <div>Acciones</div>
    </div>

    <div class="scrollable-table">
      <div
        v-for="contenedor in contenedores"
        :key="contenedor.id"
        class="grid-row"
      >
        <div>{{ contenedor.id }}</div>
        <div>{{ getWasteName(contenedor.id_waste) }}</div> 
        <div>{{ contenedor.coord_x }}</div>
        <div>{{ contenedor.coord_y }}</div>
        <div>{{ contenedor.weight }}</div>
        <div>{{ contenedor.status }}</div>
        <div class="row-actions">
          <button class="btn-edit" @click="abrirModalEditar(contenedor)">Editar</button>
          <button class="btn-delete" @click="eliminarContenedor(contenedor.id)">Eliminar</button>
        </div>
      </div>
    </div>
  </div>

  <div class="container-wrapper2">
    <h1 class="title">Contenedores sin recolección reciente</h1>

    <div class="grid-header-norec">
      <div>ID</div>
      <div>Coord X</div>
      <div>Coord Y</div>
      <div>Última Recolección</div> <div>Tipo de residuo</div>
    </div>

    <div class="scrollable-table">
      <div
        v-for="contenedorsinrecolectar in contenedoressinrecolectar"
        :key="contenedorsinrecolectar.id"
        class="grid-row-norec"
      >
        <div>{{ contenedorsinrecolectar.id_contenedor }}</div>
        <div>{{ contenedorsinrecolectar.coord_x }}</div>
        <div>{{ contenedorsinrecolectar.coord_y }}</div>
        <div>{{ contenedorsinrecolectar.ultima_recoleccion }}</div>
        <div>{{ contenedorsinrecolectar.tipo_residuo }}</div>
      </div>
    </div>
  </div>


  <div class="container-wrapper3">
    <h1 class="title">Contenedores con problemas</h1>

    <div class="grid-header-problematic">
      <div>ID Contenedor</div>
      <div>
        Conductores distintos
        <span class="subheader">(último año)</span>
      </div>
    </div>

    <div class="scrollable-table">
      <div
        v-for="contenedor in contenedoresconproblemas"
        :key="contenedor.id_container"
        class="grid-row-problematic"
      >
        <div>{{ contenedor.id_container }}</div>
        <div>{{ contenedor.drivercount }}</div>
      </div>
    </div>
  </div>

  <div class="container-wrapper4">
    <h1 class="title">Análisis de Densidad</h1>

    <div class="grid-header-density">
      <div>Mes</div>
      <div>Promedio Contenedor/Ruta</div>
      <div>Diferencia mes anterior</div>
    </div>

    <div class="scrollable-table">
      <div
        v-for="contenedordensidad in analisisdensidad"
        :key="contenedordensidad.month" 
        class="grid-row-density"
      >
        <div>{{ contenedordensidad.month }}</div>
        <div>{{ contenedordensidad.average_containers }}</div>

        <div :style="{ color: contenedordensidad.diff_vs_prev_month > 0 ? '#d1655d' : (contenedordensidad.diff_vs_prev_month < 0 ? 'green' : 'black') }">
          {{ contenedordensidad.diff_vs_prev_month !== null ? contenedordensidad.diff_vs_prev_month : '-' }}
          <span v-if="contenedordensidad.diff_vs_prev_month > 0">⬆</span>
          <span v-if="contenedordensidad.diff_vs_prev_month < 0">⬇</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import HeaderAdmin from '@/components/Admin/HeaderAdmin.vue'
import containerServices from '@/services/containerservices'
import routeServices from '@/services/routeservices'
import routeContainerServices from '@/services/route_containerservices'
import wasteService from '@/services/wasteservices'


export default {
  name: 'ContainerView',
  components: {
    HeaderAdmin
  },
  setup() {
     const mostrarModal = ref(false)
    const editando = ref(false)
    const mostrarModalMasivo = ref(false)

    const contenedores = ref([])
    const contenedoressinrecolectar = ref([])
    const contenedoresconproblemas = ref([])
    const contenedoresDeRuta= ref([])
    const analisisdensidad = ref([])
    const rutas = ref([])
    const wasteTypes = ref([]) // Lista de tipos de residuos
    // wasteactual ya no es necesario si usamos mapeo global, pero lo dejo como null
    const wasteactual = ref(null) 

    const nuevoContenedor = ref({
      id: null,
      id_waste: '',
      coord_x: '',
      coord_y: '',
      weight: '',
      status: ''
    })

    const selectedRoute = ref('')
    const nuevoPeso = ref('')

    // === FUNCIÓN AUXILIAR PARA MAPEO DE RESIDUOS ===
    const getWasteName = (idWaste) => {
        const waste = wasteTypes.value.find(w => w.id === idWaste);
        if (waste) {
            // Se asume que el nombre está en 'waste_type' o 'name'
            return waste.waste_type || waste.name || `ID ${idWaste}`;
        }
        return `ID ${idWaste} (Cargando...)`;
    };

    // === CARGAS INICIALES ===
    const obtenerContenedores = async () => {
        try {
            const response = await containerServices.getAllContainers();
            const data = response.data || response;
            contenedores.value = Array.isArray(data) ? data.sort((a, b) => a.id - b.id) : []; 
        } catch (error) {
            console.error("Error al obtener contenedores:", error);
            contenedores.value = [];
        }
    };


    const obtenerContenedoresConProblemas = async () => {
        try {
            const response = await containerServices.ContainerWithProblems();
            contenedoresconproblemas.value = response.data || response;
        } catch (error) {
            console.error("Error al obtener contenedores con problemas:", error)
            contenedoresconproblemas.value = []
        }
    }

    const obtenerContenedoresSinRecolectar = async () => {
        try {
            const response = await containerServices.ContainersWithoutCollection();
            contenedoressinrecolectar.value = response.data || response;
        } catch (error) {
            console.error("Error al obtener contenedores sin recolección:", error)
            contenedoressinrecolectar.value = []
        }
    }

      const obtenerAnalisisDensidad = async () => {
        try {
            const response = await containerServices.DensityAnalysisContainer();
            analisisdensidad.value = response.data || response;
        } catch (error) {
            console.error("Error al realizar el Análisis de densidad", error)
            analisisdensidad.value = []
        }
    }

    const cargarContenedoresDeRuta = async (routeId) => {
      if (!routeId) {
        contenedoresDeRuta.value = []
        return
      }

    try {
      const res = await routeContainerServices.getRouteContainersByRoute(routeId)
      const data = res.data || res;
      contenedoresDeRuta.value = Array.isArray(data) ? data.map(c => c.id_container) : [];
    } catch (error) {
      console.error("Error al obtener contenedores de la ruta:", error)
      contenedoresDeRuta.value = []
    }
  }


    const obtenerRutas = async () => {
      try {
        const res = await routeContainerServices.getAllRouteContainers()
        const data = res.data || res

        // Eliminar duplicados de rutas (id_route repetidos)
        const unique = []
        const seen = new Set()

        if (Array.isArray(data)) {
            for (const rel of data) {
                if (!seen.has(rel.id_route)) {
                    seen.add(rel.id_route)
                    unique.push({
                        id: rel.id_route
                    })
                }
            }
        }
      rutas.value = unique
      } catch (error) {
        console.error('Error al cargar rutas reales:', error)
        rutas.value = []
      }
    }

    // Obtener tipos de residuos (CORREGIDO: llama a getAllWastes)
    const obtenerTiposDeWaste = async () => {
    try {
        const response = await wasteService.getAllWastes(); // CORRECCIÓN AQUÍ
        
        wasteTypes.value = response.data || response;
    } catch (error) {
        console.error("Error al obtener tipos de residuos:", error);
        wasteTypes.value = [];
    }
};

    // Función que ya NO es necesaria gracias a getWasteName, pero se deja por consistencia
    // para cumplir con la petición, aunque no se usa en el template.
    const fetchWasteById = async (idwaste) => {
        try {
            if (!idwaste) return;
            const res = await wasteService.getWasteById(idwaste);
            wasteactual.value = res.data || res;
        } catch (e) {
            console.error(`Error al obtener el residuo con ID ${idwaste}:`, e);
            wasteactual.value = null;
        }
    }


    // === Hooks ===
    watch(selectedRoute, (newRouteId) => {
      cargarContenedoresDeRuta(newRouteId)
    })

    onMounted(() => {
      obtenerContenedores()
      obtenerContenedoresSinRecolectar()
      obtenerContenedoresConProblemas()
      obtenerAnalisisDensidad()
      obtenerRutas()
      obtenerTiposDeWaste()
    })

    // === MODAL NUEVO / EDITAR ===
    const abrirModalNuevo = () => {
      editando.value = false
      mostrarModal.value = true
      nuevoContenedor.value = {
        id: null,
        id_waste: '',
        coord_x: '',
        coord_y: '',
        weight: '',
        status: ''
      }
    }

    const abrirModalEditar = (contenedor) => {
      editando.value = true
      mostrarModal.value = true
      nuevoContenedor.value = { ...contenedor }
    }

    const guardarContenedor = async () => {
        try {
            await containerServices.createContainer(nuevoContenedor.value)
            obtenerContenedores()
            cerrarModal()
        } catch (error) {
            console.error("Error al crear contenedor:", error)
            alert("Error al crear contenedor. Revisa la consola.")
        }
    }

    const actualizarContenedor = async () => {
        try {
            await containerServices.updateContainer(nuevoContenedor.value.id, nuevoContenedor.value)
            obtenerContenedores()
            cerrarModal()
        } catch (error) {
            console.error("Error al actualizar contenedor:", error)
            alert("Error al actualizar contenedor. Revisa la consola.")
        }
    }

    const eliminarContenedor = async (id) => {
      if (confirm('¿Seguro que deseas eliminar este contenedor?')) {
        try {
            await containerServices.deleteContainer(id)
            obtenerContenedores()
        } catch (error) {
            console.error("Error al eliminar contenedor:", error)
            alert("Error al eliminar contenedor. Revisa la consola.")
        }
      }
    }

    const cerrarModal = () => {
      mostrarModal.value = false
    }

    const abrirModalMasivo = () => {
      mostrarModalMasivo.value = true
    }

    const cerrarModalMasivo = () => {
      mostrarModalMasivo.value = false
      selectedRoute.value = ''
      nuevoPeso.value = ''
      contenedoresDeRuta.value = [] // Limpiar al cerrar
    }

    const actualizarPesoMasivo = async () => {
      if (!selectedRoute.value || nuevoPeso.value === '') {
        alert('Debes seleccionar una ruta y un peso válido.')
        return
      }

      const peso = parseFloat(nuevoPeso.value)
      if (peso < 0 || isNaN(peso)) {
        alert('El peso debe ser un número positivo.')
        return
      }

      try {
        const res = await routeServices.updateContainerWeight(selectedRoute.value, peso)
        alert(res.data || 'Peso actualizado correctamente.')

        await obtenerContenedores()

        cerrarModalMasivo()
      } catch (error) {
        console.error('Error al actualizar peso masivo:', error)
        alert('Error al actualizar peso: ' + (error.response?.data?.message || error.message || 'Error desconocido'))
      }
    }


    return {
      mostrarModal,
      editando,
      contenedores,
      contenedoressinrecolectar,
      contenedoresconproblemas,
      analisisdensidad,
      nuevoContenedor,
      rutas,
      selectedRoute,
      nuevoPeso,
      mostrarModalMasivo,
      abrirModalMasivo,
      cerrarModalMasivo,
      actualizarPesoMasivo,
      abrirModalNuevo,
      abrirModalEditar,
      guardarContenedor,
      actualizarContenedor,
      eliminarContenedor,
      cerrarModal,
      contenedoresDeRuta,
      cargarContenedoresDeRuta,
      wasteTypes,
      wasteactual,
      fetchWasteById,
      getWasteName // Expone la función de mapeo al template
    }
  }
}
</script>


<style scoped>
.container-wrapper {
  background-color: #f9f9f9;
  padding: 25px;
  border-radius: 12px;
  width: 1000px;
  margin: 40px auto;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.container-wrapper2 {
  background-color: #f9f9f9;
  padding: 25px;
  border-radius: 12px;
  width: 1000px;
  margin: 40px auto;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.container-wrapper3 {
  background-color: #f9f9f9;
  padding: 25px;
  border-radius: 12px;
  width: 1000px;
  margin: 40px auto;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.container-wrapper4 {
  background-color: #f9f9f9;
  padding: 25px;
  border-radius: 12px;
  width: 1000px;
  margin: 40px auto;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.title {
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 25px;
  color: #4a4f37;
}

.actions-top {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 20px;
}

.btn-add {
  padding: 10px 18px;
  background: #4a4f37;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 500;
}

.btn-add:hover {
  background: #3c4030;
}

/* Tabla */
.grid-header, .grid-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  padding: 12px;
  text-align: center;
  align-items: center;
}

.grid-header {
  background: #52563f;
  color: rgb(255, 255, 255);
  border-radius: 6px;
  font-weight: bold;
}

.grid-row {
  background: white;
  border-bottom: 1px solid #000000;
  color: #000000;
}

/* === Tabla: Contenedores sin recolección === */
.grid-header-norec,
.grid-row-norec {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 5 columnas iguales */
  padding: 12px;
  text-align: center;
  align-items: center;
  width: 100%;
}

.grid-header-norec {
  background: #52563f;
  color: white;
  border-radius: 6px;
  font-weight: bold;
}

.grid-row-norec {
  background: white;
  border-bottom: 1px solid #000000;
  color: #000000;
}

.grid-row-norec div {
  padding: 8px 0;
}

/* Encabezado y filas: contenedores problemáticos */
.grid-header-problematic, .grid-row-problematic {
  display: grid;
  grid-template-columns: 1fr 1fr; /* Dos columnas iguales */
  padding: 12px;
  text-align: center;
  align-items: center;
}

.grid-header-problematic {
  background: #52563f;
  color: white;
  border-radius: 6px;
  font-weight: bold;
}

.grid-row-problematic {
  background: white;
  border-bottom: 1px solid #000000;
  color: #000000;
}

.subheader {
  font-size: 13px;
  font-weight: normal;
  display: block;
  color: #dcdcdc;
}



/*Tabla 3 columnas*/
.grid-header-density, .grid-row-density {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr; /* 3 columnas de igual tamaño */
  padding: 12px;
  text-align: center;
  align-items: center;
}

.grid-header-density {
  background: #52563f;
  color: rgb(255, 255, 255);
  border-radius: 6px;
  font-weight: bold;
}

.grid-row-density {
  background: white;
  border-bottom: 1px solid #000000;
  color: #000000;
}
/*Fin tabla 3 columnas*/

.row-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.btn-edit {
  background: #ecd674;
  border: none;
  padding: 6px 10px;
  border-radius: 25px;
  cursor: pointer;
}

.btn-delete {
  background: #d1655d;
  border: none;
  padding: 6px 10px;
  border-radius: 25px;
  color: white;
  cursor: pointer;
}

/* === MODAL MODERNO === */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(2px);
}

.modal {
  background: #fff;
  padding: 30px 35px;
  width: 420px;
  border-radius: 16px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
  text-align: center;
  animation: fadeIn 0.3s ease;
}

.modal h2 {
  font-size: 22px;
  font-weight: 600;
  color: #4a4f37;
  margin-bottom: 20px;
  border-bottom: 2px solid #e2e6d5;
  padding-bottom: 8px;
}

.modal input,
.modal select {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border-radius: 10px;
  border: 1px solid #ccc;
  background: #fafafa;
  font-size: 14px;
  transition: all 0.2s ease;
}

.modal input:focus,
.modal select:focus {
  outline: none;
  border-color: #4a4f37;
  background: #fff;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.btn-save,
.btn-cancel {
  padding: 10px 22px;
  border: none;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease;
}

.btn-save {
  background: #4a4f37;
  color: #fff;
}
.btn-save:hover {
  background: #3a3e2d;
}

.btn-cancel {
  background: #d1655d;
  color: #fff;
}
.btn-cancel:hover {
  background: #b84e47;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.scrollable-table {
  max-height: 300px; /* Ajusta la altura máxima según lo que necesites */
  overflow-y: auto;  /* Muestra el scroll cuando la tabla sea más alta que el contenedor */
}

.btn-mass-update {
  background-color: #4a4f37;
  color: white;
  padding: 10px 18px;
  border-radius: 25px;
  border: none;
  cursor: pointer;
  font-weight: 500;
}
.btn-mass-update:hover {
  background-color: #3c4030;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}
.modal {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 420px;
  color: #333;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.contenedores-info {
  background: #f7f8f3;
  border: 1px solid #cfd3c1;
  border-radius: 10px;
  padding: 12px;
  margin: 10px 0 15px;
  text-align: left;
}

.contenedores-title {
  font-weight: 600;
  color: #4a4f37;
  margin-bottom: 8px;
  font-size: 15px;
}

.contenedores-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.contenedor-chip {
  background-color: #e2e6d5;
  color: #4a4f37;
  padding: 6px 10px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: all 0.2s ease;
}

.contenedor-chip:hover {
  background-color: #d5dac4;
  transform: scale(1.05);
}

.contenedores-empty {
  color: #777;
  font-style: italic;
  margin-top: 8px;
}



</style>
