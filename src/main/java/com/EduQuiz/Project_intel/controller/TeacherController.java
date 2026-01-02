package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.*;
import com.EduQuiz.Project_intel.repository.CategoryRepository;
import com.EduQuiz.Project_intel.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final ExamService examService;
    private final CategoryService categoryService;
    private final QuestionService questionService;
    private final ClassRoomService classRoomService;
    private final ScheduleService scheduleService;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;

    public TeacherController(ExamService examService, CategoryService categoryService,
                           QuestionService questionService, ClassRoomService classRoomService,
                           ScheduleService scheduleService, FileStorageService fileStorageService,
                           CategoryRepository categoryRepository) {
        this.examService = examService;
        this.categoryService = categoryService;
        this.questionService = questionService;
        this.classRoomService = classRoomService;
        this.scheduleService = scheduleService;
        this.fileStorageService = fileStorageService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String teacherPage(@RequestParam(defaultValue = "exams") String activeTab, Model model) {
        model.addAttribute("activeTab", activeTab);

        // Load data for all sections
        model.addAttribute("exams", examService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("questions", questionService.findAllOrdered());
        model.addAttribute("classes", classRoomService.findAll());
        model.addAttribute("schedules", scheduleService.findAll());

        return "teacher";
    }

    // ==================== EXAMS ====================
    @PostMapping("/exams/create")
    public String createExam(@RequestParam String title,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) String hasTimeLimit,
                            @RequestParam(required = false) Integer timeLimit,
                            @RequestParam(required = false) String hasStartTime,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String hasEndTime,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String endTime,
                            RedirectAttributes redirectAttributes) {
        try {
            Category category = null;
            if (categoryId != null) {
                category = categoryRepository.findById(categoryId).orElse(null);
            }

            LocalDateTime startDateTime = null;
            if ("on".equals(hasStartTime) && startDate != null && startTime != null) {
                startDateTime = LocalDateTime.of(
                    LocalDate.parse(startDate),
                    LocalTime.parse(startTime)
                );
            }

            LocalDateTime endDateTime = null;
            if ("on".equals(hasEndTime) && endDate != null && endTime != null) {
                endDateTime = LocalDateTime.of(
                    LocalDate.parse(endDate),
                    LocalTime.parse(endTime)
                );
            }

            Integer finalTimeLimit = null;
            if ("on".equals(hasTimeLimit) && timeLimit != null) {
                finalTimeLimit = timeLimit;
            }

            examService.createExam(title, description, category, finalTimeLimit, startDateTime, endDateTime);
            redirectAttributes.addFlashAttribute("examSuccess", "Tạo bài kiểm tra thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("examError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/teacher?activeTab=exams";
    }

    @PostMapping("/exams/update")
    public String updateExam(@RequestParam Long id,
                            @RequestParam String title,
                            @RequestParam(required = false) String description,
                            @RequestParam(required = false) String categoryId,
                            @RequestParam(required = false) String timeLimit,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String endTime,
                            RedirectAttributes redirectAttributes) {
        try {
            Exam exam = examService.findById(id);
            if (exam == null) {
                redirectAttributes.addFlashAttribute("examError", "Không tìm thấy bài kiểm tra!");
                return "redirect:/teacher?activeTab=exams";
            }

            Category category = null;
            if (categoryId != null && !categoryId.isEmpty()) {
                try {
                    Long catId = Long.parseLong(categoryId);
                    category = categoryRepository.findById(catId).orElse(null);
                } catch (NumberFormatException e) {
                    // Ignore invalid category ID
                }
            }

            Integer finalTimeLimit = null;
            if (timeLimit != null && !timeLimit.isEmpty()) {
                try {
                    finalTimeLimit = Integer.parseInt(timeLimit);
                } catch (NumberFormatException e) {
                    // Ignore invalid time limit
                }
            }

            // Parse start time
            LocalDateTime startDateTime = null;
            if (startDate != null && !startDate.isEmpty() && startTime != null && !startTime.isEmpty()) {
                try {
                    startDateTime = LocalDateTime.of(
                        LocalDate.parse(startDate),
                        LocalTime.parse(startTime)
                    );
                } catch (Exception e) {
                    // Ignore invalid start time
                }
            }

            // Parse end time
            LocalDateTime endDateTime = null;
            if (endDate != null && !endDate.isEmpty() && endTime != null && !endTime.isEmpty()) {
                try {
                    endDateTime = LocalDateTime.of(
                        LocalDate.parse(endDate),
                        LocalTime.parse(endTime)
                    );
                } catch (Exception e) {
                    // Ignore invalid end time
                }
            }

            exam.setTitle(title);
            exam.setDescription(description);
            exam.setCategory(category);
            exam.setTimeLimit(finalTimeLimit);
            exam.setStartTime(startDateTime);
            exam.setEndTime(endDateTime);

            examService.save(exam);
            redirectAttributes.addFlashAttribute("examSuccess", "Cập nhật bài kiểm tra thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("examError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/teacher?activeTab=exams";
    }

    @PostMapping("/exams/delete")
    public String deleteExam(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            examService.deleteById(id);
            redirectAttributes.addFlashAttribute("examSuccess", "Xóa bài kiểm tra thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("examError", "Lỗi: " + e.getMessage());
        }

        return "redirect:/teacher?activeTab=exams";
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
            Category category = null;
            if (categoryId != null && !categoryId.isEmpty()) {
                try {
                    Long catId = Long.parseLong(categoryId);
                    category = categoryRepository.findById(catId).orElse(null);
                } catch (NumberFormatException e) {
                    // Ignore invalid category ID
                }
            }

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

            Category category = null;
            if (categoryId != null && !categoryId.isEmpty()) {
                try {
                    Long catId = Long.parseLong(categoryId);
                    category = categoryRepository.findById(catId).orElse(null);
                } catch (NumberFormatException e) {
                    // Ignore invalid category ID
                }
            }

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
                                 @RequestParam(required = false) Long classId,
                                 @RequestParam String platform,
                                 @RequestParam(required = false) String meetingLink,
                                 @RequestParam String startDate,
                                 @RequestParam String startTime,
                                 @RequestParam String endDate,
                                 @RequestParam String endTime,
                                 @RequestParam(required = false) String autoAccept,
                                 RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime startDateTime = LocalDateTime.of(
                LocalDate.parse(startDate),
                LocalTime.parse(startTime)
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                LocalDate.parse(endDate),
                LocalTime.parse(endTime)
            );

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

    @PostMapping("/online/update")
    public String updateSchedule(@RequestParam Long id,
                                 @RequestParam String title,
                                 @RequestParam String platform,
                                 @RequestParam String startDate,
                                 @RequestParam String startTime,
                                 @RequestParam String endDate,
                                 @RequestParam String endTime,
                                 @RequestParam(required = false) String autoAccept,
                                 RedirectAttributes redirectAttributes) {
        try {
            Schedule schedule = scheduleService.findById(id);
            if (schedule == null) {
                redirectAttributes.addFlashAttribute("scheduleError", "Không tìm thấy lịch học!");
                return "redirect:/teacher?activeTab=online";
            }

            LocalDateTime startDateTime = LocalDateTime.of(
                LocalDate.parse(startDate),
                LocalTime.parse(startTime)
            );

            LocalDateTime endDateTime = LocalDateTime.of(
                LocalDate.parse(endDate),
                LocalTime.parse(endTime)
            );

            schedule.setTitle(title);
            schedule.setPlatform(platform.toLowerCase().replace(" ", "_"));
            schedule.setStartTime(startDateTime);
            schedule.setEndTime(endDateTime);
            schedule.setAutoAccept("true".equals(autoAccept));

            scheduleService.save(schedule);
            redirectAttributes.addFlashAttribute("scheduleSuccess", "Cập nhật lịch học thành công!");
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
}

