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

export interface CreateTask {
  title: string;
  description?: string;
  deadline?: string;
  projectId: number;
  createdById: number;
  assignedUserId?: number | null;
}
