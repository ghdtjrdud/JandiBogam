package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.MealDto;
import com.ssafy.mvc.model.dto.MedicationDto;
import com.ssafy.mvc.model.service.MedicationService;
import com.ssafy.mvc.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;
    private final JwtTokenProvider jwtTokenProvider;

    public MedicationController(MedicationService medicationService, JwtTokenProvider jwtTokenProvider) {
        this.medicationService = medicationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //등록
    @PostMapping
    public ResponseEntity<?> createMedication(@RequestBody MedicationDto medication, HttpServletRequest request) {
        int userId = jwtTokenProvider.extractUserId(request);
        medication.setUserId(userId);
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
    public ResponseEntity<?> getMedicationByUserId(
            @PathVariable("userId") int userId,
            HttpServletRequest request // 추가
    ) {
        // 토큰의 사용자 ID와 요청 userId 일치 여부 확인
        int tokenUserId = jwtTokenProvider.extractUserId(request);
        if (tokenUserId != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<MedicationDto> medications = medicationService.getMedicationsByUserId(userId);
        return ResponseEntity.ok(medications);
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
    public ResponseEntity<?> updateMedication(
            HttpServletRequest request,
            @PathVariable("id") int id,
            @RequestBody MedicationDto medication
    ) {
        try {
            System.out.println("=== updateMedication 시작 ===");
            System.out.println("수정할 ID: " + id);
            System.out.println("수정 데이터: " + medication);

            // 1. 토큰에서 사용자 ID 추출
            int userId = jwtTokenProvider.extractUserId(request);
            System.out.println("토큰 사용자 ID: " + userId);

            // 2. 기존 데이터 조회
            MedicationDto existingMed = medicationService.getMedicationById(id);
            if (existingMed == null) {
                System.out.println("수정할 복약 정보를 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }

            System.out.println("기존 데이터: " + existingMed);

            // 3. 권한 확인: 수정하려는 데이터의 사용자 ID와 토큰의 사용자 ID 일치 여부
            if (existingMed.getUserId() != userId) {
                System.out.println("권한 없음 - 기존 데이터 사용자 ID: " + existingMed.getUserId() + ", 토큰 사용자 ID: " + userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
            }

            // 4. 수정할 데이터 설정
            medication.setId(id);
            medication.setUserId(userId); // ✅ 사용자 ID 설정

            System.out.println("최종 수정 데이터: " + medication);

            // 5. 데이터 업데이트
            MedicationDto updated = medicationService.updateMedication(medication);

            System.out.println("수정 완료: " + updated);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            System.out.println("❌ updateMedication 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패: " + e.getMessage());
        }
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
