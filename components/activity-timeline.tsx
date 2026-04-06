'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  FileText,
  Users,
  CheckCircle2,
  AlertCircle,
  UserPlus,
  Clock,
  GraduationCap,
  Calendar,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/lib/auth-context';
import type { UserRole } from '@/lib/types';

interface Activity {
  id: string;
  type: 'grade' | 'enrollment' | 'attendance' | 'alert' | 'document' | 'schedule' | 'task';
  title: string;
  description: string;
  timestamp: string;
  icon: React.ReactNode;
  roles: UserRole[];
}

const allActivities: Activity[] = [
  // Admin/Coordinator activities
  {
    id: '1',
    type: 'grade',
    title: 'Notas finales registradas',
    description: 'Matemáticas 4to B - Prof. Juan Pérez',
    timestamp: 'Hace 15 min',
    icon: <CheckCircle2 className="h-4 w-4" />,
    roles: ['ADMIN', 'COORDINATOR'],
  },
  {
    id: '2',
    type: 'enrollment',
    title: 'Nuevo alumno matriculado',
    description: 'Carlos Mendoza García - 3ro A',
    timestamp: 'Hace 1 hora',
    icon: <UserPlus className="h-4 w-4" />,
    roles: ['ADMIN', 'COORDINATOR'],
  },
  {
    id: '3',
    type: 'attendance',
    title: 'Reporte de asistencia',
    description: 'Asistencia diaria completada',
    timestamp: 'Hace 2 horas',
    icon: <Users className="h-4 w-4" />,
    roles: ['ADMIN', 'COORDINATOR'],
  },
  {
    id: '4',
    type: 'alert',
    title: 'Justificación pendiente',
    description: 'Diego Flores - Falta del 28/03',
    timestamp: 'Hace 3 horas',
    icon: <AlertCircle className="h-4 w-4" />,
    roles: ['ADMIN', 'COORDINATOR'],
  },
  {
    id: '5',
    type: 'document',
    title: 'Reporte exportado',
    description: 'Calificaciones 2do trimestre',
    timestamp: 'Hace 5 horas',
    icon: <FileText className="h-4 w-4" />,
    roles: ['ADMIN', 'COORDINATOR'],
  },
  // Teacher activities
  {
    id: '6',
    type: 'grade',
    title: 'Notas registradas',
    description: 'Examen de Matemáticas 3ro A',
    timestamp: 'Hace 30 min',
    icon: <CheckCircle2 className="h-4 w-4" />,
    roles: ['TEACHER'],
  },
  {
    id: '7',
    type: 'attendance',
    title: 'Asistencia tomada',
    description: '3ro B - 28 presentes, 2 ausentes',
    timestamp: 'Hace 1 hora',
    icon: <Users className="h-4 w-4" />,
    roles: ['TEACHER'],
  },
  {
    id: '8',
    type: 'task',
    title: 'Tarea creada',
    description: 'Ejercicios Cap. 5 - Vence 02/04',
    timestamp: 'Hace 2 horas',
    icon: <FileText className="h-4 w-4" />,
    roles: ['TEACHER'],
  },
  {
    id: '9',
    type: 'schedule',
    title: 'Próxima clase',
    description: '4to A - Aula 401 - 11:00',
    timestamp: 'En 45 min',
    icon: <Clock className="h-4 w-4" />,
    roles: ['TEACHER'],
  },
  {
    id: '10',
    type: 'alert',
    title: 'Notas pendientes',
    description: '12 evaluaciones por calificar',
    timestamp: 'Recordatorio',
    icon: <AlertCircle className="h-4 w-4" />,
    roles: ['TEACHER'],
  },
  // Student activities
  {
    id: '11',
    type: 'grade',
    title: 'Nueva nota publicada',
    description: 'Matemáticas - Examen: 17/20',
    timestamp: 'Hace 1 hora',
    icon: <GraduationCap className="h-4 w-4" />,
    roles: ['STUDENT'],
  },
  {
    id: '12',
    type: 'task',
    title: 'Tarea asignada',
    description: 'Comunicación - Ensayo - Vence 05/04',
    timestamp: 'Hace 2 horas',
    icon: <FileText className="h-4 w-4" />,
    roles: ['STUDENT'],
  },
  {
    id: '13',
    type: 'schedule',
    title: 'Próxima clase',
    description: 'Ciencias - Lab. 1 - 10:00',
    timestamp: 'En 30 min',
    icon: <Clock className="h-4 w-4" />,
    roles: ['STUDENT'],
  },
  {
    id: '14',
    type: 'attendance',
    title: 'Asistencia registrada',
    description: 'Presente - Matemáticas 08:00',
    timestamp: 'Hoy',
    icon: <Calendar className="h-4 w-4" />,
    roles: ['STUDENT'],
  },
  {
    id: '15',
    type: 'alert',
    title: 'Recordatorio',
    description: 'Examen de Historia el 03/04',
    timestamp: 'En 5 días',
    icon: <AlertCircle className="h-4 w-4" />,
    roles: ['STUDENT'],
  },
  // Guardian activities
  {
    id: '16',
    type: 'grade',
    title: 'Nueva nota de su hijo',
    description: 'Diego - Matemáticas: 17/20',
    timestamp: 'Hace 1 hora',
    icon: <GraduationCap className="h-4 w-4" />,
    roles: ['GUARDIAN'],
  },
  {
    id: '17',
    type: 'attendance',
    title: 'Asistencia de hoy',
    description: 'Diego - Presente a las 07:45',
    timestamp: 'Hoy',
    icon: <Calendar className="h-4 w-4" />,
    roles: ['GUARDIAN'],
  },
  {
    id: '18',
    type: 'task',
    title: 'Tarea pendiente',
    description: 'Ensayo de Comunicación - Vence 05/04',
    timestamp: 'Recordatorio',
    icon: <FileText className="h-4 w-4" />,
    roles: ['GUARDIAN'],
  },
  {
    id: '19',
    type: 'alert',
    title: 'Reunión de padres',
    description: 'Viernes 15/04 - 18:00',
    timestamp: 'Próximo evento',
    icon: <Users className="h-4 w-4" />,
    roles: ['GUARDIAN'],
  },
  {
    id: '20',
    type: 'document',
    title: 'Libreta disponible',
    description: 'Calificaciones 1er Bimestre',
    timestamp: 'Nuevo',
    icon: <FileText className="h-4 w-4" />,
    roles: ['GUARDIAN'],
  },
];

