import {ChangeDetectorRef, Component} from '@angular/core';
import {UserService} from '../../../../services/user.service';
import {User} from '../../../../models/user.model';
import {AuthService} from '../../../../auth/auth.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  templateUrl: 'user-list.component.html',
})
export class UserListComponent {
  users: User[] = [];
  successMessage: string = '';
  errorMessage: string = '';

  constructor(private userService: UserService, protected authService: AuthService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.cdr.detectChanges();
      },
      error: () => this.errorMessage = 'Fehler beim Laden der Benutzer.'
    });
  }

  deleteUser(id: number) {
    this.userService.deleteUser(id).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);
        this.successMessage = 'Benutzer erfolgreich gelöscht.';
        this.cdr.detectChanges();
      },
      error: () => this.errorMessage = 'Fehler beim Löschen des Benutzers.'
    });
  }
}

