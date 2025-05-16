package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.MealDto;
import com.ssafy.mvc.model.dto.MedicationDto;
import com.ssafy.mvc.model.service.MedicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    //등록
    @PostMapping
    public ResponseEntity<?> createMedication(@RequestBody MedicationDto medication) {
        MedicationDto created = medicationService.createMedication(medication);
        return created != null ? ResponseEntity.status(HttpStatus.CREATED).body(created)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot create Meal");
    }

    //조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicationById(@PathVariable("id") int id) {
        MedicationDto medication = medicationService.getMedicationById(id);
        if (medication == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("medication not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(medication);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getMedicationByUserId(@PathVariable("userId") int userId) {
        List<MedicationDto> medications = medicationService.getMedicationsByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(medications);
    }

    @GetMapping
    public ResponseEntity<?> getAllMedications() {
        List<MedicationDto> medications = medicationService.getAllMedications();
        if(medications != null) {
            return ResponseEntity.status(HttpStatus.OK).body(medications);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("medications not found");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedication(@PathVariable("id") int id, @RequestBody MedicationDto medication) {
        medication.setId(id);
        MedicationDto updated = medicationService.updateMedication(medication);
        if(updated != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("medication not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable("id") int id) {
        boolean isdeleted = medicationService.deleteMedication(id);
        if(isdeleted) {
            return ResponseEntity.status(HttpStatus.OK).body("medication deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("medication not found");
    }





}
