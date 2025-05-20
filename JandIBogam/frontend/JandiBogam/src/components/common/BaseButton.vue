<template>
  <!-- DaisyUI 스타일을 props로 바꿀 수도 있음 -->
  <button
    class="btn"
    :class="typeClass"
    @click="$emit('click', $event)"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: String, default: 'primary' } // primary, accent, warning, 등
})

const typeClass = computed(() => {
  return `btn-${props.type}`
})
</script>
