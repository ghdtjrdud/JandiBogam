import apiClient from './api'

const FileService = {
  async uploadImage(file) {
    // 파일 크기 체크 (서버 전송 전 마지막 체크)
    if (file.size > 5 * 1024 * 1024) {
      throw new Error('파일 크기는 5MB 이하여야 합니다.')
    }

    // 파일 타입 체크
    if (!file.type.startsWith('image/')) {
      throw new Error('이미지 파일만 업로드 가능합니다.')
    }

    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await apiClient.post('/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        timeout: 30000, // 30초 타임아웃
      })

      return response
    } catch (error) {
      console.error('이미지 업로드 실패:', error)

      if (error.code === 'ECONNABORTED') {
        throw new Error('업로드 시간이 초과되었습니다. 더 작은 이미지를 사용해주세요.')
      } else if (error.response?.status === 413) {
        throw new Error('파일이 너무 큽니다. 더 작은 이미지를 사용해주세요.')
      } else if (error.response?.data?.message) {
        throw new Error(error.response.data.message)
      } else {
        throw new Error('이미지 업로드 중 오류가 발생했습니다.')
      }
    }
  },

  // 이미지 압축 품질별 설정
  getCompressionSettings(fileSize) {
    if (fileSize > 5 * 1024 * 1024) { // 5MB 이상
      return { maxWidth: 600, maxHeight: 450, quality: 0.6 }
    } else if (fileSize > 2 * 1024 * 1024) { // 2MB 이상
      return { maxWidth: 800, maxHeight: 600, quality: 0.7 }
    } else {
      return { maxWidth: 1000, maxHeight: 750, quality: 0.8 }
    }
  }
}

export default FileService
