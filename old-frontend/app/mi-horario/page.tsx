'use client';

import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { FileDown, Clock, MapPin } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/lib/auth-context';

interface ScheduleSlot {
  id: string;
  course: string;
  teacher: string;
  room: string;
  color: string;
}

type WeekSchedule = {
  [time: string]: {
    [day: string]: ScheduleSlot | null;
  };
};

const timeSlots = [
  '08:00 - 08:45',
  '09:00 - 09:45',
  '10:00 - 10:45',
  '11:00 - 11:45',
  '12:00 - 12:45',
  '13:00 - 13:45',
  '14:00 - 14:45',
];

const days = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes'];

const mySchedule: WeekSchedule = {
  '08:00 - 08:45': {
    'Lunes': { id: '1', course: 'Matemáticas', teacher: 'Prof. Juan Pérez', room: 'Aula 301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300 dark:border-blue-800' },
    'Martes': { id: '2', course: 'Comunicación', teacher: 'Prof. María López', room: 'Aula 301', color: 'bg-green-100 dark:bg-green-950 border-green-300 dark:border-green-800' },
    'Miércoles': { id: '3', course: 'Ciencias', teacher: 'Prof. Carmen García', room: 'Lab. 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300 dark:border-purple-800' },
    'Jueves': { id: '4', course: 'Historia', teacher: 'Prof. Roberto Díaz', room: 'Aula 301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300 dark:border-orange-800' },
    'Viernes': { id: '5', course: 'Inglés', teacher: 'Prof. David Thompson', room: 'Aula 301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300 dark:border-pink-800' },
  },
  '09:00 - 09:45': {
    'Lunes': { id: '6', course: 'Matemáticas', teacher: 'Prof. Juan Pérez', room: 'Aula 301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300 dark:border-blue-800' },
    'Martes': { id: '7', course: 'Comunicación', teacher: 'Prof. María López', room: 'Aula 301', color: 'bg-green-100 dark:bg-green-950 border-green-300 dark:border-green-800' },
    'Miércoles': { id: '8', course: 'Ciencias', teacher: 'Prof. Carmen García', room: 'Lab. 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300 dark:border-purple-800' },
    'Jueves': { id: '9', course: 'Historia', teacher: 'Prof. Roberto Díaz', room: 'Aula 301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300 dark:border-orange-800' },
    'Viernes': { id: '10', course: 'Inglés', teacher: 'Prof. David Thompson', room: 'Aula 301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300 dark:border-pink-800' },
  },
  '10:00 - 10:45': {
    'Lunes': { id: '11', course: 'Ed. Física', teacher: 'Prof. Ana Castillo', room: 'Patio', color: 'bg-yellow-100 dark:bg-yellow-950 border-yellow-300 dark:border-yellow-800' },
    'Martes': { id: '12', course: 'Arte', teacher: 'Prof. Luis Vega', room: 'Aula 401', color: 'bg-cyan-100 dark:bg-cyan-950 border-cyan-300 dark:border-cyan-800' },
    'Miércoles': { id: '13', course: 'Matemáticas', teacher: 'Prof. Juan Pérez', room: 'Aula 301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300 dark:border-blue-800' },
    'Jueves': { id: '14', course: 'Comunicación', teacher: 'Prof. María López', room: 'Aula 301', color: 'bg-green-100 dark:bg-green-950 border-green-300 dark:border-green-800' },
    'Viernes': { id: '15', course: 'Ciencias', teacher: 'Prof. Carmen García', room: 'Lab. 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300 dark:border-purple-800' },
  },
  '11:00 - 11:45': {
    'Lunes': { id: '16', course: 'Ed. Física', teacher: 'Prof. Ana Castillo', room: 'Patio', color: 'bg-yellow-100 dark:bg-yellow-950 border-yellow-300 dark:border-yellow-800' },
    'Martes': { id: '17', course: 'Arte', teacher: 'Prof. Luis Vega', room: 'Aula 401', color: 'bg-cyan-100 dark:bg-cyan-950 border-cyan-300 dark:border-cyan-800' },
    'Miércoles': { id: '18', course: 'Matemáticas', teacher: 'Prof. Juan Pérez', room: 'Aula 301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300 dark:border-blue-800' },
    'Jueves': { id: '19', course: 'Comunicación', teacher: 'Prof. María López', room: 'Aula 301', color: 'bg-green-100 dark:bg-green-950 border-green-300 dark:border-green-800' },
    'Viernes': { id: '20', course: 'Ciencias', teacher: 'Prof. Carmen García', room: 'Lab. 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300 dark:border-purple-800' },
  },
  '12:00 - 12:45': {
    'Lunes': null,
    'Martes': null,
    'Miércoles': null,
    'Jueves': null,
    'Viernes': null,
  },
  '13:00 - 13:45': {
    'Lunes': { id: '21', course: 'Tutoría', teacher: 'Prof. Juan Pérez', room: 'Aula 301', color: 'bg-gray-100 dark:bg-gray-800 border-gray-300 dark:border-gray-700' },
    'Martes': { id: '22', course: 'Religión', teacher: 'Prof. Pedro Santos', room: 'Aula 301', color: 'bg-indigo-100 dark:bg-indigo-950 border-indigo-300 dark:border-indigo-800' },
    'Miércoles': { id: '23', course: 'Computación', teacher: 'Prof. Sandra Ríos', room: 'Lab. 2', color: 'bg-slate-100 dark:bg-slate-800 border-slate-300 dark:border-slate-700' },
    'Jueves': { id: '24', course: 'Inglés', teacher: 'Prof. David Thompson', room: 'Aula 301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300 dark:border-pink-800' },
    'Viernes': { id: '25', course: 'Historia', teacher: 'Prof. Roberto Díaz', room: 'Aula 301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300 dark:border-orange-800' },
  },
  '14:00 - 14:45': {
    'Lunes': { id: '26', course: 'Tutoría', teacher: 'Prof. Juan Pérez', room: 'Aula 301', color: 'bg-gray-100 dark:bg-gray-800 border-gray-300 dark:border-gray-700' },
    'Martes': { id: '27', course: 'Religión', teacher: 'Prof. Pedro Santos', room: 'Aula 301', color: 'bg-indigo-100 dark:bg-indigo-950 border-indigo-300 dark:border-indigo-800' },
    'Miércoles': { id: '28', course: 'Computación', teacher: 'Prof. Sandra Ríos', room: 'Lab. 2', color: 'bg-slate-100 dark:bg-slate-800 border-slate-300 dark:border-slate-700' },
    'Jueves': { id: '29', course: 'Inglés', teacher: 'Prof. David Thompson', room: 'Aula 301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300 dark:border-pink-800' },
    'Viernes': { id: '30', course: 'Historia', teacher: 'Prof. Roberto Díaz', room: 'Aula 301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300 dark:border-orange-800' },
  },
};

