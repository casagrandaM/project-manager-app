/**
 * Represents a task in the system.
 *
 * <p>Contains the task details together with optional metadata such as its
 * current status, project assignment, creator, and assigned user.</p>
 */
export interface Task {
  id: number;
  title: string;
  description?: string;
  deadline?: string;
  status?: string;
  projectId?: number;
  createdByName?: string;
  lastStepDesc?: string;
  assignedUserId?: number;
  assignedUserName?: string;
}

/**
 * Represents the data required to create a new task.
 *
 * <p>Contains the mandatory task information together with optional
 * description, deadline, and user assignment.</p>
 */
export interface CreateTask {
  title: string;
  description?: string;
  deadline?: string;
  projectId: number;
  createdById: number;
  assignedUserId?: number | null;
}
