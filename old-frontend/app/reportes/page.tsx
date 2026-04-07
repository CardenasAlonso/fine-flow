'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { useAuth } from '@/lib/auth-context';
import { useSwal } from '@/lib/use-swal';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  BarChart3,
  Download,
  FileText,
  TrendingUp,
  Users,
  Calendar,
  GraduationCap,
  AlertCircle,
  Printer,
  FileSpreadsheet,
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
} from 'recharts';

const attendanceData = [
  { month: 'Mar', asistencia: 96, tardanzas: 3, faltas: 1 },
  { month: 'Abr', asistencia: 94, tardanzas: 4, faltas: 2 },
  { month: 'May', asistencia: 95, tardanzas: 3, faltas: 2 },
  { month: 'Jun', asistencia: 93, tardanzas: 5, faltas: 2 },
  { month: 'Jul', asistencia: 97, tardanzas: 2, faltas: 1 },
  { month: 'Ago', asistencia: 95, tardanzas: 3, faltas: 2 },
];

const gradeDistribution = [
  { range: 'AD (18-20)', count: 120, color: '#22c55e' },
  { range: 'A (14-17)', count: 450, color: '#3b82f6' },
  { range: 'B (11-13)', count: 280, color: '#f59e0b' },
  { range: 'C (0-10)', count: 95, color: '#ef4444' },
];

const performanceByGrade = [
  { grade: '1°', promedio: 14.5 },
  { grade: '2°', promedio: 14.8 },
  { grade: '3°', promedio: 13.9 },
  { grade: '4°', promedio: 14.2 },
  { grade: '5°', promedio: 15.1 },
  { grade: '6°', promedio: 14.7 },
];

const reportTypes = [
  {
    id: 'attendance',
    title: 'Reporte de Asistencia',
    description: 'Estadísticas de asistencia por grado, sección o período',
    icon: Calendar,
    formats: ['PDF', 'Excel'],
  },
  {
    id: 'grades',
    title: 'Reporte de Calificaciones',
    description: 'Notas y promedios por alumno, sección o curso',
    icon: GraduationCap,
    formats: ['PDF', 'Excel'],
  },
  {
    id: 'students',
    title: 'Nómina de Estudiantes',
    description: 'Lista completa de estudiantes matriculados',
    icon: Users,
    formats: ['PDF', 'Excel'],
  },
  {
    id: 'teachers',
    title: 'Reporte de Docentes',
    description: 'Asignación de docentes por curso y sección',
    icon: Users,
    formats: ['PDF', 'Excel'],
  },
  {
    id: 'performance',
    title: 'Rendimiento Académico',
    description: 'Análisis comparativo del rendimiento por período',
    icon: TrendingUp,
    formats: ['PDF', 'Excel'],
  },
  {
    id: 'incidents',
    title: 'Reporte de Incidencias',
    description: 'Registro de incidencias y observaciones',
    icon: AlertCircle,
    formats: ['PDF'],
  },
];

