<!-- components/BaseLayout.vue -->
<template>
  <div class="min-h-screen w-full">
    <Header/>
  <main class="w-full max-w-[1024px] px-8 mx-auto py-10">
    <slot />
  </main>
  </div>
</template>

<script setup>
import Header from './Header.vue';
</script>
