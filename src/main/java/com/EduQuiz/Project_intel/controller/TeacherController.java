package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.*;
import com.EduQuiz.Project_intel.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final ExamService examService;
    private final CategoryService categoryService;
    private final QuestionService questionService;
    private final ClassRoomService classRoomService;
    private final ScheduleService scheduleService;
    private final FileStorageService fileStorageService;

    public TeacherController(ExamService examService,
                             CategoryService categoryService,
                             QuestionService questionService,
                             ClassRoomService classRoomService,
                             ScheduleService scheduleService,
                             FileStorageService fileStorageService) {
        this.examService = examService;
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.classRoomService = classRoomService;
        this.scheduleService = scheduleService;
        this.fileStorageService = fileStorageService;
    }

    // ==================== MAIN PAGE ====================
    @GetMapping
    public String teacherPage(
            @RequestParam(defaultValue = "exams") String activeTab,
            Model model,
            HttpSession session
    ) {
        // Kiểm tra đăng nhập
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth";
        }
        if (user.getRole() != Role.TEACHER) {
            return "redirect:/student";
        }

        // Truyền dữ liệu vào model
        model.addAttribute("activeTab", activeTab);
        model.addAttribute("exams", examService.getAll());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("questions", questionService.findAllOrdered());
        model.addAttribute("classes", classRoomService.findAll());  // Truyền danh sách lớp học
        model.addAttribute("schedules", scheduleService.findAll());

        return "teacher";
    }

    // ==================== CATEGORIES ====================
    @PostMapping("/categories/create")
    public String createCategory(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.create(name, description);
            redirectAttributes.addFlashAttribute("categorySuccess", "Thêm danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("categoryError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=categories";
    }

    @PostMapping("/categories/update")
    public String updateCategory(@RequestParam Long id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id);
            if (category == null) {
                redirectAttributes.addFlashAttribute("categoryError", "Không tìm thấy danh mục!");
                return "redirect:/teacher?activeTab=categories";
            }

            category.setName(name);
            category.setDescription(description);
            categoryService.save(category);

            redirectAttributes.addFlashAttribute("categorySuccess", "Cập nhật danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("categoryError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=categories";
    }

    @PostMapping("/categories/delete")
    public String deleteCategory(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("categorySuccess", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("categoryError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=categories";
    }

    // ==================== QUESTIONS ====================
    @PostMapping("/questions/create")
    public String createQuestion(@RequestParam String title,
                                 @RequestParam String content,
                                 @RequestParam String type,
                                 @RequestParam(required = false) String categoryId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = parseCategory(categoryId);
            questionService.createQuestion(title, content, type, category, null, null, null);
            redirectAttributes.addFlashAttribute("questionSuccess", "Tạo câu hỏi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("questionError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=questions";
    }

    @PostMapping("/questions/update")
    public String updateQuestion(@RequestParam Long id,
                                 @RequestParam String title,
                                 @RequestParam String content,
                                 @RequestParam String type,
                                 @RequestParam(required = false) String categoryId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Question question = questionService.findById(id);
            if (question == null) {
                redirectAttributes.addFlashAttribute("questionError", "Không tìm thấy câu hỏi!");
                return "redirect:/teacher?activeTab=questions";
            }

            Category category = parseCategory(categoryId);
            question.setTitle(title);
            question.setContent(content);
            question.setType(type);
            question.setCategory(category);

            questionService.save(question);
            redirectAttributes.addFlashAttribute("questionSuccess", "Cập nhật câu hỏi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("questionError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=questions";
    }

    @PostMapping("/questions/delete")
    public String deleteQuestion(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteById(id);
            redirectAttributes.addFlashAttribute("questionSuccess", "Xóa câu hỏi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("questionError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=questions";
    }

    // ==================== CLASSES ====================
    @PostMapping("/classes/create")
    public String createClass(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            ClassRoom classRoom = new ClassRoom();
            classRoom.setName(name);
            classRoom.setDescription(description);

            if (image != null && !image.isEmpty()) {
                String imagePath = fileStorageService.storeClassImage(image);
                classRoom.setImagePath(imagePath);
            }

            classRoomService.save(classRoom);
            redirectAttributes.addFlashAttribute("classSuccess", "Thêm lớp học thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("classError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=classes";
    }

    @PostMapping("/classes/update")
    public String updateClass(@RequestParam Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false) MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            ClassRoom classRoom = classRoomService.findById(id);
            if (classRoom == null) {
                redirectAttributes.addFlashAttribute("classError", "Không tìm thấy lớp học!");
                return "redirect:/teacher?activeTab=classes";
            }

            classRoom.setName(name);
            classRoom.setDescription(description);

            if (image != null && !image.isEmpty()) {
                String imagePath = fileStorageService.storeClassImage(image);
                classRoom.setImagePath(imagePath);
            }

            classRoomService.save(classRoom);
            redirectAttributes.addFlashAttribute("classSuccess", "Cập nhật lớp học thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("classError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=classes";
    }

    @PostMapping("/classes/delete")
    public String deleteClass(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            classRoomService.deleteById(id);
            redirectAttributes.addFlashAttribute("classSuccess", "Xóa lớp học thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("classError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=classes";
    }

    // ==================== ONLINE SCHEDULES ====================
    @PostMapping("/online/create")
    public String createSchedule(@RequestParam String title,
                                 @RequestParam String platform,
                                 @RequestParam String startDate,
                                 @RequestParam String startTime,
                                 @RequestParam String endDate,
                                 @RequestParam String endTime,
                                 @RequestParam(required = false) String autoAccept,
                                 RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.parse(startTime));
            LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.parse(endTime));

            Schedule schedule = new Schedule();
            schedule.setTitle(title);
            schedule.setPlatform(platform.toLowerCase().replace(" ", "_"));
            schedule.setStartTime(startDateTime);
            schedule.setEndTime(endDateTime);
            schedule.setAutoAccept("true".equals(autoAccept));

            scheduleService.save(schedule);
            redirectAttributes.addFlashAttribute("scheduleSuccess", "Thêm lịch học thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("scheduleError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=online";
    }

    @PostMapping("/online/delete")
    public String deleteSchedule(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteById(id);
            redirectAttributes.addFlashAttribute("scheduleSuccess", "Xóa lịch học thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("scheduleError", "Lỗi: " + e.getMessage());
        }
        return "redirect:/teacher?activeTab=online";
    }

    // ==================== HELPERS ====================
    private Category parseCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) return null;
        try {
            return categoryService.findById(Long.parseLong(categoryId));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