// Today's schedule highlight
const today = new Date().getDay(); // 0=Sunday, 1=Monday...
const todayName = days[today - 1] || 'Lunes';

export default function MiHorarioPage() {
  const { user } = useAuth();
  
  const isTeacher = user?.role === 'TEACHER';
  const title = isTeacher ? 'Mi Horario de Clases' : 'Mi Horario';
  const subtitle = isTeacher 
    ? 'Tu horario semanal de clases asignadas'
    : 'Tu horario semanal de clases - 3ro A Secundaria';

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
            <div>
              <h1 className="text-3xl font-bold text-foreground">{title}</h1>
              <p className="text-muted-foreground mt-1">{subtitle}</p>
            </div>
            <Button variant="outline" className="gap-2">
              <FileDown size={16} />
              Descargar PDF
            </Button>
          </div>

          {/* Today's Classes Quick View */}
          <Card className="mb-6">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Clock className="h-5 w-5" />
                Clases de Hoy - {todayName}
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex flex-wrap gap-3">
                {timeSlots.map((time) => {
                  const slot = mySchedule[time]?.[todayName];
                  if (!slot) return null;
                  return (
                    <div key={time} className={cn('p-3 rounded-lg border flex-1 min-w-[200px]', slot.color)}>
                      <div className="flex justify-between items-start mb-1">
                        <span className="font-semibold text-sm">{slot.course}</span>
                        <Badge variant="outline" className="text-xs">{time.split(' - ')[0]}</Badge>
                      </div>
                      <p className="text-xs text-muted-foreground">{slot.teacher}</p>
                      <div className="flex items-center gap-1 mt-1 text-xs text-muted-foreground">
                        <MapPin className="h-3 w-3" />
                        {slot.room}
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          {/* Full Week Schedule */}
          <Card>
            <CardHeader>
              <CardTitle>Horario Semanal Completo</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <table className="w-full border-collapse">
                  <thead>
                    <tr>
                      <th className="border border-border bg-muted p-2 text-left text-sm font-medium w-28">
                        Hora
                      </th>
                      {days.map((day) => (
                        <th 
                          key={day} 
                          className={cn(
                            'border border-border bg-muted p-2 text-center text-sm font-medium',
                            day === todayName && 'bg-primary/10'
                          )}
                        >
                          {day}
                          {day === todayName && (
                            <Badge className="ml-2 text-xs">Hoy</Badge>
                          )}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {timeSlots.map((time) => (
                      <tr key={time}>
                        <td className="border border-border p-2 text-sm font-medium bg-muted/50">
                          {time}
                        </td>
                        {days.map((day) => {
                          const slot = mySchedule[time]?.[day];
                          return (
                            <td 
                              key={`${time}-${day}`} 
                              className={cn(
                                'border border-border p-1',
                                day === todayName && 'bg-primary/5'
                              )}
                            >
                              {slot ? (
                                <div className={cn(
                                  'p-2 rounded-md border text-xs h-full min-h-[70px]',
                                  slot.color
                                )}>
                                  <p className="font-semibold">{slot.course}</p>
                                  <p className="text-muted-foreground mt-1">{slot.teacher}</p>
                                  <div className="flex items-center gap-1 mt-1 text-muted-foreground/70">
                                    <MapPin className="h-3 w-3" />
                                    {slot.room}
                                  </div>
                                </div>
                              ) : (
                                <div className="p-2 text-center text-muted-foreground/50 text-xs min-h-[70px] flex items-center justify-center">
                                  Recreo
                                </div>
                              )}
                            </td>
                          );
                        })}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}
