'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { useAuth } from '@/lib/auth-context';
import { useSwal } from '@/lib/use-swal';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@/components/ui/accordion';
import { Label } from '@/components/ui/label';
import { Progress } from '@/components/ui/progress';
import { Plus, Search, Edit, Trash2, BookOpen, Users, Target, ChevronRight } from 'lucide-react';
import { demoCourses, demoCourseCompetencies, demoCourseAssignments, demoTeachers } from '@/lib/demo-data';
import type { Course, CourseCompetency } from '@/lib/types';

// Helper function to get competencies for a course
function getCompetenciesByCourse(courseId: string): CourseCompetency[] {
  return demoCourseCompetencies.filter(c => c.course_id === courseId && c.is_active);
}

// Helper function to get teacher count for a course
function getTeacherCount(courseId: string): number {
  const assignments = demoCourseAssignments.filter(ca => ca.course_id === courseId && ca.is_active);
  const uniqueTeachers = [...new Set(assignments.map(a => a.teacher_id))];
  return uniqueTeachers.length;
}

// Helper function to get section count for a course
function getSectionCount(courseId: string): number {
  const assignments = demoCourseAssignments.filter(ca => ca.course_id === courseId && ca.is_active);
  const uniqueSections = [...new Set(assignments.map(a => a.section_id))];
  return uniqueSections.length;
}

// Helper function to get total hours for a course
function getTotalHours(courseId: string): number {
  const assignments = demoCourseAssignments.filter(ca => ca.course_id === courseId && ca.is_active);
  return assignments.reduce((total, a) => total + a.hours_per_week, 0);
}

