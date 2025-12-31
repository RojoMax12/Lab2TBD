<template>
  <div>
    <HeaderAdmin />
  </div>

  <div class="admin-container">
    <!-- Header -->


    <!-- Contenido -->
    <main class="admin-main">
      <h1 class="admin-title">Panel de administración</h1>
      <div class="efficiency-section">
        <h2 class="admin-title">Últimos 6 meses</h2>
        <button class="calculate-btn" @click="calculateEfficiency">
          Calcular eficiencia
        </button>

        <div class="admin-grid">
          <div class="admin-box">
            <div class="grid-header">
              <div>Nombre conductor</div>
              <div>Apellido conductor</div>
              <div>Tiempo promedio (Horas)</div>
            </div>

            <div class="scrollable-table">
              <div class="grid-row" v-for="item in efficiency" :key="item.driver_id">
                <div>{{ item.driver_name }}</div>
                <div>{{ item.driver_last_name }}</div>
                <div>{{ item.average_time_hours }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="waste-performance">
        <h2 class="admin-title">Desempeño de residuos recogidos</h2>
        <button class="calculate-btn" @click="fetchWastePerformance">
          Obtener desempeño
        </button>

        <div class="admin-grid">
          <div class="admin-box">
            <div class="grid-header-waste">
              <div>Nombre y apellido conductor A</div>
              <div>Cantidad de residuos (kg) A</div>
              <div>Nombre y apellido conductor B</div>
              <div>Cantidad de residuos (kg) B</div>
              <div>Tipo de residuo</div>
            </div>

            <div class="scrollable-table">
              <div class="grid-row-waste" v-for="item in Wasteperformance" :key="item.driver_id">
                <div>{{ item.driver_a_name }}</div>
                <div>{{ item.driver_a_weight }}</div>
                <div>{{ item.driver_b_name }}</div>
                <div>{{ item.driver_b_weight }}</div>
                <div>{{ item.waste_type }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import HeaderAdmin from '@/components/Admin/HeaderAdmin.vue'
import routeServices from '@/services/routeservices'

const router = useRouter()
const efficiency = ref([])
const Wasteperformance = ref([])

// Function to fetch efficiency data
function calculateEfficiency() {
  routeServices.efficiency()
    .then(response => {
      efficiency.value = response.data
    })
    .catch(error => {
      console.error("Error al calcular eficiencia:", error)
    })
}


function fetchWastePerformance() {
  routeServices.wasteperformance()
    .then(response => {
      Wasteperformance.value = response.data
      console.log("Desempeño de residuos:", Array.from(Wasteperformance.value));

    })
    .catch(error => {
      console.error("Error al obtener desempeño de residuos:", error)
    })
}
</script>

<style scoped>
/* General page styles */
.admin-container {
  min-height: auto;
  background-color: #f4e9da;
  padding: 20px;
}

/* Main title */
.admin-title {
  text-align: center;
  font-size: 2rem;
  font-weight: bold;
  color: #4a4f37;
  margin-bottom: 25px;
}

/* Efficiency section styles */
.efficiency-section {
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  padding-bottom: 40px;
  width: 1000px;
  justify-content: center;
}

/* Button style */
.calculate-btn {
  margin-top: 20px;
  background-color: #5f6949;
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  position: relative;
  right: -760px;
  top: -20px;
  margin-bottom: auto;

}

.calculate-btn:hover {
  background-color: #4c553a;
}

/* Table styling */
.admin-grid {
  display: grid;
  grid-template-columns: 1fr; /* One column */
  gap: 20px;
  justify-items: center;
  align-items: start;
}

.admin-box {
  background-color: white;
  border-radius: 12px;
  width: 100%;
  max-width: 900px;
  padding: 20px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.grid-header {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr ; /* Five equally spaced columns */
  background: #5f6949;
  color: white;
  padding: 12px 16px;
  font-weight: bold;
  text-align: center;
  border-radius: 8px;
}

.grid-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr; /* Same as header */
  padding: 12px;
  border-bottom: 1px solid #ddd;
  text-align: center;
  background: #f9f9f9;
  color: #333;
}

.grid-header-waste {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr ; /* Five equally spaced columns */
  background: #5f6949;
  color: white;
  padding: 12px 16px;
  font-weight: bold;
  text-align: center;
  border-radius: 8px;
}

.grid-row-waste {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr; /* Same as header */
  padding: 12px;
  border-bottom: 1px solid #ddd;
  text-align: center;
  background: #f9f9f9;
  color: #333;
}

.scrollable-table {
  max-height: 300px; /* Set a max height */
  overflow-y: auto; /* Scrollable */
  margin-top: 16px;
}

/* Responsive design for smaller screens */
@media (max-width: 900px) {
  .admin-grid {
    grid-template-columns: 1fr;
  }

  .admin-box {
    width: 100%;
    max-width: 100%;
  }
}

.admin-main{
  display: flex;
  flex-direction: column;
  align-items: center;
}

.waste-performance {
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  padding-bottom: 40px;
  width: 1000px;
  justify-content: center;
  margin-top: 20px;
}
</style>
