package at.jku.app.dto;

/**
 * Data transfer object for creating a new task.
 */
public class TaskCreateDto {

    /** The title of the task */
    public String title;

    /** An optional description of the task */
    public String description;

    /** The deadline in ISO date format (yyyy-MM-dd), or {@code null} if none */
    public String deadline;

    /** The ID of the project this task belongs to */
    public Long projectId;

    /** Optional user ID to assign the task to upon creation */
    public Long assignedUserId;
}