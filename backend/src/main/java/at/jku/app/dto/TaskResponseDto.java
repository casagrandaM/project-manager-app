package at.jku.app.dto;

public class TaskResponseDto {

    public Long id;
    public String title;
    public String description;
    public String deadline;

    public String status;
    public Long projectId;

    public String createdByName;
    public Long assignedUserId;
    public String assignedUserName;
}