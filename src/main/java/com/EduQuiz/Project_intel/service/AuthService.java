package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.dto.RegisterRequest;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra độ dài mật khẩu
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        // Tạo user mới
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Mã hóa mật khẩu nếu có passwordEncoder, nếu không lưu trực tiếp
        if (passwordEncoder != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            user.setPassword(request.getPassword());
        }

        user.setRole(request.getRoleEnum());

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

        // Kiểm tra mật khẩu
        if (passwordEncoder != null) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Email hoặc mật khẩu không đúng");
            }
        } else {
            if (!password.equals(user.getPassword())) {
                throw new RuntimeException("Email hoặc mật khẩu không đúng");
            }
        }

        return user;
    }
}

