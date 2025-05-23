package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.WeeklyReportDto;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface WeeklyReportDao {

    WeeklyReportDto selectWeeklyReportById(int id);

    WeeklyReportDto selectWeeklyReportByUserIdAndDataRange(@Param("userId") int userId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    List<WeeklyReportDto> selectByUserId(int userId);

    int insert(WeeklyReportDto report);

    int update(WeeklyReportDto report);

    // AI 추천만 업데이트
    int updateAiRecommendation(@Param("id") int id,
                               @Param("aiRecommendation") String aiRecommendation,
                               @Param("aiGeneratedAt") LocalDateTime aiGeneratedAt);

    // 건강 조언만 업데이트
    int updateHealthTips(@Param("id") int id,
                         @Param("healthTips") String healthTips);
}
