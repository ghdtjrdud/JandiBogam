package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dto.WeeklyReportDto;
import com.ssafy.mvc.model.dto.UserDto;
import com.ssafy.mvc.model.service.ReportService;
import com.ssafy.mvc.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자 주간 리포트 조회 (요청된 날짜 기준)
    @GetMapping("/weekly/{userId}")
    public ResponseEntity<?> getWeeklyReport(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean requested
    ) {
        int userId = jwtTokenProvider.extractUserId(request);
        WeeklyReportDto report;

        if (!requested) {
            report = reportService.getWeeklyReportByDate(userId, date);
        } else {
            report = null;
        }

        if (report == null) {
            LocalDate endDate = date;
            LocalDate startDate = endDate.minusDays(6);
            report = reportService.generateReport(userId, startDate, endDate);
        }

        return ResponseEntity.ok(report);
    }

    // 사용자 최신 리포트 조회
    @GetMapping("/weekly/latest/{userId}")
    public ResponseEntity<?> getWeeklyReportLatest(HttpServletRequest request) {
        int userId = jwtTokenProvider.extractUserId(request);
        WeeklyReportDto report = reportService.getLatestReport(userId);
        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 생성 요청 (기간 지정)
    @PostMapping("/weekly/generate/{userId}")
    public ResponseEntity<?> generateWeeklyReport(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        int userId = jwtTokenProvider.extractUserId(request);
        WeeklyReportDto report = reportService.generateReport(userId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    // — 수동 테스트용 엔드포인트 추가 —
    // 토큰 없이도 userId와 기간을 직접 지정해 리포트+AI 추천을 실행해 봅니다.
    @PostMapping("/weekly/test")
    public ResponseEntity<WeeklyReportDto> testGenerate(
            @RequestParam int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        WeeklyReportDto dto = reportService.generateReport(userId, startDate, endDate);
        return ResponseEntity.ok(dto);
    }

    // AI 식단 추천만 조회 (텍스트 형태)
    @GetMapping("/weekly/ai-recommendation")
    public ResponseEntity<?> getAiDietRecommendation(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        int userId = jwtTokenProvider.extractUserId(request);

        // 기간이 지정되지 않으면 지난주로 설정
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            endDate = today.minusDays(today.getDayOfWeek().getValue()); // 지난 주 일요일
            startDate = endDate.minusDays(6); // 지난 주 월요일
        }

        try {
            String recommendation = reportService.generateDietRecommendation(userId, startDate, endDate);
            return ResponseEntity.ok(Map.of(
                    "recommendation", recommendation,
                    "period", Map.of("start", startDate, "end", endDate),
                    "generatedAt", LocalDate.now()
            ));
        } catch (Exception e) {
            System.err.println("AI 추천 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "AI 추천 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 완전한 주간 리포트 조회 (건강 분석 + AI 추천)
    @GetMapping("/weekly/complete")
    public ResponseEntity<?> getCompleteWeeklyReport(
            HttpServletRequest request,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        int userId = jwtTokenProvider.extractUserId(request);

        if (date == null) {
            date = LocalDate.now();
        }

        try {
            // 1. 기본 리포트 조회/생성
            WeeklyReportDto report = reportService.getWeeklyReportByDate(userId, date);
            if (report == null) {
                LocalDate endDate = date;
                LocalDate startDate = endDate.minusDays(6);
                report = reportService.generateReport(userId, startDate, endDate);
            }

            // 2. AI 추천 별도 생성
            String aiRecommendation = null;
            try {
                aiRecommendation = reportService.generateDietRecommendation(
                        userId, report.getStartDate(), report.getEndDate());
            } catch (Exception aiError) {
                System.err.println("AI 추천 생성 실패 (리포트는 정상): " + aiError.getMessage());
                // AI 실패해도 기본 리포트는 반환
            }

            // 3. 통합 응답 생성
            Map<String, Object> completeReport = new HashMap<>();
            completeReport.put("healthAnalysis", report);
            completeReport.put("aiRecommendation", aiRecommendation);
            completeReport.put("generatedAt", LocalDate.now());

            return ResponseEntity.ok(completeReport);

        } catch (Exception e) {
            System.err.println("완전한 리포트 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "리포트 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


}
