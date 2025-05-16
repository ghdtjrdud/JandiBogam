package com.ssafy.mvc.model.dao;


import com.ssafy.mvc.model.dto.MedicationDto;

import java.util.List;

public interface MedicationDao {

    //복약 정보 등록
    int insertMedication(MedicationDto medication);

    //복약 정보 상세 조회(복약 기록 id / 사용자 id)
    MedicationDto selectMedicationById(int id);
    List<MedicationDto> selectMedicationByUserId(int userId);
    //복약 정보 전체 조회
    List<MedicationDto> selectAllMedications();

    //복약 정보 수정
    int updateMedication(MedicationDto medication);
    //복약 정보 삭제
    int deleteMedication(int id);

}
