// src/services/MedicationService.js
import apiClient from './api'

const MedicationService = {
  createMedication(medicationData) {
    return apiClient.post('/medications', medicationData)
  },

  getMedicationById(id) {
    return apiClient.get(`/medications/${id}`)
  },

  getMedicationsByUserId(userId) {
    return apiClient.get(`/medications/users/${userId}`)
  },

  updateMedication(id, medicationData) {
    return apiClient.put(`/medications/${id}`, medicationData)
  },

  deleteMedication(id) {
    return apiClient.delete(`/medications/${id}`)
  }
}

export default MedicationService
