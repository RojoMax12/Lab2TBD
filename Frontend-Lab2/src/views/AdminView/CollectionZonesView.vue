<template>
  <div>
    <HomeAdminView />

    <div class="container-routes collection-zones-view">
      <h1 class="title">Zonas de Recolección</h1>
      <div class="map-box">
        <div id="map-zones" style="height: 100%; width: 100%;"></div>
      </div>
    </div>

    <div class="funcionalidades">
      <h2>Funcionalidades</h2>

      <div class="func-panel">
        <form @submit.prevent="isEditing ? updateZone() : createZone()" class="zone-form">
          <label>Nombre</label>
          <input v-model="zoneForm.name" class="input" placeholder="Nombre de la zona" />

          <div style="display:flex; gap:8px; align-items:center;">
            <button type="button" class="btn-small" @click="startDrawing" v-if="!drawing">Dibujar zona</button>
            <button type="button" class="btn-small" @click="finishDrawing" v-if="drawing">Finalizar dibujo</button>
            <button type="button" class="btn-small danger" @click="cancelDrawing" v-if="drawing">Cancelar dibujo</button>
            <small style="color:#666; margin-left:8px">O pega WKT en el campo si ya lo tienes</small>
          </div>

          <label>WKT (POLYGON)</label>
          <textarea v-model="zoneForm.location" rows="3" class="input" placeholder="POLYGON((lng lat, lng lat, ...))"></textarea>

          <div class="form-actions">
            <button class="btn-save" type="submit">{{ isEditing ? 'Guardar cambios' : 'Crear zona' }}</button>
            <button class="btn-cancel" type="button" @click="resetForm">Limpiar</button>
          </div>
        </form>

        <div class="zone-list">
          <h3>Zonas existentes</h3>
          <ul>
            <li v-for="z in zones" :key="z.id" class="zone-item">
              <div class="zone-meta">
                <strong>{{ z.name }}</strong>
                <div class="zone-actions">
                  <button @click="editZone(z)" class="btn-small">Editar</button>
                  <button @click="deleteZone(z.id)" class="btn-small danger">Eliminar</button>
                </div>
              </div>
              <div class="zone-wkt">{{ z.location }}</div>
            </li>
          </ul>
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
  // center map to zone bounds if possible
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
      // Clear any drawing previews and refresh rendered layers
      try { drawLayer.clearLayers(); renderZones() } catch (e) { /* ignore */ }
      alert('Zona eliminada')
  } catch (e) {
    console.error('Error eliminando zona', e)
    alert('Error al eliminar zona')
  }
}

