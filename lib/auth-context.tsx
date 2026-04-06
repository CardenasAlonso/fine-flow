'use client';

import { createContext, useContext, useState, ReactNode } from 'react';
import type { 
  User, 
  UserRole, 
  School, 
  SchoolSubscription, 
  FeatureFlag,
  AcademicPeriod,
  SchoolYear
} from './types';

interface AuthContextType {
  user: User | null;
  school: School | null;
  subscription: SchoolSubscription | null;
  featureFlags: FeatureFlag[];
  currentPeriod: AcademicPeriod | null;
  currentSchoolYear: SchoolYear | null;
  setUser: (user: User | null) => void;
  setRole: (role: UserRole) => void;
  login: (user: User) => void;
  logout: () => void;
  isAuthenticated: boolean;
  hasFeature: (featureName: string) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Demo users for each role - matching v4.0 schema
const demoUsers: Record<UserRole, User> = {
  ADMIN: {
    id: 'user-admin-20188',
    school_id: 'school-20188-canete',
    email: 'admin@ie20188.edu.pe',
    role: 'ADMIN',
    first_name: 'Administrador',
    last_name: 'Principal',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-01'),
    updated_at: new Date(),
  },
  COORDINATOR: {
    id: 'user-coord-20188',
    school_id: 'school-20188-canete',
    email: 'coordinador@ie20188.edu.pe',
    role: 'COORDINATOR',
    first_name: 'Maria',
    last_name: 'Garcia',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  TEACHER: {
    id: 'user-teacher-20188',
    school_id: 'school-20188-canete',
    email: 'jperez@ie20188.edu.pe',
    role: 'TEACHER',
    first_name: 'Juan',
    last_name: 'Perez',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-01-15'),
    updated_at: new Date(),
  },
  STUDENT: {
    id: 'user-student-20188',
    school_id: 'school-20188-canete',
    email: 'diego.vargas@ie20188.edu.pe',
    role: 'STUDENT',
    first_name: 'Diego',
    last_name: 'Vargas',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-03-01'),
    updated_at: new Date(),
  },
  GUARDIAN: {
    id: 'user-guardian-20188',
    school_id: 'school-20188-canete',
    email: 'roberto.vargas@gmail.com',
    role: 'GUARDIAN',
    first_name: 'Roberto',
    last_name: 'Vargas',
    status: 'ACTIVE',
    avatar_url: '/placeholder.svg?height=40&width=40',
    last_login_at: new Date(),
    created_at: new Date('2025-03-01'),
    updated_at: new Date(),
  },
};

// Demo school - matching v4.0 schema with institution_type
const demoSchool: School = {
  id: 'school-20188-canete',
  name: 'I.E. Centro de Varones N°20188',
  document_number: '20543210001',
  institution_type: 'K12',
  address: 'Calle Bolivar 150, Canete, Lima',
  phone: '01-5840123',
  email: 'ie20188.canete@gmail.com',
  logo_url: '/placeholder.svg?height=64&width=64',
  status: 'ACTIVE',
  created_at: new Date('2025-01-01'),
  updated_at: new Date(),
};

// Demo subscription
const demoSubscription: SchoolSubscription = {
  id: 'sub-demo-20188',
  school_id: 'school-20188-canete',
  plan_name: 'PREMIUM',
  max_students: 500,
  max_teachers: 50,
  max_storage_gb: 10.00,
  features_json: JSON.stringify({
    blockchain: true,
    ai_chat: true,
    reports: true,
    qr_attendance: true,
    bulk_import: true
  }),
  billing_cycle: 'MONTHLY',
  starts_at: new Date('2025-01-01'),
  is_trial: true,
  status: 'ACTIVE',
  created_at: new Date('2025-01-01'),
  updated_at: new Date(),
};

// Demo feature flags (based on FEATURE_CATALOG for K12 + PREMIUM plan)
const demoFeatureFlags: FeatureFlag[] = [
  { id: 'ff-01', school_id: 'school-20188-canete', feature_name: 'MOD_SCHEDULES', enabled: true, description: 'Gestion de Horarios', plan_required: 'BASIC', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-02', school_id: 'school-20188-canete', feature_name: 'MOD_SECTIONS', enabled: true, description: 'Gestion de Secciones', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-03', school_id: 'school-20188-canete', feature_name: 'MOD_CURRICULUM_K12', enabled: true, description: 'Malla Curricular CNEB/MINEDU', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-04', school_id: 'school-20188-canete', feature_name: 'MOD_ATTENDANCE', enabled: true, description: 'Control de Asistencia', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-05', school_id: 'school-20188-canete', feature_name: 'FEAT_QR_ATTENDANCE', enabled: true, description: 'Asistencia por Codigo QR', plan_required: 'STANDARD', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-06', school_id: 'school-20188-canete', feature_name: 'FEAT_JUSTIFICATIONS', enabled: true, description: 'Justificaciones de Inasistencia', plan_required: 'BASIC', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-07', school_id: 'school-20188-canete', feature_name: 'MOD_GRADES_K12', enabled: true, description: 'Calificaciones Escala Vigesimal MINEDU', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-08', school_id: 'school-20188-canete', feature_name: 'MOD_BLOCKCHAIN_GRADES', enabled: true, description: 'Blockchain de Calificaciones', plan_required: 'PREMIUM', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-09', school_id: 'school-20188-canete', feature_name: 'FEAT_GRADE_REPORTS', enabled: true, description: 'Generacion de Boletas de Notas', plan_required: 'BASIC', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-10', school_id: 'school-20188-canete', feature_name: 'FEAT_CERTIFICATES', enabled: true, description: 'Certificados de Estudios', plan_required: 'STANDARD', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-11', school_id: 'school-20188-canete', feature_name: 'MOD_ENROLLMENT', enabled: true, description: 'Gestion de Matriculas', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-12', school_id: 'school-20188-canete', feature_name: 'MOD_GUARDIAN_PORTAL', enabled: true, description: 'Portal de Apoderados', plan_required: 'BASIC', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-13', school_id: 'school-20188-canete', feature_name: 'MOD_STUDENT_PORTAL', enabled: true, description: 'Portal del Estudiante', plan_required: 'BASIC', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-14', school_id: 'school-20188-canete', feature_name: 'MOD_NOTIFICATIONS', enabled: true, description: 'Centro de Notificaciones', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-15', school_id: 'school-20188-canete', feature_name: 'MOD_AI_CHAT_MINEDU', enabled: true, description: 'Asistente IA Normativa MINEDU', plan_required: 'PREMIUM', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-16', school_id: 'school-20188-canete', feature_name: 'FEAT_ANNOUNCEMENTS', enabled: true, description: 'Comunicados y Circulares', plan_required: 'BASIC', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-17', school_id: 'school-20188-canete', feature_name: 'MOD_BLOCKCHAIN', enabled: true, description: 'Registro Blockchain de Eventos', plan_required: 'PREMIUM', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-18', school_id: 'school-20188-canete', feature_name: 'MOD_ADVANCED_REPORTS', enabled: true, description: 'Reportes Avanzados PDF/Excel', plan_required: 'STANDARD', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
  { id: 'ff-19', school_id: 'school-20188-canete', feature_name: 'MOD_SECURITY_AUDIT', enabled: true, description: 'Auditoria de Seguridad', plan_required: 'FREE', rollout_pct: 100, created_at: new Date(), updated_at: new Date() },
];

// Demo academic period
const demoCurrentPeriod: AcademicPeriod = {
  id: 'ap-b1-3sec-2025',
  school_id: 'school-20188-canete',
  school_year_id: 'sy-3sec-2025',
  name: '1° Bimestre',
  period_type: 'BIMESTER',
  start_date: new Date('2025-03-10'),
  end_date: new Date('2025-05-09'),
  is_active: true,
  created_at: new Date('2025-01-01'),
};

// Demo school year
const demoCurrentSchoolYear: SchoolYear = {
  id: 'sy-3sec-2025',
  school_id: 'school-20188-canete',
  academic_level_id: 'level-sec-20188',
  name: '3° Secundaria 2025',
  grade_number: 3,
  calendar_year: 2025,
  is_active: true,
};

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(demoUsers.ADMIN);
  const [school] = useState<School | null>(demoSchool);
  const [subscription] = useState<SchoolSubscription | null>(demoSubscription);
  const [featureFlags] = useState<FeatureFlag[]>(demoFeatureFlags);
  const [currentPeriod] = useState<AcademicPeriod | null>(demoCurrentPeriod);
  const [currentSchoolYear] = useState<SchoolYear | null>(demoCurrentSchoolYear);

  const setRole = (role: UserRole) => {
    setUser(demoUsers[role]);
  };

  const login = (newUser: User) => {
    setUser(newUser);
  };

  const logout = () => {
    setUser(null);
  };

  const hasFeature = (featureName: string): boolean => {
    const flag = featureFlags.find(f => f.feature_name === featureName);
    return flag?.enabled ?? false;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        school,
        subscription,
        featureFlags,
        currentPeriod,
        currentSchoolYear,
        setUser,
        setRole,
        login,
        logout,
        isAuthenticated: !!user,
        hasFeature,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