const typeColors = {
  grade: 'text-blue-600 dark:text-blue-400 bg-blue-100 dark:bg-blue-950',
  enrollment: 'text-green-600 dark:text-green-400 bg-green-100 dark:bg-green-950',
  attendance: 'text-purple-600 dark:text-purple-400 bg-purple-100 dark:bg-purple-950',
  alert: 'text-orange-600 dark:text-orange-400 bg-orange-100 dark:bg-orange-950',
  document: 'text-slate-600 dark:text-slate-400 bg-slate-100 dark:bg-slate-800',
  schedule: 'text-cyan-600 dark:text-cyan-400 bg-cyan-100 dark:bg-cyan-950',
  task: 'text-pink-600 dark:text-pink-400 bg-pink-100 dark:bg-pink-950',
};

export function ActivityTimeline() {
  const { user } = useAuth();

  const visibleActivities = allActivities.filter((activity) => 
    user?.role && activity.roles.includes(user.role)
  );

  const title = user?.role === 'TEACHER' 
    ? 'Mi Actividad Reciente' 
    : user?.role === 'STUDENT' 
      ? 'Mis Notificaciones' 
      : user?.role === 'GUARDIAN' 
        ? 'Actividad del Alumno' 
        : 'Actividad Reciente';

  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {visibleActivities.map((activity, index) => (
            <div key={activity.id} className="flex gap-3">
              <div className="flex flex-col items-center">
                <div className={cn('p-2 rounded-full', typeColors[activity.type])}>
                  {activity.icon}
                </div>
                {index < visibleActivities.length - 1 && (
                  <div className="w-0.5 flex-1 bg-border my-2 min-h-[2rem]" />
                )}
              </div>
              <div className="flex-1 pt-0.5 min-w-0">
                <div className="flex items-start justify-between gap-2">
                  <p className="font-medium text-sm truncate">{activity.title}</p>
                  <span className="text-xs text-muted-foreground whitespace-nowrap">{activity.timestamp}</span>
                </div>
                <p className="text-sm text-muted-foreground mt-0.5 truncate">{activity.description}</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
