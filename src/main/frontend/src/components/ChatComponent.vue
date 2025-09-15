<template>
  <div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h5><i class="fas fa-robot"></i> Task Assistant (KI-Bot)</h5>
      <span 
        :class="['badge', isConnected ? 'bg-success' : 'bg-danger']"
      >
        {{ isConnected ? 'Online' : 'Offline' }}
      </span>
    </div>

    <div class="card-body">
      <div 
        class="chat-container" 
        ref="chatContainer"
      >
        <div 
          v-for="message in messages" 
          :key="message.id"
          :class="['chat-message', message.isUser ? 'chat-user' : 'chat-bot', { 'chat-bot-typing': message.isTyping }]"
        >
          <strong>{{ message.isUser ? 'Du' : 'Bot' }}:</strong>
          <template v-if="message.isUser">
            {{ message.text }}
          </template>
          <div v-else class="bot-message-content">
            <VueMarkdown :source="message.text" />
          </div>
        </div>
      </div>

      <div class="mt-3">
        <div class="input-group">
          <input 
            type="text" 
            class="form-control" 
            v-model="currentMessage"
            @keypress="handleKeyPress"
            placeholder="Frage zum Projekt..."
            :disabled="!isConnected"
          >
          <button 
            class="btn btn-primary" 
            @click="sendMessage"
            :disabled="!isConnected || !currentMessage.trim()"
          >
            <i class="fas fa-paper-plane"></i>
          </button>
        </div>

        <div class="mt-2">
          <small class="text-muted">
            Beispiele: "Welche Tasks sind überfällig?", "Zeige mir alle High Priority Tasks", "Was arbeitet Max gerade?"
          </small>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { websocketService } from '../services/websocketService'
import { useTaskStore } from '../stores/taskStore'
import VueMarkdown from 'vue-markdown-render'

export default {
  name: 'ChatComponent',
  components: {
    VueMarkdown
  },
  setup() {
    const taskStore = useTaskStore()
    const chatContainer = ref(null)

    const isConnected = ref(false)
    const currentMessage = ref('')
    const messages = ref([
      {
        id: Date.now(),
        text: 'Verbinde mit dem Server...',
        isUser: false,
        isTyping: false
      }
    ])
    const sessionId = generateUUID()

    const scrollToBottom = () => {
      nextTick(() => {
        if (chatContainer.value) {
          chatContainer.value.scrollTop = chatContainer.value.scrollHeight
        }
      })
    }

    const addMessage = (text, isUser = false, isTyping = false) => {
      const message = {
        id: Date.now() + Math.random(),
        text,
        isUser,
        isTyping
      }

      messages.value.push(message)
      scrollToBottom()

      if (isTyping) {
        // Remove typing indicator after 2 seconds
        setTimeout(() => {
          const index = messages.value.findIndex(m => m.id === message.id)
          if (index !== -1) {
            messages.value.splice(index, 1)
          }
        }, 2000)
      }

      return message
    }

    const handleChatMessage = (data) => {
      switch(data.type) {
        case 'bot_message':
          addMessage(data.message, false)
          break
        case 'bot_typing':
          addMessage(data.message, false, true)
          break
        case 'bot_error':
          addMessage(`❌ ${data.message}`, false)
          break
        case 'error':
          addMessage(`⚠️ ${data.message}`, false)
          break
      }
    }

    const updateChatStatus = (connected) => {
      isConnected.value = connected

      if (connected) {
        // Clear initial message and show connected message
        messages.value = []
        addMessage('Hallo! Ich bin Ihr Task-Assistent. Wie kann ich Ihnen helfen?', false)
      } else {
        addMessage('Verbindung verloren. Versuche erneut zu verbinden...', false)
      }
    }

    const sendMessage = () => {
      const message = currentMessage.value.trim()
      if (!message || !isConnected.value) return

      // Add user message
      addMessage(message, true)

      // Send to server
      const chatMessage = {
        type: 'user_message',
        message: message,
        sessionId: sessionId
      }

      websocketService.sendChatMessage(chatMessage)
      currentMessage.value = ''
    }

    const handleKeyPress = (event) => {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        sendMessage()
      }
    }

    function generateUUID() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0
        const v = c == 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
      })
    }

    onMounted(() => {
      websocketService.connectChat(
        taskStore.PROJECT_ID,
        handleChatMessage,
        updateChatStatus
      )
    })

    return {
      chatContainer,
      isConnected,
      currentMessage,
      messages,
      sendMessage,
      handleKeyPress
    }
  }
}
</script>

<style scoped>
.chat-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 1rem;
  background-color: #f8f9fa;
}

.chat-message {
  margin-bottom: 1rem;
  padding: 0.75rem;
  border-radius: 0.375rem;
}

.chat-user {
  background-color: #007bff;
  color: white;
  margin-left: 2rem;
}

.chat-bot {
  background-color: #f5f5f5;
  margin-right: 2rem;
  color: #333;
}

.chat-bot-typing {
  opacity: 0.7;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.7;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.7;
  }
}

.bot-message-content {
  margin-top: 0.5rem;
}

/* Markdown-spezifische Styles */
.bot-message-content :deep(h1),
.bot-message-content :deep(h2),
.bot-message-content :deep(h3),
.bot-message-content :deep(h4),
.bot-message-content :deep(h5),
.bot-message-content :deep(h6) {
  margin-top: 1rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.bot-message-content :deep(h1) { font-size: 1.5rem; }
.bot-message-content :deep(h2) { font-size: 1.35rem; }
.bot-message-content :deep(h3) { font-size: 1.2rem; }

.bot-message-content :deep(p) {
  margin-bottom: 0.75rem;
  line-height: 1.5;
}

.bot-message-content :deep(code) {
  background-color: #f1f3f4;
  padding: 0.125rem 0.25rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-family: 'Courier New', Courier, monospace;
}

.bot-message-content :deep(pre) {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 0.375rem;
  overflow-x: auto;
  margin-bottom: 0.75rem;
  border: 1px solid #dee2e6;
}

.bot-message-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
  font-size: 0.875rem;
}

.bot-message-content :deep(ul),
.bot-message-content :deep(ol) {
  margin-bottom: 0.75rem;
  padding-left: 1.5rem;
}

.bot-message-content :deep(li) {
  margin-bottom: 0.25rem;
}

.bot-message-content :deep(blockquote) {
  border-left: 4px solid #007bff;
  padding-left: 1rem;
  margin: 1rem 0;
  color: #6c757d;
  font-style: italic;
}

.bot-message-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 0.75rem;
}

.bot-message-content :deep(th),
.bot-message-content :deep(td) {
  border: 1px solid #dee2e6;
  padding: 0.5rem;
  text-align: left;
}

.bot-message-content :deep(th) {
  background-color: #f8f9fa;
  font-weight: 600;
}

.bot-message-content :deep(a) {
  color: #007bff;
  text-decoration: none;
}

.bot-message-content :deep(a:hover) {
  text-decoration: underline;
}

.bot-message-content :deep(strong) {
  font-weight: 600;
}

.bot-message-content :deep(em) {
  font-style: italic;
}
</style>
