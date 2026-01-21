<template>
  <HeaderAdmin />

  <div class="admin-container">
    <h1 class="page-title">Contenedores Detectados Fuera de Zona</h1>

    <div class="admin-card">
      <div class="card-header-actions">
        <button class="btn-custom" @click="cargarDatos">
          <HuRefresh class="refresh-icon" /> Refrescar Análisis
        </button>
      </div>

      <div class="table-responsive">
        <table class="custom-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Coordenadas</th>
              <th>Ubicación</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>

          <tbody v-if="!cargando && contenedores.length > 0">
            <tr v-for="cont in contenedores" :key="cont.id">
              <td class="fw-bold">#{{ cont.id }}</td>
              
              <td class="coords-cell">
                <div><span class="fw-bold">Lat:</span> {{ cont.lat }}</div>
                <div><span class="fw-bold">Lng:</span> {{ cont.lng }}</div>
              </td>

              <td class="address-cell">
                <div v-if="cont.calle_cercana">
                  <i class="bi bi-signpost-2 text-success me-1"></i> 
                  <strong>{{ cont.calle_cercana }}</strong>
                  <br>
                  <small class="text-muted ms-3">
                    <i class="bi bi-building"></i> {{ cont.nombre_comuna }}
                  </small>
                </div>
                <span v-else class="text-muted small fst-italic">
                  Sin referencia geográfica
                </span>
              </td>

              <td>
                <span class="badge-error">Fuera de Zona</span>
              </td>

              <td>
                <button class="btn-icon" @click="abrirMapa(cont)" title="Ver en Mapa">
                   Ver Ubicación <FaMapLocationDot class="map-icon" />
                </button>
              </td>
            </tr>
          </tbody>

          <tbody v-if="cargando">
            <tr>
              <td colspan="5" class="text-center py-4">
                <div class="spinner-border text-success" role="status"></div>
                <p class="mt-2 text-muted">Consultando tablas espaciales...</p>
              </td>
            </tr>
          </tbody>

          <tbody v-if="!cargando && contenedores.length === 0">
            <tr>
              <td colspan="5" class="text-center py-4 text-success">
                <strong>¡Excelente!</strong> No se detectaron contenedores fuera de su zona asignada.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="mostrarModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Ubicación #{{ selectedContainer?.id }}</h3>
          <button class="close-btn" @click="cerrarModal">×</button>
        </div>

        <div class="modal-body">
          <div id="mapModal" style="height: 400px; width: 100%; border-radius: 8px;"></div>
          
          <div class="mt-3 text-center">
             <p class="mb-1 text-danger fw-bold">
               <i class="bi bi-exclamation-triangle-fill"></i> Fuera de polígono asignado
             </p>
             <p class="text-muted border p-2 rounded bg-light d-inline-block">
               Ref: {{ selectedContainer?.calle_cercana }}, {{ selectedContainer?.nombre_comuna }}
             </p>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import ContainerService from '../../services/containerservices.js';
import { FaMapLocationDot, HuRefresh } from '@kalimahapps/vue-icons';
import HeaderAdmin from '@/components/Admin/HeaderAdmin.vue';

// --- ESTADO ---
const contenedores = ref([]);
const cargando = ref(false);
const mostrarModal = ref(false);
const selectedContainer = ref(null);
let map = null;

// --- PARSEO WKT (Extraer Lat/Lng del string POINT) ---
const parseWKT = (wkt) => {
  if (!wkt) return null;
  const match = wkt.match(/POINT\s*\(([^ ]+)\s+([^ ]+)\)/);
  if (match) {
    return {
      lng: parseFloat(match[1]), 
      lat: parseFloat(match[2])
    };
  }
  return null;
};

