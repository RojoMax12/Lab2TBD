<template>
  <div>
    <HomeAdminView />

    <div class="view-wrapper">
      <h1 class="title">Zonas de Recolección</h1>

      <div class="container-routes">
        <div class="map-box">
          <div id="map-zones"></div>
        </div>

        <div class="funcionalidades">
          <h2>Funcionalidades</h2>

          <div class="func-panel">
            <form @submit.prevent="isEditing ? updateZone() : createZone()" class="zone-form">
              <label>Nombre</label>
              <input v-model="zoneForm.name" class="input" placeholder="Nombre de la zona" />

              <div class="draw-controls">
                <button type="button" class="btn-small" @click="startDrawing" v-if="!drawing">
                  <LuSquareDashedMousePointer class="icon-spacing" /> Dibujar zona
                </button>
                <button
                  type="button"
                  class="btn-small action-confirm"
                  @click="finishDrawing"
                  v-if="drawing"
                >
                  Finalizar
                </button>
                <button
                  type="button"
                  class="btn-small danger"
                  @click="cancelDrawing"
                  v-if="drawing"
                >
                  Cancelar
                </button>
              </div>
              <small v-if="drawing" class="draw-hint">Haz clic en el mapa para trazar puntos</small>

              <label>WKT (POLYGON)</label>
              <textarea
                v-model="zoneForm.location"
                rows="3"
                class="input"
                placeholder="POLYGON((...))"
                readonly
              ></textarea>
              <small style="color: #888; font-size: 0.75em; margin-bottom: 10px"
                >Se llena automáticamente al dibujar</small
              >

              <div class="form-actions">
                <button class="btn-save" type="submit">
                  <FlAddSquareMultiple class="icon-spacing" />
                  {{ isEditing ? 'Guardar cambios' : 'Crear zona' }}
                </button>
                <button class="btn-cancel" type="button" @click="resetForm">
                  <CaClean class="icon-spacing" /> Limpiar datos
                </button>
              </div>
            </form>

            <hr class="separator" />

            <div class="zone-list">
              <h3>Zonas existentes</h3>
              <div
                v-if="zones.length === 0"
                style="text-align: center; color: #999; margin-top: 10px"
              >
                No hay zonas
              </div>
              <ul>
                <li v-for="z in zones" :key="z.id" class="zone-item">
                  <div class="zone-meta">
                    <strong>{{ z.name }}</strong>
                    <div class="zone-actions">
                      <button @click="editZone(z)" class="btn-icon" title="Editar">
                        <BxSolidEditAlt />
                      </button>
                      <button @click="deleteZone(z.id)" class="btn-icon danger" title="Eliminar">
                        <McDelete2Fill />
                      </button>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import HomeAdminView from '@/components/Admin/HeaderAdmin.vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import collectionZoneServices from '@/services/collectionzoneservices'
import containerServices from '@/services/containerservices'
import wasteServices from '@/services/wasteservices'
import {
  LuSquareDashedMousePointer,
  BxSolidEditAlt,
  McDelete2Fill,
  CaClean,
  FlAddSquareMultiple,
} from '@kalimahapps/vue-icons'

const zones = ref([])
const containers = ref([])
let map = null
let zonesLayer = null
let containersLayer = null
let drawLayer = null
const wastes = ref([])
const wasteMap = {}

// Drawing state
const drawing = ref(false)
let drawPoints = [] // array of [lng, lat]

// Form state for CRUD
const zoneForm = ref({ name: '', location: '' })
const isEditing = ref(false)
const editingId = ref(null)

function resetForm() {
  zoneForm.value = { name: '', location: '' }
  isEditing.value = false
  editingId.value = null
}

async function fetchZones() {
  try {
    const res = await collectionZoneServices.getAllCollectionZones()
    zones.value = res.data || res
    renderZones()
  } catch (e) {
    console.error('Error al obtener zonas', e)
  }
}

function editZone(z) {
  isEditing.value = true
  editingId.value = z.id
  zoneForm.value.name = z.name
  zoneForm.value.location = z.location
  const coords = parseWKTPolygon(z.location)
  if (coords.length && map) {
    const poly = L.polygon(coords)
    map.fitBounds(poly.getBounds().pad(0.2))
  }
}

async function createZone() {
  if (!zoneForm.value.name || !zoneForm.value.location) {
    alert('Complete nombre y WKT de la zona')
    return
  }
  try {
    await collectionZoneServices.createCollectionZone(zoneForm.value)
    resetForm()
    await fetchZones()
    alert('Zona creada')
  } catch (e) {
    console.error('Error creando zona', e)
    alert('Error al crear zona')
  }
}

async function updateZone() {
  if (!editingId.value) return
  try {
    await collectionZoneServices.updateCollectionZone(editingId.value, zoneForm.value)
    resetForm()
    await fetchZones()
    alert('Zona actualizada')
  } catch (e) {
    console.error('Error actualizando zona', e)
    alert('Error al actualizar zona')
  }
}

