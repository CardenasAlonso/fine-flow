'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { useSwal } from '@/lib/use-swal';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { FileDown, Plus, GraduationCap, TrendingUp, TrendingDown, Edit, Save, BookOpen, Target, Link2 } from 'lucide-react';
import { useAuth } from '@/lib/auth-context';
import { demoStudents, demoCourses, demoCourseCompetencies, demoStudentScores, demoSections, demoSchoolYears, demoAcademicPeriods, demoClassTasks } from '@/lib/demo-data';
import { getAchievementLevel } from '@/lib/types';
import type { CourseCompetency } from '@/lib/types';

interface Competency {
  id: string;
  name: string;
  description: string;
  weight: number;
}

interface StudentGrade {
  id: string;
  studentName: string;
  studentCode: string;
  competencies: { [key: string]: string }; // AD, A, B, C
  average: number;
}

// Get competencies from demo data, organized by course
function getCompetenciesByCourseCode(courseCode: string): Competency[] {
  const courseMap: { [key: string]: string } = {
    'mat': 'course-mat-20188',
    'com': 'course-com-20188',
    'cyt': 'course-cie-20188',
    'his': 'course-his-20188',
    'ing': 'course-ing-20188',
  };
  
  const courseId = courseMap[courseCode];
  if (!courseId) return [];
  
  return demoCourseCompetencies
    .filter(c => c.course_id === courseId && c.is_active)
    .map((c, index) => ({
      id: `c${index + 1}`,
      name: c.name,
      description: c.description ?? '',
      weight: c.weight,
    }));
}

// Competencias por curso desde la base de datos v4.0
const courseCompetencies: { [key: string]: Competency[] } = {
  mat: getCompetenciesByCourseCode('mat'),
  com: getCompetenciesByCourseCode('com'),
  cyt: getCompetenciesByCourseCode('cyt'),
  his: getCompetenciesByCourseCode('his'),
  ing: getCompetenciesByCourseCode('ing'),
};

// Generate grades data from demo students
const gradesData: StudentGrade[] = demoStudents
  .filter(s => s.section_id === 'sec-3a-2025')
  .map((student, index) => {
    // Generate competency grades based on demo scores
    const scores = demoStudentScores.filter(sc => sc.student_id === student.id);
    const avgScore = scores.length > 0 
      ? scores.reduce((sum, sc) => sum + sc.score, 0) / scores.length 
      : 15 + Math.random() * 4;
    
    const { level } = getAchievementLevel(avgScore);
    
    // Generate competency values based on the average
    const compValues: { [key: string]: string } = {};
    ['c1', 'c2', 'c3', 'c4'].forEach((c, i) => {
      const variation = Math.random() * 2 - 1;
      const compScore = avgScore + variation;
      const { level: compLevel } = getAchievementLevel(compScore);
      compValues[c] = compLevel;
    });

    return {
      id: student.id,
      studentName: `${student.first_name} ${student.last_name}`,
      studentCode: student.document_number,
      competencies: compValues,
      average: parseFloat(avgScore.toFixed(1)),
    };
  });

const gradeColors: { [key: string]: string } = {
  'AD': 'bg-green-500/10 text-green-600',
  'A': 'bg-blue-500/10 text-blue-600',
  'B': 'bg-amber-500/10 text-amber-600',
  'C': 'bg-red-500/10 text-red-600',
};

function getGradeBadge(average: number) {
  if (average >= 18) return { label: 'AD', className: 'bg-green-500/10 text-green-600' };
  if (average >= 14) return { label: 'A', className: 'bg-blue-500/10 text-blue-600' };
  if (average >= 11) return { label: 'B', className: 'bg-amber-500/10 text-amber-600' };
  return { label: 'C', className: 'bg-red-500/10 text-red-600' };
}

