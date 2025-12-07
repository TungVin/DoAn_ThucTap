package com.EduQuiz.Project_intel.service;

import com.EduQuiz.Project_intel.model.Schedule;
import com.EduQuiz.Project_intel.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private static final Set<String> SUPPORTED_PLATFORMS = Set.of("microsoft_teams", "zoom", "google_meet");

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Schedule save(Schedule schedule) {
        if (schedule.getTitle() == null || schedule.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu/kết thúc không hợp lệ");
        }
        if (!schedule.getEndTime().isAfter(schedule.getStartTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        if (schedule.getPlatform() == null || !SUPPORTED_PLATFORMS.contains(schedule.getPlatform())) {
            throw new IllegalArgumentException("Hình thức học không hợp lệ");
        }
        return scheduleRepository.save(schedule);
    }
}
