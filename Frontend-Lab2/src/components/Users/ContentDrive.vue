<template>
  <div class="container">
    <div class="map-container">
      <div class="map-inner">
        <img class="map-image" src="/Mapa-ruta.png" alt="Mapa de ruta" />

        <!-- SVG overlay for lines connecting markers -->
        <svg class="map-overlay" viewBox="0 0 100 100" preserveAspectRatio="none" aria-hidden="true">
          <!-- line connecting only containers (solid/highlight) -->
          <polyline :points="polylineContainersPoints" class="overlay-line-containers" />
          <!-- full route line (start -> containers -> finish) dashed -->
          <polyline :points="polylinePoints" class="overlay-line" />
        </svg>

        <!-- Markers for containers (positioned over the image) -->
     <div v-for="m in markers" :key="m.id" 
       class="marker" 
       :class="[ { next: routeactual.next_point && String(routeactual.next_point.id) === String(m.id) }, m.type ]"
       :style="{ left: m.left, top: m.top }"
       @click="handleMarkerClick(m)">
          <span class="marker-label">{{ m.label }}</span>
        </div>
      </div>
    </div>

    <div class="info-wrapper">
      <button class="info-button" @click="routeAssigned">
        Rutas asignadas
      </button>

      <div class="info-panel">
        <div class="info-title">
          <h2>Ruta actual</h2>
        </div>
        
        <div class="grid">
          <div>
            <span class="label">Fecha - hora</span>
            <input class="value-input" readonly :value="routeactual.date + ' / ' + routeactual.start_time" />
          </div>
          <div>
            <span class="label">Estado ruta</span>
            <input class="value-input" readonly v-model="routeactual.route_status" />
          </div>
          <div>
            <span class="label">Central inicio</span>
            <input class="value-input" readonly :value="centralStart?.name || 'Cargando...'" />
          </div>
          <div>
            <span class="label">Central fin</span>
            <input class="value-input" readonly :value="centralFinish?.name || 'Cargando...'" />
          </div>
          <div>
            <span class="label">Coord. inicio</span>
            <input 
              class="value-input" 
              readonly 
              :value="`${centralStart?.coord_x || 'N/A'}, ${centralStart?.coord_y || 'N/A'}`" 
            />
          </div>
          <div>
            <span class="label">Coord. fin</span>
            <input 
              class="value-input" 
              readonly 
              :value="`${centralFinish?.coord_x || 'N/A'}, ${centralFinish?.coord_y || 'N/A'}`" 
            />
          </div>
        </div>

        <div class="full-width-box">
          <span class="label">Punto siguiente (Coordenadas)</span>
          <input 
            class="value-input" 
            readonly 
            :value="containersData[0]?.coord_x + ' , ' + containersData[0]?.coord_y|| 'No asignado'" 
          />
        </div>
        
        <div class="full-width-box">
          <span class="label">Tipo de residuo</span>
          <input 
            class="value-input" 
            readonly 
            :value="wastesData[0]?.waste_type || 'N/A'" 
          />
        </div>
        
        <div class="controls-container">
          <button class="nextpoint" @click="handleNextPoint" :disabled="processingNext">
            Siguiente punto
          </button>
          <button class="routefinish" @click="complete(routeactual)">
            Ruta completada
          </button>
        </div>
      </div>
    </div>
  </div>
</template>




<script setup>
import { ref, onMounted } from 'vue';
import { watch } from 'vue';
import { jwtDecode } from "jwt-decode";
import { useRouter } from 'vue-router';
// Asume que las rutas de los servicios son correctas
import DriverServices from '@/services/driverservices';
import RouteServices from '@/services/routeservices';
import centralServices from '@/services/centralservices';
import RouteContainer from '@/services/route_containerservices';
import ContainerServices from '@/services/containerservices';
import WasteServices from '@/services/wasteservices';
import PickUpServices from '@/services/pickupservices';


