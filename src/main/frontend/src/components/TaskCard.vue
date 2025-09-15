<template>
  <div 
    :class="['task-card', `priority-${task.priority.toLowerCase()}`, { 'dragging': isDragging }]"
    draggable="true"
    :data-task-id="task.id"
    @dragstart="handleDragStart"
    @dragend="handleDragEnd"
  >
    <div class="task-actions">
      <button 
        class="btn btn-sm btn-outline-primary" 
        @click="$emit('edit', task)"
        title="Bearbeiten"
      >
        <i class="fas fa-edit"></i>
      </button>
    </div>

    <h6>{{ task.title }}</h6>

    <p v-if="task.description" class="text-muted small">
      {{ task.description }}
    </p>

    <small v-if="task.dueDate" :class="['text-muted', { 'overdue-indicator': isOverdue }]">
      <i class="fas fa-calendar"></i> {{ formatDate(task.dueDate) }}
      <span v-if="isOverdue"> (ÜBERFÄLLIG)</span>
    </small>

    <div class="d-flex justify-content-between align-items-center mt-2">
      <span :class="['badge', priorityBadgeClass]">
        {{ task.priority }}
      </span>
      <small class="text-muted">
        <i class="fas fa-user"></i> {{ assigneeName }}
      </small>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { formatDate } from '../utils/dateUtils'

export default {
  name: 'TaskCard',
  props: {
    task: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'drag-start', 'drag-end'],
  setup(props, { emit }) {
    const isDragging = ref(false)

    const isOverdue = computed(() => {
      return props.task.dueDate && 
             new Date(props.task.dueDate) < new Date() && 
             props.task.status !== 'DONE'
    })

    const assigneeName = computed(() => {
      return props.task.assignedTo ? props.task.assignedTo.name : 'Unassigned'
    })

    const priorityBadgeClass = computed(() => {
      const classes = {
        'LOW': 'bg-success',
        'MEDIUM': 'bg-warning',
        'HIGH': 'bg-danger'
      }
      return classes[props.task.priority] || 'bg-secondary'
    })

    const handleDragStart = (event) => {
      isDragging.value = true
      event.dataTransfer.setData('text/plain', props.task.id.toString())
      emit('drag-start', props.task.id)
    }

    const handleDragEnd = () => {
      isDragging.value = false
      emit('drag-end')
    }

    return {
      isDragging,
      isOverdue,
      assigneeName,
      priorityBadgeClass,
      handleDragStart,
      handleDragEnd,
      formatDate
    }
  }
}
</script>
