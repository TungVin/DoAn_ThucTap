package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================== REGISTER =====================
    public String register(String name, String email, String password, String roleStr) {

        if (userRepository.existsByEmail(email)) {
            return "Email đã tồn tại";
        }

        if (password.length() < 6) {
            return "Mật khẩu phải >= 6 ký tự";
        }

        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (Exception e) {
            return "Role không hợp lệ";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);
        return "SUCCESS";
    }

    // ===================== LOGIN =====================
    public Optional<User> login(String email, String rawPassword) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }
}
