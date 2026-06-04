export interface Project {
  id: number;
  title: string;
  description?: string;
  createdAt?: string;
  createdById?: number;
  createdByName?: string;
  isOwner?: boolean;
}

export interface CreateProject {
  title: string;
  description?: string;
}

export interface UpdateProject {
  title: string;
  description?: string;
}

export interface ActivityEvent {
  type: string;         // TASK_CREATED, STATUS_CHANGED, TASK_ASSIGNED
  timestamp: string;
  userName: string;
  taskTitle: string;
  detail: string;
}
