package com.ssafy.mvc.model.service;

import com.ssafy.mvc.model.dao.MedicationDao;
import com.ssafy.mvc.model.dto.MedicationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationServiceImpl implements MedicationService {

    private  final MedicationDao medicationDao;

    public MedicationServiceImpl(MedicationDao medicationDao) {
        this.medicationDao = medicationDao;
    }


    @Override
    public MedicationDto createMedication(MedicationDto medication) {
        int result = medicationDao.insertMedication(medication);
        if(result > 0){
            return medication;
        }
        return null;
    }

    @Override
    public MedicationDto getMedicationById(int id) {
        return medicationDao.selectMedicationById(id);
    }

    @Override
    public List<MedicationDto> getAllMedications() {
        return medicationDao.selectAllMedications();
    }

    @Override
    public List<MedicationDto> getMedicationsByUserId(int userId) {
        return medicationDao.selectMedicationByUserId(userId);
    }

    @Override
    public MedicationDto updateMedication(MedicationDto medication) {
        int result = medicationDao.updateMedication(medication);
        if(result > 0){
            return medication;
        }
        return null;
    }

    @Override
    public boolean deleteMedication(int id) {
        return medicationDao.deleteMedication(id) > 0;
    }
}
