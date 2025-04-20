package com.ticketsystem.zimsmartvillages.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String type,
                                              @PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, type, filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(type);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String type) {
        if ("image".equalsIgnoreCase(type)) {
            return "image/jpeg"; // Adjust based on actual file types
        } else if ("audio".equalsIgnoreCase(type)) {
            return "audio/mpeg"; // Adjust based on actual file types
        }
        return "application/octet-stream";
    }
}
