import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `<div class="page">
    <div class="page-header">
      <div>
        <h1>Horarios</h1>
        <p>Gestión de horarios de clase</p>
      </div>
      <button mat-raised-button color="primary"><mat-icon>add</mat-icon> Nuevo Horario</button>
    </div>
    <mat-card
      ><mat-card-content
        ><p class="empty"><mat-icon>schedule</mat-icon>Horarios en desarrollo</p></mat-card-content
      ></mat-card
    >
  </div>`,
  styles: [
    `
      .page-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 1.5rem;
      }
      h1 {
        font-size: 1.75rem;
        font-weight: 600;
        color: #1e293b;
      }
      .empty {
        text-align: center;
        padding: 3rem;
        color: #64748b;
      }
      .empty mat-icon {
        font-size: 3rem;
      }
    `,
  ],
})
export class ScheduleComponent {}
