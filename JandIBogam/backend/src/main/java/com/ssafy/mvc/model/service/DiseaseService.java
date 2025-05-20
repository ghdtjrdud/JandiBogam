package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.DiseaseNutrientGuidelineDao;
import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dto.NutrientGuideDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiseaseService {

    private final UserDao userDao;
    private final DiseaseNutrientGuidelineDao diseaseNutrientGuidelineDao;

    public DiseaseService(UserDao userDao, DiseaseNutrientGuidelineDao diseaseNutrientGuidelineDao) {
        this.userDao = userDao;
        this.diseaseNutrientGuidelineDao = diseaseNutrientGuidelineDao;
    }


    //사용자의 모든 질병에 기반한 영양소 가이드라인 조회 및 병합
    public Map<Integer, NutrientGuideDto> getUserGuidelines(int userId){
        //1.사용자 질병 정보 조회
        List<Integer> diseaseIds = userDao.selectUserDiseases(userId);
        //2.사용자 질병 정보 없으면 기본 가이드라인 -> 1번(일반인) 사용
        if(diseaseIds.isEmpty()){
            diseaseIds = List.of(1);
        }

        //3.질병별 영양소 가이드라인 조회
        List<NutrientGuideDto> allGuidelines = diseaseNutrientGuidelineDao.selectByDiseaseIds(diseaseIds);
        System.out.println("---------------질병 가이드라인 조회 결과"+ allGuidelines);
        //4.가이드라인 병합
        Map<Integer, NutrientGuideDto> mergeGuidelines = new HashMap<>();
            for(NutrientGuideDto guideline : allGuidelines){
                int nutrientId = guideline.getNutrientId();
                int diseaseId = guideline.getDiseaseId();

                if(mergeGuidelines.containsKey(nutrientId)){
                    NutrientGuideDto existingGuide = mergeGuidelines.get(nutrientId);
                    boolean isRestriction = existingGuide.isRestriction() || guideline.isRestriction(); // 제한 여부 병합

                    if (isRestriction) {
                        // 제한 영양소: 더 낮은 max 선택
                        BigDecimal newMax = existingGuide.getMax().min(guideline.getMax());
                        mergeGuidelines.put(nutrientId, new NutrientGuideDto(
                                diseaseId,nutrientId, existingGuide.getMin(), newMax, true, existingGuide.getRecommendation()
                        ));
                    } else {
                        // 권장 영양소: 더 높은 min 선택
                        BigDecimal newMin = existingGuide.getMin().max(guideline.getMin());
                        mergeGuidelines.put(nutrientId, new NutrientGuideDto(
                                diseaseId,nutrientId, newMin, existingGuide.getMax(), false, existingGuide.getRecommendation()
                        ));
                    }
                } else {
                    mergeGuidelines.put(nutrientId, new NutrientGuideDto(
                            guideline.getDiseaseId(),
                            guideline.getNutrientId(),
                            guideline.getMin(),
                            guideline.getMax(),
                            guideline.isRestriction(),
                            guideline.getRecommendation()
                    ));
                }

            }
            System.out.println("---------------병합된 질병 가이드라인 조회 결과"+ mergeGuidelines);
            return mergeGuidelines;

    }
}
