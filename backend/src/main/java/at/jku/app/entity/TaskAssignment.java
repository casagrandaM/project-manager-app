package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entity representing the assignment of a user to a task.
 */
@Entity
@Data
@Table(name = "task_assignments")
public class TaskAssignment {

    /** The unique assignment ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The task that is assigned */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    /** The user the task is assigned to */
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    /** The user who created this assignment */
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    /** The timestamp when the assignment was created */
    @Column
    private LocalDateTime createdAt;
}