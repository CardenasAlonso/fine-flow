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
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { GraduationCap, TrendingUp, Award, AlertCircle, User } from 'lucide-react';

const children = [
  { id: 1, name: 'Miguel Chávez Huamán', grade: '4° Primaria', section: 'A', photo: '' },
  { id: 2, name: 'Sofía Chávez Huamán', grade: '2° Primaria', section: 'B', photo: '' },
];

const mockGrades = [
  { course: 'Matemática', comp1: 'AD', comp2: 'A', comp3: 'A', comp4: 'A', avg: 17 },
  { course: 'Comunicación', comp1: 'A', comp2: 'A', comp3: 'AD', comp4: 'A', avg: 16 },
  { course: 'Ciencia y Tecnología', comp1: 'A', comp2: 'B', comp3: 'A', comp4: 'A', avg: 15 },
  { course: 'Personal Social', comp1: 'A', comp2: 'A', comp3: 'A', comp4: 'A', avg: 16 },
  { course: 'Arte y Cultura', comp1: 'AD', comp2: 'AD', comp3: 'A', comp4: 'AD', avg: 18 },
  { course: 'Educación Física', comp1: 'A', comp2: 'A', comp3: 'A', comp4: 'A', avg: 16 },
  { course: 'Inglés', comp1: 'A', comp2: 'A', comp3: 'B', comp4: 'A', avg: 15 },
  { course: 'Religión', comp1: 'A', comp2: 'A', comp3: 'A', comp4: 'A', avg: 16 },
];

const gradeColors: Record<string, string> = {
  'AD': 'bg-green-500/10 text-green-600',
  'A': 'bg-blue-500/10 text-blue-600',
  'B': 'bg-amber-500/10 text-amber-600',
  'C': 'bg-red-500/10 text-red-600',
};

export default function NotasHijoPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [selectedChild, setSelectedChild] = useState(children[0].id.toString());
  const [selectedPeriod, setSelectedPeriod] = useState('bim1');

  const currentChild = children.find(c => c.id.toString() === selectedChild) || children[0];
  const avgGeneral = (mockGrades.reduce((sum, g) => sum + g.avg, 0) / mockGrades.length).toFixed(1);

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
              <h1 className="text-2xl font-bold text-foreground">Notas de mi Hijo(a)</h1>
              <p className="text-muted-foreground">Consulta el rendimiento académico</p>
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
              <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
                <SelectTrigger className="w-40">
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
                  <p className="text-sm text-muted-foreground">Promedio General</p>
                  <p className="text-3xl font-bold text-primary">{avgGeneral}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Stats Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-green-500/10 rounded-lg">
                    <Award className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{mockGrades.filter(g => g.avg >= 18).length}</p>
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
                    <p className="text-2xl font-bold">{mockGrades.filter(g => g.avg >= 14 && g.avg < 18).length}</p>
                    <p className="text-sm text-muted-foreground">Logro Esperado</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-amber-500/10 rounded-lg">
                    <TrendingUp className="h-5 w-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{mockGrades.filter(g => g.avg >= 11 && g.avg < 14).length}</p>
                    <p className="text-sm text-muted-foreground">En Proceso</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-red-500/10 rounded-lg">
                    <AlertCircle className="h-5 w-5 text-red-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{mockGrades.filter(g => g.avg < 11).length}</p>
                    <p className="text-sm text-muted-foreground">En Inicio</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Grades Table */}
          <Card>
            <CardHeader>
              <CardTitle>Calificaciones por Competencia</CardTitle>
              <CardDescription>I Bimestre - Año Escolar 2025</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Curso</TableHead>
                      <TableHead className="text-center">Comp. 1</TableHead>
                      <TableHead className="text-center">Comp. 2</TableHead>
                      <TableHead className="text-center">Comp. 3</TableHead>
                      <TableHead className="text-center">Comp. 4</TableHead>
                      <TableHead className="text-center">Promedio</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {mockGrades.map((grade, index) => (
                      <TableRow key={index}>
                        <TableCell className="font-medium">{grade.course}</TableCell>
                        <TableCell className="text-center">
                          <Badge className={gradeColors[grade.comp1]}>{grade.comp1}</Badge>
                        </TableCell>
                        <TableCell className="text-center">
                          <Badge className={gradeColors[grade.comp2]}>{grade.comp2}</Badge>
                        </TableCell>
                        <TableCell className="text-center">
                          <Badge className={gradeColors[grade.comp3]}>{grade.comp3}</Badge>
                        </TableCell>
                        <TableCell className="text-center">
                          <Badge className={gradeColors[grade.comp4]}>{grade.comp4}</Badge>
                        </TableCell>
                        <TableCell className="text-center">
                          <span className={`font-bold ${grade.avg >= 14 ? 'text-green-600' : grade.avg >= 11 ? 'text-amber-600' : 'text-red-600'}`}>
                            {grade.avg}
                          </span>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>

              {/* Legend */}
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
        </main>
      </div>
    </div>
  );
}
