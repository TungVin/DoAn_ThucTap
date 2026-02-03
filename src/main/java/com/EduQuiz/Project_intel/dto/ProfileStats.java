package com.EduQuiz.Project_intel.dto;

public class ProfileStats {
    private long totalExams;
    private long publicExams;
    private long totalQuestions;
    private long totalCategories;
    private long totalClasses;
    private long totalSchedules;

    // Nếu chưa có thống kê theo user thì để -1
    private long myExams = -1;

    public long getTotalExams() { return totalExams; }
    public void setTotalExams(long totalExams) { this.totalExams = totalExams; }

    public long getPublicExams() { return publicExams; }
    public void setPublicExams(long publicExams) { this.publicExams = publicExams; }

    public long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }

    public long getTotalCategories() { return totalCategories; }
    public void setTotalCategories(long totalCategories) { this.totalCategories = totalCategories; }

    public long getTotalClasses() { return totalClasses; }
    public void setTotalClasses(long totalClasses) { this.totalClasses = totalClasses; }

    public long getTotalSchedules() { return totalSchedules; }
    public void setTotalSchedules(long totalSchedules) { this.totalSchedules = totalSchedules; }

    public long getMyExams() { return myExams; }
    public void setMyExams(long myExams) { this.myExams = myExams; }
}
