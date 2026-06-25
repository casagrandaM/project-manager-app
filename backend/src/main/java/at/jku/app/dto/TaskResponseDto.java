package at.jku.app.dto;

/**
 * Data transfer object representing a task in API responses.
 */
public class TaskResponseDto {

    /** The unique task ID */
    public Long id;

    /** The title of the task */
    public String title;

    /** The description of the task */
    public String description;

    /** The deadline as an ISO date string, or {@code null} if none */
    public String deadline;

    /** The name of the task's current status (e.g. "To Do", "Done") */
    public String status;

    /** The ID of the project this task belongs to */
    public Long projectId;

    /** The name of the user who created the task */
    public String createdByName;

    /** The ID of the assigned user, or {@code null} if unassigned */
    public Long assignedUserId;

    /** The name of the assigned user, or {@code null} if unassigned */
    public String assignedUserName;
}