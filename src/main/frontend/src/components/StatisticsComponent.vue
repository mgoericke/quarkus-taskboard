<template>
  <div class="card">
    <div class="card-header">
      <h5><i class="fas fa-chart-bar"></i> Projekt Statistiken</h5>
    </div>

    <div class="card-body">
      <div class="row text-center mb-3">
        <div class="col-3">
          <h3 class="text-primary">{{ statistics.totalCount || 0 }}</h3>
          <small>Gesamt Tasks</small>
        </div>
        <div class="col-3">
          <h3 class="text-warning">{{ statistics.inProgressCount || 0 }}</h3>
          <small>In Arbeit</small>
        </div>
        <div class="col-3">
          <h3 class="text-success">{{ statistics.doneCount || 0 }}</h3>
          <small>Erledigt</small>
        </div>
        <div class="col-3">
          <h3 class="text-danger">{{ overdueTasksCount }}</h3>
          <small>Überfällig</small>
        </div>
      </div>

      <div class="progress mb-3">
        <div 
          class="progress-bar" 
          role="progressbar" 
          :style="{ width: `${completionPercentage}%` }"
          :aria-valuenow="completionPercentage" 
          aria-valuemin="0" 
          aria-valuemax="100"
        >
          {{ completionPercentage }}% abgeschlossen
        </div>
      </div>

      <hr>

      <h6>Team Members</h6>
      <div class="d-flex flex-wrap">
        <span 
          v-for="member in membersWithTaskCount" 
          :key="member.id"
          class="badge bg-primary me-2 mb-1"
        >
          {{ member.name }} ({{ member.taskCount }} Tasks)
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useTaskStore } from '../stores/taskStore'

export default {
  name: 'StatisticsComponent',
  setup() {
    const taskStore = useTaskStore()

    const statistics = computed(() => taskStore.statistics)
    const tasks = computed(() => taskStore.tasks)
    const members = computed(() => taskStore.members)

    const overdueTasksCount = computed(() => taskStore.overdueTasksCount)

    const completionPercentage = computed(() => {
      return Math.round(statistics.value.completionPercentage || 0)
    })

    const membersWithTaskCount = computed(() => {
      return members.value.map(member => ({
        ...member,
        taskCount: tasks.value.filter(task => 
          task.assignedTo && task.assignedTo.id === member.id
        ).length
      }))
    })

    return {
      statistics,
      overdueTasksCount,
      completionPercentage,
      membersWithTaskCount
    }
  }
}
</script>
