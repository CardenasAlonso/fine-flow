import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
  ],
  template: `
    <div class="dashboard">
      <div class="page-header">
        <h1>{{ title() }}</h1>
        <p>{{ subtitle() }}</p>
      </div>

      <div class="kpi-grid">
        @for (kpi of kpis(); track kpi.title) {
          <mat-card class="kpi-card">
            <div class="kpi-icon" [style.background]="kpi.color">
              <mat-icon>{{ kpi.icon }}</mat-icon>
            </div>
            <div class="kpi-content">
              <span class="kpi-value">{{ kpi.value }}</span>
              <span class="kpi-title">{{ kpi.title }}</span>
            </div>
          </mat-card>
        }
      </div>

      <div class="charts-grid">
        <mat-card>
          <mat-card-header>
            <mat-card-title>Estadísticas Recientes</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="stats-list">
              <div class="stat-item">
                <mat-icon class="text-success">trending_up</mat-icon>
                <span>Asistencia promedio: 95%</span>
              </div>
              <div class="stat-item">
                <mat-icon class="text-primary">grade</mat-icon>
                <span>Promedio general: 14.5</span>
              </div>
              <div class="stat-item">
                <mat-icon class="text-warning">warning</mat-icon>
                <span>12 alertas de asistencia</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <mat-card>
          <mat-card-header>
            <mat-card-title>Actividad Reciente</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="activity-list">
              @for (activity of activities; track activity.id) {
                <div class="activity-item">
                  <mat-icon>{{ activity.icon }}</mat-icon>
                  <div class="activity-content">
                    <span>{{ activity.text }}</span>
                    <small>{{ activity.time }}</small>
                  </div>
                </div>
              }
            </div>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [
    `
      .dashboard {
        padding: 0;
      }
      .page-header {
        margin-bottom: 2rem;
      }
      .page-header h1 {
        font-size: 1.75rem;
        font-weight: 600;
        color: #1e293b;
      }
      .page-header p {
        color: #64748b;
        margin-top: 0.25rem;
      }
      .kpi-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 1.5rem;
        margin-bottom: 1.5rem;
      }
      .kpi-card {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1.25rem;
      }
      .kpi-icon {
        width: 56px;
        height: 56px;
        border-radius: 1rem;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
      }
      .kpi-icon mat-icon {
        font-size: 1.75rem;
      }
      .kpi-content {
        display: flex;
        flex-direction: column;
      }
      .kpi-value {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1e293b;
      }
      .kpi-title {
        font-size: 0.875rem;
        color: #64748b;
      }
      .charts-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
        gap: 1.5rem;
      }
      .stats-list,
      .activity-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
      }
      .stat-item,
      .activity-item {
        display: flex;
        align-items: center;
        gap: 0.75rem;
      }
      .stat-item mat-icon {
        color: #64748b;
      }
      .activity-item {
        padding: 0.75rem;
        background: #f8fafc;
        border-radius: 0.5rem;
      }
      .activity-content {
        display: flex;
        flex-direction: column;
      }
      .activity-content small {
        color: #94a3b8;
        font-size: 0.75rem;
      }
      .text-success {
        color: #22c55e !important;
      }
      .text-primary {
        color: #3b82f6 !important;
      }
      .text-warning {
        color: #f59e0b !important;
      }
    `,
  ],
})
export class DashboardComponent implements OnInit {
  title = signal('Panel de Administración');
  subtitle = signal('Gestiona tu institución educativa de forma integral.');

  kpis = signal([
    { icon: 'people', title: 'Total Alumnos', value: '245', color: '#3b82f6' },
    { icon: 'groups', title: 'Profesores', value: '28', color: '#8b5cf6' },
    { icon: 'school', title: 'Secciones', value: '12', color: '#22c55e' },
    { icon: 'assignment', title: 'Cursos Activos', value: '8', color: '#f59e0b' },
  ]);

  activities = [
    { id: 1, icon: 'person_add', text: 'Nuevo estudiante registrado', time: 'Hace 5 min' },
    { id: 2, icon: 'grade', text: 'Calificaciones actualizadas', time: 'Hace 1 hora' },
    { id: 3, icon: 'fact_check', text: 'Registro de asistencia completado', time: 'Hace 2 horas' },
    { id: 4, icon: 'notifications', text: 'Nueva notificación enviada', time: 'Hace 3 horas' },
  ];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    const user = this.authService.user();
    if (user) {
      const roleGreetings: Record<string, { title: string; subtitle: string }> = {
        ADMIN: {
          title: 'Panel de Administración',
          subtitle: 'Gestiona tu institución educativa de forma integral.',
        },
        COORDINATOR: {
          title: 'Panel de Coordinación',
          subtitle: 'Supervisa el rendimiento académico y la asistencia.',
        },
        TEACHER: {
          title: 'Mi Panel Docente',
          subtitle: 'Gestiona tus clases, asistencia y calificaciones.',
        },
        STUDENT: {
          title: 'Mi Portal Estudiantil',
          subtitle: 'Consulta tu horario, notas y tareas pendientes.',
        },
        GUARDIAN: {
          title: 'Portal del Apoderado',
          subtitle: 'Seguimiento del rendimiento académico de tu hijo.',
        },
      };
      const greeting = roleGreetings[user.role] || {
        title: 'Panel de Control',
        subtitle: 'Bienvenido a Fine Flow',
      };
      this.title.set(greeting.title);
      this.subtitle.set(greeting.subtitle);
    }
  }
}