// Referencias reactivas
const routeactual = ref({
  id_route: null,
  date: 'N/A',
  route_status: 'Pendiente',
  id_central: null,
  id_central_finish: null,
  start_time: 'N/A',
  end_time: 'N/A',
  next_point: 'N/A', 
});

// Inicialización como objetos vacíos para evitar errores de acceso a propiedades
const centralStart = ref({}); 
const centralFinish = ref({}); 
const routecontainers = ref([]); // Lista de la tabla de unión
const containersData = ref([]); // Lista de objetos contenedor
const wastesData = ref([]);   // Lista de objetos waste (residuos)
const processingNext = ref(false);
const markers = ref([]); // marcadores para mostrar en el mapa
const polylinePoints = ref(''); // cadena de puntos para polyline SVG (inicio->contenedores->fin)
const polylineContainersPoints = ref(''); // cadena de puntos para polyline que conecta solo contenedores

// Datos del conductor
const name = ref('');
const lastname = ref('');
const userEmail = ref('');
const router = useRouter();


onMounted(() => {
  const token = localStorage.getItem('jwt');
  if (!token) return;

  try {
    const decoded = jwtDecode(token);
    userEmail.value = decoded.sub || decoded.email || null;
    if (userEmail.value) {
      getDriverData(userEmail.value);
    }
  } catch (error) {
    console.error("Error al decodificar token:", error);
  }
});


// Obtener datos del conductor
async function getDriverData(email) {
  try {
    const response = await DriverServices.getDriverByEmail(email);
    const driver = response.data;
    name.value = driver.name;
    lastname.value = driver.last_name;
    if (driver.id) {
      getactualroute(driver.id); 
    }
  } catch (err) {
    console.error("Error obteniendo datos del conductor:", err);
  }
}

// Obtener ruta actual
async function getactualroute(driverId) {
  try {
    const response = await RouteServices.findRouteByStatusAndIdDriver(driverId, 'EnProceso');
    
    if (!response.data) {
      routeactual.value.route_status = 'Sin Ruta';
      return; 
    }
    
    const route = Array.isArray(response.data) ? response.data[0] : response.data;

    // Asignación de propiedades
    routeactual.value = {
      id_route: route.id,
      date: route.date_,
      route_status: route.route_status,
      id_central: route.id_central,
      id_central_finish: route.id_central_finish,
      start_time: route.start_time,
      end_time: route.end_time,
      next_point: route.next_point || 'N/A', 
    };

    // Obtener las centrales de inicio y fin y los contenedores concurrentemente
    await Promise.all([
      fetchCentralesId(route.id_central, route.id_central_finish),
      fetchContainerRoute(route.id) 
    ]);

  } catch (error) {
    console.error("Error al obtener la ruta en proceso:", error);
  }
}

// Función para obtener las centrales por su ID
async function fetchCentralesId(idcentralStart, idcentralFinish) {
  try {
    const [resStart, resFinish] = await Promise.all([
      centralServices.getCentralById(idcentralStart),
      centralServices.getCentralById(idcentralFinish)
    ]);
    
    centralStart.value = resStart.data;
    centralFinish.value = resFinish.data;
  } catch (e) {
    console.warn('Error al cargar las centrales:', e);
    centralStart.value = { name: 'Error' };
    centralFinish.value = { name: 'Error' };
  }
}

// Función para obtener los contenedores y wastes de la ruta
async function fetchContainerRoute(routeId) {
  if (!routeId) return;

  try {
    const resRouteContainers = await RouteContainer.getRouteContainersByRoute(routeId);
    routecontainers.value = resRouteContainers.data;

    // Mapear y crear promesas para obtener cada contenedor y su waste asociado
    const containerPromises = routecontainers.value.map(async (rc) => {
      const resContainer = await ContainerServices.getContainerById(rc.id_container);
      const container = resContainer.data;
      
      let waste = null;
      if (container?.id_waste) {
        const resWaste = await WasteServices.getWasteById(container.id_waste);
        waste = resWaste.data;
      }
      return { container, waste };
    });

  const results = await Promise.all(containerPromises);
    
  containersData.value = results.map(r => r.container).filter(c => c);
  wastesData.value = results.map(r => r.waste).filter(w => w);
  // Recalcular marcadores para la superposición del mapa
  computeMarkers();

  } catch (error) {
    console.error("Error al obtener contenedores y wastes de la ruta:", error);
  }
}

