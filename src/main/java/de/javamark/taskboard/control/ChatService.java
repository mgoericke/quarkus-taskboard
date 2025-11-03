package de.javamark.taskboard.control;

import de.javamark.taskboard.entity.model.Member;
import de.javamark.taskboard.entity.model.Task;


import de.javamark.taskboard.entity.model.Task.Priority;
import de.javamark.taskboard.entity.model.Task.TaskStatus;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ChatService {

    @Inject
    TaskAssistant taskAssistant;

    @Transactional
    @ActivateRequestContext
    public String processQuestion(String question, String sessionId, Long projectId) {
        try {
            Log.info("Processing question: " + question);

            // Load project context
            ProjectContext context = loadProjectContext(projectId);
            Log.info("Loaded context - Tasks: " + context.projectTasks.size() +
                    ", Members: " + context.teamMembers.size());

            // Question with context
            String enrichedQuestion = enrichQuestionWithContext(question, context);
            Log.info("Enriched question length: " + enrichedQuestion.length());

            // AI call mit Debugging
            Log.info("Calling TaskAssistant...");
            String result = taskAssistant.askAboutTasks(enrichedQuestion, sessionId);
            Log.info("TaskAssistant response: " + result);

            return result;

        } catch (Exception e) {
            Log.error("Error in ChatService.processQuestion: " + e.getMessage());
            e.printStackTrace();

            // Fallback-Antwort basierend auf einfachen Regeln
            return generateFallbackResponse(question, projectId);
        }
    }

    private String generateFallbackResponse(String question, Long projectId) {
        try {
            String lowerQuestion = question.toLowerCase();

            if (lowerQuestion.contains("überfällig")) {
                List<Task> overdue = Task.findOverdue();
                if (overdue.isEmpty()) {
                    return "Es gibt keine überfälligen Tasks.";
                } else {
                    return "Überfällige Tasks: " + overdue.stream()
                            .map(t -> t.title)
                            .collect(Collectors.joining(", "));
                }
            } else if (lowerQuestion.contains("high priority") || lowerQuestion.contains("high")) {
                List<Task> highPriorityTasks = Task.find("priority = ?1", Task.Priority.HIGH).list();
                return "High Priority Tasks: " + highPriorityTasks.stream()
                        .map(t -> t.title)
                        .collect(Collectors.joining(", "));
            } else if (lowerQuestion.contains("statistik") || lowerQuestion.contains("fortschritt")) {
                List<Task> allTasks = Task.findByProject(projectId);
                long done = allTasks.stream().filter(t -> t.status == Task.TaskStatus.DONE).count();
                return String.format("Projekt-Fortschritt: %d von %d Tasks erledigt (%d%%)",
                        done, allTasks.size(), allTasks.size() > 0 ? (done * 100 / allTasks.size()) : 0);
            } else {
                return "Ich kann dir bei Fragen zu Tasks, Prioritäten, Team-Mitgliedern und Projektfortschritt helfen. " +
                        "Probiere Fragen wie: 'Welche Tasks sind überfällig?' oder 'Zeige mir alle High Priority Tasks'";
            }
        } catch (Exception e) {
            return "Ich konnte deine Frage nicht beantworten, aber die Datenbank-Verbindung funktioniert.";
        }
    }
    private ProjectContext loadProjectContext(Long projectId) {
        List<Task> projectTasks = Task.findByProject(projectId);
        List<Task> overdueTasks = Task.findOverdue();
        List<Member> teamMembers = Member.find(
                "SELECT DISTINCT m FROM Member m JOIN m.team t JOIN t.projects p WHERE p.id = ?1",
                projectId
        ).list();

        return new ProjectContext(projectTasks, overdueTasks, teamMembers);
    }

    private String enrichQuestionWithContext(String question, ProjectContext context) {
        StringBuilder enriched = new StringBuilder();
        enriched.append("KONTEXT-INFORMATIONEN:\n\n");

        // Current tasks overview
        enriched.append("=== AKTUELLE TASKS ===\n");
        if (context.projectTasks.isEmpty()) {
            enriched.append("Keine Tasks im Projekt vorhanden.\n\n");
        } else {
            for (Task task : context.projectTasks) {
                enriched.append(String.format(
                        "- Task ID: %d | Titel: '%s' | Status: %s | Priorität: %s | Zugewiesen an: %s",
                        task.id,
                        task.title,
                        task.status,
                        task.priority,
                        task.assignedTo != null ? task.assignedTo.name : "Niemand"
                ));

                if (task.dueDate != null) {
                    enriched.append(" | Deadline: ").append(task.dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                }
                enriched.append("\n");
            }
            enriched.append("\n");
        }

        // Overdue tasks
        enriched.append("=== ÜBERFÄLLIGE TASKS ===\n");
        if (context.overdueTasks.isEmpty()) {
            enriched.append("Keine überfälligen Tasks.\n\n");
        } else {
            for (Task task : context.overdueTasks) {
                enriched.append(String.format(
                        "- ÜBERFÄLLIG: '%s' | Zugewiesen an: %s | Deadline war: %s\n",
                        task.title,
                        task.assignedTo != null ? task.assignedTo.name : "Niemand",
                        task.dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                ));
            }
            enriched.append("\n");
        }

        // Team members
        enriched.append("=== TEAM-MITGLIEDER ===\n");
        if (context.teamMembers.isEmpty()) {
            enriched.append("Keine Team-Mitglieder gefunden.\n\n");
        } else {
            for (Member member : context.teamMembers) {
                long memberTaskCount = context.projectTasks.stream()
                        .filter(task -> task.assignedTo != null && task.assignedTo.id.equals(member.id))
                        .count();
                enriched.append(String.format(
                        "- %s | Role: %s | Aktuelle Tasks: %d\n",
                        member.name, member.role, memberTaskCount
                ));
            }
            enriched.append("\n");
        }

        // Statistics
        long todoCount = context.projectTasks.stream().filter(t -> t.status == TaskStatus.TODO).count();
        long inProgressCount = context.projectTasks.stream().filter(t -> t.status == TaskStatus.IN_PROGRESS).count();
        long reviewCount = context.projectTasks.stream().filter(t -> t.status == TaskStatus.REVIEW).count();
        long doneCount = context.projectTasks.stream().filter(t -> t.status == TaskStatus.DONE).count();
        long highPriorityCount = context.projectTasks.stream().filter(t -> t.priority == Priority.HIGH).count();

        enriched.append("=== PROJEKT-STATISTIKEN ===\n");
        enriched.append(String.format("TODO: %d | IN_PROGRESS: %d | REVIEW: %d | DONE: %d\n",
                todoCount, inProgressCount, reviewCount, doneCount));
        enriched.append(String.format("High Priority Tasks: %d | Überfällige Tasks: %d\n\n",
                highPriorityCount, context.overdueTasks.size()));

        enriched.append("BENUTZER-FRAGE:\n").append(question);

        return enriched.toString();
    }

    // Helper class for context
    public static class ProjectContext {
        public final List<Task> projectTasks;
        public final List<Task> overdueTasks;
        public final List<Member> teamMembers;

        public ProjectContext(List<Task> projectTasks, List<Task> overdueTasks, List<Member> teamMembers) {
            this.projectTasks = projectTasks;
            this.overdueTasks = overdueTasks;
            this.teamMembers = teamMembers;
        }
    }
}