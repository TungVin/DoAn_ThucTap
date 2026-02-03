package com.EduQuiz.Project_intel.repository;

import com.EduQuiz.Project_intel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    /**
     * Cập nhật password theo email (tiện cho đổi mật khẩu / reset mật khẩu)
     * Lưu ý: newPassword phải là chuỗi đã encode BCrypt.
     */
    @Transactional
    @Modifying
    @Query("update User u set u.password = :newPassword where u.email = :email")
    int updatePasswordByEmail(String email, String newPassword);
}
