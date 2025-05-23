import apiClient from './api'

const ReportService = {
  //ai 호출임시 비활성화
  AI_ENABLED: true,

  // 완전한 주간 리포트 조회 (건강 분석 + AI 추천)
  async getCompleteWeeklyReport(date = null) {
    const params = {}
    if (date) {
      params.date = date
    }
    if (!this.AI_ENABLED) {
      // AI 없이 기본 리포트만 조회
      return apiClient.get('/reports/weekly', { params })
    }

    return apiClient.get('/reports/weekly/complete', { params })
  },

  // 건강 분석만 조회
  async getWeeklyReport(date = null, requested = false) {
    const params = { requested }
    if (date) {
      params.date = date
    }

    return apiClient.get('/reports/weekly', { params })
  },

  // AI 추천만 조회 (텍스트 형태)
  async getAiRecommendation(startDate = null, endDate = null) {
    if (!this.AI_ENABLED) {
      // 목업 데이터 반환
      return {
        data: {
          recommendation: "AI 분석이 일시적으로 비활성화되었습니다.",
          period: { start: startDate, end: endDate },
          generatedAt: new Date().toISOString()
        }
      }
    }

    const params = {}
    if (startDate) params.startDate = startDate
    if (endDate) params.endDate = endDate

    return apiClient.get('/reports/weekly/ai-recommendation', { params })
  },


  // 최신 리포트 조회
  async getLatestReport() {
    return apiClient.get('/reports/weekly/latest')
  },

  // 리포트 생성 요청
  async generateReport(startDate, endDate) {
    const params = { startDate, endDate }
    return apiClient.post('/reports/weekly/generate', null, { params })
  }
}

export default ReportService
