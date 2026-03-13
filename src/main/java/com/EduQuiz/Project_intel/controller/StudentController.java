package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.ClassEnrollment;
import com.EduQuiz.Project_intel.model.ClassRoom;
import com.EduQuiz.Project_intel.model.ExamAttempt;
import com.EduQuiz.Project_intel.model.Role;
import com.EduQuiz.Project_intel.model.User;
import com.EduQuiz.Project_intel.repository.ExamAttemptRepository;
import com.EduQuiz.Project_intel.service.ClassEnrollmentService;
import com.EduQuiz.Project_intel.service.ClassRoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final ExamAttemptRepository attemptRepository;
    private final ClassRoomService classRoomService;
    private final ClassEnrollmentService classEnrollmentService;

    public StudentController(ExamAttemptRepository attemptRepository,
                             ClassRoomService classRoomService,
                             ClassEnrollmentService classEnrollmentService) {
        this.attemptRepository = attemptRepository;
        this.classRoomService = classRoomService;
        this.classEnrollmentService = classEnrollmentService;
    }

    @GetMapping
    public String studentPage(@RequestParam(name = "tab", required = false) String tab,
                              HttpSession session,
                              Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.STUDENT) {
            return "redirect:/teacher";
        }

        String normalizedTab = tab == null ? "news" : tab.trim().toLowerCase();
        if (!normalizedTab.equals("news")
                && !normalizedTab.equals("history")
                && !normalizedTab.equals("classes")) {
            normalizedTab = "news";
        }

        model.addAttribute("studentActiveTab", normalizedTab);

        // Lịch sử bài làm của học sinh (mới nhất -> cũ nhất)
        List<ExamAttempt> attempts = attemptRepository.findByStudentIdOrderBySubmittedAtDesc(user.getId());
        model.addAttribute("attempts", attempts);

        // Lớp đã tham gia
        classRoomService.ensureClassCodes();
        List<ClassEnrollment> enrollments = classEnrollmentService.findByStudentId(user.getId());
        List<ClassRoom> joinedClasses = enrollments.stream()
                .map(ClassEnrollment::getClassRoom)
                .collect(Collectors.toList());

        model.addAttribute("joinedClasses", joinedClasses);
        model.addAttribute(
                "joinedClassMemberCounts",
                classEnrollmentService.countMembersByClassIds(
                        joinedClasses.stream().map(ClassRoom::getId).toList()
                )
        );

        return "student";
    }

    @PostMapping("/classes/join")
    public String joinClassByCode(@RequestParam("code") String code,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.STUDENT) {
            return "redirect:/teacher";
        }

        String normalized = code == null ? "" : code.trim();
        if (normalized.isEmpty()) {
            redirectAttributes.addFlashAttribute("joinError", "Vui lòng nhập mã lớp");
            return "redirect:/student?tab=classes";
        }

        ClassRoom classRoom = classRoomService.findByClassCode(normalized);
        if (classRoom == null) {
            redirectAttributes.addFlashAttribute("joinError", "Mã lớp không tồn tại hoặc không hợp lệ");
            return "redirect:/student?tab=classes";
        }

        if (classEnrollmentService.isEnrolled(classRoom.getId(), user.getId())) {
            redirectAttributes.addFlashAttribute("joinError", "Bạn đã tham gia lớp này rồi");
            return "redirect:/student?tab=classes";
        }

        try {
            classEnrollmentService.enroll(user, classRoom);
            redirectAttributes.addFlashAttribute("joinSuccess", "Tham gia lớp thành công: " + classRoom.getName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("joinError", "Không thể tham gia lớp: " + e.getMessage());
        }

        return "redirect:/student?tab=classes";
    }

    /**
     * Trang chi tiết một lần làm bài
     * Chỉ học sinh sở hữu attempt mới xem được
     * URL: /student/attempts/{attemptId}
     */
    @GetMapping("/attempts/{attemptId}")
    public String attemptDetail(@PathVariable Long attemptId,
                                HttpSession session,
                                Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }

        if (user.getRole() == null) {
            session.invalidate();
            return "redirect:/auth";
        }

        if (user.getRole() != Role.STUDENT) {
            return "redirect:/teacher";
        }

        ExamAttempt attempt = attemptRepository
                .findByIdAndStudentId(attemptId, user.getId())
                .orElse(null);

        if (attempt == null) {
            return "redirect:/student?tab=history";
        }

        Double attemptPercent = attempt.getPercent();
        double pct = attemptPercent != null ? attemptPercent : 0.0;
        boolean passed = pct >= 50;

        String submittedAtText = "";
        if (attempt.getSubmittedAt() != null) {
            submittedAtText = attempt.getSubmittedAt()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        model.addAttribute("attempt", attempt);
        model.addAttribute("exam", attempt.getExam());
        model.addAttribute("student", attempt.getStudent());
        model.addAttribute("pct", pct);
        model.addAttribute("passed", passed);
        model.addAttribute("submittedAtText", submittedAtText);

        return "student_attempt_detail";
    }
}