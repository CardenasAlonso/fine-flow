import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {}

  private getHeaders() {
    const token = this.authService.getToken();
    return {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    };
  }

  // Profile Service - Students
  getStudents(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/profile/students`, { headers: this.getHeaders() });
  }

  getStudent(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/profile/students/${id}`, {
      headers: this.getHeaders(),
    });
  }

  createStudent(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/profile/students`, data, {
      headers: this.getHeaders(),
    });
  }

  updateStudent(id: string, data: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/profile/students/${id}`, data, {
      headers: this.getHeaders(),
    });
  }

  deleteStudent(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/profile/students/${id}`, {
      headers: this.getHeaders(),
    });
  }

  searchStudents(q: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/profile/students/search?q=${q}`, {
      headers: this.getHeaders(),
    });
  }

  // Profile Service - Teachers
  getTeachers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/profile/teachers`, { headers: this.getHeaders() });
  }

  getTeacher(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/profile/teachers/${id}`, {
      headers: this.getHeaders(),
    });
  }

  createTeacher(data: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/profile/teachers`, data, {
      headers: this.getHeaders(),
    });
  }

  // Profile Service - Guardians
  getGuardians(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/profile/guardians`, {
      headers: this.getHeaders(),
    });
  }

  getGuardiansByStudent(studentId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/profile/guardians/student/${studentId}`, {
      headers: this.getHeaders(),
    });
  }

  // Academic Service
  getAcademicLevels(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/academic/levels`, { headers: this.getHeaders() });
  }

  getSchoolYears(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/academic/school-years`, {
      headers: this.getHeaders(),
    });
  }

  getSections(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/academic/sections`, {
      headers: this.getHeaders(),
    });
  }

  getCourses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/academic/courses`, { headers: this.getHeaders() });
  }

  getAcademicPeriods(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/academic/periods`, { headers: this.getHeaders() });
  }

  // Evaluation Service
  getScores(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/evaluation/scores`, {
      headers: this.getHeaders(),
    });
  }

  getAttendances(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/evaluation/attendances`, {
      headers: this.getHeaders(),
    });
  }

  // Support Service
  getNotifications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/support/notifications`, {
      headers: this.getHeaders(),
    });
  }
}