// Calcular posiciones de marcadores (porcentajes) a partir de containersData y centrales
function computeMarkers() {
  // Preparar puntos: incluir contenedores y opcionalmente centrales para escala
  const points = [];
  containersData.value.forEach(c => {
    if (c && c.coord_x != null && c.coord_y != null) points.push({ id: c.id, x: Number(c.coord_x), y: Number(c.coord_y), label: c.id });
  });
  if (centralStart.value && centralStart.value.coord_x != null && centralStart.value.coord_y != null) {
    points.push({ id: 'start', x: Number(centralStart.value.coord_x), y: Number(centralStart.value.coord_y), label: 'I' });
  }
  if (centralFinish.value && centralFinish.value.coord_x != null && centralFinish.value.coord_y != null) {
    points.push({ id: 'finish', x: Number(centralFinish.value.coord_x), y: Number(centralFinish.value.coord_y), label: 'F' });
  }

  if (points.length === 0) {
    markers.value = [];
    return;
  }

  const xs = points.map(p => p.x);
  const ys = points.map(p => p.y);
  const minX = Math.min(...xs);
  const maxX = Math.max(...xs);
  const minY = Math.min(...ys);
  const maxY = Math.max(...ys);

  const widthRange = (maxX - minX) || 1;
  const heightRange = (maxY - minY) || 1;

  // Construir lista ordenada: inicio -> contenedores (orden de routecontainers) -> fin
  const containerById = {};
  containersData.value.forEach(c => { containerById[String(c.id)] = c; });

  const ordered = [];
  // añadir central de inicio si está presente
  if (centralStart.value && centralStart.value.coord_x != null && centralStart.value.coord_y != null) {
    ordered.push({ type: 'start', id: 'start', x: Number(centralStart.value.coord_x), y: Number(centralStart.value.coord_y), label: 'I', raw: centralStart.value });
  }

  // añadir contenedores en orden de routecontainers si está disponible, de lo contrario usar el orden de containersData
  if (routecontainers.value && routecontainers.value.length > 0) {
    routecontainers.value.forEach((rc, idx) => {
      const c = containerById[String(rc.id_container)];
      if (c) ordered.push({ type: 'container', id: c.id, x: Number(c.coord_x), y: Number(c.coord_y), label: ordered.length + 1, raw: c });
    });
    // incluir contenedores que no estén en routecontainers al final
    containersData.value.forEach(c => {
      if (!ordered.some(o => String(o.id) === String(c.id))) {
        ordered.push({ type: 'container', id: c.id, x: Number(c.coord_x), y: Number(c.coord_y), label: ordered.length + 1, raw: c });
      }
    });
  } else {
    containersData.value.forEach((c, idx) => ordered.push({ type: 'container', id: c.id, x: Number(c.coord_x), y: Number(c.coord_y), label: idx + 1, raw: c }));
  }

  // añadir central de fin si está presente
  if (centralFinish.value && centralFinish.value.coord_x != null && centralFinish.value.coord_y != null) {
    ordered.push({ type: 'finish', id: 'finish', x: Number(centralFinish.value.coord_x), y: Number(centralFinish.value.coord_y), label: 'F', raw: centralFinish.value });
  }

  // Mapear puntos ordenados a markers con posiciones en porcentaje
  markers.value = ordered.map((p) => {
    const leftPct = ((p.x - minX) / widthRange) * 100;
    const topPct = ((maxY - p.y) / heightRange) * 100;
    return {
      id: p.id,
      type: p.type,
      left: `${leftPct}%`,
      top: `${topPct}%`,
      leftPct: leftPct,
      topPct: topPct,
      label: p.label,
      raw: p.raw,
    };
  });

  // Construir cadena de puntos para polyline (ordenada)
  polylinePoints.value = markers.value.map(m => `${m.leftPct},${m.topPct}`).join(' ');
  // Construir polyline que conecta solo contenedores (en orden)
  const containerMarkers = markers.value.filter(m => m.type === 'container');
  polylineContainersPoints.value = containerMarkers.map(m => `${m.leftPct},${m.topPct}`).join(' ');
}

