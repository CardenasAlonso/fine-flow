export interface User {
  id: string;
  email: string;
  role: 'ADMIN' | 'COORDINATOR' | 'TEACHER' | 'STUDENT' | 'GUARDIAN';
  firstName: string;
  lastName: string;
  schoolId?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface LoginRequest {
  email: string;
  password: string;
  schoolId: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  schoolId: string;
  role: string;
  firstName: string;
  lastName: string;
}
