import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-teachers',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Profesores</h1>
          <p>Gestión del personal docente</p>
        </div>
        <button mat-raised-button color="primary"><mat-icon>add</mat-icon> Nuevo Profesor</button>
      </div>
      <mat-card>
        <mat-card-content>
          <p class="empty-state"><mat-icon>groups</mat-icon> Lista de profesores en desarrollo</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
      }
      .page-header h1 {
        font-size: 1.75rem;
        font-weight: 600;
        color: #1e293b;
      }
      .empty-state {
        text-align: center;
        padding: 3rem;
        color: #64748b;
      }
      .empty-state mat-icon {
        font-size: 3rem;
        width: 3rem;
        height: 3rem;
      }
    `,
  ],
})
export class TeachersComponent {}
