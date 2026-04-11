package at.jku.app.controller;

import at.jku.app.dto.TaskCreateDto;
import at.jku.app.dto.TaskResponseDto;
import at.jku.app.dto.TaskUpdateDto;
import at.jku.app.entity.*;
import at.jku.app.service.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final StatusHistoryService statusHistoryService;
    private final StatusService statusService;

    public TaskController(TaskService taskService,
                          StatusHistoryService statusHistoryService,
                          StatusService statusService) {
        this.taskService = taskService;
        this.statusHistoryService = statusHistoryService;
        this.statusService = statusService;
    }

    @GetMapping
    public List<TaskResponseDto> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public TaskResponseDto createTask(@RequestBody TaskCreateDto dto) {
        Task task = new Task();
        task.setTitle(dto.title);
        task.setDescription(dto.description);
        task.setDeadline(dto.deadline != null ? LocalDate.parse(dto.deadline) : null);

        Project project = new Project();
        project.setId(dto.projectId != null ? dto.projectId : 1L);
        task.setProject(project);

        User user = new User();
        user.setId(1L);
        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        Task saved = taskService.createTask(task);

        // Standard Status "To Do" setzen
        Status todo = statusService.getByName("To Do");
        statusHistoryService.changeStatus(saved, todo, user);

        return toDto(saved);
    }

    @PutMapping("/{id}")
    public TaskResponseDto updateTask(@PathVariable Long id, @RequestBody TaskUpdateDto dto) {
        Task task = taskService.getTaskById(id);
        task.setTitle(dto.title);
        task.setDescription(dto.description);
        task.setDeadline(dto.deadline != null ? LocalDate.parse(dto.deadline) : null);
        task.setLastStepDesc(dto.lastStepDesc);
        task.setModifiedAt(LocalDateTime.now());

        return toDto(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/{taskId}/status/{statusId}")
    public void changeStatus(@PathVariable Long taskId, @PathVariable Long statusId) {
        Task task = taskService.getTaskById(taskId);
        Status status = statusService.getById(statusId);

        User user = new User();
        user.setId(1L);

        statusHistoryService.changeStatus(task, status, user);
    }

    private TaskResponseDto toDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.id = task.getId();
        dto.title = task.getTitle();
        dto.description = task.getDescription();
        dto.deadline = task.getDeadline() != null ? task.getDeadline().toString() : null;
        dto.projectId = task.getProject() != null ? task.getProject().getId() : null;

        Status currentStatus = taskService.getCurrentStatus(task.getId());
        dto.status = currentStatus != null ? currentStatus.getName() : "Unknown";

        dto.createdByName = null;
        return dto;
    }
}