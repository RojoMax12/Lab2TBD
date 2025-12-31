<template>
  <section class="hero" aria-labelledby="hero-title">
    <div class="hero-card">
      <figure class="hero-media" aria-hidden="true">
        <img src="/camion.png" alt="Camión de recolección" />
      </figure>

      <div class="hero-body">
        <h1 id="hero-title">Recolección eficiente y sostenible</h1>
        <p class="hero-text">
          “Una recolección de residuos más ágil y eficiente significa ciudades más limpias,
          menos impacto ambiental y un mejor aprovechamiento de los recursos. Así,
          contribuimos juntos a un entorno urbano ordenado, saludable y sostenible para las
          próximas generaciones.”
        </p>
      </div>
    </div>
  </section>
</template>

<style scoped>
:root {
  --bg: #ffffff;
  --card-bg: #ffffff;
  --accent: #ffffff;
  --muted: #3a3a3a;
  --shadow: 0 10px 30px rgba(20, 20, 20, 0.08);
}

/* Contenedor general */
.hero {
  background: var(--bg);
  padding: 48px 20px;
  display: flex;
  justify-content: center;
}

/* Card interior para controlar anchura y separación */
.hero-card {
  width: 100%;
  max-width: 1100px;
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: 36px;
  align-items: center;
  background: linear-gradient(180deg, #4e5336, #3f4732);
  padding: 28px;
  border-radius: 12px;
  box-shadow: var(--shadow);
  border: 1px solid rgba(78,83,54,0.06);
}

/* Imagen */
.hero-media img {
  display: block;
  width: 100%;
  height: auto;
  object-fit: cover;
  border-radius: 10px;
  border: 3px solid rgba(78,83,54,0.12);
  box-shadow: 0 8px 20px rgba(78,83,54,0.06);
}

/* Texto */
.hero-body {
  padding: 4px 8px;
}
#hero-title {
  margin: 0 0 12px 0;
  color: #ffffff;
  font-size: 1.5rem;
  line-height: 1.15;
  font-weight: 700;
  letter-spacing: 0.2px;
}
.hero-text {
  margin: 0;
  color: #ffffff;
  font-size: 1rem;
  line-height: 1.7;
  text-align: left;
  font-weight: 500;
}

/* Ajustes para pantallas pequeñas */
@media (max-width: 900px) {
  .hero-card {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
    text-align: center;
  }
  .hero-media img {
    max-width: 520px;
    margin: 0 auto;
  }
  #hero-title {
    font-size: 1.35rem;
  }
  .hero-text {
    text-align: center;
    font-size: 0.98rem;
  }
}

/* Pequeños refinamientos */
@media (max-width: 480px) {
  .hero {
    padding: 28px 12px;
  }
  .hero-card {
    padding: 16px;
    border-radius: 10px;
  }
  #hero-title { font-size: 1.15rem; }
  .hero-text { font-size: 0.95rem; line-height: 1.6; }
}
</style>