// Recalcular marcadores cuando cambien containersData, centralStart o centralFinish
watch([containersData, centralStart, centralFinish], () => computeMarkers());

function handleMarkerClick(m) {
  // establecer next_point a este contenedor (o central si se clicó start/finish)
  if (!m) return;
  if (m.type === 'container') {
    routeactual.value.next_point = { type: 'container', id: m.id, coord_x: m.raw.coord_x, coord_y: m.raw.coord_y };
  } else if (m.type === 'start') {
    routeactual.value.next_point = { type: 'central_start', id: 'start', coord_x: m.raw.coord_x, coord_y: m.raw.coord_y };
  } else if (m.type === 'finish') {
    routeactual.value.next_point = { type: 'central_finish', id: 'finish', coord_x: m.raw.coord_x, coord_y: m.raw.coord_y };
  }
  // opcional: mostrar información pequeña o desplazarse al contenedor en la UI
  console.log('Marcador clicado', m);
}


// Función para completar la ruta
function complete(routeactual) {
  if (routeactual && routeactual.id_route) {
    RouteServices.updateRouteStatus(routeactual.id_route, "Finalizada")
      .then(() => {
        alert("Ruta marcada como 'Finalizada' exitosamente.");
        routeactual.route_status = "Finalizada";
        // Limpiar datos de la ruta actual en la UI
        clearCurrentRouteData();
      })
      .catch(error => {
        console.error("Error al actualizar la ruta:", error);
        alert("Error al finalizar la ruta.");
      });
  } else {
    console.error("La ruta no tiene un ID válido.");
  }
}

// Limpiar los datos de la ruta actual en la UI (tabla de ruta actual)
function clearCurrentRouteData() {
  routeactual.value = {
    id_route: null,
    date: 'N/A',
    route_status: 'Sin Ruta',
    id_central: null,
    id_central_finish: null,
    start_time: 'N/A',
    end_time: 'N/A',
    next_point: 'N/A',
  };
  centralStart.value = {};
  centralFinish.value = {};
  routecontainers.value = [];
  containersData.value = [];
  wastesData.value = [];
}

