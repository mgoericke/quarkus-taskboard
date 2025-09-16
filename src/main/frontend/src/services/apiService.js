import axios from 'axios'

const API_BASE = 'http://localhost:8080/api'

const apiClient = axios.create({
  baseURL: API_BASE,
  timeout: 10000
})

export const apiService = {
  async getProjects() {
    const response = await apiClient.get(`/projects`)
    return response.data
  },
  async getTasks(projectId) {
    const response = await apiClient.get(`/tasks?projectId=${projectId}`)
    return response.data
  },

  async createTask(taskData) {
    const response = await apiClient.post('/tasks', taskData)
    return response.data
  },

  async updateTask(taskId, taskData) {
    const response = await apiClient.put(`/tasks/${taskId}`, taskData)
    return response.data
  },

  async updateTaskStatus(taskId, status) {
    const response = await apiClient.put(`/tasks/${taskId}/status?status=${status}`)
    return response.data
  },

  async deleteTask(taskId) {
    await apiClient.delete(`/tasks/${taskId}`)
  },

  async getMembers() {
    const response = await apiClient.get('/members')
    return response.data
  },

  async getStatistics(projectId) {
    const response = await apiClient.get(`/tasks/stats/${projectId}`)
    return response.data
  }
}
