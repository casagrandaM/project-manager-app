package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entity recording a status transition for a task. Each entry represents
 * a single change to a new status, forming an audit trail.
 */
@Entity
@Data
@Table(name = "status_histories")
public class StatusHistory {

    /** The unique status history entry ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The task whose status was changed */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    /** The new status that was set */
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    /** The user who performed the status change */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    /** The timestamp when the status change occurred */
    @Column
    private LocalDateTime createdAt;
}