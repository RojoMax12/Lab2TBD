<template>
  <div class="page-wrapper">
    <Header />

    <main class="about-content">
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">¿Quiénes somos?</h1>
          <p class="hero-subtitle">
            En <strong>CleanOps</strong>, trabajamos para ofrecer soluciones eficientes para la
            recolección de basura, optimizando la gestión de conductores, rutas y contenedores. 
            Nuestro objetivo es mejorar la productividad y eficiencia de las empresas encargadas de la recolección de residuos.
          </p>
        </div>
      </section>

      <section id="about" class="about-grid-container">

        <div class="about-card">
          <div class="icon-wrapper">
            <CdTarget aria-hidden="true" color="black"/>
          </div>
          <h3>Nuestra misión</h3>
          <p>
            Desarrollar una plataforma integral que permita la gestión eficiente de conductores, rutas y contenedores,
            optimizando la asignación de rutas y proporcionando una interfaz intuitiva para que los conductores gestionen sus tareas.
          </p>
        </div>

        <div class="about-card">
          <div class="icon-wrapper">
            <HiSolidEye aria-hidden="true" color="black" />
          </div>
          <h3>Nuestra visión</h3>
          <p>
            Convertirnos en la plataforma líder en la gestión digital de la recolección de basura, reconocida por su eficiencia,
            facilidad de uso y capacidad de adaptación a los diferentes requisitos de las empresas de servicios de recolección.
          </p>
        </div>

        <div class="about-card">
          <div class="icon-wrapper">
            <BxSolidCog aria-hidden="true" color="black"/>
          </div>
          <h3>¿Qué nos diferencia?</h3>
          <ul>
            <li>Interfaz clara y fácil de usar</li>
            <li>Optimización en la asignación de rutas</li>
            <li>Gestión centralizada de contenedores y recolección</li>
            <li>Escalable para distintos tipos de empresas de recolección</li>
          </ul>
        </div>

        <div class="about-card">
          <div class="icon-wrapper">
            <FaHandshake aria-hidden="true" color="black"/>
          </div>
          <h3>Nuestro compromiso</h3>
          <p>
            En <strong>CleanOps</strong> estamos comprometidos con la mejora continua y la seguridad de la información. Nos
            dedicamos a crear soluciones tecnológicas que aporten un valor real a las empresas de recolección de basura,
            promoviendo un entorno más limpio y eficiente.
          </p>
        </div>

      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup>
// Importamos los iconos necesarios de Vue Icons
import { CdTarget, HiSolidEye, BxSolidCog, FaHandshake } from '@kalimahapps/vue-icons';
import Header from '@/components/Home/Header.vue'
import Footer from '@/components/Home/Footer.vue'
</script>

<style scoped>
.page-wrapper {
  background-color: #f9f9f9;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.about-content {
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

.about-grid-container {
  max-width: 1200px;
  margin: -60px auto 60px;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 30px;
}

.about-card {
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

.about-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(78,83,54,0.15);
  border-color: #5e6541;
}

.about-card h3 {
  color: #3f4732;
  font-size: 1.5rem;
  margin-bottom: 15px;
  font-weight: 700;
}

.about-card p {
  color: #555;
  line-height: 1.6;
  font-size: 0.95rem;
}

.about-card ul {
  padding-left: 1.5rem;
}

.about-card li {
  font-size: 1.1rem;
  color: #34495e;
  margin-bottom: 0.8rem;
  line-height: 1.6;
}

.icon-wrapper {
  margin-bottom: 20px;
  background: #f4f5f0;
  width: 100px;  /* Tamaño ajustado */
  height: 100px; /* Tamaño ajustado */
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  border: 2px solid #e0e2d6;
}

.icon-wrapper svg {
  width: 60px;   /* Ajuste del tamaño del SVG */
  height: 60px;  /* Ajuste del tamaño del SVG */
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.2rem;
  }
  .about-grid-container {
    margin-top: 40px;
  }
}
</style>
