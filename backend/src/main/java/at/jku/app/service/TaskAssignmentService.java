package at.jku.app.service;

import at.jku.app.entity.TaskAssignment;
import at.jku.app.repository.TaskAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskAssignmentService {

    private final TaskAssignmentRepository repository;

    public TaskAssignmentService(TaskAssignmentRepository repository) {
        this.repository = repository;
    }

    public List<TaskAssignment> getByTaskId(Long taskId) {
        return repository.findByTaskId(taskId);
    }

    public List<TaskAssignment> getByAssigneeId(Long assigneeId) {
        return repository.findByAssigneeId(assigneeId);
    }

    public TaskAssignment assign(TaskAssignment assignment) {
        return repository.save(assignment);
    }

    public void unassign(Long assignmentId) {
        repository.deleteById(assignmentId);
    }
}