// Función placeholder para el siguiente punto
async function handleNextPoint() {
  if (!routeactual.value || !routeactual.value.id_route) {
    alert('No hay una ruta activa definida.');
    return;
  }

  const routeId = routeactual.value.id_route;

  // Si no hay contenedores pendientes, finalizar la ruta
  if (!containersData.value || containersData.value.length === 0) {
    alert('No quedan contenedores por visitar. La ruta se finalizará.');
    try {
      await RouteServices.updateRouteStatus(routeId, 'Finalizada');
      routeactual.value.route_status = 'Finalizada';
      // Limpiar UI de ruta actual
      clearCurrentRouteData();
      try {
        await refreshRouteByRouteId(routeId);
      } catch (refreshErr) {
        console.warn('Ruta finalizada pero fallo al refrescar desde servidor:', refreshErr);
      }
    } catch (err) {
      console.error('Error al finalizar la ruta automáticamente:', err);
      alert('Error al finalizar la ruta.');
    }
    return;
  }

  // Determinar el siguiente contenedor: usar el primero de la lista (ordenado) como objetivo
  const nextContainer = containersData.value[0];
  if (!nextContainer) {
    alert('No se encontró el siguiente contenedor.');
    return;
  }

  processingNext.value = true;
  try {
    // Crear un registro de pickup en el backend para marcar la visita
    const payload = { id_container: nextContainer.id, id_route: routeId };
    await PickUpServices.createPickUp(payload);

    // Remover localmente el contenedor visitado para avanzar
    containersData.value = containersData.value.filter(c => String(c.id) !== String(nextContainer.id));
    routecontainers.value = routecontainers.value.filter(rc => String(rc.id_container) !== String(nextContainer.id));

    // Si quedan contenedores, el siguiente objetivo sera el primero de la lista actualizada
    if (containersData.value.length > 0) {
      const nxt = containersData.value[0];
      routeactual.value.next_point = { type: 'container', id: nxt.id, coord_x: nxt.coord_x, coord_y: nxt.coord_y };
    } else {
      // No quedan mas contenedores: finalizar ruta (se intentará también en backend)
      routeactual.value.next_point = null;
      alert('No quedan contenedores por visitar. La ruta se finalizará.');
      try {
        await RouteServices.updateRouteStatus(routeId, 'Finalizada');
        routeactual.value.route_status = 'Finalizada';
        // Limpiar UI de ruta actual
        clearCurrentRouteData();
      } catch (statusErr) {
        console.warn('No se pudo marcar la ruta como Finalizada en el servidor:', statusErr);
      }
    }

    // Forzar reactividad
    routeactual.value = { ...routeactual.value };

    // Intentar refrescar desde backend; si falla, no mostrar error crítico al usuario
    try {
      await refreshRouteByRouteId(routeId);
    } catch (refreshErr) {
      console.warn('Recoleccion creada:', refreshErr);
      alert('Recoleccion creada, Avanzando al siguiente punto');
    }

  } catch (err) {
    // Error al crear el pickup (autorización o inserción)
    console.error('Error creando pickup o avanzando al siguiente contenedor:', err);
    if (err && err.response && err.response.status === 403) {
      console.warn('403 recibido al crear pickup. Token y permisos podrían ser la causa.');
      const token = localStorage.getItem('jwt');
      if (token) {
        try {
          const decoded = jwtDecode(token);
          console.log('Token presente. Usuario esperado (sub):', decoded.sub || decoded.email || decoded.username, 'Rol token (usertype claim):', decoded.usertype || '(claim usertype no encontrado)');
        } catch (decErr) {
          console.warn('No se pudo decodificar token para depuración:', decErr);
        }
      } else {
        console.warn('No hay token JWT en localStorage.');
      }
      alert('Acceso denegado (403). Verifica que estés autenticado y tengas permisos para crear pick-ups.');
    } else {
      // Mostrar mensaje más claro
      alert('Ocurrió un error al crear el pickup. Revisa la consola o intenta de nuevo.');
    }
  } finally {
    processingNext.value = false;
  }
}

function routeAssigned() {
  router.push({ name: 'route-assigned' })
}

</script>

<style scoped>

.info-title h2 {
  color: #4a4f37; /* color visible */
  margin: 0 0 0.6rem 0;
  font-size: 1.25rem;
  line-height: 1.2;
  font-weight: 700;
  text-align: center;
}
.container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  width: 100%;
  max-width: 1200px;
  margin: 2rem auto;
}

.value-input {
  width: 100%;
  background: rgba(255,255,255,0.85);
  border: 1.5px solid #dcd6c8;
  color: #4e5336;
  font-size: 1.08rem;
  font-weight: 700;
  padding: 0.35rem 0.7rem;
  border-radius: 0.5rem;
  outline: none;
  pointer-events: none;
  box-shadow: 0 1px 4px rgba(78,83,54,0.08);
  text-align: left;
  margin-top: 0.15rem;
  margin-bottom: 0.15rem;
}

.value-input:read-only {
  background: rgba(255,255,255,0.85);
  border: 1.5px solid #dcd6c8;
}

@media (min-width: 768px) {
  .container {
    flex-direction: row;
    align-items: flex-start;
  }
}

.map-container {
  flex: 1;
  background: #fff;
  border-radius: 1.2rem;
  box-shadow: 0 6px 18px rgba(78,83,54,0.10);
  padding: 1.2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 320px;
  transform: translateY(120px);

}

