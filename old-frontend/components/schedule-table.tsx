'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { useAuth } from '@/lib/auth-context';

interface Schedule {
  id: string;
  time: string;
  grade: string;
  subject: string;
  teacher: string;
  room: string;
  status: 'active' | 'upcoming' | 'completed';
}

// Admin/Coordinator view - all classes
const adminScheduleData: Schedule[] = [
  {
    id: '1',
    time: '08:00 - 08:45',
    grade: '1ro A',
    subject: 'Matemáticas',
    teacher: 'Prof. Carmen García',
    room: 'Aula 101',
    status: 'completed',
  },
  {
    id: '2',
    time: '09:00 - 09:45',
    grade: '2do B',
    subject: 'Comunicación',
    teacher: 'Prof. Luis Rodríguez',
    room: 'Aula 201',
    status: 'completed',
  },
  {
    id: '3',
    time: '10:00 - 10:45',
    grade: '3ro A',
    subject: 'Matemáticas',
    teacher: 'Prof. Juan Pérez',
    room: 'Aula 301',
    status: 'active',
  },
  {
    id: '4',
    time: '11:00 - 11:45',
    grade: '4to B',
    subject: 'Ciencias',
    teacher: 'Prof. María López',
    room: 'Lab. 1',
    status: 'upcoming',
  },
  {
    id: '5',
    time: '13:00 - 13:45',
    grade: '5to A',
    subject: 'Inglés',
    teacher: 'Prof. David Thompson',
    room: 'Aula 501',
    status: 'upcoming',
  },
];

// Teacher view - their classes only
const teacherScheduleData: Schedule[] = [
  {
    id: '1',
    time: '08:00 - 08:45',
    grade: '3ro A',
    subject: 'Matemáticas',
    teacher: '',
    room: 'Aula 301',
    status: 'completed',
  },
  {
    id: '2',
    time: '09:00 - 09:45',
    grade: '3ro B',
    subject: 'Matemáticas',
    teacher: '',
    room: 'Aula 302',
    status: 'completed',
  },
  {
    id: '3',
    time: '10:00 - 10:45',
    grade: '3ro C',
    subject: 'Matemáticas',
    teacher: '',
    room: 'Aula 303',
    status: 'active',
  },
  {
    id: '4',
    time: '11:00 - 11:45',
    grade: '4to A',
    subject: 'Matemáticas',
    teacher: '',
    room: 'Aula 401',
    status: 'upcoming',
  },
  {
    id: '5',
    time: '13:00 - 13:45',
    grade: '4to B',
    subject: 'Matemáticas',
    teacher: '',
    room: 'Aula 402',
    status: 'upcoming',
  },
];

// Student view - their classes
const studentScheduleData: Schedule[] = [
  {
    id: '1',
    time: '08:00 - 08:45',
    grade: '',
    subject: 'Matemáticas',
    teacher: 'Prof. Juan Pérez',
    room: 'Aula 301',
    status: 'completed',
  },
  {
    id: '2',
    time: '09:00 - 09:45',
    grade: '',
    subject: 'Comunicación',
    teacher: 'Prof. María López',
    room: 'Aula 301',
    status: 'completed',
  },
  {
    id: '3',
    time: '10:00 - 10:45',
    grade: '',
    subject: 'Ciencias',
    teacher: 'Prof. Carmen García',
    room: 'Lab. 1',
    status: 'active',
  },
  {
    id: '4',
    time: '11:00 - 11:45',
    grade: '',
    subject: 'Historia',
    teacher: 'Prof. Roberto Díaz',
    room: 'Aula 301',
    status: 'upcoming',
  },
  {
    id: '5',
    time: '13:00 - 13:45',
    grade: '',
    subject: 'Inglés',
    teacher: 'Prof. David Thompson',
    room: 'Aula 301',
    status: 'upcoming',
  },
];

const statusConfig = {
  active: { label: 'En Curso', className: 'bg-green-100 text-green-800 dark:bg-green-950 dark:text-green-300' },
  upcoming: { label: 'Próxima', className: 'bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300' },
  completed: { label: 'Completada', className: 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300' },
};

export function ScheduleTable() {
  const { user } = useAuth();
  
  const isAdminOrCoordinator = user?.role === 'ADMIN' || user?.role === 'COORDINATOR';
  const isTeacher = user?.role === 'TEACHER';
  const isStudentOrGuardian = user?.role === 'STUDENT' || user?.role === 'GUARDIAN';

  const scheduleData = isAdminOrCoordinator 
    ? adminScheduleData 
    : isTeacher 
      ? teacherScheduleData 
      : studentScheduleData;

  const title = isTeacher 
    ? 'Mis Clases de Hoy' 
    : isStudentOrGuardian 
      ? 'Mi Horario de Hoy' 
      : 'Horario General de Hoy';

  // Determine which columns to show
  const showGrade = isAdminOrCoordinator || isTeacher;
  const showTeacher = isAdminOrCoordinator || isStudentOrGuardian;

  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Hora</TableHead>
                {showGrade && <TableHead>Grado</TableHead>}
                <TableHead>Asignatura</TableHead>
                {showTeacher && <TableHead>Profesor</TableHead>}
                <TableHead>Aula</TableHead>
                <TableHead>Estado</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {scheduleData.map((item) => (
                <TableRow key={item.id}>
                  <TableCell className="font-medium">{item.time}</TableCell>
                  {showGrade && <TableCell>{item.grade}</TableCell>}
                  <TableCell>{item.subject}</TableCell>
                  {showTeacher && <TableCell>{item.teacher}</TableCell>}
                  <TableCell>{item.room}</TableCell>
                  <TableCell>
                    <Badge className={statusConfig[item.status].className}>
                      {statusConfig[item.status].label}
                    </Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  );
}
