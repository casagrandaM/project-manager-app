package at.jku.app.service;

import at.jku.app.entity.TaskAssignment;
import at.jku.app.repository.TaskAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing task-to-user assignments.
 */
@Service
public class TaskAssignmentService {

    private final TaskAssignmentRepository repository;

    public TaskAssignmentService(TaskAssignmentRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all assignments for a given task.
     *
     * @param taskId The task ID
     * @return The list of task assignments
     */
    public List<TaskAssignment> getByTaskId(Long taskId) {
        return repository.findByTaskId(taskId);
    }

    /**
     * Retrieves all assignments for a given user.
     *
     * @param assigneeId The assignee's user ID
     * @return The list of task assignments for the user
     */
    public List<TaskAssignment> getByAssigneeId(Long assigneeId) {
        return repository.findByAssigneeId(assigneeId);
    }

    /**
     * Creates or updates a task assignment.
     *
     * @param assignment The task assignment to save
     * @return The saved task assignment
     */
    public TaskAssignment assign(TaskAssignment assignment) {
        return repository.save(assignment);
    }

    /**
     * Removes a task assignment by its ID.
     *
     * @param assignmentId The assignment ID to delete
     */
    public void unassign(Long assignmentId) {
        repository.deleteById(assignmentId);
    }
}