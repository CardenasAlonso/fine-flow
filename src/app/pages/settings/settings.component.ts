import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSlideToggleModule,
    FormsModule,
  ],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Configuración</h1>
        <p>Personaliza tu experiencia</p>
      </div>
      <div class="settings-grid">
        <mat-card>
          <mat-card-header><mat-card-title>Apariencia</mat-card-title></mat-card-header>
          <mat-card-content>
            <div class="setting-item">
              <span>Modo Oscuro</span><mat-slide-toggle [(ngModel)]="darkMode"></mat-slide-toggle>
            </div>
          </mat-card-content>
        </mat-card>
        <mat-card>
          <mat-card-header><mat-card-title>Notificaciones</mat-card-title></mat-card-header>
          <mat-card-content>
            <div class="setting-item">
              <span>Correo electrónico</span
              ><mat-slide-toggle [(ngModel)]="emailNotifs"></mat-slide-toggle>
            </div>
            <div class="setting-item">
              <span>Push notifications</span
              ><mat-slide-toggle [(ngModel)]="pushNotifs"></mat-slide-toggle>
            </div>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [
    `
      .page-header {
        margin-bottom: 1.5rem;
      }
      .page-header h1 {
        font-size: 1.75rem;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
      }
      .page-header p {
        color: #64748b;
        margin: 0.25rem 0 0;
      }
      .settings-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 1.5rem;
      }
      .setting-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 0;
        border-bottom: 1px solid #e2e8f0;
      }
      .setting-item:last-child {
        border: none;
      }
    `,
  ],
})
export class SettingsComponent {
  darkMode = false;
  emailNotifs = true;
  pushNotifs = true;
}
