import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatBadgeModule,
  ],
  template: `
    <mat-sidenav-container class="app-container">
      <mat-sidenav #sidenav mode="side" opened class="sidebar">
        <div class="sidebar-header">
          <mat-icon>school</mat-icon>
          <span>FineFlow</span>
        </div>

        <mat-nav-list>
          <a mat-list-item routerLink="/dashboard" routerLinkActive="active">
            <mat-icon matListItemIcon>dashboard</mat-icon>
            <span matListItemTitle>Dashboard</span>
          </a>

          @if (isAdmin() || isCoordinator()) {
            <a mat-list-item routerLink="/alumnos" routerLinkActive="active">
              <mat-icon matListItemIcon>people</mat-icon>
              <span matListItemTitle>Alumnos</span>
            </a>
            <a mat-list-item routerLink="/profesores" routerLinkActive="active">
              <mat-icon matListItemIcon>groups</mat-icon>
              <span matListItemTitle>Profesores</span>
            </a>
          }

          <a mat-list-item routerLink="/cursos" routerLinkActive="active">
            <mat-icon matListItemIcon>menu_book</mat-icon>
            <span matListItemTitle>Cursos</span>
          </a>

          @if (isAdmin() || isCoordinator()) {
            <a mat-list-item routerLink="/secciones" routerLinkActive="active">
              <mat-icon matListItemIcon>meeting_room</mat-icon>
              <span matListItemTitle>Secciones</span>
            </a>
          }

          <a mat-list-item routerLink="/calificaciones" routerLinkActive="active">
            <mat-icon matListItemIcon>grade</mat-icon>
            <span matListItemTitle>Calificaciones</span>
          </a>

          <a mat-list-item routerLink="/asistencia" routerLinkActive="active">
            <mat-icon matListItemIcon>fact_check</mat-icon>
            <span matListItemTitle>Asistencia</span>
          </a>

          <a mat-list-item routerLink="/horarios" routerLinkActive="active">
            <mat-icon matListItemIcon>schedule</mat-icon>
            <span matListItemTitle>Horarios</span>
          </a>

          @if (isAdmin()) {
            <a mat-list-item routerLink="/usuarios" routerLinkActive="active">
              <mat-icon matListItemIcon>person</mat-icon>
              <span matListItemTitle>Usuarios</span>
            </a>
            <a mat-list-item routerLink="/reportes" routerLinkActive="active">
              <mat-icon matListItemIcon>assessment</mat-icon>
              <span matListItemTitle>Reportes</span>
            </a>
          }

          <a mat-list-item routerLink="/configuracion" routerLinkActive="active">
            <mat-icon matListItemIcon>settings</mat-icon>
            <span matListItemTitle>Configuración</span>
          </a>
        </mat-nav-list>

        <div class="sidebar-footer">
          <button mat-button (click)="logout()">
            <mat-icon>logout</mat-icon>
            Cerrar Sesión
          </button>
        </div>
      </mat-sidenav>

      <mat-sidenav-content class="main-content">
        <mat-toolbar class="header">
          <button mat-icon-button (click)="sidenav.toggle()">
            <mat-icon>menu</mat-icon>
          </button>

          <span class="spacer"></span>

          <button mat-icon-button [matBadge]="notificationCount()" matBadgeColor="warn">
            <mat-icon>notifications</mat-icon>
          </button>

          <button mat-icon-button [matMenuTriggerFor]="userMenu">
            <mat-icon>account_circle</mat-icon>
          </button>

          <mat-menu #userMenu="matMenu">
            <div class="user-info">
              <strong>{{ userName() }}</strong>
              <small>{{ userRole() }}</small>
            </div>
            <mat-divider></mat-divider>
            <button mat-menu-item (click)="logout()">
              <mat-icon>logout</mat-icon>
              Cerrar Sesión
            </button>
          </mat-menu>
        </mat-toolbar>

        <main class="content">
          <router-outlet></router-outlet>
        </main>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: [
    `
      .app-container {
        height: 100vh;
      }
      .sidebar {
        width: 260px;
        background: #1e293b;
        color: white;
      }
      .sidebar-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1.25rem;
        font-size: 1.25rem;
        font-weight: 600;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      }
      .sidebar-header mat-icon {
        color: #818cf8;
      }
      mat-nav-list {
        padding: 0.5rem;
      }
      mat-nav-list a {
        border-radius: 0.5rem;
        margin-bottom: 0.25rem;
        color: rgba(255, 255, 255, 0.7);
      }
      mat-nav-list a:hover {
        background: rgba(255, 255, 255, 0.1);
        color: white;
      }
      mat-nav-list a.active {
        background: #4f46e5;
        color: white;
      }
      .sidebar-footer {
        position: absolute;
        bottom: 0;
        width: 100%;
        padding: 1rem;
        border-top: 1px solid rgba(255, 255, 255, 0.1);
      }
      .sidebar-footer button {
        width: 100%;
        color: rgba(255, 255, 255, 0.7);
        justify-content: flex-start;
      }
      .main-content {
        display: flex;
        flex-direction: column;
        background: #f8fafc;
      }
      .header {
        background: white;
        border-bottom: 1px solid #e2e8f0;
        position: sticky;
        top: 0;
        z-index: 100;
      }
      .spacer {
        flex: 1;
      }
      .content {
        flex: 1;
        padding: 1.5rem;
        overflow-y: auto;
      }
      .user-info {
        padding: 1rem;
        display: flex;
        flex-direction: column;
      }
      .user-info small {
        color: #64748b;
      }
    `,
  ],
})
export class LayoutComponent {
  notificationCount = signal(3);

  constructor(private authService: AuthService) {}

  userName() {
    const user = this.authService.user();
    return user ? `${user.firstName} ${user.lastName}` : '';
  }

  userRole() {
    const user = this.authService.user();
    const roles: Record<string, string> = {
      ADMIN: 'Administrador',
      COORDINATOR: 'Coordinador',
      TEACHER: 'Profesor',
      STUDENT: 'Estudiante',
      GUARDIAN: 'Apoderado',
    };
    return user ? roles[user.role] || user.role : '';
  }

  isAdmin() {
    return this.authService.hasRole(['ADMIN']);
  }

  isCoordinator() {
    return this.authService.hasRole(['ADMIN', 'COORDINATOR']);
  }

  isTeacher() {
    return this.authService.hasRole(['TEACHER']);
  }

  logout() {
    this.authService.logout();
  }
}
