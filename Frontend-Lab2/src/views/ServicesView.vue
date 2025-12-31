<template>
  <div class="page-wrapper">
    <Header />

    <main class="services-content">
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">Nuestros Servicios</h1>
          <p class="hero-subtitle">
            Gestión integral de residuos con tecnología de optimización de rutas
            para un entorno más limpio y eficiente.
          </p>
        </div>
      </section>

      <section id="servicios" class="services-grid-container">

        <div class="service-card">
          <div class="icon-wrapper">
            <Truck :size="48" color="#4e5336" stroke-width="1.5" />
          </div>
          <h3>Recolección Domiciliaria</h3>
          <p>
            Servicio programado de retiro de residuos sólidos urbanos.
            Garantizamos puntualidad y cobertura total en las zonas asignadas.
          </p>
        </div>

        <div class="service-card">
          <div class="icon-wrapper">
            <Recycle :size="48" color="#4e5336" stroke-width="1.5" />
          </div>
          <h3>Gestión de Reciclaje</h3>
          <p>
            Clasificación y recolección diferenciada de:
            <strong>Papel, vidrio, plástico y orgánicos</strong>.
            Fomentamos la economía circular.
          </p>
        </div>

        <div class="service-card">
          <div class="icon-wrapper">
            <Trash2 :size="48" color="#4e5336" stroke-width="1.5" />
          </div>
          <h3>Gestión de Contenedores</h3>
          <p>
            Monitoreo de estado y capacidad en tiempo real. Ubicación estratégica y
            mantenimiento para evitar desbordes.
          </p>
        </div>

        <div class="service-card">
          <div class="icon-wrapper">
            <MapPin :size="48" color="#4e5336" stroke-width="1.5" />
          </div>
          <h3>Rutas Optimizadas</h3>
          <p>
            Algoritmos avanzados para trazar los recorridos más eficientes.
            Reducimos la huella de carbono y los tiempos de tránsito.
          </p>
        </div>

        <div class="service-card">
          <div class="icon-wrapper">
            <TriangleAlert :size="48" color="#4e5336" stroke-width="1.5" />
          </div>
          <h3>Residuos Especiales</h3>
          <p>
            Retiro seguro de residuos voluminosos, escombros y materiales
            que requieren tratamiento especial.
          </p>
        </div>

        <div class="service-card">
          <div class="icon-wrapper">
            <ChartNoAxesCombined :size="48" color="#4e5336" stroke-width="1.5" />
          </div>
          <h3>Monitoreo y Reportes</h3>
          <p>
            Generamos reportes de desempeño detallados y estado de recolección para
            administradores y municipios.
          </p>
        </div>

      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/Home/Header.vue'
import Footer from '@/components/Home/Footer.vue'

import {
  Truck,
  Recycle,
  Trash2,
  MapPin,
  TriangleAlert,
  ChartNoAxesCombined
} from 'lucide-vue-next';

</script>

<style scoped>

.page-wrapper {
  background-color: #f9f9f9;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.services-content {
  flex: 1;
}

.hero-section {
  background: linear-gradient(180deg, #4e5336, #3f4732);
  padding: 80px 20px 100px;
  text-align: center;
  color: #ffffff;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 15px;
}

.hero-subtitle {
  font-size: 1.2rem;
  max-width: 700px;
  margin: 0 auto;
  opacity: 0.9;
  line-height: 1.6;
}

.services-grid-container {
  max-width: 1200px;
  margin: -60px auto 60px;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
}

.service-card {
  background: #ffffff;
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
  border: 1px solid rgba(78,83,54,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.service-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(78,83,54,0.15);
  border-color: #5e6541;
}

.icon-wrapper {
  margin-bottom: 20px;
  background: #f4f5f0;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 2px solid #e0e2d6;
}

.service-card h3 {
  color: #3f4732;
  font-size: 1.5rem;
  margin-bottom: 15px;
  font-weight: 700;
}

.service-card p {
  color: #555;
  line-height: 1.6;
  font-size: 0.95rem;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.2rem;
  }
  .services-grid-container {
    margin-top: 40px;
  }
}
</style>