async function deleteZone(id) {
  if (!confirm('¿Eliminar esta zona de recolección?')) return
  try {
    await collectionZoneServices.deleteCollectionZone(id)
    await fetchZones()
    try {
      drawLayer.clearLayers()
      renderZones()
    } catch (e) {
    }
    alert('Zona eliminada')
  } catch (e) {
    console.error('Error eliminando zona', e)
    alert('Error al eliminar zona')
  }
}

function parseWKTPolygon(wkt) {
  if (!wkt || !wkt.startsWith('POLYGON')) return []
  const inner = wkt.replace('POLYGON((', '').replace('))', '')
  const pairs = inner.split(',')
  return pairs.map((pair) => {
    const [lng, lat] = pair.trim().split(' ').map(Number)
    return [lat, lng]
  })
}

function parseWKTPoint(wkt) {
  if (!wkt || !wkt.startsWith('POINT')) return null
  const coords = wkt.replace('POINT(', '').replace(')', '').split(' ')
  const lng = parseFloat(coords[0])
  const lat = parseFloat(coords[1])
  if (Number.isNaN(lat) || Number.isNaN(lng)) return null
  return [lat, lng]
}

function initMap() {
  map = L.map('map-zones').setView([-33.45, -70.66], 12)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap',
  }).addTo(map)
  zonesLayer = L.layerGroup().addTo(map)
  containersLayer = L.layerGroup().addTo(map)
  drawLayer = L.layerGroup().addTo(map)
}

function renderZones() {
  if (!map) return
  zonesLayer.clearLayers()
  containersLayer.clearLayers()

  const allBounds = L.latLngBounds()

  zones.value.forEach((z) => {
    const coords = parseWKTPolygon(z.location)
    if (coords.length) {
      const polygon = L.polygon(coords, { color: '#4e5336', weight: 2, fillOpacity: 0.2 }).addTo(
        zonesLayer,
      )
      polygon.bindPopup(`<strong>${z.name}</strong>`)
      allBounds.extend(polygon.getBounds())
    }
  })

  containers.value.forEach((c) => {
    const latlng = parseWKTPoint(c.location)
    if (latlng) {
      const marker = L.circleMarker(latlng, {
        radius: 6,
        color: '#d35400',
        fillColor: '#f39c12',
        fillOpacity: 0.9,
      }).addTo(containersLayer)
      const weight =
        c.weight !== undefined && c.weight !== null ? Number(c.weight).toFixed(1) + ' kg' : 'N/A'
      const wasteName = c.id_waste ? wasteMap[c.id_waste] || `ID ${c.id_waste}` : 'N/A'
      const popupHtml = `<strong>Contenedor ${c.id}</strong><br/>Estado: ${c.status || 'N/A'}<br/>Peso: ${weight}<br/>Tipo residuo: ${wasteName}`
      marker.bindPopup(popupHtml)
      allBounds.extend(marker.getLatLng())
    }
  })

  if (allBounds.isValid()) {
    map.fitBounds(allBounds.pad(0.1))
  }
}

function onMapClickForDraw(e) {
  if (!drawing.value) return
  const lat = e.latlng.lat
  const lng = e.latlng.lng
  drawPoints.push([lng, lat])
  drawLayer.clearLayers()
  drawPoints.forEach((p) => {
    L.circleMarker([p[1], p[0]], { radius: 4, color: '#2c3e50' }).addTo(drawLayer)
  })
  if (drawPoints.length === 1) return
  const latlngs = drawPoints.map((p) => [p[1], p[0]])
  if (drawPoints.length < 3) {
    L.polyline(latlngs, { color: '#2c3e50', dashArray: '4' }).addTo(drawLayer)
  } else {
    L.polygon(latlngs, { color: '#2c3e50', fillOpacity: 0.1 }).addTo(drawLayer)
  }
}

function startDrawing() {
  drawing.value = true
  drawPoints = []
  drawLayer.clearLayers()
  map.getContainer().style.cursor = 'crosshair'
  map.on('click', onMapClickForDraw)
}

function cancelDrawing() {
  drawing.value = false
  drawPoints = []
  drawLayer.clearLayers()
  map.getContainer().style.cursor = ''
  map.off('click', onMapClickForDraw)
  try {
    renderZones()
  } catch (e) {}
}

function buildWKTFromDrawPoints(points) {
  if (!points || points.length < 3) return null
  const pts = points.slice()
  const first = pts[0]
  const last = pts[pts.length - 1]
  if (first[0] !== last[0] || first[1] !== last[1]) pts.push([first[0], first[1]])
  const inner = pts.map((p) => `${p[0]} ${p[1]}`).join(', ')
  return `POLYGON((${inner}))`
}