export default function CursosPage() {
  const { user, school } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [courses, setCourses] = useState(demoCourses);
  const [expandedCourse, setExpandedCourse] = useState<string | null>(null);
  const { Swal } = useSwal();

  const filteredCourses = courses.filter(course => {
    const matchesSearch = course.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (course.code?.toLowerCase() ?? '').includes(searchTerm.toLowerCase());
    return matchesSearch && course.is_active;
  });

  const handleDelete = async (courseId: string, courseName: string) => {
    if (!Swal) return;
    
    const result = await Swal.fire({
      title: 'Eliminar curso?',
      text: `Estas seguro de eliminar el curso "${courseName}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Si, eliminar',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      setCourses(courses.filter(c => c.id !== courseId));
      Swal.fire({
        title: 'Eliminado!',
        text: 'El curso ha sido eliminado.',
        icon: 'success',
        timer: 2000,
        showConfirmButton: false
      });
    }
  };

  const handleAddCourse = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!Swal) return;

    const formData = new FormData(e.currentTarget);
    const newCourse: Course = {
      id: `course-${Date.now()}`,
      school_id: school?.id ?? 'school-20188-canete',
      name: formData.get('name') as string,
      code: formData.get('code') as string,
      description: formData.get('description') as string,
      color_hex: formData.get('color') as string,
      is_active: true,
      created_at: new Date(),
    };

    setCourses([...courses, newCourse]);
    setIsDialogOpen(false);
    
    await Swal.fire({
      title: 'Curso creado!',
      text: `El curso "${newCourse.name}" ha sido registrado exitosamente.`,
      icon: 'success',
      timer: 2000,
      showConfirmButton: false
    });
  };

  if (!user || !['ADMIN', 'COORDINATOR'].includes(user.role)) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Card className="p-6">
          <p className="text-muted-foreground">No tienes permisos para acceder a esta pagina.</p>
        </Card>
      </div>
    );
  }

  // Calculate stats
  const totalCompetencies = demoCourseCompetencies.filter(c => c.is_active).length;
  const totalAssignments = demoCourseAssignments.filter(a => a.is_active).length;

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />
        
      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
            <div>
              <h1 className="text-2xl font-bold text-foreground">Gestion de Cursos</h1>
              <p className="text-muted-foreground">
                Areas curriculares CNEB/MINEDU con competencias - Sistema v4.0
              </p>
            </div>
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="gap-2">
                  <Plus size={20} />
                  Nuevo Curso
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-md">
                <DialogHeader>
                  <DialogTitle>Registrar Nuevo Curso</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleAddCourse} className="space-y-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="name">Nombre del Curso</Label>
                    <Input id="name" name="name" placeholder="Ej: Matematica" required />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="code">Codigo</Label>
                      <Input id="code" name="code" placeholder="Ej: MAT" required />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="color">Color</Label>
                      <Input id="color" name="color" type="color" defaultValue="#1e407a" />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="description">Descripcion</Label>
                    <Input id="description" name="description" placeholder="Descripcion del curso" />
                  </div>
                  <div className="flex justify-end gap-2 pt-4">
                    <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                      Cancelar
                    </Button>
                    <Button type="submit">Guardar Curso</Button>
                  </div>
                </form>
              </DialogContent>
            </Dialog>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <BookOpen className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{filteredCourses.length}</p>
                    <p className="text-sm text-muted-foreground">Total Cursos</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-amber-500/10 rounded-lg">
                    <Target className="h-5 w-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{totalCompetencies}</p>
                    <p className="text-sm text-muted-foreground">Competencias CNEB</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-green-500/10 rounded-lg">
                    <Users className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{totalAssignments}</p>
                    <p className="text-sm text-muted-foreground">Asignaciones Activas</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-blue-500/10 rounded-lg">
                    <BookOpen className="h-5 w-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{school?.institution_type === 'K12' ? 'CNEB' : 'Custom'}</p>
                    <p className="text-sm text-muted-foreground">Curriculo Base</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Search */}
          <Card className="mb-6">
            <CardContent className="p-4">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar por nombre o codigo..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </CardContent>
          </Card>

          {/* Courses with Competencies */}
          <div className="space-y-4">
            {filteredCourses.map((course) => {
              const competencies = getCompetenciesByCourse(course.id);
              const teacherCount = getTeacherCount(course.id);
              const sectionCount = getSectionCount(course.id);
              const totalHours = getTotalHours(course.id);

              return (
                <Card key={course.id}>
                  <CardHeader className="pb-2">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div 
                          className="w-10 h-10 rounded-lg flex items-center justify-center text-white font-bold"
                          style={{ backgroundColor: course.color_hex ?? '#1e407a' }}
                        >
                          {course.code?.substring(0, 2) ?? course.name.substring(0, 2)}
                        </div>
                        <div>
                          <CardTitle className="text-lg">{course.name}</CardTitle>
                          <CardDescription>
                            {course.code && <Badge variant="outline" className="mr-2">{course.code}</Badge>}
                            {teacherCount} docentes | {sectionCount} secciones | {totalHours}h/sem
                          </CardDescription>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Button variant="ghost" size="icon">
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button 
                          variant="ghost" 
                          size="icon" 
                          className="text-destructive"
                          onClick={() => handleDelete(course.id, course.name)}
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    {competencies.length > 0 ? (
                      <Accordion type="single" collapsible className="w-full">
                        <AccordionItem value="competencies" className="border-none">
                          <AccordionTrigger className="py-2 hover:no-underline">
                            <div className="flex items-center gap-2 text-sm">
                              <Target size={16} className="text-amber-600" />
                              {competencies.length} Competencias CNEB/MINEDU
                            </div>
                          </AccordionTrigger>
                          <AccordionContent>
                            <div className="space-y-3 pt-2">
                              {competencies.map((comp) => (
                                <div key={comp.id} className="border rounded-lg p-3">
                                  <div className="flex items-center justify-between mb-2">
                                    <span className="font-medium text-sm">{comp.name}</span>
                                    <Badge variant="secondary">{comp.weight}%</Badge>
                                  </div>
                                  <Progress value={comp.weight} className="h-1.5" />
                                  {comp.description && (
                                    <p className="text-xs text-muted-foreground mt-2">{comp.description}</p>
                                  )}
                                </div>
                              ))}
                              <div className="flex justify-end pt-2">
                                <Button variant="outline" size="sm" className="gap-1">
                                  <Plus size={14} />
                                  Agregar Competencia
                                </Button>
                              </div>
                            </div>
                          </AccordionContent>
                        </AccordionItem>
                      </Accordion>
                    ) : (
                      <div className="flex items-center justify-between py-2 text-muted-foreground text-sm">
                        <span>Sin competencias configuradas</span>
                        <Button variant="outline" size="sm" className="gap-1">
                          <Plus size={14} />
                          Agregar Competencia
                        </Button>
                      </div>
                    )}
                  </CardContent>
                </Card>
              );
            })}
          </div>

          {filteredCourses.length === 0 && (
            <Card>
              <CardContent className="p-8 text-center text-muted-foreground">
                No se encontraron cursos con los filtros seleccionados
              </CardContent>
            </Card>
          )}
        </div>
      </main>
    </div>
  );
}
