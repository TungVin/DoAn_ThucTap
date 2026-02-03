package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.ChangePasswordForm;
import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.service.AccountService;
import com.EduQuiz.Project_intel.service.AvatarService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

@Controller
public class AccountController {

    private final AccountService accountService;
    private final AvatarService avatarService;

    public AccountController(AccountService accountService, AvatarService avatarService) {
        this.accountService = accountService;
        this.avatarService = avatarService;
    }

    // =========================
    // PROFILE
    // =========================
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";

        model.addAttribute("user", user);
        model.addAttribute("backUrl", user.getRole() == Role.TEACHER ? "/teacher" : "/student");

        // ✅ stats thật (tổng users, tổng exams, exams public...)
        model.addAttribute("stats", accountService.getProfileStats());

        // ✅ prefill form (nếu không có flash từ lần submit lỗi)
        if (!model.containsAttribute("profileName")) {
            model.addAttribute("profileName", user.getName());
        }
        if (!model.containsAttribute("profileEmail")) {
            model.addAttribute("profileEmail", user.getEmail());
        }

        return "profile";
    }

    // ✅ Update profile: name + email
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                HttpSession session,
                                RedirectAttributes ra) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/auth";

        String n = (name != null) ? name.trim() : "";
        String e = (email != null) ? email.trim().toLowerCase() : "";

        boolean hasError = false;

        // validate cơ bản
        if (!StringUtils.hasText(n)) {
            ra.addFlashAttribute("nameError", "Họ tên không được để trống");
            hasError = true;
        } else if (n.length() < 2 || n.length() > 60) {
            ra.addFlashAttribute("nameError", "Họ tên phải từ 2 đến 60 ký tự");
            hasError = true;
        }

        if (!StringUtils.hasText(e)) {
            ra.addFlashAttribute("emailError", "Email không được để trống");
            hasError = true;
        } else if (!EMAIL_PATTERN.matcher(e).matches()) {
            ra.addFlashAttribute("emailError", "Email không đúng định dạng");
            hasError = true;
        }

        // giữ lại dữ liệu người dùng vừa nhập
        ra.addFlashAttribute("profileName", n);
        ra.addFlashAttribute("profileEmail", e);

        if (hasError) {
            ra.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/profile";
        }

        try {
            User updated = accountService.updateProfile(sessionUser.getId(), n, e);

            // ✅ update session để header/student/teacher/profile đồng bộ luôn
            session.setAttribute("user", updated);

            ra.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
            return "redirect:/profile";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/profile";
        }
    }

    // =========================
    // SETTINGS
    // =========================
    @GetMapping("/settings")
    public String settings(HttpSession session,
                           Model model,
                           @ModelAttribute("form") ChangePasswordForm form) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";

        model.addAttribute("user", user);
        model.addAttribute("backUrl", user.getRole() == Role.TEACHER ? "/teacher" : "/student");

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ChangePasswordForm());
        }

        return "settings";
    }

    // =========================
    // Upload avatar
    // =========================
    @PostMapping("/settings/avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile avatar,
                               HttpSession session,
                               RedirectAttributes ra) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/auth";

        try {
            User updated = avatarService.updateAvatar(sessionUser.getEmail(), avatar);
            session.setAttribute("user", updated);
            ra.addFlashAttribute("success", "Cập nhật avatar thành công");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/settings";
    }

    // =========================
    // Change password
    // =========================
    @PostMapping("/settings/password")
    public String changePassword(@Valid @ModelAttribute("form") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes ra) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/auth";

        if (!bindingResult.hasErrors()) {
            if (!form.getNewPassword().equals(form.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "confirmPassword",
                        "Xác nhận mật khẩu không khớp");
            }
            if (form.getNewPassword().equals(form.getCurrentPassword())) {
                bindingResult.rejectValue("newPassword", "newPassword",
                        "Mật khẩu mới phải khác mật khẩu hiện tại");
            }
        }

        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            ra.addFlashAttribute("form", form);
            ra.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/settings";
        }

        try {
            User updated = accountService.changePassword(
                    sessionUser.getEmail(),
                    form.getCurrentPassword(),
                    form.getNewPassword()
            );

            session.setAttribute("user", updated);
            ra.addFlashAttribute("success", "Đổi mật khẩu thành công");
            return "redirect:/settings";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("form", form);
            return "redirect:/settings";
        }
    }

    // ===== simple email regex =====
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
}