function finishDrawing() {
  if (!drawing.value) return
  if (drawPoints.length < 3) {
    alert('Se requieren al menos 3 puntos')
    return
  }
  const wkt = buildWKTFromDrawPoints(drawPoints)
  if (!wkt) {
    alert('Error generando polígono')
    return
  }
  zoneForm.value.location = wkt
  drawing.value = false
  map.getContainer().style.cursor = ''
  map.off('click', onMapClickForDraw)
  const latlngs = drawPoints.map((p) => [p[1], p[0]])
  const poly = L.polygon(latlngs)
  map.fitBounds(poly.getBounds().pad(0.2))
}

onMounted(async () => {
  await nextTick()
  initMap()
  if (map) {
    map.invalidateSize()
  }
  try {
    const [zonesRes, containersRes] = await Promise.all([
      collectionZoneServices.getAllCollectionZones(),
      containerServices.getAllContainers(),
    ])
    zones.value = zonesRes.data || zonesRes
    containers.value = containersRes.data || containersRes
    try {
      const wastesRes = await wasteServices.getAllWastes()
      wastes.value = wastesRes.data || wastesRes
      wastes.value.forEach((w) => {
        wasteMap[w.id] = w.waste_type || w.wasteType || w.type || null
      })
    } catch (we) {
      console.warn('No se pudieron obtener tipos de residuo', we)
    }
    renderZones()
    await fetchZones()
  } catch (e) {
    console.error('Error cargando zonas', e)
  }
})
</script>

<style scoped>
.view-wrapper {
  background-color: #f0f3e7;
  padding-bottom: 2rem;
  min-height: calc(100vh - 80px);
}

.title {
  text-align: center;
  padding-top: 1.5rem;
  margin-bottom: 1.5rem;
  color: #4e5336;
  font-size: 2.2rem;
  border-bottom: 2px solid #3e5c44;
  padding-bottom: 0.5rem;
  margin-right: 2.5%;
}

/* --- LAYOUT PRINCIPAL (FLEXBOX) --- */
.container-routes {
  display: flex;
  gap: 20px;
  width: 90%;
  max-width: 1400px;
  margin: 0 auto;
  align-items: flex-start;
  height: 750px;
}

/* LADO IZQUIERDO: MAPA */
.map-box {
  flex: 1;
  height: 100%;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #ddd;
  padding: 0;
  overflow: hidden;
}

#map-zones {
  height: 100%;
  width: 100%;
}

/* LADO DERECHO: FUNCIONALIDADES */
.funcionalidades {
  width: 500px;
  flex-shrink: 0;
  height: 100%;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  color: #333;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.funcionalidades h2 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #4e5336;
  border-bottom: 2px solid #eee;
  padding-bottom: 0.5rem;
  font-weight: 600;
}

.zone-list h3 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #4e5336;
  border-bottom: 2px solid #eee;
  padding-bottom: 0.5rem;
  font-weight: 600;
}


.func-panel {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  flex: 1;
  overflow: hidden; /* Para permitir scroll interno en la lista */
}

/* FORMULARIO */
.zone-form {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  background-color: #f9f9f9;
  padding: 1rem;
  border-radius: 8px;
}

.draw-controls {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.draw-hint {
  color: #e67e22;
  font-weight: bold;
  text-align: left;
}

/* LISTA */
.zone-list ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  flex: 1;
  overflow-y: auto; /* Scroll si hay muchas zonas */
  padding-top: 10px;
}

.zone-item {
  padding: 0.8rem;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
  margin-bottom: 5px;
  border-radius: 6px;
  border: 1px solid #eee;
}

.zone-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Inputs y Botones */
.input {
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #ddd;
  width: 100%;
  box-sizing: border-box;
}

.btn-small {
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid #ccc;
  background: #e0e0e0;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.85rem;
}

.btn-small.danger {
  background: #fff;
  color: #e74c3c;
  border-color: #e74c3c;
}

.btn-small.action-confirm {
  background: #27ae60;
  color: white;
  border: none;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.btn-save {
  flex: 2;
  background: #3e5c44;
  color: #fff;
  padding: 10px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.btn-cancel {
  flex: 1.5;
  background: #eee;
  padding: 10px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  color: #666;
  padding: 4px;
}
.btn-icon.danger {
  color: #e74c3c;
}

.icon-spacing {
  margin-right: 4px;
  font-size: 1.2em;
}
.separator {
  border: 0;
  border-top: 1px solid #ddd;
  margin: 5px 0;
}

/* Responsive */
@media (max-width: 900px) {
  .container-routes {
    flex-direction: column;
    height: auto;
  }
  .map-box {
    width: 100%;
    height: 400px;
  }
  .funcionalidades {
    width: 100%;
    height: auto;
  }
}
</style>
