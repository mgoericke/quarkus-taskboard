export function formatDate(dateString) {
  if (!dateString) return ''

  const date = new Date(dateString)
  const now = new Date()
  const diffTime = date.getTime() - now.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

  const formattedDate = date.toLocaleDateString('de-DE', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })

  if (diffDays < 0) {
    return formattedDate
  } else if (diffDays === 0) {
    return `Heute, ${date.toLocaleTimeString('de-DE', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })}`
  } else if (diffDays === 1) {
    return `Morgen, ${date.toLocaleTimeString('de-DE', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })}`
  } else {
    return formattedDate
  }
}

export function isOverdue(dateString) {
  if (!dateString) return false
  return new Date(dateString) < new Date()
}

export function formatDateForInput(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
    .toISOString().slice(0, 16)
}
