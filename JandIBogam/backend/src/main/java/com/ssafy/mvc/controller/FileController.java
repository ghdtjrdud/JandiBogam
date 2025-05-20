package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.service.FileStorageService;
import com.ssafy.mvc.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public FileController(FileStorageService fileStorageService, JwtTokenProvider jwtTokenProvider) {
        this.fileStorageService = fileStorageService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //사진 등록
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try{
            int userId;
           try{
                userId = jwtTokenProvider.extractUserId(request);
                System.out.println("JWT에서 추출한 사용자 ID: " + userId); // 추출된 ID 로깅
            } catch (Exception e) {
               System.err.println("JWT에서 사용자 ID 추출 중 오류: " + e.getMessage());
               e.printStackTrace();
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                       .body("JWT 인증 실패: " + e.getMessage());

           }
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("사진 파일이 비어 있습니다.");
            }

            //파일 확장자 확인
            String originalFilename = file.getOriginalFilename();
            if(originalFilename != null){
                String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
                if(!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png")){
                    return ResponseEntity.badRequest().body("이미지 파일만 업로드 가능합니다.");
                }
            }
            //파일 저장
            String fileUrl = fileStorageService.storeFileWithUser(file, userId);

            //응답
            Map<String, String> response = new HashMap<>();
            response.put("photoUrl", fileUrl);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사진 업로드 중 오류가 발생했습니다.");
        }
    }





}