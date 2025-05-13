package com.ssafy.mvc.controller;

import com.ssafy.mvc.model.service.FileStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    //사진 등록
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try{
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
            String fileUrl = fileStorageService.storeFile(file);

            //응답
            Map<String, String> response = new HashMap<>();
            response.put("photoUrl", fileUrl);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사진 업로드 중 오류가 발생했습니다.");
        }
    }





}