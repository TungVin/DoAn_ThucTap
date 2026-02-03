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
     * ✅ Dùng khi update hồ sơ: kiểm tra email trùng nhưng loại trừ chính user hiện tại
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * ✅ Cập nhật password theo email (tiện cho đổi mật khẩu / reset mật khẩu)
     * Lưu ý: newPassword phải là chuỗi đã encode BCrypt.
     *
     * @return 
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.password = :newPassword where u.email = :email")
    int updatePasswordByEmail(String email, String newPassword);

    /**
     * ✅ (Tuỳ chọn) Cập nhật avatarUrl theo id (nếu bạn thích update nhanh)
     * @return số bản ghi được update (0/1)
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.avatarUrl = :avatarUrl where u.id = :id")
    int updateAvatarById(Long id, String avatarUrl);

    /**
     * ✅ (Tuỳ chọn) Cập nhật name + email theo id (update profile nhanh)
     * @return số bản ghi được update (0/1)
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.name = :name, u.email = :email where u.id = :id")
    int updateProfileById(Long id, String name, String email);
}
