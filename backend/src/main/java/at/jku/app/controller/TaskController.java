package at.jku.app.controller;

import at.jku.app.dto.TaskCreateDto;
import at.jku.app.dto.TaskResponseDto;
import at.jku.app.dto.TaskUpdateDto;
import at.jku.app.repository.TaskAssignmentRepository;
import at.jku.app.entity.*;
import at.jku.app.service.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing tasks, including CRUD operations,
 * status transitions and user assignments.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final StatusHistoryService statusHistoryService;
    private final StatusService statusService;
    private final UserService userService;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public TaskController(TaskService taskService,
                          StatusHistoryService statusHistoryService,
                          StatusService statusService,
                          UserService userService,
                          TaskAssignmentRepository taskAssignmentRepository) {
        this.taskService = taskService;
        this.statusHistoryService = statusHistoryService;
        this.statusService = statusService;
        this.userService = userService;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    /**
     * Retrieves all tasks, optionally filtered by project.
     *
     * @param projectId Optional project ID to filter tasks by; if {@code null},
     *                  all tasks are returned
     * @return The list of tasks as {@link TaskResponseDto}s
     */
    @GetMapping
    public List<TaskResponseDto> getAllTasks(@RequestParam(required = false) Long projectId) {
        List<Task> tasks = projectId != null
                ? taskService.getTasksByProjectId(projectId)
                : taskService.getAllTasks();
        return tasks.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Creates a new task and assigns it the default status "To Do". If an
     * assigned user ID is provided, the task is also assigned to that user.
     *
     * @param dto The task creation data (title, description, deadline,
     *            project ID and optional assignee)
     * @return The created task as {@link TaskResponseDto}
     */
    @PostMapping
    public TaskResponseDto createTask(@RequestBody TaskCreateDto dto) {
        Task task = new Task();
        task.setTitle(dto.title);
        task.setDescription(dto.description);
        task.setDeadline(dto.deadline != null ? LocalDate.parse(dto.deadline) : null);

        if (dto.projectId != null) {
            Project project = new Project();
            project.setId(dto.projectId);
            task.setProject(project);
        }

        User user = userService.getCurrentUser();
        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        Task saved = taskService.createTask(task);

        // Set standard status "To Do"
        Status todo = statusService.getByName("To Do");
        statusHistoryService.changeStatus(saved, todo, user);

        if (dto.assignedUserId != null) {
            taskService.assignUser(saved.getId(), dto.assignedUserId);
        }

        return toDto(saved);
    }

    /**
     * Updates an existing task's title, description, deadline and last step
     * description. If an assigned user ID is provided, the task assignment
     * is updated as well.
     *
     * @param id  The task ID
     * @param dto The updated task data
     * @return The updated task as {@link TaskResponseDto}
     */
    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id, @RequestBody TaskUpdateDto dto) {
        Task task = taskService.getTaskById(id);
        task.setTitle(dto.title);
        task.setDescription(dto.description);
        task.setDeadline(dto.deadline != null ? LocalDate.parse(dto.deadline) : null);
        task.setLastStepDesc(dto.lastStepDesc);
        task.setModifiedAt(LocalDateTime.now());

        Task updated = taskService.updateTask(id, task);

        if (dto.assignedUserId != null) {
            taskService.assignUser(id, dto.assignedUserId);
        }

        return toDto(updated);
    }

    /**
     * Deletes a task by ID together with its associated data.
     *
     * @param id The task ID
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    /**
     * Changes the status of a task. The transition is recorded in the
     * status history for the currently authenticated user.
     *
     * @param taskId   The task ID
     * @param statusId The ID of the new status
     */
    @PostMapping("/{taskId}/status/{statusId}")
    public void changeStatus(@PathVariable Long taskId, @PathVariable Long statusId) {
        Task task = taskService.getTaskById(taskId);
        Status status = statusService.getById(statusId);

        User user = userService.getCurrentUser();

        statusHistoryService.changeStatus(task, status, user);
    }

    /**
     * Converts a {@link Task} entity to a {@link TaskResponseDto}, including
     * the current status and the first assigned user if present.
     *
     * @param task The task entity
     * @return The populated {@link TaskResponseDto}
     */
    private TaskResponseDto toDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.id = task.getId();
        dto.title = task.getTitle();
        dto.description = task.getDescription();
        dto.deadline = task.getDeadline() != null ? task.getDeadline().toString() : null;
        dto.projectId = task.getProject() != null ? task.getProject().getId() : null;

        Status currentStatus = taskService.getCurrentStatus(task.getId());
        dto.status = currentStatus != null ? currentStatus.getName() : "Unknown";

        List<TaskAssignment> assignments = taskAssignmentRepository.findByTaskId(task.getId());
        if (!assignments.isEmpty()) {
            TaskAssignment assignment = assignments.get(0);
            dto.assignedUserId = assignment.getAssignee().getId();
            dto.assignedUserName = assignment.getAssignee().getName();
        }

        dto.createdByName = null;
        return dto;
    }
}