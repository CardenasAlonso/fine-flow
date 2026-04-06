'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
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
import { Search, Plus, MoreHorizontal, FileDown, Filter, QrCode, History } from 'lucide-react';
import { demoStudents, demoSections, demoSchoolYears, demoGuardians, demoScoresByStudent } from '@/lib/demo-data';
import type { Student, StudentStatus } from '@/lib/types';
import { getStatusColor, getAchievementLevel } from '@/lib/types';

const statusConfig: Record<StudentStatus, { label: string; className: string }> = {
  ACTIVE: { label: 'Activo', className: 'bg-green-100 text-green-800 dark:bg-green-950 dark:text-green-300' },
  INACTIVE: { label: 'Inactivo', className: 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300' },
  GRADUATED: { label: 'Graduado', className: 'bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300' },
  TRANSFERRED: { label: 'Trasladado', className: 'bg-orange-100 text-orange-800 dark:bg-orange-950 dark:text-orange-300' },
  EGRESADO: { label: 'Egresado', className: 'bg-purple-100 text-purple-800 dark:bg-purple-950 dark:text-purple-300' },
};

// Helper function to get section display name
function getSectionDisplay(sectionId: string | undefined): string {
  if (!sectionId) return 'Sin seccion';
  const section = demoSections.find(s => s.id === sectionId);
  if (!section) return 'Sin seccion';
  const schoolYear = demoSchoolYears.find(sy => sy.id === section.school_year_id);
  return schoolYear ? `${schoolYear.grade_number}° ${section.name}` : section.name;
}

// Helper function to get school year name
function getSchoolYearName(sectionId: string | undefined): string {
  if (!sectionId) return '';
  const section = demoSections.find(s => s.id === sectionId);
  if (!section) return '';
  const schoolYear = demoSchoolYears.find(sy => sy.id === section.school_year_id);
  return schoolYear?.name ?? '';
}

// Calculate average for a student (mock calculation)
function getStudentAverage(studentId: string): number {
  const scores = demoStudents.find(s => s.id === studentId) ? [16.8, 17.2, 15.5, 18.0] : [];
  return scores.length > 0 ? scores.reduce((a, b) => a + b, 0) / scores.length : 0;
}

export default function AlumnosPage() {
  const [searchQuery, setSearchQuery] = useState('');
  const [sectionFilter, setSectionFilter] = useState<string>('all');
  const [statusFilter, setStatusFilter] = useState<string>('all');

  // Get unique sections for filter
  const uniqueSections = [...new Set(demoStudents.map(s => s.section_id).filter(Boolean))];

  const filteredStudents = demoStudents.filter((student) => {
    const matchesSearch = 
      student.first_name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.last_name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.document_number.includes(searchQuery);
    
    const matchesSection = sectionFilter === 'all' || student.section_id === sectionFilter;
    const matchesStatus = statusFilter === 'all' || student.status === statusFilter;

    return matchesSearch && matchesSection && matchesStatus;
  });

  // Calculate stats
  const stats = {
    total: demoStudents.length,
    active: demoStudents.filter(s => s.status === 'ACTIVE').length,
    inactive: demoStudents.filter(s => s.status === 'INACTIVE').length,
    transferred: demoStudents.filter(s => s.status === 'TRANSFERRED').length,
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
              <h1 className="text-3xl font-bold text-foreground">Alumnos</h1>
              <p className="text-muted-foreground mt-1">
                Gestion de estudiantes matriculados - Sistema Multi-Tenant v4.0
              </p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" className="gap-2">
                <FileDown size={16} />
                Exportar
              </Button>
              <Button className="gap-2">
                <Plus size={16} />
                Nuevo Alumno
              </Button>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="grid gap-4 md:grid-cols-4 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold">{stats.total}</div>
                <p className="text-xs text-muted-foreground">Total Matriculados</p>
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
                <div className="text-2xl font-bold text-orange-600">{stats.inactive}</div>
                <p className="text-xs text-muted-foreground">Inactivos</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold text-blue-600">{stats.transferred}</div>
                <p className="text-xs text-muted-foreground">Trasladados</p>
              </CardContent>
            </Card>
          </div>

          {/* Search and Filters */}
          <Card>
            <CardHeader className="pb-4">
              <div className="flex flex-col sm:flex-row gap-4">
                <div className="relative flex-1">
                  <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Buscar por nombre o DNI..."
                    className="pl-8"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                  />
                </div>
                <Select value={sectionFilter} onValueChange={setSectionFilter}>
                  <SelectTrigger className="w-40">
                    <SelectValue placeholder="Seccion" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Todas las secciones</SelectItem>
                    {uniqueSections.map((sectionId) => (
                      <SelectItem key={sectionId} value={sectionId!}>
                        {getSectionDisplay(sectionId)}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Select value={statusFilter} onValueChange={setStatusFilter}>
                  <SelectTrigger className="w-32">
                    <SelectValue placeholder="Estado" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Todos</SelectItem>
                    <SelectItem value="ACTIVE">Activos</SelectItem>
                    <SelectItem value="INACTIVE">Inactivos</SelectItem>
                    <SelectItem value="TRANSFERRED">Trasladados</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Alumno</TableHead>
                      <TableHead>DNI</TableHead>
                      <TableHead>Grado / Seccion</TableHead>
                      <TableHead>Tipo Doc.</TableHead>
                      <TableHead>Grupo Sanguineo</TableHead>
                      <TableHead>Promedio</TableHead>
                      <TableHead>Estado</TableHead>
                      <TableHead className="w-10"></TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredStudents.map((student) => {
                      const average = 16.5; // Mock average
                      const { level, label } = getAchievementLevel(average);
                      const guardian = demoGuardians.find(g => g.student_id === student.id && g.is_primary_contact);
                      
                      return (
                        <TableRow key={student.id}>
                          <TableCell>
                            <div className="flex items-center gap-3">
                              <Avatar className="h-8 w-8">
                                <AvatarImage src={student.photo_url} />
                                <AvatarFallback>
                                  {student.first_name[0]}{student.last_name[0]}
                                </AvatarFallback>
                              </Avatar>
                              <div>
                                <p className="font-medium">{student.first_name} {student.last_name}</p>
                                {guardian && (
                                  <p className="text-xs text-muted-foreground">
                                    Apoderado: {guardian.first_name} {guardian.last_name}
                                  </p>
                                )}
                              </div>
                            </div>
                          </TableCell>
                          <TableCell className="font-mono text-sm">{student.document_number}</TableCell>
                          <TableCell>
                            <div>
                              <p className="font-medium">{getSectionDisplay(student.section_id)}</p>
                              <p className="text-xs text-muted-foreground">{getSchoolYearName(student.section_id)}</p>
                            </div>
                          </TableCell>
                          <TableCell>
                            <Badge variant="outline">{student.document_type}</Badge>
                          </TableCell>
                          <TableCell>{student.blood_type ?? '-'}</TableCell>
                          <TableCell>
                            <div className="flex items-center gap-2">
                              <span className={average >= 14 ? 'text-green-600' : average >= 11 ? 'text-orange-600' : 'text-red-600'}>
                                {average.toFixed(1)}
                              </span>
                              <Badge variant="outline" className="text-xs">
                                {level}
                              </Badge>
                            </div>
                          </TableCell>
                          <TableCell>
                            <Badge className={statusConfig[student.status].className}>
                              {statusConfig[student.status].label}
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
                                <DropdownMenuItem className="gap-2">
                                  <QrCode size={14} />
                                  Ver QR Carnet
                                </DropdownMenuItem>
                                <DropdownMenuItem className="gap-2">
                                  <History size={14} />
                                  Historial de Secciones
                                </DropdownMenuItem>
                                <DropdownMenuItem>Ver notas</DropdownMenuItem>
                                <DropdownMenuItem>Ver asistencia</DropdownMenuItem>
                              </DropdownMenuContent>
                            </DropdownMenu>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>
              
              {filteredStudents.length === 0 && (
                <div className="text-center py-8 text-muted-foreground">
                  No se encontraron alumnos con los filtros seleccionados
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
