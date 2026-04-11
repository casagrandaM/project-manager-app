package at.jku.app.service;

import at.jku.app.entity.Status;
import at.jku.app.entity.StatusHistory;
import at.jku.app.entity.Task;
import at.jku.app.entity.User;
import at.jku.app.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;

    public StatusHistoryService(StatusHistoryRepository statusHistoryRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
    }

    public List<StatusHistory> getByTaskId(Long taskId) {
        return statusHistoryRepository.findByTaskId(taskId);
    }

    public StatusHistory create(StatusHistory history) {
        return statusHistoryRepository.save(history);
    }

    public StatusHistory changeStatus(Task task, Status status, User user) {

        StatusHistory history = new StatusHistory();
        history.setTask(task);
        history.setStatus(status);
        history.setCreatedBy(user);
        history.setCreatedAt(LocalDateTime.now());

        return statusHistoryRepository.save(history);
    }

    public Status getLatestStatus(Long taskId) {
        return statusHistoryRepository.findByTaskId(taskId)
                .stream()
                .max(Comparator.comparing(StatusHistory::getCreatedAt))
                .map(StatusHistory::getStatus)
                .orElse(null);
    }

    @Transactional
    public void deleteByTaskId(Long taskId) {
        statusHistoryRepository.deleteByTaskId(taskId);
    }
}