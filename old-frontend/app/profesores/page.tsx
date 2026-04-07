'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Search, Plus, MoreHorizontal, FileDown, Calendar, BookOpen } from 'lucide-react';
import { demoTeachers, demoCourseAssignments, demoCourses, demoUsers } from '@/lib/demo-data';
import type { TeacherStatus } from '@/lib/types';

const statusConfig: Record<TeacherStatus, { label: string; className: string }> = {
  ACTIVE: { label: 'Activo', className: 'bg-green-100 text-green-800 dark:bg-green-950 dark:text-green-300' },
  INACTIVE: { label: 'Inactivo', className: 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300' },
  ON_LEAVE: { label: 'Con Licencia', className: 'bg-orange-100 text-orange-800 dark:bg-orange-950 dark:text-orange-300' },
};

// Helper function to get teacher's assigned sections count
function getTeacherSectionsCount(teacherId: string): number {
  const assignments = demoCourseAssignments.filter(ca => ca.teacher_id === teacherId && ca.is_active);
  const uniqueSections = [...new Set(assignments.map(a => a.section_id))];
  return uniqueSections.length;
}

// Helper function to get teacher's total hours per week
function getTeacherHoursPerWeek(teacherId: string): number {
  const assignments = demoCourseAssignments.filter(ca => ca.teacher_id === teacherId && ca.is_active);
  return assignments.reduce((total, a) => total + a.hours_per_week, 0);
}

// Helper function to get teacher's courses
function getTeacherCourses(teacherId: string): string[] {
  const assignments = demoCourseAssignments.filter(ca => ca.teacher_id === teacherId && ca.is_active);
  const courseIds = [...new Set(assignments.map(a => a.course_id))];
  return courseIds.map(id => {
    const course = demoCourses.find(c => c.id === id);
    return course?.name ?? 'Unknown';
  });
}

// Helper function to get teacher's user email
function getTeacherEmail(userId: string | undefined): string {
  if (!userId) return '';
  const user = demoUsers.find(u => u.id === userId);
  return user?.email ?? '';
}

export default function ProfesoresPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');

  const filteredTeachers = demoTeachers.filter((teacher) => {
    const matchesSearch = 
      teacher.first_name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      teacher.last_name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (teacher.specialty?.toLowerCase() ?? '').includes(searchQuery.toLowerCase()) ||
      teacher.document_number.includes(searchQuery);
    
    const matchesStatus = statusFilter === 'all' || teacher.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  // Calculate stats
  const stats = {
    total: demoTeachers.length,
    active: demoTeachers.filter(t => t.status === 'ACTIVE').length,
    onLeave: demoTeachers.filter(t => t.status === 'ON_LEAVE').length,
    inactive: demoTeachers.filter(t => t.status === 'INACTIVE').length,
  };

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
            <div>
              <h1 className="text-3xl font-bold text-foreground">Profesores</h1>
              <p className="text-muted-foreground mt-1">
                Gestion del personal docente - Asignaciones de curso y horarios
              </p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" className="gap-2">
                <FileDown size={16} />
                Exportar
              </Button>
              <Button className="gap-2">
                <Plus size={16} />
                Nuevo Profesor
              </Button>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="grid gap-4 md:grid-cols-4 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold">{stats.total}</div>
                <p className="text-xs text-muted-foreground">Total Docentes</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-green-600">{stats.active}</div>
                <p className="text-xs text-muted-foreground">Activos</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-orange-600">{stats.onLeave}</div>
                <p className="text-xs text-muted-foreground">Con Licencia</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-gray-600">{stats.inactive}</div>
                <p className="text-xs text-muted-foreground">Inactivos</p>
              </CardContent>
            </Card>
          </div>

          {/* Search and Table */}
          <Card>
            <CardHeader className="pb-4">
              <div className="flex flex-col sm:flex-row gap-4">
                <div className="relative flex-1">
                  <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Buscar por nombre, especialidad o DNI..."
                    className="pl-8"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>
                <Select value={statusFilter} onValueChange={setStatusFilter}>
                  <SelectTrigger className="w-40">
                    <SelectValue placeholder="Estado" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Todos</SelectItem>
                    <SelectItem value="ACTIVE">Activos</SelectItem>
                    <SelectItem value="ON_LEAVE">Con Licencia</SelectItem>
                    <SelectItem value="INACTIVE">Inactivos</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Profesor</TableHead>
                      <TableHead>DNI</TableHead>
                      <TableHead>Especialidad</TableHead>
                      <TableHead>Cursos Asignados</TableHead>
                      <TableHead>Secciones</TableHead>
                      <TableHead>Hrs/Sem</TableHead>
                      <TableHead>Fecha Contrato</TableHead>
                      <TableHead>Estado</TableHead>
                      <TableHead className="w-10"></TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredTeachers.map((teacher) => {
                      const courses = getTeacherCourses(teacher.id);
                      const sectionsCount = getTeacherSectionsCount(teacher.id);
                      const hoursPerWeek = getTeacherHoursPerWeek(teacher.id);
                      const email = getTeacherEmail(teacher.user_id);
                      
                      return (
                        <TableRow key={teacher.id}>
                          <TableCell>
                            <div className="flex items-center gap-3">
                              <Avatar className="h-8 w-8">
                                <AvatarFallback>
                                  {teacher.first_name[0]}{teacher.last_name[0]}
                                </AvatarFallback>
                              </Avatar>
                              <div>
                                <p className="font-medium">{teacher.first_name} {teacher.last_name}</p>
                                {email && <p className="text-xs text-muted-foreground">{email}</p>}
                                {teacher.phone && <p className="text-xs text-muted-foreground">{teacher.phone}</p>}
                              </div>
                            </div>
                          </TableCell>
                          <TableCell className="font-mono text-sm">{teacher.document_number}</TableCell>
                          <TableCell>
                            {teacher.specialty ? (
                              <div className="flex flex-wrap gap-1">
                                {teacher.specialty.split(',').map((spec, i) => (
                                  <Badge key={i} variant="outline" className="text-xs">
                                    {spec.trim()}
                                  </Badge>
                                ))}
                              </div>
                            ) : (
                              <span className="text-muted-foreground">-</span>
                            )}
                          </TableCell>
                          <TableCell>
                            <div className="flex flex-wrap gap-1 max-w-48">
                              {courses.length > 0 ? courses.map((course, i) => (
                                <Badge key={i} variant="secondary" className="text-xs">
                                  {course}
                                </Badge>
                              )) : (
                                <span className="text-muted-foreground text-xs">Sin asignaciones</span>
                              )}
                            </div>
                          </TableCell>
                          <TableCell>
                            <div className="flex items-center gap-1">
                              <BookOpen size={14} className="text-muted-foreground" />
                              {sectionsCount}
                            </div>
                          </TableCell>
                          <TableCell>
                            <div className="flex items-center gap-1">
                              <Calendar size={14} className="text-muted-foreground" />
                              {hoursPerWeek}h
                            </div>
                          </TableCell>
                          <TableCell>
                            {teacher.hired_at ? (
                              <span className="text-sm">
                                {new Date(teacher.hired_at).toLocaleDateString('es-PE')}
                              </span>
                            ) : '-'}
                          </TableCell>
                          <TableCell>
                            <Badge className={statusConfig[teacher.status].className}>
                              {statusConfig[teacher.status].label}
                            </Badge>
                          </TableCell>
                          <TableCell>
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="icon">
                                  <MoreHorizontal size={16} />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="end">
                                <DropdownMenuItem>Ver perfil</DropdownMenuItem>
                                <DropdownMenuItem>Editar</DropdownMenuItem>
                                <DropdownMenuItem>Ver horario</DropdownMenuItem>
                                <DropdownMenuItem>Asignar cursos</DropdownMenuItem>
                                <DropdownMenuItem>Ver historial</DropdownMenuItem>
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>

              {filteredTeachers.length === 0 && (
                <div className="text-center py-8 text-muted-foreground">
                  No se encontraron profesores con los filtros seleccionados
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
