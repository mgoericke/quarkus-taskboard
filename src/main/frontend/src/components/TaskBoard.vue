<template>
  <div class="container-fluid mt-3">
    <!-- Header -->
    <div class="row mb-4">
      <div class="col-md-8">
        <h1><i class="fas fa-tasks"></i> Team Task Board</h1>
        <p class="text-muted">Projekt: <span>Quarkus Training Vorbereitung</span></p>
      </div>
      <div class="col-md-4 text-end">
        <span :class="['live-indicator', { 'disconnected': !isConnected }]">
          <i class="fas fa-circle"></i> 
          <span>{{ isConnected ? 'Live' : 'Offline' }}</span>
        </span>
        <button class="btn btn-primary ms-2" @click="showAddModal = true">
          <i class="fas fa-plus"></i> Neue Aufgabe
        </button>
        <button class="btn btn-secondary ms-1" @click="refreshData" :disabled="loading">
          <i class="fas fa-sync-alt"></i> 
          <span v-if="loading" class="spinner-border spinner-border-sm ms-1"></span>
        </button>
      </div>
    </div>

    <!-- Notifications -->
    <NotificationComponent ref="notifications" />

    <!-- Kanban Board -->
    <div class="row mb-4">
      <div v-for="status in statuses" :key="status.key" class="col-md-3">
        <div 
          :class="['kanban-column', { 'drag-over': dragOverColumn === status.key }]"
          :data-status="status.key"
          @dragover="handleDragOver"
          @drop="handleDrop($event, status.key)"
          @dragenter="handleDragEnter(status.key)"
          @dragleave="handleDragLeave"
        >
          <h5 class="mb-3">
            <i :class="status.icon"></i> {{ status.title }}
            <span class="badge bg-secondary">{{ taskCounts[status.key] || 0 }}</span>
          </h5>
          <div class="kanban-tasks">
            <TaskCard
              v-for="task in tasksByStatus[status.key]"
              :key="task.id"
              :task="task"
              @edit="editTask"
              @drag-start="handleDragStart"
              @drag-end="handleDragEnd"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Chat and Statistics Row -->
    <div class="row">
      <div class="col-md-6">
        <ChatComponent />
      </div>
      <div class="col-md-6">
        <StatisticsComponent />
      </div>
    </div>

    <!-- Task Modals -->
    <TaskModal
      v-model:show="showAddModal"
      :members="members"
      :is-edit="false"
      @save="handleCreateTask"
    />

    <TaskModal
      v-model:show="showEditModal"
      :members="members"
      :task="editingTask"
      :is-edit="true"
      @save="handleUpdateTask"
      @delete="handleDeleteTask"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useTaskStore } from '../stores/taskStore'
import { websocketService } from '../services/websocketService'
import TaskCard from './TaskCard.vue'
import TaskModal from './TaskModal.vue'
import ChatComponent from './ChatComponent.vue'
import StatisticsComponent from './StatisticsComponent.vue'
import NotificationComponent from './NotificationComponent.vue'

