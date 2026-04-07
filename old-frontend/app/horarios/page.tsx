'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { FileDown, Plus, Clock, Building2 } from 'lucide-react';
import { cn } from '@/lib/utils';

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

const scheduleData: WeekSchedule = {
  '08:00 - 08:45': {
    'Lunes': { id: '1', course: 'Matemáticas', teacher: 'J. Pérez', room: '301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300' },
    'Martes': { id: '2', course: 'Comunicación', teacher: 'M. López', room: '301', color: 'bg-green-100 dark:bg-green-950 border-green-300' },
    'Miércoles': { id: '3', course: 'Ciencias', teacher: 'C. García', room: 'Lab 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300' },
    'Jueves': { id: '4', course: 'Historia', teacher: 'R. Díaz', room: '301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300' },
    'Viernes': { id: '5', course: 'Inglés', teacher: 'D. Thompson', room: '301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300' },
  },
  '09:00 - 09:45': {
    'Lunes': { id: '6', course: 'Matemáticas', teacher: 'J. Pérez', room: '301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300' },
    'Martes': { id: '7', course: 'Comunicación', teacher: 'M. López', room: '301', color: 'bg-green-100 dark:bg-green-950 border-green-300' },
    'Miércoles': { id: '8', course: 'Ciencias', teacher: 'C. García', room: 'Lab 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300' },
    'Jueves': { id: '9', course: 'Historia', teacher: 'R. Díaz', room: '301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300' },
    'Viernes': { id: '10', course: 'Inglés', teacher: 'D. Thompson', room: '301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300' },
  },
  '10:00 - 10:45': {
    'Lunes': { id: '11', course: 'Ed. Física', teacher: 'A. Castillo', room: 'Patio', color: 'bg-yellow-100 dark:bg-yellow-950 border-yellow-300' },
    'Martes': { id: '12', course: 'Arte', teacher: 'L. Vega', room: '401', color: 'bg-cyan-100 dark:bg-cyan-950 border-cyan-300' },
    'Miércoles': { id: '13', course: 'Matemáticas', teacher: 'J. Pérez', room: '301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300' },
    'Jueves': { id: '14', course: 'Comunicación', teacher: 'M. López', room: '301', color: 'bg-green-100 dark:bg-green-950 border-green-300' },
    'Viernes': { id: '15', course: 'Ciencias', teacher: 'C. García', room: 'Lab 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300' },
  },
  '11:00 - 11:45': {
    'Lunes': { id: '16', course: 'Ed. Física', teacher: 'A. Castillo', room: 'Patio', color: 'bg-yellow-100 dark:bg-yellow-950 border-yellow-300' },
    'Martes': { id: '17', course: 'Arte', teacher: 'L. Vega', room: '401', color: 'bg-cyan-100 dark:bg-cyan-950 border-cyan-300' },
    'Miércoles': { id: '18', course: 'Matemáticas', teacher: 'J. Pérez', room: '301', color: 'bg-blue-100 dark:bg-blue-950 border-blue-300' },
    'Jueves': { id: '19', course: 'Comunicación', teacher: 'M. López', room: '301', color: 'bg-green-100 dark:bg-green-950 border-green-300' },
    'Viernes': { id: '20', course: 'Ciencias', teacher: 'C. García', room: 'Lab 1', color: 'bg-purple-100 dark:bg-purple-950 border-purple-300' },
  },
  '12:00 - 12:45': {
    'Lunes': null,
    'Martes': null,
    'Miércoles': null,
    'Jueves': null,
    'Viernes': null,
  },
  '13:00 - 13:45': {
    'Lunes': { id: '21', course: 'Tutoría', teacher: 'J. Pérez', room: '301', color: 'bg-gray-100 dark:bg-gray-800 border-gray-300' },
    'Martes': { id: '22', course: 'Religión', teacher: 'P. Santos', room: '301', color: 'bg-indigo-100 dark:bg-indigo-950 border-indigo-300' },
    'Miércoles': { id: '23', course: 'Computación', teacher: 'S. Ríos', room: 'Lab 2', color: 'bg-slate-100 dark:bg-slate-800 border-slate-300' },
    'Jueves': { id: '24', course: 'Inglés', teacher: 'D. Thompson', room: '301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300' },
    'Viernes': { id: '25', course: 'Historia', teacher: 'R. Díaz', room: '301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300' },
  },
  '14:00 - 14:45': {
    'Lunes': { id: '26', course: 'Tutoría', teacher: 'J. Pérez', room: '301', color: 'bg-gray-100 dark:bg-gray-800 border-gray-300' },
    'Martes': { id: '27', course: 'Religión', teacher: 'P. Santos', room: '301', color: 'bg-indigo-100 dark:bg-indigo-950 border-indigo-300' },
    'Miércoles': { id: '28', course: 'Computación', teacher: 'S. Ríos', room: 'Lab 2', color: 'bg-slate-100 dark:bg-slate-800 border-slate-300' },
    'Jueves': { id: '29', course: 'Inglés', teacher: 'D. Thompson', room: '301', color: 'bg-pink-100 dark:bg-pink-950 border-pink-300' },
    'Viernes': { id: '30', course: 'Historia', teacher: 'R. Díaz', room: '301', color: 'bg-orange-100 dark:bg-orange-950 border-orange-300' },
  },
};

