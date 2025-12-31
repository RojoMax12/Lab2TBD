<template>
  <div class="flex flex-col min-h-screen">
    <Header />
    <main class="flex-1 flex flex-col items-center justify-center gap-8 p-4">
      <Hero />
      <Categories />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from '../components/Home/Header.vue'
import Hero from '../components/Home/Hero.vue'
import Categories from '../components/Home/Categories.vue'
import Footer from '../components/Home/Footer.vue'
</script>
