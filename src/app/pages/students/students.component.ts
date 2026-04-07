import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatChipsModule } from '@angular/material/chips';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatPaginatorModule,
    MatDialogModule,
    MatChipsModule,
  ],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Alumnos</h1>
          <p>Gestión de estudiantes matriculados</p>
        </div>
        <button mat-raised-button color="primary"><mat-icon>add</mat-icon> Nuevo Alumno</button>
      </div>

      <mat-card>
        <mat-card-content>
          <div class="filters">
            <mat-form-field appearance="outline">
              <mat-label>Buscar</mat-label>
              <input
                matInput
                [(ngModel)]="searchQuery"
                (keyup)="filterStudents()"
                placeholder="Nombre o DNI"
              />
              <mat-icon matSuffix>search</mat-icon>
            </mat-form-field>
          </div>

          <table mat-table [dataSource]="filteredStudents()">
            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Nombre Completo</th>
              <td mat-cell *matCellDef="let s">{{ s.firstName }} {{ s.lastName }}</td>
            </ng-container>
            <ng-container matColumnDef="document">
              <th mat-header-cell *matHeaderCellDef>Documento</th>
              <td mat-cell *matCellDef="let s">{{ s.documentNumber }}</td>
            </ng-container>
            <ng-container matColumnDef="status">
              <th mat-header-cell *matHeaderCellDef>Estado</th>
              <td mat-cell *matCellDef="let s">
                <mat-chip [color]="s.status === 'ACTIVE' ? 'primary' : 'warn'" mode="flat">
                  {{ s.status === 'ACTIVE' ? 'Activo' : 'Inactivo' }}
                </mat-chip>
              </td>
            </ng-container>
            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Acciones</th>
              <td mat-cell *matCellDef="let s">
                <button mat-icon-button color="primary"><mat-icon>edit</mat-icon></button>
                <button mat-icon-button color="warn"><mat-icon>delete</mat-icon></button>
              </td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
          </table>
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
        margin: 0;
      }
      .page-header p {
        color: #64748b;
        margin: 0.25rem 0 0;
      }
      .filters {
        display: flex;
        gap: 1rem;
        margin-bottom: 1rem;
      }
      mat-form-field {
        width: 300px;
      }
      table {
        width: 100%;
      }
    `,
  ],
})
export class StudentsComponent implements OnInit {
  students = signal<any[]>([]);
  filteredStudents = signal<any[]>([]);
  searchQuery = '';
  displayedColumns = ['name', 'document', 'status', 'actions'];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadStudents();
  }

  loadStudents() {
    this.api.getStudents().subscribe({
      next: (data) => {
        this.students.set(data);
        this.filteredStudents.set(data);
      },
      error: () => this.filteredStudents.set([]),
    });
  }

  filterStudents() {
    const q = this.searchQuery.toLowerCase();
    this.filteredStudents.set(
      this.students().filter(
        (s) =>
          s.firstName?.toLowerCase().includes(q) ||
          s.lastName?.toLowerCase().includes(q) ||
          s.documentNumber?.includes(q),
      ),
    );
  }
}
