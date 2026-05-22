package at.jku.app.controller;

import at.jku.app.dto.ActivityEventDto;
import at.jku.app.dto.ProjectCreateDto;
import at.jku.app.dto.ProjectResponseDto;
import at.jku.app.dto.ProjectUpdateDto;
import at.jku.app.entity.Project;
import at.jku.app.entity.StatusHistory;
import at.jku.app.entity.Task;
import at.jku.app.entity.TaskAssignment;
import at.jku.app.repository.StatusHistoryRepository;
import at.jku.app.repository.TaskAssignmentRepository;
import at.jku.app.repository.TaskRepository;
import at.jku.app.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskRepository taskRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private static final Long CURRENT_USER_ID = 1L;

    public ProjectController(ProjectService projectService,
                             TaskRepository taskRepository,
                             StatusHistoryRepository statusHistoryRepository,
                             TaskAssignmentRepository taskAssignmentRepository) {
        this.projectService = projectService;
        this.taskRepository = taskRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    @GetMapping
    public List<ProjectResponseDto> getProjects() {
        return projectService.getProjectsForUser(CURRENT_USER_ID)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProjectResponseDto getProject(@PathVariable Long id) {
        return toDto(projectService.getProjectById(id));
    }

    @PostMapping
    public ProjectResponseDto createProject(@RequestBody ProjectCreateDto dto) {
        Project project = projectService.createProject(dto.title, dto.description, CURRENT_USER_ID);
        return toDto(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        try {
            Project project = projectService.updateProject(id, dto.title, dto.description, CURRENT_USER_ID);
            return ResponseEntity.ok(toDto(project));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id, CURRENT_USER_ID);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/activity")
    public List<ActivityEventDto> getActivity(@PathVariable Long id) {
        List<ActivityEventDto> events = new ArrayList<>();

        for (Task task : taskRepository.findByProjectId(id)) {
            if (task.getCreatedAt() != null) {
                ActivityEventDto e = new ActivityEventDto();
                e.type = "TASK_CREATED";
                e.timestamp = task.getCreatedAt().toString();
                e.userName = task.getCreatedBy() != null ? task.getCreatedBy().getName() : "Unbekannt";
                e.taskTitle = task.getTitle();
                e.detail = "Task erstellt";
                events.add(e);
            }
        }

        for (StatusHistory sh : statusHistoryRepository.findByTaskProjectId(id)) {
            ActivityEventDto e = new ActivityEventDto();
            e.type = "STATUS_CHANGED";
            e.timestamp = sh.getCreatedAt() != null ? sh.getCreatedAt().toString() : "";
            e.userName = sh.getCreatedBy() != null ? sh.getCreatedBy().getName() : "Unbekannt";
            e.taskTitle = sh.getTask() != null ? sh.getTask().getTitle() : "";
            e.detail = "Status geändert zu \"" + (sh.getStatus() != null ? sh.getStatus().getName() : "") + "\"";
            events.add(e);
        }

        for (TaskAssignment ta : taskAssignmentRepository.findByTaskProjectId(id)) {
            ActivityEventDto e = new ActivityEventDto();
            e.type = "TASK_ASSIGNED";
            e.timestamp = ta.getCreatedAt() != null ? ta.getCreatedAt().toString() : "";
            e.userName = ta.getCreatedBy() != null ? ta.getCreatedBy().getName() : "Unbekannt";
            e.taskTitle = ta.getTask() != null ? ta.getTask().getTitle() : "";
            e.detail = "Zugewiesen an " + (ta.getAssignee() != null ? ta.getAssignee().getName() : "Unbekannt");
            events.add(e);
        }

        events.sort(Comparator.comparing((ActivityEventDto e) -> e.timestamp).reversed());
        return events;
    }

    private ProjectResponseDto toDto(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.description = project.getDescription();
        dto.createdAt = project.getCreatedAt() != null ? project.getCreatedAt().toString() : null;
        if (project.getCreatedBy() != null) {
            dto.createdById = project.getCreatedBy().getId();
            dto.createdByName = project.getCreatedBy().getName();
            dto.isOwner = project.getCreatedBy().getId().equals(CURRENT_USER_ID);
        }
        return dto;
    }
}
