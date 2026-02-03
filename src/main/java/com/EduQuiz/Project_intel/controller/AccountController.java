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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

    private final AccountService accountService;
    private final AvatarService avatarService;

    public AccountController(AccountService accountService, AvatarService avatarService) {
        this.accountService = accountService;
        this.avatarService = avatarService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/auth";

        model.addAttribute("user", user);
        model.addAttribute("backUrl", user.getRole() == Role.TEACHER ? "/teacher" : "/student");
        return "profile";
    }

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
}
