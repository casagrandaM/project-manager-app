export interface ActivityEvent {
  type: 'TASK_CREATED' | 'STATUS_CHANGED' | 'TASK_ASSIGNED';
  timestamp: string;
  userName: string;
  taskTitle: string;
  detail: string;
}
