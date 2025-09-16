import { defineStore } from 'pinia'
import { apiService } from '../services/apiService'

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    projects: [],
    tasks: [],
    members: [],
    statistics: {},
    loading: false,
    error: null,
    PROJECT_ID: 1
  }),

  getters: {
    tasksByStatus: (state) => {
      return {
        'TODO': state.tasks.filter(task => task.status === 'TODO'),
        'IN_PROGRESS': state.tasks.filter(task => task.status === 'IN_PROGRESS'),
        'REVIEW': state.tasks.filter(task => task.status === 'REVIEW'),
        'DONE': state.tasks.filter(task => task.status === 'DONE')
      }
    },

    taskCounts: (state) => {
      const counts = { 'TODO': 0, 'IN_PROGRESS': 0, 'REVIEW': 0, 'DONE': 0 }
      state.tasks.forEach(task => {
        if (counts.hasOwnProperty(task.status)) {
          counts[task.status]++
        }
      })
      return counts
    },

    overdueTasksCount: (state) => {
      const now = new Date()
      return state.tasks.filter(task => 
        task.dueDate && 
        new Date(task.dueDate) < now && 
        task.status !== 'DONE'
      ).length
    }
  },

  actions: {
    async loadProjects() {
      this.loading = true
      this.error = null
      try {
        this.projects = await apiService.getProjects()
      } catch (error) {
        this.error = 'Fehler beim Laden der Projekte'
        console.error('Error loading projects:', error)
      } finally {
        this.loading = false
      }
    },
    async loadTasks() {
      this.loading = true
      this.error = null
      try {
        this.tasks = await apiService.getTasks(this.PROJECT_ID)
      } catch (error) {
        this.error = 'Fehler beim Laden der Tasks'
        console.error('Error loading tasks:', error)
      } finally {
        this.loading = false
      }
    },

    async loadMembers() {
      try {
        this.members = await apiService.getMembers()
      } catch (error) {
        this.error = 'Fehler beim Laden der Team-Mitglieder'
        console.error('Error loading members:', error)
      }
    },

    async loadStatistics() {
      try {
        this.statistics = await apiService.getStatistics(this.PROJECT_ID)
      } catch (error) {
        console.error('Error loading statistics:', error)
      }
    },

    async createTask(taskData) {
      this.loading = true
      try {
        const newTask = await apiService.createTask({
          ...taskData,
          projectId: this.PROJECT_ID
        })
        this.tasks.push(newTask)
        await this.loadStatistics()
        return newTask
      } catch (error) {
        this.error = 'Fehler beim Erstellen der Task'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateTask(taskId, taskData) {
      this.loading = true
      try {
        const updatedTask = await apiService.updateTask(taskId, taskData)
        const index = this.tasks.findIndex(t => t.id === taskId)
        if (index !== -1) {
          this.tasks[index] = updatedTask
        }
        await this.loadStatistics()
        return updatedTask
      } catch (error) {
        this.error = 'Fehler beim Aktualisieren der Task'
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateTaskStatus(taskId, newStatus) {
      try {
        const updatedTask = await apiService.updateTaskStatus(taskId, newStatus)
        const index = this.tasks.findIndex(t => t.id === taskId)
        if (index !== -1) {
          this.tasks[index] = updatedTask
        }
        await this.loadStatistics()
        return updatedTask
      } catch (error) {
        this.error = 'Fehler beim Aktualisieren des Task-Status'
        throw error
      }
    },

    async deleteTask(taskId) {
      this.loading = true
      try {
        await apiService.deleteTask(taskId)
        this.tasks = this.tasks.filter(t => t.id !== taskId)
        await this.loadStatistics()
      } catch (error) {
        this.error = 'Fehler beim Löschen der Task'
        throw error
      } finally {
        this.loading = false
      }
    },

    handleWebSocketMessage(data) {
      switch(data.type) {
        case 'task_created':
        case 'task_updated':
        case 'status_changed':
          this.loadTasks()
          this.loadStatistics()
          break
      }
    }
  }
})
