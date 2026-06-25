package at.jku.app.dto;

/**
 * Response payload representing a single entry in a project's activity feed,
 * such as a task creation, status change or task assignment.
 */
public class ActivityEventDto {
    /** The event type: {@code TASK_CREATED}, {@code STATUS_CHANGED} or {@code TASK_ASSIGNED}. */
    public String type;
    /** The timestamp of the event as an ISO string. */
    public String timestamp;
    /** The name of the user who triggered the event. */
    public String userName;
    /** The title of the task the event refers to. */
    public String taskTitle;
    /** A human-readable description of the event. */
    public String detail;
}
