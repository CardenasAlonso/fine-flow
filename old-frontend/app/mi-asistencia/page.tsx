'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { useAuth } from '@/lib/auth-context';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Calendar, CheckCircle2, XCircle, Clock, TrendingUp } from 'lucide-react';

const mockAttendanceData = [
  { date: '2025-03-28', day: 'Viernes', status: 'PRESENT', time: '07:32', note: '' },
  { date: '2025-03-27', day: 'Jueves', status: 'PRESENT', time: '07:28', note: '' },
  { date: '2025-03-26', day: 'Miércoles', status: 'LATE', time: '07:52', note: 'Llegó 22 min tarde' },
  { date: '2025-03-25', day: 'Martes', status: 'PRESENT', time: '07:15', note: '' },
  { date: '2025-03-24', day: 'Lunes', status: 'PRESENT', time: '07:25', note: '' },
  { date: '2025-03-21', day: 'Viernes', status: 'ABSENT', time: '-', note: 'Falta justificada - Cita médica' },
  { date: '2025-03-20', day: 'Jueves', status: 'PRESENT', time: '07:18', note: '' },
  { date: '2025-03-19', day: 'Miércoles', status: 'PRESENT', time: '07:30', note: '' },
  { date: '2025-03-18', day: 'Martes', status: 'PRESENT', time: '07:22', note: '' },
  { date: '2025-03-17', day: 'Lunes', status: 'LATE', time: '07:48', note: 'Llegó 18 min tarde' },
];

const monthlyStats = {
  totalDays: 22,
  present: 18,
  late: 3,
  absent: 1,
  percentage: 95.5,
};

const statusConfig = {
  PRESENT: { label: 'Presente', color: 'bg-green-500/10 text-green-600', icon: CheckCircle2 },
  LATE: { label: 'Tardanza', color: 'bg-amber-500/10 text-amber-600', icon: Clock },
  ABSENT: { label: 'Falta', color: 'bg-red-500/10 text-red-600', icon: XCircle },
};

export default function MiAsistenciaPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState('marzo');

  if (!user || user.role !== 'STUDENT') {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Card className="p-6">
          <p className="text-muted-foreground">No tienes permisos para acceder a esta página.</p>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <div className={`transition-all duration-300 ${sidebarCollapsed ? 'ml-20' : 'ml-64'}`}>
        <Header sidebarCollapsed={sidebarCollapsed} />
        
        <main className="p-6 pt-24">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
            <div>
              <h1 className="text-2xl font-bold text-foreground">Mi Asistencia</h1>
              <p className="text-muted-foreground">Revisa tu historial de asistencia escolar</p>
            </div>
            <Select value={selectedMonth} onValueChange={setSelectedMonth}>
              <SelectTrigger className="w-40">
                <SelectValue placeholder="Mes" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="marzo">Marzo 2025</SelectItem>
                <SelectItem value="abril">Abril 2025</SelectItem>
                <SelectItem value="mayo">Mayo 2025</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
            <Card className="col-span-2 lg:col-span-1">
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <TrendingUp className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{monthlyStats.percentage}%</p>
                    <p className="text-sm text-muted-foreground">Asistencia</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-muted rounded-lg">
                    <Calendar className="h-5 w-5 text-muted-foreground" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{monthlyStats.totalDays}</p>
                    <p className="text-sm text-muted-foreground">Días Hábiles</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-green-500/10 rounded-lg">
                    <CheckCircle2 className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{monthlyStats.present}</p>
                    <p className="text-sm text-muted-foreground">Asistencias</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-amber-500/10 rounded-lg">
                    <Clock className="h-5 w-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{monthlyStats.late}</p>
                    <p className="text-sm text-muted-foreground">Tardanzas</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-red-500/10 rounded-lg">
                    <XCircle className="h-5 w-5 text-red-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{monthlyStats.absent}</p>
                    <p className="text-sm text-muted-foreground">Faltas</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Attendance History */}
          <Card>
            <CardHeader>
              <CardTitle>Historial de Asistencia</CardTitle>
              <CardDescription>Registro detallado de tus asistencias</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {mockAttendanceData.map((record, index) => {
                  const config = statusConfig[record.status as keyof typeof statusConfig];
                  const Icon = config.icon;
                  
                  return (
                    <div
                      key={index}
                      className="flex items-center justify-between p-4 rounded-lg border bg-card hover:bg-muted/50 transition-colors"
                    >
                      <div className="flex items-center gap-4">
                        <div className={`p-2 rounded-lg ${config.color.split(' ')[0]}`}>
                          <Icon className={`h-5 w-5 ${config.color.split(' ')[1]}`} />
                        </div>
                        <div>
                          <p className="font-medium">{record.day}, {new Date(record.date).toLocaleDateString('es-PE', { day: 'numeric', month: 'long' })}</p>
                          {record.note && (
                            <p className="text-sm text-muted-foreground">{record.note}</p>
                          )}
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        {record.time !== '-' && (
                          <span className="text-sm text-muted-foreground">
                            Entrada: {record.time}
                          </span>
                        )}
                        <Badge className={config.color}>
                          {config.label}
                        </Badge>
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          {/* Progress Card */}
          <Card className="mt-6">
            <CardHeader>
              <CardTitle>Progreso del Mes</CardTitle>
              <CardDescription>Tu desempeño de asistencia en {selectedMonth}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span>Asistencia General</span>
                    <span className="font-medium">{monthlyStats.percentage}%</span>
                  </div>
                  <div className="h-3 bg-muted rounded-full overflow-hidden">
                    <div 
                      className="h-full bg-primary rounded-full transition-all duration-500"
                      style={{ width: `${monthlyStats.percentage}%` }}
                    />
                  </div>
                </div>
                <p className="text-sm text-muted-foreground">
                  {monthlyStats.percentage >= 95 
                    ? '¡Excelente asistencia! Sigue así para mantener tu buen rendimiento.' 
                    : monthlyStats.percentage >= 85 
                    ? 'Buena asistencia. Intenta reducir las faltas y tardanzas.' 
                    : 'Tu asistencia necesita mejorar. Recuerda que es importante asistir a clases.'}
                </p>
              </div>
            </CardContent>
          </Card>
        </main>
      </div>
    </div>
  );
}
