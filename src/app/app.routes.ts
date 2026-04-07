import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    loadComponent: () => import('./pages/layout/layout.component').then((m) => m.LayoutComponent),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'alumnos',
        loadComponent: () =>
          import('./pages/students/students.component').then((m) => m.StudentsComponent),
      },
      {
        path: 'profesores',
        loadComponent: () =>
          import('./pages/teachers/teachers.component').then((m) => m.TeachersComponent),
      },
      {
        path: 'cursos',
        loadComponent: () =>
          import('./pages/courses/courses.component').then((m) => m.CoursesComponent),
      },
      {
        path: 'secciones',
        loadComponent: () =>
          import('./pages/sections/sections.component').then((m) => m.SectionsComponent),
      },
      {
        path: 'calificaciones',
        loadComponent: () =>
          import('./pages/grades/grades.component').then((m) => m.GradesComponent),
      },
      {
        path: 'asistencia',
        loadComponent: () =>
          import('./pages/attendance/attendance.component').then((m) => m.AttendanceComponent),
      },
      {
        path: 'horarios',
        loadComponent: () =>
          import('./pages/schedule/schedule.component').then((m) => m.ScheduleComponent),
      },
      {
        path: 'reportes',
        loadComponent: () =>
          import('./pages/reports/reports.component').then((m) => m.ReportsComponent),
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./pages/users/users.component').then((m) => m.UsersComponent),
      },
      {
        path: 'configuracion',
        loadComponent: () =>
          import('./pages/settings/settings.component').then((m) => m.SettingsComponent),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
