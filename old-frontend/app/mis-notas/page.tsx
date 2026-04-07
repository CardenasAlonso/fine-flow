'use client';

import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { TrendingUp, BookOpen, Award, Target } from 'lucide-react';

interface CourseGrade {
  id: string;
  course: string;
  teacher: string;
  exam1: number;
  exam2: number;
  homework: number;
  project: number;
  average: number;
  trend: 'up' | 'down' | 'stable';
}

const myGrades: CourseGrade[] = [
  { id: '1', course: 'Matemáticas', teacher: 'Prof. Juan Pérez', exam1: 17, exam2: 16, homework: 18, project: 17, average: 17.0, trend: 'up' },
  { id: '2', course: 'Comunicación', teacher: 'Prof. María López', exam1: 16, exam2: 17, homework: 18, project: 16, average: 16.8, trend: 'stable' },
  { id: '3', course: 'Ciencias', teacher: 'Prof. Carmen García', exam1: 18, exam2: 17, homework: 17, project: 18, average: 17.5, trend: 'up' },
  { id: '4', course: 'Historia', teacher: 'Prof. Roberto Díaz', exam1: 15, exam2: 16, homework: 17, project: 16, average: 16.0, trend: 'up' },
  { id: '5', course: 'Inglés', teacher: 'Prof. David Thompson', exam1: 18, exam2: 19, homework: 18, project: 19, average: 18.5, trend: 'stable' },
  { id: '6', course: 'Educación Física', teacher: 'Prof. Ana Castillo', exam1: 17, exam2: 17, homework: 18, project: 17, average: 17.3, trend: 'stable' },
];

function getGradeColor(grade: number) {
  if (grade >= 18) return 'text-green-600 font-semibold';
  if (grade >= 14) return 'text-foreground';
  if (grade >= 11) return 'text-orange-600';
  return 'text-red-600';
}

function getGradeBadge(average: number) {
  if (average >= 18) return { label: 'AD - Logro Destacado', className: 'bg-green-100 text-green-800 dark:bg-green-950 dark:text-green-300' };
  if (average >= 14) return { label: 'A - Logro Esperado', className: 'bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300' };
  if (average >= 11) return { label: 'B - En Proceso', className: 'bg-orange-100 text-orange-800 dark:bg-orange-950 dark:text-orange-300' };
  return { label: 'C - En Inicio', className: 'bg-red-100 text-red-800 dark:bg-red-950 dark:text-red-300' };
}

export default function MisNotasPage() {
  const generalAverage = myGrades.reduce((sum, g) => sum + g.average, 0) / myGrades.length;
  const bestCourse = myGrades.reduce((best, g) => g.average > best.average ? g : best, myGrades[0]);
  const coursesAbove16 = myGrades.filter(g => g.average >= 16).length;

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-foreground">Mis Notas</h1>
            <p className="text-muted-foreground mt-1">
              Consulta tu rendimiento académico del período actual
            </p>
          </div>

          {/* Summary Cards */}
          <div className="grid gap-4 md:grid-cols-4 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <TrendingUp className="h-5 w-5 text-primary" />
                  <div className="text-2xl font-bold">{generalAverage.toFixed(1)}</div>
                </div>
                <p className="text-xs text-muted-foreground">Promedio General</p>
                <Progress value={(generalAverage / 20) * 100} className="mt-2 h-2" />
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Award className="h-5 w-5 text-green-600" />
                  <div className="text-2xl font-bold text-green-600">{bestCourse.average}</div>
                </div>
                <p className="text-xs text-muted-foreground">Mejor Nota - {bestCourse.course}</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <BookOpen className="h-5 w-5 text-blue-600" />
                  <div className="text-2xl font-bold text-blue-600">{myGrades.length}</div>
                </div>
                <p className="text-xs text-muted-foreground">Cursos Activos</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Target className="h-5 w-5 text-purple-600" />
                  <div className="text-2xl font-bold text-purple-600">{coursesAbove16}</div>
                </div>
                <p className="text-xs text-muted-foreground">Cursos con 16+</p>
              </CardContent>
            </Card>
          </div>

          {/* General Status */}
          <Card className="mb-6">
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="font-semibold text-lg">Estado General del Bimestre</h3>
                  <p className="text-muted-foreground text-sm">1er Bimestre 2025 - 3ro A Secundaria</p>
                </div>
                <Badge className={getGradeBadge(generalAverage).className}>
                  {getGradeBadge(generalAverage).label}
                </Badge>
              </div>
            </CardContent>
          </Card>

          {/* Grades Table */}
          <Card>
            <CardHeader>
              <CardTitle>Detalle por Curso</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Curso</TableHead>
                      <TableHead>Profesor</TableHead>
                      <TableHead className="text-center">Examen 1</TableHead>
                      <TableHead className="text-center">Examen 2</TableHead>
                      <TableHead className="text-center">Tareas</TableHead>
                      <TableHead className="text-center">Proyecto</TableHead>
                      <TableHead className="text-center">Promedio</TableHead>
                      <TableHead className="text-center">Nivel</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {myGrades.map((grade) => {
                      const badge = getGradeBadge(grade.average);
                      return (
                        <TableRow key={grade.id}>
                          <TableCell className="font-medium">{grade.course}</TableCell>
                          <TableCell className="text-muted-foreground">{grade.teacher}</TableCell>
                          <TableCell className={`text-center ${getGradeColor(grade.exam1)}`}>
                            {grade.exam1}
                          </TableCell>
                          <TableCell className={`text-center ${getGradeColor(grade.exam2)}`}>
                            {grade.exam2}
                          </TableCell>
                          <TableCell className={`text-center ${getGradeColor(grade.homework)}`}>
                            {grade.homework}
                          </TableCell>
                          <TableCell className={`text-center ${getGradeColor(grade.project)}`}>
                            {grade.project}
                          </TableCell>
                          <TableCell className={`text-center font-bold ${getGradeColor(grade.average)}`}>
                            {grade.average.toFixed(1)}
                          </TableCell>
                          <TableCell className="text-center">
                            <Badge className={badge.className}>
                              {badge.label.split(' - ')[0]}
                            </Badge>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
