'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  LayoutDashboard,
  Users,
  UserCheck,
  Calendar,
  BarChart3,
  Clock,
  Settings,
  ChevronLeft,
  GraduationCap,
  BookOpen,
  Building2,
  Bell,
  Shield,
  ClipboardList,
  FileText,
  Bot,
  CreditCard,
  FileCheck,
  History,
  type LucideIcon,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/lib/auth-context';
import type { UserRole } from '@/lib/types';

interface MenuItem {
  icon: LucideIcon;
  label: string;
  href: string;
  roles: UserRole[];
  featureFlag?: string;
}

// Complete menu configuration with role-based access and feature flags
const allMenuItems: MenuItem[] = [
  // Dashboard - All roles
  { icon: LayoutDashboard, label: 'Dashboard', href: '/', roles: ['ADMIN', 'COORDINATOR', 'TEACHER', 'STUDENT', 'GUARDIAN'] },
  
  // ADMIN & COORDINATOR only
  { icon: Users, label: 'Alumnos', href: '/alumnos', roles: ['ADMIN', 'COORDINATOR'], featureFlag: 'MOD_ENROLLMENT' },
  { icon: UserCheck, label: 'Profesores', href: '/profesores', roles: ['ADMIN', 'COORDINATOR'] },
  { icon: Building2, label: 'Secciones', href: '/secciones', roles: ['ADMIN', 'COORDINATOR'], featureFlag: 'MOD_SECTIONS' },
  { icon: BookOpen, label: 'Cursos', href: '/cursos', roles: ['ADMIN', 'COORDINATOR'], featureFlag: 'MOD_CURRICULUM_K12' },
  
  // Attendance - Different views per role
  { icon: Calendar, label: 'Asistencia', href: '/asistencia', roles: ['ADMIN', 'COORDINATOR', 'TEACHER'], featureFlag: 'MOD_ATTENDANCE' },
  { icon: Calendar, label: 'Mi Asistencia', href: '/mi-asistencia', roles: ['STUDENT'], featureFlag: 'MOD_STUDENT_PORTAL' },
  { icon: Calendar, label: 'Asistencia Hijo', href: '/asistencia-hijo', roles: ['GUARDIAN'], featureFlag: 'MOD_GUARDIAN_PORTAL' },
  
  // Grades - Different views per role
  { icon: ClipboardList, label: 'Calificaciones', href: '/calificaciones', roles: ['ADMIN', 'COORDINATOR', 'TEACHER'], featureFlag: 'MOD_GRADES_K12' },
  { icon: GraduationCap, label: 'Mis Notas', href: '/mis-notas', roles: ['STUDENT'], featureFlag: 'MOD_STUDENT_PORTAL' },
  { icon: GraduationCap, label: 'Notas Hijo', href: '/notas-hijo', roles: ['GUARDIAN'], featureFlag: 'MOD_GUARDIAN_PORTAL' },
  
  // Schedules - Different views per role
  { icon: Clock, label: 'Horarios', href: '/horarios', roles: ['ADMIN', 'COORDINATOR'], featureFlag: 'MOD_SCHEDULES' },
  { icon: Clock, label: 'Mi Horario', href: '/mi-horario', roles: ['TEACHER', 'STUDENT'] },
  
  // Justifications - Admin, Coordinator, Guardian
  { icon: FileCheck, label: 'Justificaciones', href: '/justificaciones', roles: ['ADMIN', 'COORDINATOR', 'GUARDIAN'], featureFlag: 'FEAT_JUSTIFICATIONS' },
  
  // Reports - ADMIN & COORDINATOR
  { icon: BarChart3, label: 'Reportes', href: '/reportes', roles: ['ADMIN', 'COORDINATOR'], featureFlag: 'MOD_ADVANCED_REPORTS' },
  
  // AI Chat - Role specific
  { icon: Bot, label: 'Asistente IA', href: '/asistente', roles: ['ADMIN', 'COORDINATOR', 'TEACHER'], featureFlag: 'MOD_AI_CHAT_MINEDU' },
  
  // Blockchain - Admin only
  { icon: History, label: 'Blockchain', href: '/blockchain', roles: ['ADMIN'], featureFlag: 'MOD_BLOCKCHAIN' },
  
  // Notifications - All roles
  { icon: Bell, label: 'Notificaciones', href: '/notificaciones', roles: ['ADMIN', 'COORDINATOR', 'TEACHER', 'STUDENT', 'GUARDIAN'], featureFlag: 'MOD_NOTIFICATIONS' },
  
  // Subscription - Admin only
  { icon: CreditCard, label: 'Suscripcion', href: '/suscripcion', roles: ['ADMIN'] },
  
  // Admin only
  { icon: Shield, label: 'Usuarios', href: '/usuarios', roles: ['ADMIN'] },
  { icon: Settings, label: 'Configuracion', href: '/configuracion', roles: ['ADMIN'] },
];

