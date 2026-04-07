'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { useAuth } from '@/lib/auth-context';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Calendar, CheckCircle2, XCircle, Clock, TrendingUp, AlertTriangle } from 'lucide-react';

const children = [
  { id: 1, name: 'Miguel Chávez Huamán', grade: '4° Primaria', section: 'A' },
  { id: 2, name: 'Sofía Chávez Huamán', grade: '2° Primaria', section: 'B' },
];

const mockAttendanceData = [
  { date: '2025-03-28', day: 'Viernes', status: 'PRESENT', time: '07:32', note: '' },
  { date: '2025-03-27', day: 'Jueves', status: 'PRESENT', time: '07:28', note: '' },
  { date: '2025-03-26', day: 'Miércoles', status: 'LATE', time: '07:52', note: 'Llegó 22 min tarde' },
  { date: '2025-03-25', day: 'Martes', status: 'PRESENT', time: '07:15', note: '' },
  { date: '2025-03-24', day: 'Lunes', status: 'PRESENT', time: '07:25', note: '' },
  { date: '2025-03-21', day: 'Viernes', status: 'ABSENT', time: '-', note: 'Falta justificada - Cita médica' },
  { date: '2025-03-20', day: 'Jueves', status: 'PRESENT', time: '07:18', note: '' },
  { date: '2025-03-19', day: 'Miércoles', status: 'PRESENT', time: '07:30', note: '' },
];

const statusConfig = {
  PRESENT: { label: 'Presente', color: 'bg-green-500/10 text-green-600', icon: CheckCircle2 },
  LATE: { label: 'Tardanza', color: 'bg-amber-500/10 text-amber-600', icon: Clock },
  ABSENT: { label: 'Falta', color: 'bg-red-500/10 text-red-600', icon: XCircle },
};

export default function AsistenciaHijoPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [selectedChild, setSelectedChild] = useState(children[0].id.toString());
  const [selectedMonth, setSelectedMonth] = useState('marzo');

  const currentChild = children.find(c => c.id.toString() === selectedChild) || children[0];
  
  const stats = {
    totalDays: 22,
    present: 18,
    late: 3,
    absent: 1,
    percentage: 95.5,
  };

  if (!user || user.role !== 'GUARDIAN') {
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
              <h1 className="text-2xl font-bold text-foreground">Asistencia de mi Hijo(a)</h1>
              <p className="text-muted-foreground">Consulta el registro de asistencia</p>
            </div>
            <div className="flex gap-2">
              <Select value={selectedChild} onValueChange={setSelectedChild}>
                <SelectTrigger className="w-52">
                  <SelectValue placeholder="Seleccionar hijo" />
                </SelectTrigger>
                <SelectContent>
                  {children.map(child => (
                    <SelectItem key={child.id} value={child.id.toString()}>
                      {child.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
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
          </div>

          {/* Student Info Card */}
          <Card className="mb-6">
            <CardContent className="p-6">
              <div className="flex items-center gap-4">
                <Avatar className="h-16 w-16">
                  <AvatarFallback className="text-lg bg-primary/10 text-primary">
                    {currentChild.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
                  </AvatarFallback>
                </Avatar>
                <div className="flex-1">
                  <h2 className="text-xl font-semibold">{currentChild.name}</h2>
                  <p className="text-muted-foreground">{currentChild.grade} - Sección {currentChild.section}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-muted-foreground">Asistencia del Mes</p>
                  <p className="text-3xl font-bold text-primary">{stats.percentage}%</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Stats Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-muted rounded-lg">
                    <Calendar className="h-5 w-5 text-muted-foreground" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{stats.totalDays}</p>
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
                    <p className="text-2xl font-bold">{stats.present}</p>
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
                    <p className="text-2xl font-bold">{stats.late}</p>
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
                    <p className="text-2xl font-bold">{stats.absent}</p>
                    <p className="text-sm text-muted-foreground">Faltas</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <TrendingUp className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{stats.percentage}%</p>
                    <p className="text-sm text-muted-foreground">Porcentaje</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Alert if low attendance */}
          {stats.percentage < 85 && (
            <Card className="mb-6 border-amber-500/50 bg-amber-500/5">
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <AlertTriangle className="h-5 w-5 text-amber-600" />
                  <div>
                    <p className="font-medium text-amber-600">Alerta de Asistencia</p>
                    <p className="text-sm text-muted-foreground">
                      La asistencia de {currentChild.name.split(' ')[0]} está por debajo del 85%. 
                      Por favor, comuníquese con el tutor.
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Attendance History */}
          <Card>
            <CardHeader>
              <CardTitle>Historial de Asistencia</CardTitle>
              <CardDescription>Registro detallado del mes de {selectedMonth}</CardDescription>
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
        </main>
      </div>
    </div>
  );
}
