package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.dto.ChangePasswordForm;
import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.service.AccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
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

        // Nếu lần đầu vào settings (chưa có form từ flash), tạo form mới
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ChangePasswordForm());
        }

        return "settings";
    }

    @PostMapping("/settings/password")
    public String changePassword(@Valid @ModelAttribute("form") ChangePasswordForm form,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes ra) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/auth";

        // Validate logic: confirm password + new != current
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

        // Nếu có lỗi validate: redirect về /settings và giữ lỗi + dữ liệu
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

            // cập nhật lại user trong session để đồng bộ
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