function parseWKTPolygon(wkt) {
  if (!wkt || !wkt.startsWith('POLYGON')) return []
  // Remove POLYGON(( and trailing ))
  const inner = wkt.replace('POLYGON((', '').replace('))', '')
  const pairs = inner.split(',')
  return pairs.map(pair => {
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
    attribution: '© OpenStreetMap'
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

  zones.value.forEach(z => {
    const coords = parseWKTPolygon(z.location)
    if (coords.length) {
      const polygon = L.polygon(coords, { color: '#4e5336', weight: 2, fillOpacity: 0.2 }).addTo(zonesLayer)
      polygon.bindPopup(`<strong>${z.name}</strong>`)
      allBounds.extend(polygon.getBounds())
    }
  })

  containers.value.forEach(c => {
    const latlng = parseWKTPoint(c.location)
    if (latlng) {
      const marker = L.circleMarker(latlng, { radius: 6, color: '#d35400', fillColor: '#f39c12', fillOpacity: 0.9 }).addTo(containersLayer)
      const weight = (c.weight !== undefined && c.weight !== null) ? Number(c.weight).toFixed(1) + ' kg' : 'N/A'
      const wasteName = c.id_waste ? (wasteMap[c.id_waste] || `ID ${c.id_waste}`) : 'N/A'
      const popupHtml = `<strong>Contenedor ${c.id}</strong><br/>Estado: ${c.status || 'N/A'}<br/>Peso: ${weight}<br/>Tipo residuo: ${wasteName}`
      marker.bindPopup(popupHtml)
      allBounds.extend(marker.getLatLng())
    }
  })

  if (allBounds.isValid()) {
    map.fitBounds(allBounds.pad(0.1))
  }
}

// Drawing handlers
function onMapClickForDraw(e) {
  if (!drawing.value) return
  const lat = e.latlng.lat
  const lng = e.latlng.lng
  drawPoints.push([lng, lat])
  // update preview
  drawLayer.clearLayers()
  // markers
  drawPoints.forEach(p => {
    L.circleMarker([p[1], p[0]], { radius: 4, color: '#2c3e50' }).addTo(drawLayer)
  })
  if (drawPoints.length === 1) return
  // polyline or polygon preview
  const latlngs = drawPoints.map(p => [p[1], p[0]])
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
  // Refresh base layers so map reflects current zones/containers
  try { renderZones() } catch (e) { /* ignorar si el mapa no está listo */ }
}

function buildWKTFromDrawPoints(points) {
  if (!points || points.length < 3) return null
  // ensure closed
  const pts = points.slice()
  const first = pts[0]
  const last = pts[pts.length - 1]
  if (first[0] !== last[0] || first[1] !== last[1]) pts.push([first[0], first[1]])
  const inner = pts.map(p => `${p[0]} ${p[1]}`).join(', ')
  return `POLYGON((${inner}))`
}

function finishDrawing() {
  if (!drawing.value) return
  if (drawPoints.length < 3) {
    alert('Se requieren al menos 3 puntos para formar un polígono')
    return
  }
  const wkt = buildWKTFromDrawPoints(drawPoints)
  if (!wkt) {
    alert('Error generando polígono')
    return
  }
  zoneForm.value.location = wkt
  // keep preview, stop drawing
  drawing.value = false
  map.getContainer().style.cursor = ''
  map.off('click', onMapClickForDraw)
  // zoom to drawn polygon
  const latlngs = drawPoints.map(p => [p[1], p[0]])
  const poly = L.polygon(latlngs)
  map.fitBounds(poly.getBounds().pad(0.2))
}

onMounted(async () => {
  await nextTick()
  initMap()
  if (map) { // Ensure map is initialized before invalidating size
    map.invalidateSize();
  }
  try {
    const [zonesRes, containersRes] = await Promise.all([
      collectionZoneServices.getAllCollectionZones(),
      containerServices.getAllContainers()
    ])
    zones.value = zonesRes.data || zonesRes
    containers.value = containersRes.data || containersRes
    // obtiene tipos de residuo y crea mapa id -> nombre
    try {
      const wastesRes = await wasteServices.getAllWastes()
      wastes.value = wastesRes.data || wastesRes
      wastes.value.forEach(w => { wasteMap[w.id] = w.waste_type || w.wasteType || w.type || null })
    } catch (we) {
      console.warn('No se pudieron obtener tipos de residuo', we)
    }
    renderZones()
    // populate CRUD list
    // ensure zones are fresh
    await fetchZones()
  } catch (e) {
    console.error('Error cargando zonas o contenedores', e)
  }
})
</script>

<style scoped>
.collection-zones-view {
  padding: 1.25rem;
}
.title {
  font-size: 1.6rem;
  margin-bottom: 1rem;
  color: #333;
  text-align: center; 
}

.map-box {
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  width: 80%; /* From original inline style */
  max-width: 1000px; /* From original inline style */
  margin: 0 auto; /* From original inline style */
  box-shadow: 0 2px 10px rgba(0,0,0,0.1); /* Subtle shadow */
  border: 1px solid #ccc; /* Optional, from original inline style */
  height: 800px; /* Explicit height for the box itself */
}
.funcionalidades {
  padding: 1.25rem;
  max-width: 1000px;
  margin: 1.25rem auto;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.06);
  color: #333;
}
.func-panel {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}
.zone-form { flex: 1; display:flex; flex-direction:column; gap:0.5rem }
.zone-list { flex: 1; max-height: 380px; overflow:auto }
.zone-item { padding: 0.5rem; border-bottom: 1px solid #eee }
.zone-meta { display:flex; justify-content:space-between; align-items:center }
.zone-wkt { font-size: 12px; color:#666; margin-top:6px }
.form-actions { display:flex; gap:0.5rem }
.btn-small { padding:4px 8px; border-radius:6px; border:1px solid #ccc; background: #ccc; cursor:pointer }
.btn-small.danger { border-color:#e74c3c; color:#e74c3c }
.btn-save { background:#4e5336; color:#fff; padding:8px 12px; border-radius:8px; border:none }
.btn-cancel { background:#eee; padding:8px 12px; border-radius:8px; border:none }
.input { padding:8px; border-radius:6px; border:1px solid #ddd; width:100% }

</style>
