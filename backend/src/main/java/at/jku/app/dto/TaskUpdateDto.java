package at.jku.app.dto;

/**
 * Data transfer object for updating an existing task.
 */
public class TaskUpdateDto {

    /** The updated title */
    public String title;

    /** The updated description */
    public String description;

    /** The updated deadline in ISO date format (yyyy-MM-dd), or {@code null} */
    public String deadline;

    /** A description of the last completed step */
    public String lastStepDesc;

    /** Optional user ID to reassign the task to */
    public Long assignedUserId;
}