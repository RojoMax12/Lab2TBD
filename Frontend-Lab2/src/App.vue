<template>
  <div class="app-wrapper">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style>
html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  background-color: #f7e8dd;
  padding-bottom: 80px;
}

#app, .app-wrapper {
  display: flex;
  flex-direction: column;
  min-height: auto;
  height: auto;
  width: 100%;
  background-color: #f7e8dd;
  margin: 0;
  padding: 0;
  padding-bottom: 80px; /* espacio abajo */
}

/* Quitamos el max-width y el margin-top que limitaba la vista */
</style>
