'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Users, UserCheck, AlertCircle, TrendingUp, Calendar, BookOpen, GraduationCap, Clock, Shield, FileCheck } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/lib/auth-context';
import type { UserRole } from '@/lib/types';
import { demoStudents, demoTeachers, demoAttendances, demoJustifications, demoNotifications, demoCourseAssignments } from '@/lib/demo-data';

interface KPICardProps {
  title: string;
  value: string | number;
  icon: React.ReactNode;
  trend?: string;
  trendType?: 'positive' | 'negative' | 'neutral';
  color?: 'blue' | 'green' | 'orange' | 'red';
}

const KPICard = ({ title, value, icon, trend, trendType = 'neutral', color = 'blue' }: KPICardProps) => {
  const colorClasses = {
    blue: 'bg-blue-50 dark:bg-blue-950/30 text-primary',
    green: 'bg-green-50 dark:bg-green-950/30 text-accent',
    orange: 'bg-orange-50 dark:bg-orange-950/30 text-orange-600 dark:text-orange-400',
    red: 'bg-red-50 dark:bg-red-950/30 text-destructive',
  };

  const trendClasses = {
    positive: 'text-accent',
    negative: 'text-destructive',
    neutral: 'text-muted-foreground',
  };

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <div className={cn('p-2 rounded-lg', colorClasses[color])}>
          {icon}
        </div>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
        {trend && (
          <p className={cn('text-xs mt-1', trendClasses[trendType])}>{trend}</p>
        )}
      </CardContent>
    </Card>
  );
};

// Calculate real metrics from demo data
function calculateMetrics() {
  const totalStudents = demoStudents.filter(s => s.status === 'ACTIVE').length;
  const totalTeachers = demoTeachers.filter(t => t.status === 'ACTIVE').length;
  const teachersOnLeave = demoTeachers.filter(t => t.status === 'ON_LEAVE').length;
  
  // Attendance calculation for today (using most recent date in demo data)
  const latestDate = new Date('2025-03-11');
  const todayAttendances = demoAttendances.filter(a => 
    a.attendance_date.toISOString().split('T')[0] === latestDate.toISOString().split('T')[0]
  );
  const presentToday = todayAttendances.filter(a => a.status === 'PRESENT' || a.status === 'LATE').length;
  const attendanceRate = todayAttendances.length > 0 
    ? ((presentToday / todayAttendances.length) * 100).toFixed(1) 
    : '0';
  
  // Pending justifications
  const pendingJustifications = demoJustifications.filter(j => j.status === 'PENDING').length;
  
  // Unread notifications
  const unreadNotifications = demoNotifications.filter(n => !n.is_read).length;

  return {
    totalStudents,
    totalTeachers,
    teachersOnLeave,
    attendanceRate,
    presentToday,
    totalTodayAttendance: todayAttendances.length,
    pendingJustifications,
    unreadNotifications,
  };
}

interface KPIConfig {
  title: string;
  value: string;
  icon: React.ReactNode;
  trend: string;
  trendType: 'positive' | 'negative' | 'neutral';
  color: 'blue' | 'green' | 'orange' | 'red';
  roles: UserRole[];
}

