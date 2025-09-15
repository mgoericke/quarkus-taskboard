<template>
  <div id="notifications-container">
    <div 
      v-for="notification in notifications" 
      :key="notification.id"
      :class="['alert', `alert-${notification.type}`, 'alert-dismissible', 'fade', 'show', 'notification']"
    >
      {{ notification.message }}
      <button 
        type="button" 
        class="btn-close" 
        @click="removeNotification(notification.id)"
      ></button>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'NotificationComponent',
  setup() {
    const notifications = ref([])

    const show = (message, type = 'info') => {
      const id = Date.now() + Math.random()

      const notification = {
        id,
        message,
        type
      }

      notifications.value.push(notification)

      // Auto remove after 5 seconds
      setTimeout(() => {
        removeNotification(id)
      }, 5000)
    }

    const removeNotification = (id) => {
      const index = notifications.value.findIndex(n => n.id === id)
      if (index !== -1) {
        notifications.value.splice(index, 1)
      }
    }

    return {
      notifications,
      show,
      removeNotification
    }
  }
}
</script>