.map-container img {
  max-width: 100%;
  height: auto;
  border-radius: 1rem;
  box-shadow: 0 2px 8px rgba(78,83,54,0.08);
  border: 1px solid #eaeaea;
}

.map-inner { position: relative; display: block; width: 100%; }
.map-image { display: block; width: 100%; height: auto; border-radius: 1rem; }
.marker {
  position: absolute;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(94,101,65,0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 0.75rem;
  font-weight: 700;
  cursor: pointer;
  transform: translate(-50%, -50%);
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}
.marker.next { background: #27ae60; box-shadow: 0 2px 8px rgba(39,174,96,0.25); }
.marker-label { pointer-events: none; }

.marker.start { background: #2d8cff; }
.marker.finish { background: #ff6b6b; }

.map-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* allow clicks through to markers */
}
.overlay-line {
  fill: none;
  stroke: rgba(78,83,54,0.85);
  stroke-width: 0.8;
  stroke-dasharray: 2 3;
  stroke-linecap: round;
}

.overlay-line-containers {
  fill: none;
  stroke: rgba(39,174,96,0.9);
  stroke-width: 1.2;
  stroke-dasharray: 0; /* solid */
  stroke-linecap: round;
  opacity: 0.9;
}

.info-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 1.2rem;
  min-width: 320px;
}

.info-button {
  background: linear-gradient(180deg,#5e6541,#52563f);
  color: #fff;
  border: none;
  padding: 1rem 2.5rem;
  border-radius: 999px;
  cursor: pointer;
  font-weight: 600;
  font-size: 1.1rem;
  align-self: center;
  box-shadow: 0 4px 14px rgba(94,101,65,0.10);
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: background 0.18s, box-shadow 0.18s;
  outline: none;
}

.info-button:hover,
.info-button:focus {
  background: #4a4f37;
  box-shadow: 0 6px 18px rgb(255, 255, 255);
}

.info-panel {
  background: #f7f7f7;
  border-radius: 1.2rem;
  box-shadow: 0 6px 18px rgba(78,83,54,0.10);
  padding: 2rem 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.2rem;
}

.grid div {
  background: linear-gradient(180deg,#5e6541,#52563f);
  color: #fff;
  text-align: left;
  padding: 1rem 1.2rem;
  border-radius: 0.7rem;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  font-size: 1rem;
  box-shadow: 0 2px 8px rgba(78,83,54,0.08);
}

.label {
  font-size: 0.98rem;
  font-weight: 500;
  color: #dcd6c8;
  letter-spacing: 0.2px;
}

.value {
  font-size: 1.08rem;
  font-weight: 700;
  color: #fff;
}

.full-width-box {
  background: linear-gradient(180deg,#5e6541,#52563f);
  color: #fff;
  text-align: left;
  padding: 1rem 1.2rem;
  border-radius: 0.7rem;
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  font-size: 1rem;
  box-shadow: 0 2px 8px rgba(78,83,54,0.08);
}

.routefinish {
  color:white;
  background: linear-gradient(180deg,#5e6541,#52563f);
  padding: 1rem;
  border-radius: 1rem;
  margin-left: 5rem;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(94,101,65,0.10);
  transition: background 0.18s, box-shadow 0.18s;
  outline: none;
  border: none;
}

.nextpoint {
  color:white;
  background: linear-gradient(180deg,#5e6541,#52563f);
  padding: 1rem;
  border-radius: 1rem;
  margin-left: 5rem;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(94,101,65,0.10);
  transition: background 0.18s, box-shadow 0.18s;
  outline: none;  
  border: none;

}



@media (max-width: 900px) {
  .container {
    flex-direction: column;
    gap: 1.5rem;
    max-width: 98vw;
  }
  .map-container,
  .info-wrapper {
    min-width: 0;
  }
  .info-panel { padding: 1.2rem 0.7rem; }
  .grid div, .full-width-box { font-size: 0.97rem; padding: 0.7rem 0.7rem; }
}
</style>