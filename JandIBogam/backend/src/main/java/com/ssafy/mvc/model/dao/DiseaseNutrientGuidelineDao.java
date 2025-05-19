package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.NutrientGuideDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiseaseNutrientGuidelineDao {
    //질병 id로 가이드라인 내용 가져오기
    List<NutrientGuideDto> selectByDiseaseId(int diseaseId);
    //다중질병일 때
    List<NutrientGuideDto> selectByDiseaseIds(@Param("diseaseIds") List<Integer> diseaseIds);


//    //질병 - 영양소 id
//    DiseaseNutrientGuidelineDto selectByDiseaseIdAndNutrientId(
//            @Param("diseaseId") int diseaseId,
//            @Param("nutrientId") int nutrientId
//    );
}
