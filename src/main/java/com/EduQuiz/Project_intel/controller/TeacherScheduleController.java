package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Schedule;
import com.EduQuiz.Project_intel.model.ClassRoom;
import com.EduQuiz.Project_intel.model.Question;
import com.EduQuiz.Project_intel.service.ScheduleService;
import com.EduQuiz.Project_intel.service.ClassRoomService;
import com.EduQuiz.Project_intel.service.QuestionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class TeacherScheduleController {

    private final ScheduleService scheduleService;
    private final ClassRoomService classRoomService;
    private final com.EduQuiz.Project_intel.service.FileStorageService fileStorageService;
    private final QuestionService questionService;

    public TeacherScheduleController(ScheduleService scheduleService,
                                     ClassRoomService classRoomService,
                                     com.EduQuiz.Project_intel.service.FileStorageService fileStorageService,
                                     QuestionService questionService) {
        this.scheduleService = scheduleService;
        this.classRoomService = classRoomService;
        this.fileStorageService = fileStorageService;
        this.questionService = questionService;
    }

    @GetMapping("/teacher")
    public String teacher(Model model) {
        model.addAttribute("schedules", scheduleService.findAll());
        model.addAttribute("classes", classRoomService.findAll());
        model.addAttribute("questions", questionService.findAllOrdered());
        model.addAttribute("historyQuestions", questionService.findAllOrdered());
        return "teacher";
    }

    @PostMapping("/teacher/schedules")
    public String addSchedule(
            @RequestParam String title,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam String platform,
            @RequestParam(defaultValue = "false") boolean autoAccept,
            Model model
    ) {
        Schedule s = new Schedule();
        s.setTitle(title);
        s.setStartTime(startTime);
        s.setEndTime(endTime);
        s.setPlatform(platform);
        s.setAutoAccept(autoAccept);
        scheduleService.save(s);
        model.addAttribute("schedules", scheduleService.findAll());
        return "redirect:/teacher";
    }

    @PostMapping("/teacher/classes")
    public String addClass(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirectAttributes
    ) {
        if (name == null || name.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("classError", "Tên lớp không được để trống");
            return "redirect:/teacher";
        }

        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(name.trim());
        classRoom.setDescription(description);

        try {
            String imagePath = fileStorageService.storeClassImage(image);
            classRoom.setImagePath(imagePath);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("classError", "Tải ảnh thất bại. Vui lòng thử lại.");
            return "redirect:/teacher";
        }

        classRoomService.save(classRoom);
        redirectAttributes.addFlashAttribute("classSuccess", "Tạo lớp học thành công");
        return "redirect:/teacher";
    }
}
