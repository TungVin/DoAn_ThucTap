package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/png", "image/jpeg", "image/webp");

    private final UserRepository userRepository;

    public AvatarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateAvatar(String email, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ảnh avatar");
        }
        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Chỉ hỗ trợ ảnh PNG/JPG/WEBP");
        }
        // giới hạn 2MB
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Ảnh quá lớn (tối đa 2MB)");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        try {
            Path dir = Paths.get("uploads", "avatars");
            Files.createDirectories(dir);

            String ext = switch (file.getContentType()) {
                case "image/png" -> "png";
                case "image/webp" -> "webp";
                default -> "jpg"; // image/jpeg
            };

            String filename = "u" + user.getId() + "_" + UUID.randomUUID() + "." + ext;
            Path target = dir.resolve(filename).normalize();

            // an toàn: đảm bảo không thoát khỏi thư mục avatars
            if (!target.startsWith(dir)) {
                throw new IllegalArgumentException("Tên file không hợp lệ");
            }

            // lưu file
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // lưu URL để hiển thị
            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);

            return userRepository.save(user);

        } catch (IOException e) {
            throw new IllegalArgumentException("Lỗi khi lưu ảnh, thử lại sau");
        }
    }
}
