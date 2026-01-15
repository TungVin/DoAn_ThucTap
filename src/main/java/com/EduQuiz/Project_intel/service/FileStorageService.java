package com.EduQuiz.Project_intel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadClassesDir;
    private final Path uploadAttachmentsDir;

    private static final Set<String> ALLOWED_ATTACHMENT_TYPES = Set.of(
            "image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif",
            "application/pdf"
    );

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();

        this.uploadClassesDir = base.resolve("classes");
        this.uploadAttachmentsDir = base.resolve("attachments");

        Files.createDirectories(this.uploadClassesDir);
        Files.createDirectories(this.uploadAttachmentsDir);
    }

    // ==== CŨ: lưu ảnh lớp (giữ lại) ====
    public String storeClassImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Only image files are allowed");
        }

        String fileName = buildSafeRandomFileName(file.getOriginalFilename());
        Path target = this.uploadClassesDir.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/classes/" + fileName;
    }

    // ==== MỚI: dùng cho upload đính kèm đáp án/câu hỏi ====
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File rỗng");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_ATTACHMENT_TYPES.contains(contentType)) {
            throw new IOException("Không hỗ trợ định dạng: " + contentType);
        }

        String fileName = buildSafeRandomFileName(file.getOriginalFilename());
        Path target = this.uploadAttachmentsDir.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/attachments/" + fileName;
    }

    private String buildSafeRandomFileName(String originalFilename) {
        String clean = StringUtils.cleanPath(originalFilename == null ? "" : originalFilename);

        String ext = "";
        int dot = clean.lastIndexOf('.');
        if (dot >= 0 && dot < clean.length() - 1) {
            ext = clean.substring(dot);
        }
        return UUID.randomUUID() + ext;
    }
}
