package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.WeeklyNutrientStatsDto;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyReportMapper { //복잡한 sql 최적화를 위한 인터페이스
    List<WeeklyNutrientStatsDto> getWeeklyStats(
            @Param("userId") int userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    int getMealCount(
            @Param("userId") int userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
