package at.jku.app.service;

import at.jku.app.entity.Status;
import at.jku.app.entity.StatusHistory;
import at.jku.app.entity.Task;
import at.jku.app.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final StatusHistoryService statusHistoryService;

    public TaskService(TaskRepository taskRepository,
                       StatusHistoryService statusHistoryService) {
        this.taskRepository = taskRepository;
        this.statusHistoryService = statusHistoryService;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDeadline(updatedTask.getDeadline());
        task.setLastStepDesc(updatedTask.getLastStepDesc());

        return taskRepository.save(task);
    }

    // STATUS LOGIC
    public Status getCurrentStatus(Long taskId) {
        List<StatusHistory> history = statusHistoryService.getByTaskId(taskId);

        return history.stream()
                .max(Comparator.comparing(StatusHistory::getCreatedAt))
                .map(StatusHistory::getStatus)
                .orElse(null);
    }

    @Transactional
    public void deleteTask(Long id) {
        // 1. Zuerst alle Status-Histories löschen, die auf diesen Task zeigen
        statusHistoryService.deleteByTaskId(id);

        // 2. Dann den Task löschen
        taskRepository.deleteById(id);
    }
}