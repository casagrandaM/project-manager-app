package at.jku.app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a task within a project, including metadata
 * such as deadline, creator and last modification details.
 */
@Entity
@Data
@Table(name = "tasks")
public class Task {

    /** The unique task ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The project this task belongs to */
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    /** The title of the task */
    @Column
    private String title;

    /** A detailed description of the task */
    @Column
    private String description;

    /** The due date for the task */
    @Column
    private LocalDate deadline;

    /** The user who created the task */
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    /** The timestamp when the task was created */
    @Column
    private LocalDateTime createdAt;

    /** The user who last modified the task */
    @ManyToOne
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;

    /** The timestamp of the last modification */
    @Column
    private LocalDateTime modifiedAt;

    /** A description of the last completed step */
    @Column
    private String lastStepDesc;
}