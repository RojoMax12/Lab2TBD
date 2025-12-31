<template>
  <div>
    <Header />

    <main class="contact-container">
      <!-- Sección de encabezado del formulario de contacto -->
      <section class="contact-hero">
        <h1 class="contact-title">Contáctanos</h1>
        <p class="contact-subtitle">
          Nos encantaría saber de ti. Si tienes alguna pregunta, inquietud o sugerencia, no dudes en ponerte en contacto con nosotros. Estamos aquí para ayudarte.
        </p>
      </section>

      <!-- Formulario de contacto -->
      <section class="contact-form-section">
        <form @submit.prevent="submitForm" class="contact-form">
          <div class="form-group">
            <label for="name" class="form-label">Nombre</label>
            <input id="name" type="text" v-model="form.name" required placeholder="Ingresa tu nombre" class="form-input" />
          </div>

          <div class="form-group">
            <label for="email" class="form-label">Correo Electrónico</label>
            <input id="email" type="email" v-model="form.email" required placeholder="Ingresa tu correo electrónico" class="form-input" />
          </div>

          <div class="form-group">
            <label for="subject" class="form-label">Asunto</label>
            <input id="subject" type="text" v-model="form.subject" required placeholder="Ingresa el asunto" class="form-input" />
          </div>

          <div class="form-group">
            <label for="message" class="form-label">Mensaje</label>
            <textarea id="message" v-model="form.message" required placeholder="Escribe tu mensaje" class="form-input"></textarea>
          </div>

          <button type="submit" class="submit-btn">Enviar</button>
        </form>
      </section>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import Header from '@/components/Home/Header.vue';
import Footer from '@/components/Home/Footer.vue';

const form = ref({
  name: '',
  email: '',
  subject: '',
  message: ''
});

function submitForm() {
  // Lógica para enviar el formulario
  console.log('Formulario enviado', form.value);
  alert('¡Gracias por contactarnos!');
  form.value = { name: '', email: '', subject: '', message: '' }; // Limpiar formulario después de enviarlo
}
</script>

<style scoped>
:root {
  --green-start: #4e5336;  /* Verde más oscuro */
  --green-end: #6a704a;    /* Verde más claro */
  --text-color: #000000;    /* Texto negro */
  --button-text-color: #ffffff; /* Texto blanco en botones */
}

.contact-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 3rem 2rem;
  font-family: 'Roboto', sans-serif;
}

.contact-hero {
  text-align: center;
  margin-bottom: 0.1rem;
  background: linear-gradient(180deg, var(--green-start), var(--green-end)); /* Fondo verde */
  padding: 4rem 2rem;
  border-radius: 8px;
}

.contact-title {
  font-size: 2.8rem;
  font-weight: 700;
  color: black; /* Texto blanco */
  margin-bottom: 15px;
}

.contact-subtitle {
  font-size: 1.2rem;
  color: black; /* Texto blanco */
  max-width: 750px;
  margin: 0 auto;
  line-height: 1.6;
  opacity: 0.9;
}

.contact-form-section {
  background-color: #4e5336;
  padding: 3rem 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-size: 1rem;
  color: white; /* Texto negro */
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.form-input {
  width: 100%;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #000000;
  font-size: 1rem;
  background-color: #fff;
  transition: border 0.3s ease;
}

.form-input:focus {
  border-color: var(--green-start);  /* Borde verde en el foco */
  outline: none;
}

textarea.form-input {
  min-height: 150px;
}

.submit-btn {
  background-color: white;  /* Botón verde */
  color: #6a704a;  /* Texto blanco */
  padding: 1rem 2rem;
  border-radius: 50px;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.3s ease, transform 0.3s ease;
  font-weight: 700;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.submit-btn:hover {
  background-color: var(--green-end);  /* Fondo verde claro en hover */
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .contact-title {
    font-size: 2.2rem;
  }

  .contact-form-section {
    padding: 2.5rem 1.5rem;
  }
}
</style>
