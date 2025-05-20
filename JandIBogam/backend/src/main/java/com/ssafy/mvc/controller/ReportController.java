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
}