export default function HorariosPage() {
  const [selectedSection, setSelectedSection] = useState('3A');

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
            <div>
              <h1 className="text-3xl font-bold text-foreground">Horarios</h1>
              <p className="text-muted-foreground mt-1">
                Gestiona los horarios de clases por sección
              </p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" className="gap-2">
                <FileDown size={16} />
                Exportar PDF
              </Button>
              <Button className="gap-2">
                <Plus size={16} />
                Editar Horario
              </Button>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="grid gap-4 md:grid-cols-3 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Clock className="h-5 w-5 text-primary" />
                  <div className="text-2xl font-bold">35</div>
                </div>
                <p className="text-xs text-muted-foreground">Horas Semanales</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Building2 className="h-5 w-5 text-blue-600" />
                  <div className="text-2xl font-bold text-blue-600">12</div>
                </div>
                <p className="text-xs text-muted-foreground">Aulas Utilizadas</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="text-2xl font-bold">8</div>
                <p className="text-xs text-muted-foreground">Profesores Asignados</p>
              </CardContent>
            </Card>
          </div>

          {/* Section Selector */}
          <Card className="mb-6">
            <CardHeader className="pb-4">
              <div className="flex items-center justify-between">
                <CardTitle>Horario Semanal</CardTitle>
                <Select value={selectedSection} onValueChange={setSelectedSection}>
                  <SelectTrigger className="w-36">
                    <SelectValue placeholder="Sección" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="3A">3ro A</SelectItem>
                    <SelectItem value="3B">3ro B</SelectItem>
                    <SelectItem value="3C">3ro C</SelectItem>
                    <SelectItem value="4A">4to A</SelectItem>
                    <SelectItem value="4B">4to B</SelectItem>
                  </SelectContent>
                </Select>
              </div>
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
                        <th key={day} className="border border-border bg-muted p-2 text-center text-sm font-medium">
                          {day}
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
                          const slot = scheduleData[time]?.[day];
                          return (
                            <td key={`${time}-${day}`} className="border border-border p-1">
                              {slot ? (
                                <div className={cn(
                                  'p-2 rounded-md border text-xs h-full min-h-[60px]',
                                  slot.color
                                )}>
                                  <p className="font-semibold truncate">{slot.course}</p>
                                  <p className="text-muted-foreground truncate">{slot.teacher}</p>
                                  <p className="text-muted-foreground/70 truncate">{slot.room}</p>
                                </div>
                              ) : (
                                <div className="p-2 text-center text-muted-foreground/50 text-xs min-h-[60px] flex items-center justify-center">
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
