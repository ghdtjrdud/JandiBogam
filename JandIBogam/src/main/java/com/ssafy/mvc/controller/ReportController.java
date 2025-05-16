package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.dao.UserDao;
import com.ssafy.mvc.model.dto.WeeklyReportDto;
import com.ssafy.mvc.model.service.ReportService;
import com.ssafy.mvc.security.JwtTokenProvider;
import com.ssafy.mvc.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final JwtTokenProvider jwtTokenProvider;

    public ReportController(ReportService reportService,JwtTokenProvider jwtTokenProvider) {
        this.reportService = reportService;
        this.jwtTokenProvider = jwtTokenProvider;

    }

    //사용자 주간 리포트 조회
    @GetMapping("/weekly/{userId}")
    public ResponseEntity<?> getWeeklyReport(HttpServletRequest request, @RequestParam LocalDate date, @RequestParam(defaultValue = "false") boolean requested) {
        int userId = jwtTokenProvider.extractUserId(request);
        WeeklyReportDto report = null;

        if(!requested) {
            report = reportService.getWeeklyReportByDate(userId, date);
        }
        if(report == null) {
            LocalDate endDate = date;
            LocalDate startDate = endDate.minusDays(6); //일주일치
            report = reportService.generateReport(userId, startDate, endDate);
        }
        return ResponseEntity.status(HttpStatus.OK).body(report);
    }

    //사용자 최신 리포트 조회
    @GetMapping("/weekly/latest/{userId}")
    public ResponseEntity<?> getWeeklyReportLatest(HttpServletRequest request) {
        int userId = jwtTokenProvider.extractUserId(request);
        WeeklyReportDto report = reportService.getLatestReport(userId);
        if(report != null) {
            return ResponseEntity.status(HttpStatus.OK).body(report);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //생성 요청
    @PostMapping("/weekly/generate/{userId}")
    public ResponseEntity<?> generateWeeklyReport(HttpServletRequest request, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        int userId = jwtTokenProvider.extractUserId(request);
        WeeklyReportDto report = reportService.generateReport(userId, startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(report);
    }



}
