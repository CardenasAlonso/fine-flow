'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
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
import { Plus, Users, GraduationCap, BookOpen } from 'lucide-react';

interface Section {
  id: string;
  grade: string;
  section: string;
  level: string;
  tutor: string;
  studentCount: number;
  maxCapacity: number;
  shift: 'MORNING' | 'AFTERNOON';
}

const sectionsData: Section[] = [
  { id: '1', grade: '1ro', section: 'A', level: 'Secundaria', tutor: 'Prof. Ana García', studentCount: 32, maxCapacity: 35, shift: 'MORNING' },
  { id: '2', grade: '1ro', section: 'B', level: 'Secundaria', tutor: 'Prof. Luis Mendoza', studentCount: 30, maxCapacity: 35, shift: 'MORNING' },
  { id: '3', grade: '2do', section: 'A', level: 'Secundaria', tutor: 'Prof. María López', studentCount: 33, maxCapacity: 35, shift: 'MORNING' },
  { id: '4', grade: '2do', section: 'B', level: 'Secundaria', tutor: 'Prof. Carlos Ríos', studentCount: 31, maxCapacity: 35, shift: 'MORNING' },
  { id: '5', grade: '3ro', section: 'A', level: 'Secundaria', tutor: 'Prof. Juan Pérez', studentCount: 28, maxCapacity: 35, shift: 'MORNING' },
  { id: '6', grade: '3ro', section: 'B', level: 'Secundaria', tutor: 'Prof. Carmen García', studentCount: 29, maxCapacity: 35, shift: 'MORNING' },
  { id: '7', grade: '3ro', section: 'C', level: 'Secundaria', tutor: 'Prof. Roberto Díaz', studentCount: 27, maxCapacity: 35, shift: 'AFTERNOON' },
  { id: '8', grade: '4to', section: 'A', level: 'Secundaria', tutor: 'Prof. Sandra Vega', studentCount: 30, maxCapacity: 35, shift: 'MORNING' },
  { id: '9', grade: '4to', section: 'B', level: 'Secundaria', tutor: 'Prof. Pedro Santos', studentCount: 32, maxCapacity: 35, shift: 'AFTERNOON' },
  { id: '10', grade: '5to', section: 'A', level: 'Secundaria', tutor: 'Prof. Laura Castillo', studentCount: 31, maxCapacity: 35, shift: 'MORNING' },
  { id: '11', grade: '5to', section: 'B', level: 'Secundaria', tutor: 'Prof. David Thompson', studentCount: 28, maxCapacity: 35, shift: 'AFTERNOON' },
];

export default function SeccionesPage() {
  const [selectedLevel, setSelectedLevel] = useState('all');
  const [selectedGrade, setSelectedGrade] = useState('all');

  const filteredSections = sectionsData.filter((section) => {
    if (selectedLevel !== 'all' && section.level !== selectedLevel) return false;
    if (selectedGrade !== 'all' && section.grade !== selectedGrade) return false;
    return true;
  });

  const totalStudents = filteredSections.reduce((sum, s) => sum + s.studentCount, 0);
  const totalCapacity = filteredSections.reduce((sum, s) => sum + s.maxCapacity, 0);

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
            <div>
              <h1 className="text-3xl font-bold text-foreground">Secciones</h1>
              <p className="text-muted-foreground mt-1">
                Gestiona las secciones y aulas del colegio
              </p>
            </div>
            <Button className="gap-2">
              <Plus size={16} />
              Nueva Sección
            </Button>
          </div>

          {/* Stats Cards */}
          <div className="grid gap-4 md:grid-cols-4 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <BookOpen className="h-5 w-5 text-primary" />
                  <div className="text-2xl font-bold">{filteredSections.length}</div>
                </div>
                <p className="text-xs text-muted-foreground">Total Secciones</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Users className="h-5 w-5 text-blue-600" />
                  <div className="text-2xl font-bold text-blue-600">{totalStudents}</div>
                </div>
                <p className="text-xs text-muted-foreground">Total Alumnos</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <GraduationCap className="h-5 w-5 text-green-600" />
                  <div className="text-2xl font-bold text-green-600">{totalCapacity}</div>
                </div>
                <p className="text-xs text-muted-foreground">Capacidad Total</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold">{Math.round((totalStudents / totalCapacity) * 100)}%</div>
                <p className="text-xs text-muted-foreground">Ocupación</p>
              </CardContent>
            </Card>
          </div>

          {/* Filters and Table */}
          <Card>
            <CardHeader>
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <CardTitle>Listado de Secciones</CardTitle>
                <div className="flex gap-2">
                  <Select value={selectedLevel} onValueChange={setSelectedLevel}>
                    <SelectTrigger className="w-32">
                      <SelectValue placeholder="Nivel" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">Todos</SelectItem>
                      <SelectItem value="Primaria">Primaria</SelectItem>
                      <SelectItem value="Secundaria">Secundaria</SelectItem>
                    </SelectContent>
                  </Select>
                  <Select value={selectedGrade} onValueChange={setSelectedGrade}>
                    <SelectTrigger className="w-32">
                      <SelectValue placeholder="Grado" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">Todos</SelectItem>
                      <SelectItem value="1ro">1ro</SelectItem>
                      <SelectItem value="2do">2do</SelectItem>
                      <SelectItem value="3ro">3ro</SelectItem>
                      <SelectItem value="4to">4to</SelectItem>
                      <SelectItem value="5to">5to</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Sección</TableHead>
                      <TableHead>Nivel</TableHead>
                      <TableHead>Tutor</TableHead>
                      <TableHead>Turno</TableHead>
                      <TableHead>Alumnos</TableHead>
                      <TableHead>Ocupación</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredSections.map((section) => {
                      const occupancy = Math.round((section.studentCount / section.maxCapacity) * 100);
                      return (
                        <TableRow key={section.id}>
                          <TableCell>
                            <Badge variant="outline" className="font-medium">
                              {section.grade} {section.section}
                            </Badge>
                          </TableCell>
                          <TableCell>{section.level}</TableCell>
                          <TableCell>
                            <div className="flex items-center gap-2">
                              <Avatar className="h-6 w-6">
                                <AvatarFallback className="text-xs">
                                  {section.tutor.split(' ').slice(1).map(n => n[0]).join('')}
                                </AvatarFallback>
                              </Avatar>
                              <span>{section.tutor}</span>
                            </div>
                          </TableCell>
                          <TableCell>
                            <Badge 
                              className={section.shift === 'MORNING' 
                                ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-950 dark:text-yellow-300' 
                                : 'bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300'
                              }
                            >
                              {section.shift === 'MORNING' ? 'Mañana' : 'Tarde'}
                            </Badge>
                          </TableCell>
                          <TableCell>
                            {section.studentCount} / {section.maxCapacity}
                          </TableCell>
                          <TableCell>
                            <div className="flex items-center gap-2">
                              <div className="w-16 h-2 bg-muted rounded-full overflow-hidden">
                                <div 
                                  className={`h-full rounded-full ${
                                    occupancy >= 90 ? 'bg-red-500' : 
                                    occupancy >= 70 ? 'bg-orange-500' : 'bg-green-500'
                                  }`}
                                  style={{ width: `${occupancy}%` }}
                                />
                              </div>
                              <span className="text-sm">{occupancy}%</span>
                            </div>
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
