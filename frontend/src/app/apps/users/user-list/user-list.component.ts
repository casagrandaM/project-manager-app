import {ChangeDetectorRef, Component} from '@angular/core';
import {UserService} from '../../../../services/user.service';
import {User} from '../../../../models/user.model';
import {AuthService} from '../../../../auth/auth.service';

/**
 * Component responsible for displaying and managing the list of users.
 * <p>
 * Provides functionality to load all users and delete selected users.
 * Only available for users with ADMIN role
 */
@Component({
  selector: 'app-user-list',
  standalone: true,
  templateUrl: 'user-list.component.html',
})
export class UserListComponent {
  users: User[] = [];
  errorMessage: string = '';

  constructor(private userService: UserService, protected authService: AuthService, private cdr: ChangeDetectorRef) {}

  /**
   * Initiates the component by loading all users.
   */
  ngOnInit() {
    this.loadUsers();
  }

  /**
   * Loads all users from the API.
   */
  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.cdr.detectChanges();
      },
      error: () => this.errorMessage = 'Fehler beim Laden der Benutzer.'
    });
  }
}

