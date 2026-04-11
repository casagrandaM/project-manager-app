export interface Task {
  id: number;
  title: string;
  description?: string;
  deadline?: string;
  status?: string;
  projectId?: number;
  createdByName?: string;
  lastStepDesc?: string;
}

export interface CreateTask {
  title: string;
  description?: string;
  deadline?: string;
  projectId: number;
  createdById: number;
}

export interface UpdateTask {
  title: string;
  description?: string;
  deadline?: string;
  lastStepDesc?: string;
}
