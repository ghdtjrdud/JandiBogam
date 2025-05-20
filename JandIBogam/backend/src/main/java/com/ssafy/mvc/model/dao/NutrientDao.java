package com.ssafy.mvc.model.dao;

import com.ssafy.mvc.model.dto.NutrientDto;
import com.ssafy.mvc.model.dto.NutrientIdNameDto;

import java.util.List;
import java.util.Map;

public interface NutrientDao {

    //영양소 id로 조회 -> 특정 영양소의 상세 정보 가져오기
    NutrientDto selectNutrientById(int id);

    //영양소 이름으로 조회
    NutrientDto selectNutrientByName(String name);

    //영양소 전부 조회
    List<NutrientDto> selectAllNutrients();

    //영양소의 id를 key, 이름을 값으로 하는 맵 조회 -> 영양소 ID만 알고 있고 이름이 필요할 때 빠르게 조회
    Map<Integer, String> findAllAsMap();

    //영양소 이름 -> id
    List<NutrientIdNameDto> findAllNameToIdMap();

}
