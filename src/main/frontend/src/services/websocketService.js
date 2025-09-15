class WebSocketService {
  constructor() {
    this.boardWebSocket = null
    this.chatWebSocket = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectInterval = 3000
  }

  connectBoard(projectId, onMessage, onStatusChange) {
    try {
      this.boardWebSocket = new WebSocket(`ws://localhost:8080/ws/board/${projectId}`)

      this.boardWebSocket.onopen = () => {
        console.log('Board WebSocket connected')
        this.reconnectAttempts = 0
        if (onStatusChange) onStatusChange(true)
      }

      this.boardWebSocket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          if (onMessage) onMessage(data)
        } catch (error) {
          console.error('Error parsing board message:', error)
        }
      }

      this.boardWebSocket.onclose = () => {
        console.log('Board WebSocket disconnected')
        if (onStatusChange) onStatusChange(false)
        this.handleReconnect(() => this.connectBoard(projectId, onMessage, onStatusChange))
      }

      this.boardWebSocket.onerror = (error) => {
        console.error('Board WebSocket error:', error)
        if (onStatusChange) onStatusChange(false)
      }
    } catch (error) {
      console.error('Error connecting board WebSocket:', error)
      if (onStatusChange) onStatusChange(false)
    }
  }

  connectChat(projectId, onMessage, onStatusChange) {
    try {
      this.chatWebSocket = new WebSocket(`ws://localhost:8080/ws/chat/${projectId}`)

      this.chatWebSocket.onopen = () => {
        console.log('Chat WebSocket connected')
        if (onStatusChange) onStatusChange(true)
      }

      this.chatWebSocket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data)
          if (onMessage) onMessage(data)
        } catch (error) {
          console.error('Error parsing chat message:', error)
        }
      }

      this.chatWebSocket.onclose = () => {
        console.log('Chat WebSocket disconnected')
        if (onStatusChange) onStatusChange(false)
        this.handleReconnect(() => this.connectChat(projectId, onMessage, onStatusChange))
      }

      this.chatWebSocket.onerror = (error) => {
        console.error('Chat WebSocket error:', error)
        if (onStatusChange) onStatusChange(false)
      }
    } catch (error) {
      console.error('Error connecting chat WebSocket:', error)
      if (onStatusChange) onStatusChange(false)
    }
  }

  sendChatMessage(message) {
    if (this.chatWebSocket && this.chatWebSocket.readyState === WebSocket.OPEN) {
      this.chatWebSocket.send(JSON.stringify(message))
    }
  }

  handleReconnect(reconnectFunction) {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      setTimeout(() => {
        this.reconnectAttempts++
        reconnectFunction()
      }, this.reconnectInterval)
    }
  }

  disconnect() {
    if (this.boardWebSocket) {
      this.boardWebSocket.close()
      this.boardWebSocket = null
    }
    if (this.chatWebSocket) {
      this.chatWebSocket.close()
      this.chatWebSocket = null
    }
  }

  sendHeartbeat() {
    if (this.boardWebSocket && this.boardWebSocket.readyState === WebSocket.OPEN) {
      this.boardWebSocket.send(JSON.stringify({ type: 'ping' }))
    }
  }
}

export const websocketService = new WebSocketService()
