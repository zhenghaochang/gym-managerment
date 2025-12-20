<script setup>
</script>

<template>
  <div class="app-container">
    <MainMenu />
    <div class="main-container">
      <Navigation />
      <div class="content-wrapper">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.main-container {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.content-wrapper {
  flex: 1;
  margin-top: 60px;
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}
</style>
