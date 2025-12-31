<template>
  <section class="categories" aria-labelledby="categories-title">
    <div class="categories-card">
      <figure class="categories-media" aria-hidden="true">
        <img src="/residuos.png" alt="Tipos de residuos" />
      </figure>

      <div class="categories-body">
        <h2 id="categories-title">Nos encargamos de todo tipo de residuos</h2>
        <p class="categories-text">
          Ofrecemos servicios integrales de recolección y gestión de residuos:
          orgánicos, inorgánicos, papel, plástico, metal, vidrio, madera y textil.
          Soluciones seguras, responsables y sostenibles adaptadas a tu comunidad.
        </p>

        <div class="actions">
          <router-link to="/services" class="btn">Ver servicios</router-link>
        </div>
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

/* CONTENEDOR GENERAL */
.categories {
  background: var(--bg);
  padding: 48px 20px;
  display: flex;
  justify-content: center;
}

/* CARD PRINCIPAL */
.categories-card {
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

/* IMAGEN */
.categories-media img {
  display: block;
  width: 100%;
  height: auto;
  object-fit: cover;

  border-radius: 10px;
  border: 3px solid rgba(78,83,54,0.12);
  box-shadow: 0 8px 20px rgba(78,83,54,0.06);
}

/* TEXTO */
.categories-body {
  padding: 4px 8px;
}

#categories-title {
  margin: 0 0 14px 0;
  color: #ffffff;
  font-size: 1.5rem;
  line-height: 1.15;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.categories-text {
  margin: 0 0 20px 0;
  color: #ffffff;
  font-size: 1rem;
  line-height: 1.7;
  font-weight: 500;
  max-width: 650px;
}

/* BOTÓN */
.actions .btn {
  display: inline-block;
  background: linear-gradient(180deg, #5e6541, #52563f);
  color: #fff;
  padding: 0.65rem 1.1rem;
  border-radius: 999px;
  text-decoration: none;
  font-weight: 600;

  box-shadow: 0 8px 18px rgba(78,83,54,0.12);
  border: 1px solid rgba(255,255,255,0.08);
  transition: transform 0.2s ease;
}

.actions .btn:hover {
  transform: translateY(-3px);
}

/* RESPONSIVE */
@media (max-width: 900px) {
  .categories-card {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
    text-align: center;
  }

  .categories-media img {
    max-width: 520px;
    margin: 0 auto;
  }

  #categories-title {
    font-size: 1.35rem;
  }

  .categories-text {
    font-size: 0.98rem;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .categories {
    padding: 28px 12px;
  }

  .categories-card {
    padding: 16px;
    border-radius: 10px;
  }

  #categories-title {
    font-size: 1.15rem;
  }

  .categories-text {
    font-size: 0.95rem;
    line-height: 1.6;
  }
}
</style>
