package de.javamark.taskboard.boundary.model;

public class ProjectStatsResponse {
    public final Long todoCount;
    public final Long inProgressCount;
    public final Long reviewCount;
    public final Long doneCount;
    public final Long overdueCount;
    public final Long totalCount;
    public final Double completionPercentage;

    public ProjectStatsResponse(Long todo, Long inProgress, Long review, Long done, Long overdue) {
        this.todoCount = todo;
        this.inProgressCount = inProgress;
        this.reviewCount = review;
        this.doneCount = done;
        this.overdueCount = overdue;
        this.totalCount = todo + inProgress + review + done;
        this.completionPercentage = totalCount > 0 ? (done.doubleValue() / totalCount.doubleValue()) * 100 : 0.0;
    }
}