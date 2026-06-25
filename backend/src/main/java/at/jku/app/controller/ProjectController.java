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
import at.jku.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing projects and exposing project-related data
 * such as project CRUD operations and the aggregated activity feed.
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public ProjectController(ProjectService projectService,
                             UserService userService,
                             TaskRepository taskRepository,
                             StatusHistoryRepository statusHistoryRepository,
                             TaskAssignmentRepository taskAssignmentRepository) {
        this.projectService = projectService;
		this.userService = userService;
		this.taskRepository = taskRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    /**
     * Retrieves all projects the currently authenticated user is a member of.
     *
     * @return The list of the user's projects as {@link ProjectResponseDto}s
     */
    @GetMapping
    public List<ProjectResponseDto> getProjects() {
        return projectService.getProjectsForUser(userService.getCurrentUser().getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single project by ID.
     *
     * @param id The project ID
     * @return The project as {@link ProjectResponseDto}
     */
    @GetMapping("/{id}")
    public ProjectResponseDto getProject(@PathVariable Long id) {
        return toDto(projectService.getProjectById(id));
    }

    /**
     * Creates a new project owned by the currently authenticated user. The
     * creator is automatically added as a project member and project manager.
     *
     * @param dto The project creation data (title and description)
     * @return The created project as {@link ProjectResponseDto}
     */
    @PostMapping
    public ProjectResponseDto createProject(@RequestBody ProjectCreateDto dto) {
        Project project = projectService.createProject(dto.title, dto.description, userService.getCurrentUser().getId());
        return toDto(project);
    }

    /**
     * Updates the title and description of a project. Only the project owner
     * may perform this operation.
     *
     * @param id  The project ID
     * @param dto The updated project data
     * @return The updated project as {@link ProjectResponseDto}, or HTTP 403 if
     *         the current user is not the owner
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        try {
            Project project = projectService.updateProject(id, dto.title, dto.description, userService.getCurrentUser().getId());
            return ResponseEntity.ok(toDto(project));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * Deletes a project together with all of its tasks, status history, task
     * assignments and memberships. Only the project owner may perform this
     * operation.
     *
     * @param id The project ID
     * @return HTTP 200 on success, or HTTP 403 if the current user is not the owner
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id, userService.getCurrentUser().getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * Builds a chronologically sorted activity feed for a project. The feed
     * aggregates task creations, status changes and task assignments across all
     * tasks belonging to the project, ordered from newest to oldest.
     *
     * @param id The project ID
     * @return The list of activity events as {@link ActivityEventDto}s
     */
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

    /**
     * Converts a {@link Project} entity to a {@link ProjectResponseDto},
     * including creator information and whether the current user owns the project.
     *
     * @param project The project entity
     * @return The populated {@link ProjectResponseDto}
     */
    private ProjectResponseDto toDto(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.description = project.getDescription();
        dto.createdAt = project.getCreatedAt() != null ? project.getCreatedAt().toString() : null;
        if (project.getCreatedBy() != null) {
            dto.createdById = project.getCreatedBy().getId();
            dto.createdByName = project.getCreatedBy().getName();
            dto.isOwner = project.getCreatedBy().getId().equals(userService.getCurrentUser().getId());
        }
        return dto;
    }
}
