import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatTableModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Cursos</h1>
          <p>Gestión de áreas curriculares</p>
        </div>
        <button mat-raised-button color="primary"><mat-icon>add</mat-icon> Nuevo Curso</button>
      </div>
      <mat-card>
        <mat-card-content>
          <div class="course-grid">
            @for (course of courses(); track course.id) {
              <div class="course-card" [style.border-left-color]="course.colorHex || '#6366f1'">
                <h3>{{ course.name }}</h3>
                <p>{{ course.code }}</p>
                <span class="badge">{{ course.isActive === 1 ? 'Activo' : 'Inactivo' }}</span>
              </div>
            }
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [
    `
      .page-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 1.5rem;
      }
      .page-header h1 {
        font-size: 1.75rem;
        font-weight: 600;
        color: #1e293b;
      }
      .course-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 1rem;
      }
      .course-card {
        padding: 1.25rem;
        border-radius: 0.5rem;
        background: #f8fafc;
        border-left: 4px solid #6366f1;
      }
      .course-card h3 {
        margin: 0 0 0.5rem;
        color: #1e293b;
      }
      .course-card p {
        color: #64748b;
        font-size: 0.875rem;
        margin: 0;
      }
      .badge {
        display: inline-block;
        margin-top: 0.75rem;
        padding: 0.25rem 0.75rem;
        background: #dcfce7;
        color: #16a34a;
        border-radius: 1rem;
        font-size: 0.75rem;
      }
    `,
  ],
})
export class CoursesComponent implements OnInit {
  courses = signal<any[]>([]);
  constructor(private api: ApiService) {}
  ngOnInit() {
    this.api.getCourses().subscribe(
      (d) => this.courses.set(d),
      () => this.courses.set([]),
    );
  }
}