// --- CARGAR DATOS (Directo del Backend) ---
const cargarDatos = async () => {
  cargando.value = true;
  try {
    const res = await ContainerService.getContainersOutsideZone();
    
    // Mapeamos la respuesta. 
    // Como tu backend YA cruzó los datos con 'calles' y 'comunas', 
    // aquí solo los mostramos. Cero procesamiento extra.
    contenedores.value = res.data.map(c => {
      const coords = parseWKT(c.location);
      return {
        ...c,
        lat: coords ? coords.lat.toFixed(5) : 'N/A',
        lng: coords ? coords.lng.toFixed(5) : 'N/A',
        latRaw: coords ? coords.lat : null,
        lngRaw: coords ? coords.lng : null,
        // Estos campos vienen de tu nueva Query SQL:
        calle_cercana: c.calle_cercana, 
        nombre_comuna: c.nombre_comuna
      };
    });

  } catch (e) {
    console.error("Error cargando contenedores:", e);
  } finally {
    cargando.value = false;
  }
};

// --- LÓGICA DEL MAPA ---
const abrirMapa = async (contenedor) => {
  selectedContainer.value = contenedor;
  mostrarModal.value = true;
  await nextTick(); // Esperar a que el modal se renderice
  initMap(contenedor);
};

const cerrarModal = () => {
  mostrarModal.value = false;
  if (map) {
    map.remove(); // Limpiar instancia de Leaflet
    map = null;
  }
};

const initMap = (contenedor) => {
  if (!contenedor.latRaw || !contenedor.lngRaw) return;
  const coords = [contenedor.latRaw, contenedor.lngRaw];

  map = L.map('mapModal').setView(coords, 16);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
  }).addTo(map);

  const redIcon = L.icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  L.marker(coords, { icon: redIcon })
    .addTo(map)
    .bindPopup(`
      <div class="text-center">
        <b>${contenedor.calle_cercana}</b><br>
        <span class="text-muted">${contenedor.nombre_comuna}</span>
      </div>
    `)
    .openPopup();
};

onMounted(() => {
  cargarDatos();
});
</script>

<style scoped>
/* === LAYOUT GENERAL === */
.admin-container {
  min-height: 100vh;
  background-color: #F0F3E7;
  padding: 40px;
  padding-top: 40px;
}

.page-title {
  text-align: center;
  color: #4C7840;
  font-weight: bold;
  font-size: 2rem;
  margin-bottom: 30px;
}

/* === TARJETA === */
.admin-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
  max-width: 1100px;
  margin: 0 auto;
}

.card-header-actions {
  display: flex;
  margin-bottom: 25px;
}

/* === BOTONES === */
.btn-custom {
  background-color: #4C7840;
  color: white;
  border: none;
  padding: 10px 25px;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-custom:hover {
  background-color: #3E5C44;
}

.btn-icon {
  background: #F0F3E7;
  color: #4C7840;
  border: 1px solid #4C7840;
  padding: 6px 15px;
  border-radius: 15px;
  font-size: 0.9rem;
  cursor: pointer;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.btn-icon:hover {
  background: #4C7840;
  color: white;
}

/* === TABLA === */
.custom-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.custom-table th {
  background-color: #3E5C44;
  color: white;
  padding: 15px;
  text-align: center;
  font-weight: 500;
}

.custom-table th:first-child { border-top-left-radius: 10px; }
.custom-table th:last-child { border-top-right-radius: 10px; }

.custom-table td {
  padding: 15px;
  border-bottom: 1px solid #eee;
  color: #333;
  vertical-align: middle;
}

.custom-table tr:hover td {
  background-color: #fcfcfc;
}

/* Celdas especiales */
.coords-cell {
  font-family: monospace;
  font-size: 0.85rem;
  color: #666;
  white-space: nowrap;
}

.address-cell {
  min-width: 200px;
  font-size: 0.95rem;
  line-height: 1.4;
}

/* === MODAL === */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 25px;
  border-radius: 15px;
  width: 90%;
  max-width: 600px;
  position: relative;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #4C7840;
  font-weight: bold;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #999;
  line-height: 1;
}

/* Badge de Estado */
.badge-error {
  background-color: #d1655d;
  color: white;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}
</style>