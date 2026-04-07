import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  template: `
    <div class="login-container">
      <div class="login-bg"></div>
      <mat-card class="login-card">
        <mat-card-header>
          <div class="logo">
            <mat-icon>school</mat-icon>
            <span>FineFlow</span>
          </div>
          <p class="subtitle">Gestión Escolar Inteligente</p>
        </mat-card-header>

        <mat-card-content>
          <form (ngSubmit)="onLogin()">
            <mat-form-field appearance="outline">
              <mat-label>Correo electrónico</mat-label>
              <input matInput type="email" [(ngModel)]="email" name="email" required />
              <mat-icon matPrefix>email</mat-icon>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>Contraseña</mat-label>
              <input
                matInput
                [type]="showPassword() ? 'text' : 'password'"
                [(ngModel)]="password"
                name="password"
                required
              />
              <mat-icon matPrefix>lock</mat-icon>
              <button mat-icon-button matSuffix type="button" (click)="togglePassword()">
                <mat-icon>{{ showPassword() ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
            </mat-form-field>

            <mat-form-field appearance="outline">
              <mat-label>ID del Colegio</mat-label>
              <input matInput [(ngModel)]="schoolId" name="schoolId" required />
              <mat-icon matPrefix>business</mat-icon>
            </mat-form-field>

            @if (error()) {
              <div class="error-message">{{ error() }}</div>
            }

            <button mat-raised-button color="primary" type="submit" [disabled]="loading()">
              @if (loading()) {
                <mat-spinner diameter="20"></mat-spinner>
              } @else {
                Iniciar Sesión
              }
            </button>
          </form>
        </mat-card-content>

        <mat-card-footer>
          <p>
            Credenciales de demo: <strong>admin&#64;demo.edu.pe</strong> / <strong>admin123</strong>
          </p>
          <p>School ID: <strong>SCHOOL-001</strong></p>
        </mat-card-footer>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .login-container {
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
        overflow: hidden;
      }
      .login-bg {
        position: absolute;
        inset: 0;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        opacity: 0.9;
      }
      .login-bg::before {
        content: '';
        position: absolute;
        inset: 0;
        background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
      }
      .login-card {
        position: relative;
        width: 100%;
        max-width: 420px;
        padding: 2rem;
        margin: 1rem;
        z-index: 1;
      }
      .logo {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
      }
      .logo mat-icon {
        font-size: 2.5rem;
        width: 2.5rem;
        height: 2.5rem;
        color: #667eea;
      }
      .logo span {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1e293b;
      }
      .subtitle {
        text-align: center;
        color: #64748b;
        margin-bottom: 1.5rem;
      }
      form {
        display: flex;
        flex-direction: column;
        gap: 0.75rem;
      }
      mat-form-field {
        width: 100%;
      }
      button[mat-raised-button] {
        height: 48px;
        font-size: 1rem;
        margin-top: 0.5rem;
      }
      mat-spinner {
        margin: 0 auto;
      }
      .error-message {
        background: #fee2e2;
        color: #dc2626;
        padding: 0.75rem;
        border-radius: 0.5rem;
        font-size: 0.875rem;
        text-align: center;
      }
      mat-card-footer {
        padding: 1rem 0 0;
        text-align: center;
        color: #64748b;
        font-size: 0.875rem;
      }
    `,
  ],
})
export class LoginComponent {
  email = '';
  password = '';
  schoolId = 'SCHOOL-001';

  loading = signal(false);
  error = signal('');
  showPassword = signal(false);

  constructor(private authService: AuthService) {}

  togglePassword() {
    this.showPassword.set(!this.showPassword());
  }

  onLogin() {
    if (!this.email || !this.password || !this.schoolId) {
      this.error.set('Por favor complete todos los campos');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    this.authService
      .login({
        email: this.email,
        password: this.password,
        schoolId: this.schoolId,
      })
      .subscribe({
        next: () => {
          window.location.href = '/dashboard';
        },
        error: (err) => {
          this.loading.set(false);
          this.error.set(err.error?.message || 'Credenciales inválidas');
        },
      });
  }
}
