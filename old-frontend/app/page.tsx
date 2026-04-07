'use client';

import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { KPICards } from '@/components/kpi-cards';
import { ScheduleTable } from '@/components/schedule-table';
import { ActivityTimeline } from '@/components/activity-timeline';
import { useAuth } from '@/lib/auth-context';

const roleGreetings: Record<string, { title: string; subtitle: string }> = {
  ADMIN: {
    title: 'Panel de Administración',
    subtitle: 'Gestiona tu institución educativa de forma integral.',
  },
  COORDINATOR: {
    title: 'Panel de Coordinación',
    subtitle: 'Supervisa el rendimiento académico y la asistencia.',
  },
  TEACHER: {
    title: 'Mi Panel Docente',
    subtitle: 'Gestiona tus clases, asistencia y calificaciones.',
  },
  STUDENT: {
    title: 'Mi Portal Estudiantil',
    subtitle: 'Consulta tu horario, notas y tareas pendientes.',
  },
  GUARDIAN: {
    title: 'Portal del Apoderado',
    subtitle: 'Seguimiento del rendimiento académico de tu hijo.',
  },
};

export default function DashboardPage() {
  const { user } = useAuth();
  
  const greeting = user?.role 
    ? roleGreetings[user.role] 
    : { title: 'Panel de Control', subtitle: 'Bienvenido a Fine Flow' };

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-foreground">{greeting.title}</h1>
            <p className="text-muted-foreground mt-1">
              Bienvenido, {user?.first_name}. {greeting.subtitle}
            </p>
          </div>

          {/* KPI Cards */}
          <div className="mb-8">
            <KPICards />
          </div>

          {/* Schedule and Timeline */}
          <div className="grid gap-6 lg:grid-cols-3">
            <div className="lg:col-span-2">
              <ScheduleTable />
            </div>
            <div className="lg:col-span-1">
              <ActivityTimeline />
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
