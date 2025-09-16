<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.kanban-column {
  background-color: #f8f9fa;
  border-radius: 8px;
  min-height: 500px;
  padding: 15px;
  border: 2px dashed transparent;
  transition: all 0.3s ease;
}

.kanban-column.drag-over {
  border-color: #007bff;
  background-color: #e3f2fd;
}

.task-card {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;
  cursor: move;
  transition: all 0.2s;
  position: relative;
}

.task-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.task-card.dragging {
  opacity: 0.5;
  transform: rotate(5deg);
}

.priority-high { border-left: 4px solid #dc3545; }
.priority-medium { border-left: 4px solid #fd7e14; }
.priority-low { border-left: 4px solid #198754; }

.chat-container {
  height: 350px;
  overflow-y: auto;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #dee2e6;
}

.chat-message {
  margin-bottom: 15px;
  padding: 10px 15px;
  border-radius: 15px;
  max-width: 80%;
  word-wrap: break-word;
}

.chat-user {
  background: #007bff;
  color: white;
  margin-left: auto;
}

.chat-bot {
  background: #e9ecef;
  color: #333;
}

.chat-bot-typing {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeaa7;
}

.live-indicator {
  color: #28a745;
  animation: pulse 2s infinite;
}

.live-indicator.disconnected {
  color: #dc3545;
  animation: none;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 1050;
  min-width: 300px;
}

.task-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.task-card:hover .task-actions {
  opacity: 1;
}

.overdue-indicator {
  color: #dc3545;
  font-weight: bold;
}

.loading {
  display: none;
}

.loading.show {
  display: inline-block;
}
</style>
