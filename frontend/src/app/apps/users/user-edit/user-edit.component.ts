import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {User} from '../../../../models/user.model';
import {UserService} from '../../../../services/user.service';

/**
 * Component responsible for editing the currently authenticated user's profile.
 */
@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: 'user-edit.component.html',
})
export class UserEditComponent implements OnInit {

  user!: User;

  formData = {
    name: '',
    email: '',
  };

  saving = false;
  successMessage = '';
  errorMessage = '';

  constructor(private userService: UserService, private router: Router, private cdr: ChangeDetectorRef) {}

  /**
   * Initializes the form with the currently authenticated user's data.
   */
  ngOnInit(): void {
    this.user = this.userService.getCurrentUser()
    this.formData.name = this.user.name;
    this.formData.email = this.user.email;
  }

  /**
   * Saves updated user information.
   */
  save(): void {
    this.saving = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.userService.updateUser(1, this.formData).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Änderungen gespeichert.';
        this.cdr.markForCheck();

        setTimeout(() => {
          this.successMessage = '';
          this.cdr.markForCheck();
        }, 3000);
      },
      error: () => {
        this.saving = false;
        this.errorMessage = 'Fehler beim Speichern. Bitte erneut versuchen.';
        this.cdr.markForCheck();
      }
    });
  }


  /**
   * Cancels editing and navigates back to the user profile.
   */
  cancel(): void {
    this.router.navigate(['/user/profile']);
  }

  /**
   * Applies focus styling to form input elements.
   */
  onFocus(event: FocusEvent): void {
    (event.target as HTMLElement).style.borderColor = '#2563eb';
    (event.target as HTMLElement).style.boxShadow = '0 0 0 3px rgba(37,99,235,0.12)';
  }

  /**
   * Removes focus styling from form input elements.
   */
  onBlur(event: FocusEvent): void {
    (event.target as HTMLElement).style.borderColor = '#e2e8f0';
    (event.target as HTMLElement).style.boxShadow = 'none';
  }
}
