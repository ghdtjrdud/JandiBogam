// src/services/MealCommentService.js
import apiClient from './api'

const MealCommentService = {
  // 댓글 목록 조회
  async fetchComments(mealId) {
    return apiClient.get(`/meals/${mealId}/comments`)
  },

  // 댓글 등록
  async createComment(mealId, content) {
    // mealId는 path, content만 body로!
    return apiClient.post(`/meals/${mealId}/comments`, { content })
  },

  // 댓글 수정
  async updateComment(commentId, content) {
    return apiClient.put(`/meals/comments/${commentId}`, { content })
  },

  // 댓글 삭제
  async deleteComment(commentId) {
    return apiClient.delete(`/meals/comments/${commentId}`)
  },
}

export default MealCommentService
