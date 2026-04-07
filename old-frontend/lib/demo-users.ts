import type { User, UserRole } from './types';

// Export demo users array for list display
export const demoUsers: User[] = [
  {
    id: 'user-admin-20188',
    school_id: 'school-20188-canete',
    email: 'admin@ie20188.edu.pe',
    first_name: 'Administrador',
    last_name: 'Principal',
    role: 'ADMIN',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-01'),
    updated_at: new Date(),
  },
  {
    id: 'user-coord-20188',
    school_id: 'school-20188-canete',
    email: 'coordinador@ie20188.edu.pe',
    first_name: 'Maria',
    last_name: 'Garcia',
    role: 'COORDINATOR',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  {
    id: 'user-teacher-20188',
    school_id: 'school-20188-canete',
    email: 'jperez@ie20188.edu.pe',
    first_name: 'Juan',
    last_name: 'Perez',
    role: 'TEACHER',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  {
    id: 'user-student-20188',
    school_id: 'school-20188-canete',
    email: 'diego.vargas@ie20188.edu.pe',
    first_name: 'Diego',
    last_name: 'Vargas',
    role: 'STUDENT',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-03-01'),
    updated_at: new Date(),
  },
  {
    id: 'user-guardian-20188',
    school_id: 'school-20188-canete',
    email: 'roberto.vargas@gmail.com',
    first_name: 'Roberto',
    last_name: 'Vargas',
    role: 'GUARDIAN',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-03-01'),
    updated_at: new Date(),
  },
];

// Export demo users by role for quick access
export const demoUsersByRole: Record<UserRole, User> = {
  ADMIN: demoUsers[0],
  COORDINATOR: demoUsers[1],
  TEACHER: demoUsers[2],
  STUDENT: demoUsers[3],
  GUARDIAN: demoUsers[4],
};

// Role display names in Spanish
export const roleLabels: Record<UserRole, string> = {
  ADMIN: 'Administrador',
  COORDINATOR: 'Coordinador',
  TEACHER: 'Docente',
  STUDENT: 'Estudiante',
  GUARDIAN: 'Apoderado',
};

// Role descriptions
export const roleDescriptions: Record<UserRole, string> = {
  ADMIN: 'Acceso completo al sistema, gestion de usuarios y configuracion',
  COORDINATOR: 'Gestion academica, supervision de docentes y reportes',
  TEACHER: 'Registro de notas, asistencia y comunicacion con estudiantes',
  STUDENT: 'Visualizacion de notas, asistencia y horarios',
  GUARDIAN: 'Seguimiento del rendimiento academico de sus hijos',
};
