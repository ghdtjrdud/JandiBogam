package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.DailyNutrientSummaryDto;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface DailyNutrientSummaryDao {

    //날짜 범위로 특정 사용자의 일일 영양소 섭취량 조회
    List<DailyNutrientSummaryDto> selectByUserIdAndDateRange(@Param("userId") int userId,
                                                        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //사용자 id와 날짜로 모든 영양소 요약 조회
    List<DailyNutrientSummaryDto> selectByUserIdAndDate(@Param("userId") int userId, @Param("date") LocalDate date);

    int insert(DailyNutrientSummaryDto dailyNutrientSummary);

    int deleteByUserIdAndDate(@Param("userId") int userId,
                              @Param("summaryDate") LocalDate summaryDate);

    /// /////////////////////////////////////
    //사용자 id와 날짜, 영양소 id로 단일 정보 가져오기
    DailyNutrientSummaryDto selectByUserIdAndNutrient(  @Param("userId") int userId,
                                                    @Param("summaryDate") LocalDate summaryDate,
                                                    @Param("nutrientId") int nutrientId);

    int update(DailyNutrientSummaryDto dailyNutrientSummary);

    //특정 사용자의 특정 날짜에 특정 영양소 데이터가 이미 존재하면 업데이트, 없으면 삽입
    //데이터 존재 여부를 확인하기 위한 추가 쿼리가 필요 없기 때문에 효율성을 위해 만듦
    int upsert(@Param("userId") int userId,
               @Param("summaryDate") LocalDate summaryDate,
               @Param("nutrientId") int nutrientId,
               @Param("totalAmount") BigDecimal totalAmount);

    void batchUpsert(List<DailyNutrientSummaryDto> summaries);

}
