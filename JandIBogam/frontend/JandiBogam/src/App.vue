<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <RouterView />
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}

nav a {
  color: var(--color-text);
  text-decoration: none;
}

nav a.router-link-active {
  font-weight: bold;
  color: #4f46e5;
}
</style>
