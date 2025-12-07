package com.EduQuiz.Project_intel.controller;

import com.EduQuiz.Project_intel.model.Schedule;
import com.EduQuiz.Project_intel.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class TeacherScheduleController {

    private final ScheduleService scheduleService;

    public TeacherScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/teacher")
    public String teacher(Model model) {
        model.addAttribute("schedules", scheduleService.findAll());
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
}

