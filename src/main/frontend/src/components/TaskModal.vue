<template>
  <div 
    class="modal fade" 
    :class="{ show: show }" 
    :style="{ display: show ? 'block' : 'none' }"
    tabindex="-1"
    @click="handleBackdropClick"
  >
    <div class="modal-dialog" @click.stop>
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">
            {{ isEdit ? 'Task bearbeiten' : 'Neue Aufgabe erstellen' }}
          </h5>
          <button type="button" class="btn-close" @click="close"></button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <div class="mb-3">
              <label class="form-label">Titel *</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="formData.title" 
                required
                :class="{ 'is-invalid': errors.title }"
              >
              <div v-if="errors.title" class="invalid-feedback">
                {{ errors.title }}
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Beschreibung</label>
              <textarea 
                class="form-control" 
                v-model="formData.description" 
                rows="3"
              ></textarea>
            </div>

            <div class="mb-3">
              <label class="form-label">Zugewiesen an</label>
              <select class="form-control" v-model="formData.assignedToId">
                <option value="">Nicht zugewiesen</option>
                <option 
                  v-for="member in members" 
                  :key="member.id" 
                  :value="member.id"
                >
                  {{ member.name }} ({{ member.role }})
                </option>
              </select>
            </div>

            <div class="mb-3">
              <label class="form-label">Priorität</label>
              <select class="form-control" v-model="formData.priority">
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>

            <div class="mb-3">
              <label class="form-label">Deadline (optional)</label>
              <input 
                type="datetime-local" 
                class="form-control" 
                v-model="formData.dueDate"
              >
            </div>
          </form>
        </div>

        <div class="modal-footer">
          <button 
            v-if="isEdit" 
            type="button" 
            class="btn btn-danger" 
            @click="handleDelete"
            :disabled="loading"
          >
            <i class="fas fa-trash"></i> Löschen
          </button>

          <button type="button" class="btn btn-secondary" @click="close">
            Abbrechen
          </button>

          <button 
            type="button" 
            class="btn btn-primary" 
            @click="handleSubmit"
            :disabled="loading || !isFormValid"
          >
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            {{ isEdit ? 'Speichern' : 'Task erstellen' }}
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Backdrop -->
  <div 
    v-if="show" 
    class="modal-backdrop fade show"
  ></div>
</template>

<script>
import { ref, computed, watch, nextTick } from 'vue'

export default {
  name: 'TaskModal',
  props: {
    show: {
      type: Boolean,
      default: false
    },
    members: {
      type: Array,
      default: () => []
    },
    task: {
      type: Object,
      default: null
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:show', 'save', 'delete'],
  setup(props, { emit }) {
    const loading = ref(false)
    const errors = ref({})

    const formData = ref({
      title: '',
      description: '',
      assignedToId: '',
      priority: 'MEDIUM',
      dueDate: ''
    })

    const isFormValid = computed(() => {
      return formData.value.title.trim().length > 0
    })

    // Watch for task changes to populate form
    watch(() => props.task, (newTask) => {
      if (newTask && props.isEdit) {
        formData.value = {
          title: newTask.title || '',
          description: newTask.description || '',
          assignedToId: newTask.assignedTo ? newTask.assignedTo.id.toString() : '',
          priority: newTask.priority || 'MEDIUM',
          dueDate: newTask.dueDate ? formatDateForInput(newTask.dueDate) : ''
        }
      }
    }, { immediate: true })

    // Reset form when modal closes
    watch(() => props.show, (newShow) => {
      if (!newShow) {
        resetForm()
      } else if (!props.isEdit) {
        resetForm()
      }
    })

    const resetForm = () => {
      formData.value = {
        title: '',
        description: '',
        assignedToId: '',
        priority: 'MEDIUM',
        dueDate: ''
      }
      errors.value = {}
      loading.value = false
    }

    const validateForm = () => {
      errors.value = {}

      if (!formData.value.title.trim()) {
        errors.value.title = 'Titel ist erforderlich'
      }

      return Object.keys(errors.value).length === 0
    }

    const handleSubmit = async () => {
      if (!validateForm()) return

      loading.value = true

      try {
        const taskData = {
          title: formData.value.title.trim(),
          description: formData.value.description.trim(),
          assignedToId: formData.value.assignedToId ? parseInt(formData.value.assignedToId) : null,
          priority: formData.value.priority,
          dueDate: formData.value.dueDate || null
        }

        emit('save', taskData)
      } catch (error) {
        console.error('Form submission error:', error)
      } finally {
        loading.value = false
      }
    }

    const handleDelete = () => {
      emit('delete')
    }

    const close = () => {
      emit('update:show', false)
    }

    const handleBackdropClick = () => {
      close()
    }

    const formatDateForInput = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
        .toISOString().slice(0, 16)
    }

    return {
      loading,
      errors,
      formData,
      isFormValid,
      handleSubmit,
      handleDelete,
      close,
      handleBackdropClick
    }
  }
}
</script>
