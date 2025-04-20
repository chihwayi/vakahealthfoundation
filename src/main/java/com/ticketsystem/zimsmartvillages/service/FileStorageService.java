package com.ticketsystem.zimsmartvillages.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String fileType) throws IOException {
        String filename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        String subdirectory = fileType.toLowerCase(); // "image" or "audio"
        Path targetDir = Paths.get(uploadDir, subdirectory).toAbsolutePath().normalize();

        Files.createDirectories(targetDir);
        Path targetPath = targetDir.resolve(filename);

        Files.copy(file.getInputStream(), targetPath);

        return subdirectory + "/" + filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}