export function KPICards() {
  const { user } = useAuth();
  const metrics = calculateMetrics();

  const allKPIs: KPIConfig[] = [
    // Admin & Coordinator KPIs
    {
      title: 'Total de Alumnos',
      value: metrics.totalStudents.toString(),
      icon: <Users className="h-4 w-4" />,
      trend: `${metrics.totalStudents} activos`,
      trendType: 'positive',
      color: 'blue',
      roles: ['ADMIN', 'COORDINATOR'],
    },
    {
      title: 'Asistencia Hoy',
      value: `${metrics.attendanceRate}%`,
      icon: <TrendingUp className="h-4 w-4" />,
      trend: `${metrics.presentToday} presentes de ${metrics.totalTodayAttendance}`,
      trendType: parseFloat(metrics.attendanceRate) >= 90 ? 'positive' : 'negative',
      color: 'green',
      roles: ['ADMIN', 'COORDINATOR'],
    },
    {
      title: 'Profesores Activos',
      value: metrics.totalTeachers.toString(),
      icon: <UserCheck className="h-4 w-4" />,
      trend: `${metrics.teachersOnLeave} con licencia`,
      trendType: 'neutral',
      color: 'blue',
      roles: ['ADMIN', 'COORDINATOR'],
    },
    {
      title: 'Justificaciones Pendientes',
      value: metrics.pendingJustifications.toString(),
      icon: <FileCheck className="h-4 w-4" />,
      trend: 'Requieren revision',
      trendType: metrics.pendingJustifications > 0 ? 'negative' : 'positive',
      color: 'orange',
      roles: ['ADMIN', 'COORDINATOR'],
    },
    // Teacher KPIs
    {
      title: 'Mis Secciones',
      value: demoCourseAssignments.length.toString(),
      icon: <BookOpen className="h-4 w-4" />,
      trend: '3° A, B | 4° A',
      trendType: 'neutral',
      color: 'blue',
      roles: ['TEACHER'],
    },
    {
      title: 'Clases Hoy',
      value: '6',
      icon: <Clock className="h-4 w-4" />,
      trend: '2 completadas',
      trendType: 'neutral',
      color: 'blue',
      roles: ['TEACHER'],
    },
    {
      title: 'Asistencia Mis Clases',
      value: `${metrics.attendanceRate}%`,
      icon: <UserCheck className="h-4 w-4" />,
      trend: '+1.2% vs semana pasada',
      trendType: 'positive',
      color: 'green',
      roles: ['TEACHER'],
    },
    {
      title: 'Notas Pendientes',
      value: '12',
      icon: <GraduationCap className="h-4 w-4" />,
      trend: 'Por registrar',
      trendType: 'negative',
      color: 'orange',
      roles: ['TEACHER'],
    },
    // Student KPIs
    {
      title: 'Mi Promedio General',
      value: '16.8',
      icon: <TrendingUp className="h-4 w-4" />,
      trend: 'Nivel: A (Logrado)',
      trendType: 'positive',
      color: 'green',
      roles: ['STUDENT'],
    },
    {
      title: 'Mi Asistencia',
      value: '98%',
      icon: <Calendar className="h-4 w-4" />,
      trend: '2 tardanzas este mes',
      trendType: 'positive',
      color: 'green',
      roles: ['STUDENT'],
    },
    {
      title: 'Tareas Pendientes',
      value: '3',
      icon: <BookOpen className="h-4 w-4" />,
      trend: 'Vencen esta semana',
      trendType: 'neutral',
      color: 'orange',
      roles: ['STUDENT'],
    },
    {
      title: 'Proximos Examenes',
      value: '2',
      icon: <GraduationCap className="h-4 w-4" />,
      trend: 'Matematica, Comunicacion',
      trendType: 'neutral',
      color: 'blue',
      roles: ['STUDENT'],
    },
    // Guardian KPIs
    {
      title: 'Promedio del Alumno',
      value: '16.8',
      icon: <TrendingUp className="h-4 w-4" />,
      trend: 'Nivel: A (Logrado)',
      trendType: 'positive',
      color: 'green',
      roles: ['GUARDIAN'],
    },
    {
      title: 'Asistencia del Alumno',
      value: '98%',
      icon: <Calendar className="h-4 w-4" />,
      trend: '2 tardanzas este mes',
      trendType: 'positive',
      color: 'green',
      roles: ['GUARDIAN'],
    },
    {
      title: 'Justificaciones',
      value: metrics.pendingJustifications > 0 ? `${metrics.pendingJustifications} pendiente(s)` : 'Al dia',
      icon: <FileCheck className="h-4 w-4" />,
      trend: metrics.pendingJustifications > 0 ? 'Requiere atencion' : 'Sin pendientes',
      trendType: metrics.pendingJustifications > 0 ? 'negative' : 'positive',
      color: metrics.pendingJustifications > 0 ? 'orange' : 'green',
      roles: ['GUARDIAN'],
    },
    {
      title: 'Proxima Reunion',
      value: '15 Abr',
      icon: <Users className="h-4 w-4" />,
      trend: 'Reunion de padres',
      trendType: 'neutral',
      color: 'blue',
      roles: ['GUARDIAN'],
    },
  ];

  const visibleKPIs = allKPIs.filter((kpi) => 
    user?.role && kpi.roles.includes(user.role)
  );

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      {visibleKPIs.map((kpi) => (
        <KPICard
          key={kpi.title}
          title={kpi.title}
          value={kpi.value}
          icon={kpi.icon}
          trend={kpi.trend}
          trendType={kpi.trendType}
          color={kpi.color}
        />
      ))}
    </div>
  );
}