const roleLabels: Record<UserRole, string> = {
  ADMIN: 'Administrador',
  COORDINATOR: 'Coordinador',
  TEACHER: 'Docente',
  STUDENT: 'Alumno',
  GUARDIAN: 'Apoderado',
};

export function Sidebar() {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const pathname = usePathname();
  const { user, school, subscription, hasFeature } = useAuth();

  // Filter menu items based on user role and feature flags
  const menuItems = allMenuItems.filter((item) => {
    // Check role
    if (!user?.role || !item.roles.includes(user.role)) {
      return false;
    }
    // Check feature flag (if specified)
    if (item.featureFlag && !hasFeature(item.featureFlag)) {
      return false;
    }
    return true;
  });

  return (
    <aside
      className={cn(
        'fixed left-0 top-0 z-40 h-screen bg-sidebar text-sidebar-foreground transition-all duration-300 ease-in-out border-r border-sidebar-border flex flex-col',
        isCollapsed ? 'w-20' : 'w-64'
      )}
    >
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b border-sidebar-border h-16">
        {!isCollapsed && (
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-sidebar-primary rounded-lg flex items-center justify-center">
              <span className="text-sm font-bold text-sidebar-primary-foreground">FF</span>
            </div>
            <div className="flex flex-col">
              <span className="font-semibold text-lg leading-none">Fine Flow</span>
              <span className="text-[10px] text-sidebar-foreground/60">v4.0</span>
            </div>
          </div>
        )}
        {isCollapsed && (
          <div className="w-8 h-8 bg-sidebar-primary rounded-lg flex items-center justify-center mx-auto">
            <span className="text-sm font-bold text-sidebar-primary-foreground">FF</span>
          </div>
        )}
        <button
          onClick={() => setIsCollapsed(!isCollapsed)}
          className={cn(
            'p-1.5 hover:bg-sidebar-accent rounded-lg transition-colors',
            isCollapsed && 'absolute right-2'
          )}
        >
          <ChevronLeft
            size={20}
            className={cn('transition-transform', isCollapsed && 'rotate-180')}
          />
        </button>
      </div>

      {/* Role Badge */}
      {!isCollapsed && user && (
        <div className="px-4 py-3 border-b border-sidebar-border">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-sidebar-primary/20 text-sidebar-primary">
            {roleLabels[user.role]}
          </span>
          {subscription?.plan_name && user.role === 'ADMIN' && (
            <span className="ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-medium bg-sidebar-accent/20 text-sidebar-accent-foreground">
              {subscription.plan_name}
            </span>
          )}
        </div>
      )}

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4">
        <ul className="space-y-1 px-2">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href || 
              (item.href !== '/' && pathname.startsWith(item.href));

            return (
              <li key={item.href}>
                <Link
                  href={item.href}
                  className={cn(
                    'flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors',
                    'hover:bg-sidebar-accent/50 hover:text-sidebar-accent-foreground',
                    isActive && 'bg-sidebar-primary text-sidebar-primary-foreground'
                  )}
                  title={isCollapsed ? item.label : undefined}
                >
                  <Icon size={20} className="flex-shrink-0" />
                  {!isCollapsed && <span className="text-sm font-medium">{item.label}</span>}
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* Footer - School Info */}
      {!isCollapsed && school && (
        <div className="p-4 border-t border-sidebar-border">
          <p className="text-xs text-sidebar-foreground/60 mb-1 truncate">{school.name}</p>
          <p className="text-xs text-sidebar-foreground/40">
            {school.institution_type === 'K12' && 'Educacion Basica'}
            {school.institution_type === 'INSTITUTE' && 'Instituto Tecnico'}
            {school.institution_type === 'UNIVERSITY' && 'Universidad'}
          </p>
        </div>
      )}
    </aside>
  );
}
