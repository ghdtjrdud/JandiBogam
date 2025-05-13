package com.ssafy.mvc.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:./uploads/meals}")
    private String uploadDir;

    public String storeFile(MultipartFile file) throws IOException {
        //디렉토리 생성
        Path uploadDirPath = Paths.get(uploadDir);
        if(!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        //파일명 생성
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IOException("유효하지 않은 파일 이름: " + originalFilename);
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        //파일 저장
        Path filePath = uploadDirPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        //접근 가능한 url 형태로 반환
        return "/uploads/meals/" + newFileName;
    }
}