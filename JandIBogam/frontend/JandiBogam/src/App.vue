<script setup>
import { RouterLink, RouterView } from 'vue-router'

// 공용 버튼 컴포넌트 import
import BaseButton from './components/common/BaseButton.vue';

function onClick() {
  alert('BaseButton 클릭됨!')
}

</script>

<template>
  <header>
    <!-- 로고, HelloWorld 삭제! -->
    <div class="wrapper">
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/about">About</RouterLink>
      </nav>
    </div>
    <!-- DaisyUI + Tailwind 테스트용 버튼 -->
    <div class="p-10">
      <h1 class="text-2xl font-bold mb-6">Hello, DaisyUI + Tailwind + Vue!</h1>
      <BaseButton type="primary" @click="onClick">BaseButton - primary</BaseButton>
      <BaseButton type="accent" class="m1-2">BaseButton - Accent</BaseButton>
      <button class="btn btn-primary m1-2">DaisyUI 기본 버튼</button>
    </div>
  </header>
  <RouterView />
</template>


<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

/* .logo, HelloWorld 관련 스타일 삭제 */

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  /* .logo 관련 스타일 삭제 */

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;
    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
