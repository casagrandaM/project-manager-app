package at.jku.app.dto;

public class ActivityEventDto {
    public String type;      // TASK_CREATED, STATUS_CHANGED, TASK_ASSIGNED
    public String timestamp;
    public String userName;
    public String taskTitle;
    public String detail;
}
