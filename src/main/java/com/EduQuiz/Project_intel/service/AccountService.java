package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.ExamRepository;
import com.EduQuiz.Project_intel.repository.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // dùng ObjectProvider để tránh lỗi nếu bạn chưa cấu hình bean/repo nào đó
    private final ObjectProvider<ExamRepository> examRepoProvider;

    public AccountService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          ObjectProvider<ExamRepository> examRepoProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.examRepoProvider = examRepoProvider;
    }

    // =========================
    // 1) ĐỔI MẬT KHẨU
    // =========================
    @Transactional
    public User changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        String stored = user.getPassword();

        boolean ok = stored != null && passwordEncoder.matches(currentPassword, stored);

        // Fallback: nếu DB đang lưu plain text (tạm hỗ trợ)
        if (!ok && stored != null && stored.equals(currentPassword)) {
            ok = true;
        }

        if (!ok) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    // =========================
    // 2) CẬP NHẬT HỒ SƠ (TÊN + EMAIL)
    // =========================
    @Transactional
    public User updateProfile(Long userId, String name, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        String newName = name != null ? name.trim() : "";
        String newEmail = email != null ? email.trim().toLowerCase() : "";

        if (newName.isBlank()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (newEmail.isBlank()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        // check trùng email nhưng loại trừ chính mình
        if (userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
            throw new IllegalArgumentException("Email đã tồn tại, vui lòng dùng email khác");
        }

        user.setName(newName);
        user.setEmail(newEmail);

        return userRepository.save(user);
    }

    // =========================
    // 3) CẬP NHẬT AVATAR
    // =========================
    @Transactional
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    // =========================
    // 4) THỐNG KÊ (DỮ LIỆU THẬT)
    // =========================
    @Transactional(readOnly = true)
    public ProfileStats getProfileStats() {
        ProfileStats s = new ProfileStats();

        // tổng user
        s.setTotalUsers(userRepository.count());

        // tổng bài kiểm tra + công khai
        ExamRepository examRepo = examRepoProvider.getIfAvailable();
        if (examRepo != null) {
            s.setTotalExams(examRepo.count());
            s.setPublicExams(examRepo.countByIsPublic(Boolean.TRUE));
        } else {
            s.setTotalExams(0);
            s.setPublicExams(0);
        }

        // Các thống kê khác bạn có thể bổ sung sau khi có repo tương ứng:
        // s.setTotalQuestions(...);
        // s.setTotalCategories(...);
        // s.setTotalClasses(...);
        // s.setTotalSchedules(...);

        return s;
    }

    // DTO nội bộ để dùng trực tiếp trong Thymeleaf: ${stats.totalExams}...
    public static class ProfileStats {
        private long totalUsers;
        private long totalExams;
        private long publicExams;

        // mở rộng sau:
        private long totalQuestions = -1;
        private long totalCategories = -1;
        private long totalClasses = -1;
        private long totalSchedules = -1;

        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

        public long getTotalExams() { return totalExams; }
        public void setTotalExams(long totalExams) { this.totalExams = totalExams; }

        public long getPublicExams() { return publicExams; }
        public void setPublicExams(long publicExams) { this.publicExams = publicExams; }

        public long getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }

        public long getTotalCategories() { return totalCategories; }
        public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }

        public long getTotalClasses() { return totalClasses; }
        public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }

        public long getTotalSchedules() { return totalSchedules; }
        public void setTotalSchedules(long totalSchedules) { this.totalSchedules = totalSchedules; }
    }
}
