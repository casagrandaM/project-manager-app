package at.jku.app.service;

import at.jku.app.entity.*;
import at.jku.app.repository.TaskAssignmentRepository;
import at.jku.app.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing tasks, including CRUD operations, status
 * resolution and user assignment handling.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final StatusHistoryService statusHistoryService;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public TaskService(TaskRepository taskRepository,
                       StatusHistoryService statusHistoryService,
                       TaskAssignmentRepository taskAssignmentRepository) {
        this.taskRepository = taskRepository;
        this.statusHistoryService = statusHistoryService;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    /**
     * Retrieves all tasks.
     *
     * @return The list of all tasks
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Retrieves all tasks belonging to a given project.
     *
     * @param projectId The project ID
     * @return The list of tasks in the project
     */
    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    /**
     * Retrieves all tasks assigned to a given user.
     *
     * @param userId The user ID
     * @return The list of tasks assigned to the user
     */
    public List<Task> getTasksForUser(Long userId) {
        return taskAssignmentRepository.findByAssigneeId(userId)
                .stream()
                .map(TaskAssignment::getTask)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single task by its ID.
     *
     * @param id The task ID
     * @return The matching {@link Task}
     * @throws RuntimeException if no task with the given ID exists
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    /**
     * Persists a new task.
     *
     * @param task The task to create
     * @return The saved task
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Updates an existing task's title, description, deadline and last
     * step description.
     *
     * @param id          The ID of the task to update
     * @param updatedTask A task object carrying the new field values
     * @return The updated task
     */
    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDeadline(updatedTask.getDeadline());
        task.setLastStepDesc(updatedTask.getLastStepDesc());

        return taskRepository.save(task);
    }

    /**
     * Determines the current status of a task by finding the most recent
     * status history entry.
     *
     * @param taskId The task ID
     * @return The current {@link Status}, or {@code null} if no history exists
     */
    public Status getCurrentStatus(Long taskId) {
        List<StatusHistory> history = statusHistoryService.getByTaskId(taskId);

        return history.stream()
                .max(Comparator.comparing(StatusHistory::getCreatedAt))
                .map(StatusHistory::getStatus)
                .orElse(null);
    }

    /**
     * Deletes a task together with all of its assignments and status
     * history entries.
     *
     * @param id The task ID
     */
    @Transactional
    public void deleteTask(Long id) {
        List<TaskAssignment> assignments = taskAssignmentRepository.findByTaskId(id);
        taskAssignmentRepository.deleteAll(assignments);
        statusHistoryService.deleteByTaskId(id);
        taskRepository.deleteById(id);
    }

    /**
     * Assigns a user to a task, replacing any existing assignment.
     *
     * @param taskId The task ID
     * @param userId The ID of the user to assign
     */
    public void assignUser(Long taskId, Long userId) {
        Task task = getTaskById(taskId);

        List<TaskAssignment> existing = taskAssignmentRepository.findByTaskId(taskId);
        taskAssignmentRepository.deleteAll(existing);

        User user = new User();
        user.setId(userId);

        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setAssignee(user);
        assignment.setCreatedAt(LocalDateTime.now());

        taskAssignmentRepository.save(assignment);
    }
}