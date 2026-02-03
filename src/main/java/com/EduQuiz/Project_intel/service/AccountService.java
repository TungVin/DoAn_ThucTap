package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        String stored = user.getPassword();

        boolean ok;
        // Trường hợp chuẩn: password trong DB là BCrypt
        ok = stored != null && passwordEncoder.matches(currentPassword, stored);

        // Fallback: nếu trước đây DB đang lưu plain text (tạm thời hỗ trợ để không bị lỗi)
        if (!ok && stored != null && stored.equals(currentPassword)) {
            ok = true;
        }

        if (!ok) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
