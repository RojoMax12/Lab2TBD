<template>
  <div class="report-section">
    <h2 class="section-title">Peso Recolectado Diario</h2>

    <div class="admin-grid">
      <div class="admin-box">
        <div class="grid-header-weights">
          <div>Fecha</div>
          <div>Peso Total (Kg)</div>
        </div>

        <div class="scrollable-table">
          <div class="grid-row-weights" v-for="(item, index) in pesosDiarios" :key="index">
            <div>{{ formatearFecha(item.fecha) }}</div>
            <div>{{ item.peso_total }} kg</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ReportServices from '@/services/reportservices'

const pesosDiarios = ref([])

const fetchDailyWeights = () => {
  ReportServices.getDailyWeights()
    .then(response => {
      pesosDiarios.value = response.data
    })
    .catch(error => {
      console.error("Error al obtener reporte de pesos:", error)
    })
}

const formatearFecha = (fecha) => {
  if (!fecha) return '-';
  return new Date(fecha).toLocaleDateString('es-CL');
}

onMounted(() => {
  fetchDailyWeights()
})
</script>

<style scoped>

.report-section {
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.section-title {
  text-align: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: #4a4f37;
  margin-bottom: 20px;
}

.admin-grid {
  width: 100%;
  display: grid;
  justify-items: center;
}

.admin-box {
  background-color: white;
  width: 100%;
}

.grid-header-weights {
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: #5f6949;
  color: white;
  padding: 12px 16px;
  font-weight: bold;
  text-align: center;
  border-radius: 8px;
}

.grid-row-weights {
  display: grid;
  grid-template-columns: 1fr 1fr;
  padding: 12px;
  border-bottom: 1px solid #ddd;
  text-align: center;
  background: #f9f9f9;
  color: #333;
}

.scrollable-table {
  max-height: 300px;
  overflow-y: auto;
  margin-top: 10px;
}
</style>
