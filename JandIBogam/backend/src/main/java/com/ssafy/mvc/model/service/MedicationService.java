package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dto.MedicationDto;

import java.util.List;

public interface MedicationService {

    MedicationDto createMedication(MedicationDto medication);
    MedicationDto getMedicationById(int id);
    List<MedicationDto> getAllMedications();
    List<MedicationDto> getMedicationsByUserId(int userId);
    MedicationDto updateMedication(MedicationDto medication);
    boolean deleteMedication(int id);
}