export default {
  name: 'TaskBoard',
  components: {
    TaskCard,
    TaskModal,
    ChatComponent,
    StatisticsComponent,
    NotificationComponent
  },
  setup() {
    const taskStore = useTaskStore()
    const notifications = ref(null)

    const isConnected = ref(false)
    const dragOverColumn = ref(null)
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const editingTask = ref(null)

    const statuses = [
      { key: 'TODO', title: 'To Do', icon: 'fas fa-inbox' },
      { key: 'IN_PROGRESS', title: 'In Progress', icon: 'fas fa-play' },
      { key: 'REVIEW', title: 'Review', icon: 'fas fa-eye' },
      { key: 'DONE', title: 'Done', icon: 'fas fa-check' }
    ]

    const loading = computed(() => taskStore.loading)
    const tasks = computed(() => taskStore.tasks)
    const members = computed(() => taskStore.members)
    const tasksByStatus = computed(() => taskStore.tasksByStatus)
    const taskCounts = computed(() => taskStore.taskCounts)

    const initializeApp = async () => {
      try {
        await Promise.all([
          taskStore.loadTasks(),
          taskStore.loadMembers(),
          taskStore.loadStatistics()
        ])

        websocketService.connectBoard(
          taskStore.PROJECT_ID, 
          handleWebSocketMessage,
          updateConnectionStatus
        )

        showNotification('Anwendung erfolgreich geladen', 'success')
      } catch (error) {
        console.error('Initialization error:', error)
        showNotification('Fehler beim Laden der Daten', 'danger')
      }
    }

    const refreshData = async () => {
      try {
        await Promise.all([
          taskStore.loadTasks(),
          taskStore.loadStatistics()
        ])
        showNotification('Daten erfolgreich aktualisiert', 'success')
      } catch (error) {
        showNotification('Fehler beim Aktualisieren der Daten', 'danger')
      }
    }

    const handleWebSocketMessage = (data) => {
      taskStore.handleWebSocketMessage(data)

      switch(data.type) {
        case 'task_created':
        case 'task_updated':
        case 'status_changed':
          showNotification(data.message, 'info')
          break
        case 'user_joined':
        case 'user_left':
          showNotification(data.message, 'info')
          break
      }
    }

    const updateConnectionStatus = (connected) => {
      isConnected.value = connected
    }

    const showNotification = (message, type = 'info') => {
      if (notifications.value) {
        notifications.value.show(message, type)
      }
    }

    // Drag and Drop handlers
    const handleDragStart = (taskId) => {
      // Store dragged task ID
      window.draggedTaskId = taskId
    }

    const handleDragEnd = () => {
      dragOverColumn.value = null
      window.draggedTaskId = null
    }

    const handleDragOver = (event) => {
      event.preventDefault()
    }

    const handleDragEnter = (status) => {
      dragOverColumn.value = status
    }

    const handleDragLeave = () => {
      dragOverColumn.value = null
    }

    const handleDrop = async (event, newStatus) => {
      event.preventDefault()
      dragOverColumn.value = null

      const taskId = window.draggedTaskId
      if (!taskId) return

      const task = tasks.value.find(t => t.id === parseInt(taskId))
      if (task && task.status !== newStatus) {
        try {
          await taskStore.updateTaskStatus(parseInt(taskId), newStatus)
          showNotification(`Task zu "${newStatus}" verschoben`, 'success')
        } catch (error) {
          showNotification('Fehler beim Verschieben der Task', 'danger')
        }
      }
    }

    // Task CRUD operations
    const handleCreateTask = async (taskData) => {
      try {
        await taskStore.createTask(taskData)
        showAddModal.value = false
        showNotification(`Task "${taskData.title}" wurde erstellt`, 'success')
      } catch (error) {
        showNotification('Fehler beim Erstellen der Task', 'danger')
      }
    }

    const editTask = (task) => {
      editingTask.value = { ...task }
      showEditModal.value = true
    }

    const handleUpdateTask = async (taskData) => {
      try {
        await taskStore.updateTask(editingTask.value.id, taskData)
        showEditModal.value = false
        editingTask.value = null
        showNotification(`Task "${taskData.title}" wurde aktualisiert`, 'success')
      } catch (error) {
        showNotification('Fehler beim Aktualisieren der Task', 'danger')
      }
    }

    const handleDeleteTask = async () => {
      if (!editingTask.value) return

      if (!confirm('Task wirklich löschen?')) return

      try {
        await taskStore.deleteTask(editingTask.value.id)
        showEditModal.value = false
        editingTask.value = null
        showNotification('Task wurde gelöscht', 'info')
      } catch (error) {
        showNotification('Fehler beim Löschen der Task', 'danger')
      }
    }

    // Lifecycle
    onMounted(() => {
      initializeApp()

      // Setup heartbeat
      const heartbeatInterval = setInterval(() => {
        websocketService.sendHeartbeat()
      }, 30000)

      // Handle visibility change
      const handleVisibilityChange = () => {
        if (!document.hidden) {
          refreshData()
        }
      }
      document.addEventListener('visibilitychange', handleVisibilityChange)

      // Cleanup function will be returned from onUnmounted
      return () => {
        clearInterval(heartbeatInterval)
        document.removeEventListener('visibilitychange', handleVisibilityChange)
      }
    })

    onUnmounted(() => {
      websocketService.disconnect()
    })

    return {
      // Reactive data
      isConnected,
      dragOverColumn,
      showAddModal,
      showEditModal,
      editingTask,
      notifications,

      // Constants
      statuses,

      // Computed
      loading,
      members,
      tasksByStatus,
      taskCounts,

      // Methods
      refreshData,
      handleDragStart,
      handleDragEnd,
      handleDragOver,
      handleDragEnter,
      handleDragLeave,
      handleDrop,
      editTask,
      handleCreateTask,
      handleUpdateTask,
      handleDeleteTask
    }
  }
}
</script>
