<script setup>
import { computed } from 'vue'
import AdminDashboard from './AdminDashboard.vue'
import MemberDashboard from './MemberDashboard.vue'

const isAdmin = computed(() => {
  const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
  return [1, 2, 3].includes(localUser.userType)
})
</script>

<template>
  <AdminDashboard v-if="isAdmin" />
  <MemberDashboard v-else />
</template>