export default function CalificacionesPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [selectedSection, setSelectedSection] = useState('3A');
  const [selectedCourse, setSelectedCourse] = useState('mat');
  const [selectedPeriod, setSelectedPeriod] = useState('bim1');
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState<StudentGrade | null>(null);
  const [grades, setGrades] = useState(gradesData);
  const { Swal } = useSwal();

  const isTeacher = user?.role === 'TEACHER';
  const title = isTeacher ? 'Mis Calificaciones' : 'Gestión de Calificaciones';
  const subtitle = isTeacher
    ? 'Registra y gestiona las notas de tus estudiantes por competencias'
    : 'Supervisa las calificaciones de todos los cursos';

  const currentCompetencies = courseCompetencies[selectedCourse] || courseCompetencies.mat;
  
  const classAverage = grades.reduce((sum, s) => sum + s.average, 0) / grades.length;
  const adCount = grades.filter(s => s.average >= 18).length;
  const aCount = grades.filter(s => s.average >= 14 && s.average < 18).length;
  const bCount = grades.filter(s => s.average >= 11 && s.average < 14).length;
  const cCount = grades.filter(s => s.average < 11).length;

  const handleEditGrades = (student: StudentGrade) => {
    setSelectedStudent(student);
    setIsEditDialogOpen(true);
  };

  const handleSaveGrades = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!Swal || !selectedStudent) return;

    const formData = new FormData(e.currentTarget);
    const newCompetencies: { [key: string]: string } = {};
    
    currentCompetencies.forEach(comp => {
      newCompetencies[comp.id] = formData.get(comp.id) as string;
    });

    // Calculate new average based on competencies
    const gradeValues = { 'AD': 18.5, 'A': 15.5, 'B': 12, 'C': 8 };
    const values = Object.values(newCompetencies).map(g => gradeValues[g as keyof typeof gradeValues]);
    const newAverage = values.reduce((a, b) => a + b, 0) / values.length;

    setGrades(prev => prev.map(g => 
      g.id === selectedStudent.id 
        ? { ...g, competencies: newCompetencies, average: newAverage }
        : g
    ));

    setIsEditDialogOpen(false);
    
    await Swal.fire({
      title: 'Calificaciones Guardadas',
      text: `Las notas de ${selectedStudent.studentName} han sido actualizadas.`,
      icon: 'success',
      timer: 2000,
      showConfirmButton: false
    });
  };

  if (!user || !['ADMIN', 'COORDINATOR', 'TEACHER'].includes(user.role)) {
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
              <h1 className="text-2xl font-bold text-foreground">{title}</h1>
              <p className="text-muted-foreground">{subtitle}</p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" className="gap-2">
                <FileDown size={16} />
                Exportar
              </Button>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <GraduationCap className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{classAverage.toFixed(1)}</p>
                    <p className="text-sm text-muted-foreground">Promedio</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-green-500/10 rounded-lg">
                    <TrendingUp className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{adCount}</p>
                    <p className="text-sm text-muted-foreground">Logro Destacado</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-blue-500/10 rounded-lg">
                    <GraduationCap className="h-5 w-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{aCount}</p>
                    <p className="text-sm text-muted-foreground">Logro Esperado</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-amber-500/10 rounded-lg">
                    <TrendingDown className="h-5 w-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{bCount}</p>
                    <p className="text-sm text-muted-foreground">En Proceso</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-red-500/10 rounded-lg">
                    <TrendingDown className="h-5 w-5 text-red-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{cCount}</p>
                    <p className="text-sm text-muted-foreground">En Inicio</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <Tabs defaultValue="competencies" className="space-y-6">
            <TabsList>
              <TabsTrigger value="competencies" className="gap-2">
                <BookOpen className="h-4 w-4" />
                Por Competencias
              </TabsTrigger>
              <TabsTrigger value="summary" className="gap-2">
                <GraduationCap className="h-4 w-4" />
                Resumen
              </TabsTrigger>
            </TabsList>

            {/* Competencies Tab */}
            <TabsContent value="competencies">
              <Card>
                <CardHeader>
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                      <CardTitle>Calificaciones por Competencia</CardTitle>
                      <CardDescription>Evalúa a los estudiantes según las competencias del área curricular</CardDescription>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      <Select value={selectedSection} onValueChange={setSelectedSection}>
                        <SelectTrigger className="w-28">
                          <SelectValue placeholder="Sección" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="3A">3° A</SelectItem>
                          <SelectItem value="3B">3° B</SelectItem>
                          <SelectItem value="4A">4° A</SelectItem>
                        </SelectContent>
                      </Select>
                      <Select value={selectedCourse} onValueChange={setSelectedCourse}>
                        <SelectTrigger className="w-40">
                          <SelectValue placeholder="Curso" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="mat">Matemática</SelectItem>
                          <SelectItem value="com">Comunicación</SelectItem>
                          <SelectItem value="cyt">Ciencia y Tecnología</SelectItem>
                          <SelectItem value="ps">Personal Social</SelectItem>
                          <SelectItem value="ing">Inglés</SelectItem>
                        </SelectContent>
                      </Select>
                      <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
                        <SelectTrigger className="w-36">
                          <SelectValue placeholder="Período" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="bim1">I Bimestre</SelectItem>
                          <SelectItem value="bim2">II Bimestre</SelectItem>
                          <SelectItem value="bim3">III Bimestre</SelectItem>
                          <SelectItem value="bim4">IV Bimestre</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  {/* Competencies Legend */}
                  <div className="mb-4 p-4 bg-muted/50 rounded-lg">
                    <p className="font-medium text-sm mb-2">Competencias del Área:</p>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                      {currentCompetencies.map((comp, index) => (
                        <div key={comp.id} className="text-sm">
                          <span className="font-medium">C{index + 1}:</span>{' '}
                          <span className="text-muted-foreground">{comp.name}</span>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="overflow-x-auto">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Alumno</TableHead>
                          {currentCompetencies.map((comp, index) => (
                            <TableHead key={comp.id} className="text-center">
                              C{index + 1}
                            </TableHead>
                          ))}
                          <TableHead className="text-center">Nivel</TableHead>
                          {isTeacher && <TableHead className="text-right">Acciones</TableHead>}
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {grades.map((student) => {
                          const badge = getGradeBadge(student.average);
                          return (
                            <TableRow key={student.id}>
                              <TableCell>
                                <div className="flex items-center gap-3">
                                  <Avatar className="h-8 w-8">
                                    <AvatarFallback className="text-xs">
                                      {student.studentName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                                    </AvatarFallback>
                                  </Avatar>
                                  <div>
                                    <span className="font-medium block">{student.studentName}</span>
                                    <span className="text-xs text-muted-foreground">{student.studentCode}</span>
                                  </div>
                                </div>
                              </TableCell>
                              {currentCompetencies.map((comp) => (
                                <TableCell key={comp.id} className="text-center">
                                  <Badge className={gradeColors[student.competencies[comp.id] || 'A']}>
                                    {student.competencies[comp.id] || 'A'}
                                  </Badge>
                                </TableCell>
                              ))}
                              <TableCell className="text-center">
                                <Badge className={badge.className}>{badge.label}</Badge>
                              </TableCell>
                              {isTeacher && (
                                <TableCell className="text-right">
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    onClick={() => handleEditGrades(student)}
                                  >
                                    <Edit className="h-4 w-4" />
                                  </Button>
                                </TableCell>
                              )}
                            </TableRow>
                          );
                        })}
                      </TableBody>
                    </Table>
                  </div>

                  {/* Grade Legend */}
                  <div className="flex flex-wrap gap-4 mt-6 pt-4 border-t">
                    <div className="flex items-center gap-2">
                      <Badge className={gradeColors['AD']}>AD</Badge>
                      <span className="text-sm text-muted-foreground">Logro Destacado (18-20)</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge className={gradeColors['A']}>A</Badge>
                      <span className="text-sm text-muted-foreground">Logro Esperado (14-17)</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge className={gradeColors['B']}>B</Badge>
                      <span className="text-sm text-muted-foreground">En Proceso (11-13)</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge className={gradeColors['C']}>C</Badge>
                      <span className="text-sm text-muted-foreground">En Inicio (0-10)</span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            {/* Summary Tab */}
            <TabsContent value="summary">
              <Card>
                <CardHeader>
                  <CardTitle>Resumen de Calificaciones</CardTitle>
                  <CardDescription>Vista general del rendimiento de la sección</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="overflow-x-auto">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Alumno</TableHead>
                          <TableHead className="text-center">Promedio</TableHead>
                          <TableHead className="text-center">Nivel de Logro</TableHead>
                          <TableHead>Observaciones</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {grades.map((student) => {
                          const badge = getGradeBadge(student.average);
                          return (
                            <TableRow key={student.id}>
                              <TableCell>
                                <div className="flex items-center gap-3">
                                  <Avatar className="h-8 w-8">
                                    <AvatarFallback className="text-xs">
                                      {student.studentName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                                    </AvatarFallback>
                                  </Avatar>
                                  <span className="font-medium">{student.studentName}</span>
                                </div>
                              </TableCell>
                              <TableCell className="text-center font-bold">{student.average.toFixed(1)}</TableCell>
                              <TableCell className="text-center">
                                <Badge className={badge.className}>{badge.label}</Badge>
                              </TableCell>
                              <TableCell className="text-muted-foreground text-sm">
                                {student.average >= 18 
                                  ? 'Excelente rendimiento' 
                                  : student.average >= 14 
                                  ? 'Rendimiento satisfactorio'
                                  : student.average >= 11
                                  ? 'Requiere apoyo adicional'
                                  : 'Necesita intervención urgente'}
                              </TableCell>
                            </TableRow>
                          );
                        })}
                      </TableBody>
                    </Table>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>

          {/* Edit Dialog */}
          <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
            <DialogContent className="sm:max-w-lg">
              <DialogHeader>
                <DialogTitle>Editar Calificaciones</DialogTitle>
              </DialogHeader>
              {selectedStudent && (
                <form onSubmit={handleSaveGrades} className="space-y-4 mt-4">
                  <div className="flex items-center gap-3 p-3 bg-muted/50 rounded-lg">
                    <Avatar className="h-10 w-10">
                      <AvatarFallback className="text-xs bg-primary/10 text-primary">
                        {selectedStudent.studentName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                      </AvatarFallback>
                    </Avatar>
                    <div>
                      <p className="font-medium">{selectedStudent.studentName}</p>
                      <p className="text-sm text-muted-foreground">{selectedStudent.studentCode}</p>
                    </div>
                  </div>

                  <div className="space-y-4">
                    {currentCompetencies.map((comp, index) => (
                      <div key={comp.id} className="space-y-2">
                        <Label htmlFor={comp.id} className="text-sm">
                          <span className="font-medium">C{index + 1}:</span> {comp.name}
                        </Label>
                        <Select name={comp.id} defaultValue={selectedStudent.competencies[comp.id] || 'A'}>
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="AD">AD - Logro Destacado</SelectItem>
                            <SelectItem value="A">A - Logro Esperado</SelectItem>
                            <SelectItem value="B">B - En Proceso</SelectItem>
                            <SelectItem value="C">C - En Inicio</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    ))}
                  </div>

                  <div className="flex justify-end gap-2 pt-4">
                    <Button type="button" variant="outline" onClick={() => setIsEditDialogOpen(false)}>
                      Cancelar
                    </Button>
                    <Button type="submit" className="gap-2">
                      <Save className="h-4 w-4" />
                      Guardar Cambios
                    </Button>
                  </div>
                </form>
              )}
            </DialogContent>
          </Dialog>
        </main>
      </div>
    </div>
  );
}