export default function ReportesPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [selectedPeriod, setSelectedPeriod] = useState('2025');
  const [selectedGrade, setSelectedGrade] = useState('all');
  const { Swal } = useSwal();

  const handleGenerateReport = async (reportTitle: string, format: string) => {
    if (!Swal) return;
    
    await Swal.fire({
      title: 'Generando Reporte',
      html: `Preparando ${reportTitle} en formato ${format}...`,
      timer: 2000,
      timerProgressBar: true,
      didOpen: () => {
        Swal.showLoading();
      }
    });

    await Swal.fire({
      title: '¡Reporte Generado!',
      text: `El ${reportTitle} ha sido generado exitosamente.`,
      icon: 'success',
      confirmButtonText: 'Descargar',
      showCancelButton: true,
      cancelButtonText: 'Cerrar'
    });
  };

  if (!user || !['ADMIN', 'COORDINATOR'].includes(user.role)) {
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
              <h1 className="text-2xl font-bold text-foreground">Centro de Reportes</h1>
              <p className="text-muted-foreground">Genera y descarga reportes del sistema</p>
            </div>
            <div className="flex gap-2">
              <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
                <SelectTrigger className="w-32">
                  <SelectValue placeholder="Período" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="2025">2025</SelectItem>
                  <SelectItem value="2024">2024</SelectItem>
                  <SelectItem value="2023">2023</SelectItem>
                </SelectContent>
              </Select>
              <Select value={selectedGrade} onValueChange={setSelectedGrade}>
                <SelectTrigger className="w-40">
                  <SelectValue placeholder="Grado" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Todos los grados</SelectItem>
                  <SelectItem value="1">1° Primaria</SelectItem>
                  <SelectItem value="2">2° Primaria</SelectItem>
                  <SelectItem value="3">3° Primaria</SelectItem>
                  <SelectItem value="4">4° Primaria</SelectItem>
                  <SelectItem value="5">5° Primaria</SelectItem>
                  <SelectItem value="6">6° Primaria</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <Tabs defaultValue="dashboard" className="space-y-6">
            <TabsList>
              <TabsTrigger value="dashboard">Dashboard</TabsTrigger>
              <TabsTrigger value="generate">Generar Reportes</TabsTrigger>
            </TabsList>

            {/* Dashboard Tab */}
            <TabsContent value="dashboard" className="space-y-6">
              {/* KPI Cards */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <Card>
                  <CardContent className="p-4">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-green-500/10 rounded-lg">
                        <Calendar className="h-5 w-5 text-green-600" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold">95.2%</p>
                        <p className="text-sm text-muted-foreground">Asistencia Promedio</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-4">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-primary/10 rounded-lg">
                        <GraduationCap className="h-5 w-5 text-primary" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold">14.5</p>
                        <p className="text-sm text-muted-foreground">Promedio General</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-4">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-accent/10 rounded-lg">
                        <Users className="h-5 w-5 text-accent" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold">1,245</p>
                        <p className="text-sm text-muted-foreground">Estudiantes Activos</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-4">
                    <div className="flex items-center gap-3">
                      <div className="p-2 bg-amber-500/10 rounded-lg">
                        <AlertCircle className="h-5 w-5 text-amber-600" />
                      </div>
                      <div>
                        <p className="text-2xl font-bold">12</p>
                        <p className="text-sm text-muted-foreground">Alertas Académicas</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>

              {/* Charts */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Evolución de Asistencia</CardTitle>
                    <CardDescription>Porcentaje mensual de asistencia</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ResponsiveContainer width="100%" height={300}>
                      <LineChart data={attendanceData}>
                        <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                        <XAxis dataKey="month" className="text-xs" />
                        <YAxis domain={[90, 100]} className="text-xs" />
                        <Tooltip />
                        <Line type="monotone" dataKey="asistencia" stroke="hsl(var(--primary))" strokeWidth={2} dot={{ fill: 'hsl(var(--primary))' }} />
                      </LineChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Distribución de Calificaciones</CardTitle>
                    <CardDescription>Estudiantes por nivel de logro</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ResponsiveContainer width="100%" height={300}>
                      <PieChart>
                        <Pie
                          data={gradeDistribution}
                          cx="50%"
                          cy="50%"
                          innerRadius={60}
                          outerRadius={100}
                          paddingAngle={2}
                          dataKey="count"
                        >
                          {gradeDistribution.map((entry, index) => (
                            <Cell key={`cell-${index}`} fill={entry.color} />
                          ))}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                    <div className="flex flex-wrap justify-center gap-4 mt-4">
                      {gradeDistribution.map((item) => (
                        <div key={item.range} className="flex items-center gap-2">
                          <div className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }} />
                          <span className="text-sm text-muted-foreground">{item.range}: {item.count}</span>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                <Card className="lg:col-span-2">
                  <CardHeader>
                    <CardTitle className="text-lg">Rendimiento por Grado</CardTitle>
                    <CardDescription>Promedio general por grado</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ResponsiveContainer width="100%" height={300}>
                      <BarChart data={performanceByGrade}>
                        <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                        <XAxis dataKey="grade" className="text-xs" />
                        <YAxis domain={[0, 20]} className="text-xs" />
                        <Tooltip />
                        <Bar dataKey="promedio" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </CardContent>
                </Card>
              </div>
            </TabsContent>

            {/* Generate Reports Tab */}
            <TabsContent value="generate" className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {reportTypes.map((report) => {
                  const Icon = report.icon;
                  return (
                    <Card key={report.id} className="hover:border-primary/50 transition-colors">
                      <CardHeader>
                        <div className="flex items-start gap-3">
                          <div className="p-2 bg-primary/10 rounded-lg">
                            <Icon className="h-5 w-5 text-primary" />
                          </div>
                          <div className="flex-1">
                            <CardTitle className="text-base">{report.title}</CardTitle>
                            <CardDescription className="text-sm mt-1">{report.description}</CardDescription>
                          </div>
                        </div>
                      </CardHeader>
                      <CardContent>
                        <div className="flex gap-2">
                          {report.formats.map((format) => (
                            <Button
                              key={format}
                              variant="outline"
                              size="sm"
                              className="gap-2 flex-1"
                              onClick={() => handleGenerateReport(report.title, format)}
                            >
                              {format === 'PDF' ? <FileText className="h-4 w-4" /> : <FileSpreadsheet className="h-4 w-4" />}
                              {format}
                            </Button>
                          ))}
                        </div>
                      </CardContent>
                    </Card>
                  );
                })}
              </div>

              {/* Quick Actions */}
              <Card>
                <CardHeader>
                  <CardTitle>Acciones Rápidas</CardTitle>
                  <CardDescription>Genera reportes frecuentes con un clic</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-wrap gap-3">
                    <Button variant="outline" className="gap-2" onClick={() => handleGenerateReport('Boleta de Notas', 'PDF')}>
                      <Printer className="h-4 w-4" />
                      Imprimir Boletas
                    </Button>
                    <Button variant="outline" className="gap-2" onClick={() => handleGenerateReport('Asistencia Diaria', 'Excel')}>
                      <Download className="h-4 w-4" />
                      Asistencia del Día
                    </Button>
                    <Button variant="outline" className="gap-2" onClick={() => handleGenerateReport('Consolidado General', 'Excel')}>
                      <BarChart3 className="h-4 w-4" />
                      Consolidado General
                    </Button>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </main>
      </div>
    </div>
  );
}
