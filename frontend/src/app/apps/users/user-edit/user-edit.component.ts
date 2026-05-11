import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import {User} from '../../../../models/user.model';
import {UserService} from '../../../../services/user.service';

@Component({
  selector: 'app-user-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: 'user-edit.component.html',
})
export class UserEditComponent implements OnInit {

  user$!: Observable<User>;

  formData = {
    name: '',
    email: '',
  };

  saving = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private userService: UserService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.user$ = this.userService.getUser(1).pipe(
      tap(user => {
        this.formData.name = user.name;
        this.formData.email = user.email;
      })
    );
  }

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

  cancel(): void {
    this.router.navigate(['/user/1']);
  }

  onFocus(event: FocusEvent): void {
    (event.target as HTMLElement).style.borderColor = '#2563eb';
    (event.target as HTMLElement).style.boxShadow = '0 0 0 3px rgba(37,99,235,0.12)';
  }

  onBlur(event: FocusEvent): void {
    (event.target as HTMLElement).style.borderColor = '#e2e8f0';
    (event.target as HTMLElement).style.boxShadow = 'none';
  }
}
