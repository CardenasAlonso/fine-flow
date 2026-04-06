'use client';

import { useState, useRef, useCallback, useEffect } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
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
import { 
  Calendar, 
  CheckCircle2, 
  XCircle, 
  Clock, 
  FileDown, 
  Users, 
  QrCode, 
  Camera,
  CreditCard,
  Download,
  Search,
  Play,
  Square,
  Volume2,
  Scan
} from 'lucide-react';
import { useAuth } from '@/lib/auth-context';
import { useSwal } from '@/lib/use-swal';
import { demoStudents, demoAttendances, demoSections, demoSchoolYears } from '@/lib/demo-data';
import type { AttendanceStatus, RecordMethod } from '@/lib/types';

interface Student {
  id: string;
  code: string;
  name: string;
  grade: string;
  section: string;
  photo?: string;
}

interface AttendanceRecord {
  id: string;
  studentId: string;
  studentName: string;
  studentCode: string;
  grade: string;
  section: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED';
  checkInTime?: string;
  method: 'SCANNER' | 'MANUAL';
}

// Helper to get section display
function getSectionDisplay(sectionId: string | undefined): { grade: string; section: string } {
  if (!sectionId) return { grade: '', section: '' };
  const section = demoSections.find(s => s.id === sectionId);
  if (!section) return { grade: '', section: '' };
  const schoolYear = demoSchoolYears.find(sy => sy.id === section.school_year_id);
  return { 
    grade: schoolYear ? `${schoolYear.grade_number}°` : '', 
    section: section.name 
  };
}

// Convert demo students to local format
const mockStudents: Student[] = demoStudents.map(s => {
  const { grade, section } = getSectionDisplay(s.section_id);
  return {
    id: s.id,
    code: s.document_number,
    name: `${s.first_name} ${s.last_name}`,
    grade,
    section,
    photo: s.photo_url,
  };
});

// Convert demo attendance to local format
const initialAttendance: AttendanceRecord[] = demoAttendances.slice(0, 5).map(a => {
  const student = demoStudents.find(s => s.id === a.student_id);
  const { grade, section } = getSectionDisplay(student?.section_id);
  return {
    id: a.id,
    studentId: a.student_id,
    studentName: student ? `${student.first_name} ${student.last_name}` : 'Unknown',
    studentCode: student?.document_number ?? '',
    grade,
    section,
    status: a.status as 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED',
    checkInTime: a.check_in_time,
    method: a.record_method === 'QR' ? 'SCANNER' : 'MANUAL',
  };
});

const statusConfig = {
  PRESENT: { label: 'Presente', icon: CheckCircle2, className: 'bg-green-500/10 text-green-600' },
  ABSENT: { label: 'Ausente', icon: XCircle, className: 'bg-red-500/10 text-red-600' },
  LATE: { label: 'Tardanza', icon: Clock, className: 'bg-amber-500/10 text-amber-600' },
  EXCUSED: { label: 'Justificado', icon: Calendar, className: 'bg-blue-500/10 text-blue-600' },
};

export default function AsistenciaPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [selectedSection, setSelectedSection] = useState('3A');
  const [selectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [attendanceRecords, setAttendanceRecords] = useState<AttendanceRecord[]>(initialAttendance);
  const [isScanning, setIsScanning] = useState(false);
  const [scannerActive, setScannerActive] = useState(false);
  const [lastScannedStudent, setLastScannedStudent] = useState<Student | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [manualDialogOpen, setManualDialogOpen] = useState(false);
  const [carnetDialogOpen, setCarnetDialogOpen] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [fullscreenScanner, setFullscreenScanner] = useState(false);
  const [recentScans, setRecentScans] = useState<Array<{ student: Student; time: string; status: 'PRESENT' | 'LATE'; id: string }>>([]);
  const [currentTime, setCurrentTime] = useState(new Date());
  const { Swal } = useSwal();

  // Update clock every second for fullscreen mode
  useEffect(() => {
    if (fullscreenScanner) {
      const interval = setInterval(() => {
        setCurrentTime(new Date());
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [fullscreenScanner]);
  const videoRef = useRef<HTMLVideoElement>(null);
  const scannerRef = useRef<any>(null);

  const isTeacher = user?.role === 'TEACHER';
  const title = isTeacher ? 'Asistencia de Mis Clases' : 'Control de Asistencia';
  const subtitle = isTeacher 
    ? 'Registra y gestiona la asistencia de tus secciones'
    : 'Gestiona la asistencia diaria de todos los estudiantes';

  const presentCount = attendanceRecords.filter(a => a.status === 'PRESENT').length;
  const absentCount = mockStudents.length - attendanceRecords.length;
  const lateCount = attendanceRecords.filter(a => a.status === 'LATE').length;
  const excusedCount = attendanceRecords.filter(a => a.status === 'EXCUSED').length;

  // Start QR/Barcode Scanner (Fullscreen mode)
  const startScanner = useCallback(async (isFullscreen = false) => {
    try {
      const { Html5Qrcode } = await import('html5-qrcode');
      
      if (scannerRef.current) {
        try {
          await scannerRef.current.stop();
        } catch {
          // Scanner might not be running, ignore
        }
        scannerRef.current = null;
      }

      // Set fullscreen mode
      if (isFullscreen) {
        setFullscreenScanner(true);
      }

      // Wait for DOM to update if fullscreen
      await new Promise(resolve => setTimeout(resolve, 100));

      const readerId = isFullscreen ? 'fullscreen-reader' : 'reader';
      const html5QrCode = new Html5Qrcode(readerId);
      scannerRef.current = html5QrCode;

      await html5QrCode.start(
        { facingMode: "environment" },
        {
          fps: 10,
          qrbox: isFullscreen ? { width: 350, height: 350 } : { width: 250, height: 250 },
        },
        async (decodedText) => {
          // Find student by code
          const student = mockStudents.find(s => s.code === decodedText);
          
          if (student) {
            // Check if already registered today
            const alreadyRegistered = attendanceRecords.find(r => r.studentCode === student.code);
            
            if (alreadyRegistered) {
              // Show duplicate scan notification
              const scanId = Date.now().toString();
              setRecentScans(prev => [{
                student,
                time: new Date().toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }),
                status: 'PRESENT',
                id: scanId
              }, ...prev.slice(0, 4)]);

              // Play error sound
              try {
                const audio = new Audio('data:audio/wav;base64,UklGRl9vT19XQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YU...');
                audio.volume = 0.3;
                audio.play().catch(() => {});
              } catch {}

              // Auto-remove after 5 seconds
              setTimeout(() => {
                setRecentScans(prev => prev.filter(s => s.id !== scanId));
              }, 5000);
            } else {
              // Register attendance
              const now = new Date();
              const isLate = now.getHours() >= 8 || (now.getHours() === 7 && now.getMinutes() > 30);
              const timeStr = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;
              
              const newRecord: AttendanceRecord = {
                id: Date.now().toString(),
                studentId: student.id,
                studentName: student.name,
                studentCode: student.code,
                grade: student.grade,
                section: student.section,
                status: isLate ? 'LATE' : 'PRESENT',
                checkInTime: timeStr,
                method: 'SCANNER'
              };
              
              setAttendanceRecords(prev => [...prev, newRecord]);
              setLastScannedStudent(student);

              // Add to recent scans for fullscreen display
              const scanId = Date.now().toString();
              setRecentScans(prev => [{
                student,
                time: timeStr,
                status: isLate ? 'LATE' : 'PRESENT',
                id: scanId
              }, ...prev.slice(0, 4)]);

              // Play success sound
              try {
                const audio = new Audio('data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YU...');
                audio.volume = 0.5;
                audio.play().catch(() => {});
              } catch {}

              // Auto-remove notification after 5 seconds
              setTimeout(() => {
                setRecentScans(prev => prev.filter(s => s.id !== scanId));
              }, 5000);
            }
          } else {
            // Unknown code - show error notification
            const scanId = Date.now().toString();
            setRecentScans(prev => [{
              student: { id: 'unknown', code: decodedText, name: 'Código no registrado', grade: '', section: '' },
              time: new Date().toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }),
              status: 'PRESENT',
              id: scanId
            }, ...prev.slice(0, 4)]);

            setTimeout(() => {
              setRecentScans(prev => prev.filter(s => s.id !== scanId));
            }, 3000);
          }
        },
        () => {} // Error callback
      );

      setScannerActive(true);
    } catch (err) {
      console.error('Error starting scanner:', err);
      setFullscreenScanner(false);
      setScannerActive(false);
      scannerRef.current = null;
      
      const errorMessage = err instanceof Error ? err.message : 'Error desconocido';
      const isNotFound = errorMessage.includes('not found') || errorMessage.includes('NotFound');
      
      if (Swal) {
        Swal.fire({
          title: isNotFound ? 'Cámara no encontrada' : 'Error',
          text: isNotFound 
            ? 'No se detectó ninguna cámara. Conecta una cámara o verifica los permisos del navegador.'
            : 'No se pudo acceder a la cámara. Verifica los permisos.',
          icon: 'error'
        });
      }
    }
  }, [attendanceRecords, Swal]);

  const stopScanner = useCallback(async () => {
    if (scannerRef.current) {
      try {
        await scannerRef.current.stop();
        scannerRef.current = null;
      } catch (err) {
        console.error('Error stopping scanner:', err);
      }
    }
    setScannerActive(false);
    setFullscreenScanner(false);
    setRecentScans([]);
  }, []);

  // Manual attendance registration
  const handleManualAttendance = async (student: Student, status: 'PRESENT' | 'LATE' | 'EXCUSED') => {
    if (!Swal) return;

    const alreadyRegistered = attendanceRecords.find(r => r.studentCode === student.code);
    
    if (alreadyRegistered) {
      Swal.fire({
        title: 'Ya registrado',
        text: `${student.name} ya tiene asistencia registrada hoy.`,
        icon: 'info'
      });
      return;
    }

    const now = new Date();
    const newRecord: AttendanceRecord = {
      id: Date.now().toString(),
      studentId: student.id,
      studentName: student.name,
      studentCode: student.code,
      grade: student.grade,
      section: student.section,
      status,
      checkInTime: `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`,
      method: 'MANUAL'
    };
    
    setAttendanceRecords(prev => [...prev, newRecord]);
    setManualDialogOpen(false);
    
    await Swal.fire({
      title: '¡Asistencia Registrada!',
      text: `Se registró ${statusConfig[status].label.toLowerCase()} para ${student.name}`,
      icon: 'success',
      timer: 2000,
      showConfirmButton: false
    });
  };

  // Generate carnet download
  const handleDownloadCarnet = async (student: Student) => {
    if (!Swal) return;

    // Create carnet canvas
    const canvas = document.createElement('canvas');
    canvas.width = 400;
    canvas.height = 250;
    const ctx = canvas.getContext('2d');
    
    if (ctx) {
      // Background
      ctx.fillStyle = '#1e40af';
      ctx.fillRect(0, 0, 400, 250);
      
      // White card area
      ctx.fillStyle = '#ffffff';
      ctx.roundRect(10, 10, 380, 230, 10);
      ctx.fill();
      
      // Header
      ctx.fillStyle = '#1e40af';
      ctx.fillRect(10, 10, 380, 50);
      
      // School name
      ctx.fillStyle = '#ffffff';
      ctx.font = 'bold 16px Arial';
      ctx.textAlign = 'center';
      ctx.fillText('I.E. CENTRO DE VARONES N°20188', 200, 40);
      
      // Student info
      ctx.fillStyle = '#1e3a5f';
      ctx.font = 'bold 18px Arial';
      ctx.textAlign = 'left';
      ctx.fillText(student.name, 130, 100);
      
      ctx.fillStyle = '#666666';
      ctx.font = '14px Arial';
      ctx.fillText(`Grado: ${student.grade} - Sección: ${student.section}`, 130, 125);
      ctx.fillText(`Código: ${student.code}`, 130, 150);
      
      // Avatar placeholder
      ctx.fillStyle = '#e5e7eb';
      ctx.fillRect(25, 75, 90, 100);
      ctx.fillStyle = '#9ca3af';
      ctx.font = '40px Arial';
      ctx.textAlign = 'center';
      ctx.fillText('👤', 70, 140);
      
      // QR Code area (placeholder)
      ctx.fillStyle = '#000000';
      ctx.fillRect(300, 100, 80, 80);
      ctx.fillStyle = '#ffffff';
      ctx.font = '10px Arial';
      ctx.fillText('QR', 340, 145);
      ctx.fillText(student.code, 340, 160);
      
      // Footer
      ctx.fillStyle = '#666666';
      ctx.font = '10px Arial';
      ctx.textAlign = 'center';
      ctx.fillText('Fine Flow - Sistema de Gestión Escolar', 200, 225);
    }

    // Download
    const link = document.createElement('a');
    link.download = `carnet_${student.code}.png`;
    link.href = canvas.toDataURL('image/png');
    link.click();

    await Swal.fire({
      title: '¡Carnet Descargado!',
      text: `El carnet de ${student.name} ha sido generado.`,
      icon: 'success',
      timer: 2000,
      showConfirmButton: false
    });
  };

  // Change attendance status
  const handleChangeStatus = async (recordId: string, newStatus: 'PRESENT' | 'LATE' | 'ABSENT' | 'EXCUSED') => {
    if (!Swal) return;

    const result = await Swal.fire({
      title: '¿Cambiar estado?',
      text: `¿Deseas cambiar el estado a "${statusConfig[newStatus].label}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, cambiar',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      setAttendanceRecords(prev => 
        prev.map(r => r.id === recordId ? { ...r, status: newStatus } : r)
      );
      
      Swal.fire({
        title: '¡Estado actualizado!',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false
      });
    }
  };

  const filteredStudents = mockStudents.filter(s => 
    s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    s.code.includes(searchTerm)
  );

  if (!user || !['ADMIN', 'COORDINATOR', 'TEACHER'].includes(user.role)) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Card className="p-6">
          <p className="text-muted-foreground">No tienes permisos para acceder a esta página.</p>
        </Card>
      </div>
    );
  }

  // Fullscreen Scanner Mode
  if (fullscreenScanner) {
    return (
      <div className="fixed inset-0 z-50 bg-black">
        {/* Header */}
        <div className="absolute top-0 left-0 right-0 bg-gradient-to-b from-black/80 to-transparent p-4 z-10">
          <div className="flex items-center justify-between max-w-7xl mx-auto">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-lg bg-primary flex items-center justify-center">
                <Scan className="h-5 w-5 text-primary-foreground" />
              </div>
              <div>
                <h1 className="text-white font-bold text-lg">Fine Flow - Control de Asistencia</h1>
                <p className="text-white/70 text-sm">Escanea tu carnet para registrar tu asistencia</p>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="text-right">
                <p className="text-white/70 text-sm">Fecha</p>
                <p className="text-white font-medium">
                  {currentTime.toLocaleDateString('es-PE', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
                </p>
              </div>
              <div className="text-right">
                <p className="text-white/70 text-sm">Hora</p>
                <p className="text-white font-mono text-2xl font-bold">
                  {currentTime.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Scanner Area - Center */}
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="relative">
            <div 
              id="fullscreen-reader" 
              className="w-[500px] h-[500px] rounded-2xl overflow-hidden"
            />
            {/* Scanner frame overlay */}
            <div className="absolute inset-0 pointer-events-none">
              <div className="absolute inset-0 border-4 border-primary/50 rounded-2xl" />
              <div className="absolute top-0 left-0 w-16 h-16 border-t-4 border-l-4 border-primary rounded-tl-2xl" />
              <div className="absolute top-0 right-0 w-16 h-16 border-t-4 border-r-4 border-primary rounded-tr-2xl" />
              <div className="absolute bottom-0 left-0 w-16 h-16 border-b-4 border-l-4 border-primary rounded-bl-2xl" />
              <div className="absolute bottom-0 right-0 w-16 h-16 border-b-4 border-r-4 border-primary rounded-br-2xl" />
            </div>
          </div>
        </div>

        {/* Instructions - Bottom */}
        <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-6 z-10">
          <div className="max-w-7xl mx-auto flex items-center justify-between">
            <div className="flex items-center gap-4">
              <div className="flex items-center gap-2 text-white/80">
                <CreditCard className="h-5 w-5" />
                <span>Coloca tu carnet frente a la cámara</span>
              </div>
              <div className="flex items-center gap-2 text-white/80">
                <Volume2 className="h-5 w-5" />
                <span>Espera el sonido de confirmación</span>
              </div>
            </div>
            <Button 
              onClick={stopScanner} 
              variant="destructive" 
              size="lg"
              className="gap-2"
            >
              <Square className="h-5 w-5" />
              Cerrar Escáner
            </Button>
          </div>
        </div>

        {/* Attendance Stats - Bottom Left */}
        <div className="absolute bottom-24 left-6 z-10">
          <div className="bg-black/60 backdrop-blur-sm rounded-xl p-4 border border-white/10">
            <p className="text-white/70 text-sm mb-2">Asistencia de Hoy</p>
            <div className="flex gap-4">
              <div className="text-center">
                <p className="text-2xl font-bold text-green-400">{presentCount}</p>
                <p className="text-xs text-white/60">Presentes</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold text-amber-400">{lateCount}</p>
                <p className="text-xs text-white/60">Tardanzas</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold text-white/40">{mockStudents.length - attendanceRecords.length}</p>
                <p className="text-xs text-white/60">Pendientes</p>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Scans - Top Right */}
        <div className="absolute top-20 right-6 z-20 w-80 space-y-3">
          {recentScans.map((scan, index) => (
            <div 
              key={scan.id}
              className={`
                transform transition-all duration-500 ease-out
                ${index === 0 ? 'animate-in slide-in-from-right fade-in' : ''}
                ${scan.student.id === 'unknown' 
                  ? 'bg-red-500/90' 
                  : scan.status === 'LATE' 
                    ? 'bg-amber-500/90' 
                    : 'bg-green-500/90'
                }
                backdrop-blur-sm rounded-xl p-4 shadow-2xl border border-white/20
              `}
            >
              <div className="flex items-start gap-3">
                <div className={`
                  h-12 w-12 rounded-full flex items-center justify-center text-2xl
                  ${scan.student.id === 'unknown' ? 'bg-red-600' : scan.status === 'LATE' ? 'bg-amber-600' : 'bg-green-600'}
                `}>
                  {scan.student.id === 'unknown' ? '?' : scan.status === 'LATE' ? '!' : ''}
                </div>
                <div className="flex-1">
                  <div className="flex items-center justify-between">
                    <p className="font-bold text-white text-lg">{scan.student.name}</p>
                    <p className="text-white/80 font-mono">{scan.time}</p>
                  </div>
                  {scan.student.id !== 'unknown' && (
                    <>
                      <p className="text-white/90 text-sm">{scan.student.grade} {scan.student.section}</p>
                      <p className="text-white/70 text-xs">Código: {scan.student.code}</p>
                    </>
                  )}
                  <Badge 
                    className={`mt-2 ${
                      scan.student.id === 'unknown' 
                        ? 'bg-red-700 text-white' 
                        : scan.status === 'LATE' 
                          ? 'bg-amber-700 text-white' 
                          : 'bg-green-700 text-white'
                    }`}
                  >
                    {scan.student.id === 'unknown' 
                      ? 'No encontrado' 
                      : scan.status === 'LATE' 
                        ? 'Tardanza Registrada' 
                        : 'Asistencia Registrada'
                    }
                  </Badge>
                </div>
              </div>
            </div>
          ))}
        </div>
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
              <Dialog open={carnetDialogOpen} onOpenChange={setCarnetDialogOpen}>
                <DialogTrigger asChild>
                  <Button variant="outline" className="gap-2">
                    <CreditCard size={16} />
                    Descargar Carnets
                  </Button>
                </DialogTrigger>
                <DialogContent className="sm:max-w-lg">
                  <DialogHeader>
                    <DialogTitle>Descargar Carnet de Alumno</DialogTitle>
                  </DialogHeader>
                  <div className="space-y-4 mt-4">
                    <div className="relative">
                      <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                      <Input
                        placeholder="Buscar alumno por nombre o código..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-10"
                      />
                    </div>
                    <div className="max-h-64 overflow-y-auto space-y-2">
                      {filteredStudents.map(student => (
                        <div
                          key={student.id}
                          className="flex items-center justify-between p-3 rounded-lg border hover:bg-muted/50"
                        >
                          <div className="flex items-center gap-3">
                            <Avatar className="h-10 w-10">
                              <AvatarFallback className="text-xs bg-primary/10 text-primary">
                                {student.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
                              </AvatarFallback>
                            </Avatar>
                            <div>
                              <p className="font-medium text-sm">{student.name}</p>
                              <p className="text-xs text-muted-foreground">{student.grade} {student.section} - {student.code}</p>
                            </div>
                          </div>
                          <Button size="sm" variant="outline" onClick={() => handleDownloadCarnet(student)}>
                            <Download className="h-4 w-4" />
                          </Button>
                        </div>
                      ))}
                    </div>
                  </div>
                </DialogContent>
              </Dialog>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-green-500/10 rounded-lg">
                    <CheckCircle2 className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{presentCount}</p>
                    <p className="text-sm text-muted-foreground">Presentes</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-red-500/10 rounded-lg">
                    <XCircle className="h-5 w-5 text-red-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{absentCount}</p>
                    <p className="text-sm text-muted-foreground">Sin Registrar</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-amber-500/10 rounded-lg">
                    <Clock className="h-5 w-5 text-amber-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{lateCount}</p>
                    <p className="text-sm text-muted-foreground">Tardanzas</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-blue-500/10 rounded-lg">
                    <Calendar className="h-5 w-5 text-blue-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{excusedCount}</p>
                    <p className="text-sm text-muted-foreground">Justificados</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <Tabs defaultValue="scanner" className="space-y-6">
            <TabsList className="grid w-full grid-cols-3 lg:w-96">
              <TabsTrigger value="scanner" className="gap-2">
                <Scan className="h-4 w-4" />
                Escáner
              </TabsTrigger>
              <TabsTrigger value="manual" className="gap-2">
                <Users className="h-4 w-4" />
                Manual
              </TabsTrigger>
              <TabsTrigger value="records" className="gap-2">
                <Calendar className="h-4 w-4" />
                Registro
              </TabsTrigger>
            </TabsList>

            {/* Scanner Tab */}
            <TabsContent value="scanner">
              <div className="grid lg:grid-cols-2 gap-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <Camera className="h-5 w-5" />
                      Escáner de Carnet
                    </CardTitle>
                    <CardDescription>
                      Escanea el código QR o código de barras del carnet del alumno
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div 
                      id="reader" 
                      className="w-full aspect-video bg-muted rounded-lg overflow-hidden flex items-center justify-center"
                    >
                      {!scannerActive && (
                        <div className="text-center p-6">
                          <QrCode className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
                          <p className="text-muted-foreground">
                            Presiona &quot;Iniciar Escáner&quot; para comenzar
                          </p>
                        </div>
                      )}
                    </div>
                    
                    <div className="flex gap-2">
                      {!scannerActive ? (
                        <>
                          <Button onClick={() => startScanner(false)} variant="outline" className="flex-1 gap-2">
                            <Play className="h-4 w-4" />
                            Escáner Normal
                          </Button>
                          <Button onClick={() => startScanner(true)} className="flex-1 gap-2">
                            <Scan className="h-4 w-4" />
                            Pantalla Completa
                          </Button>
                        </>
                      ) : (
                        <Button onClick={stopScanner} variant="destructive" className="flex-1 gap-2">
                          <Square className="h-4 w-4" />
                          Detener Escáner
                        </Button>
                      )}
                    </div>

                    <div className="bg-muted/50 rounded-lg p-4 text-sm">
                      <p className="font-medium mb-2">Instrucciones:</p>
                      <ol className="list-decimal list-inside space-y-1 text-muted-foreground">
                        <li>Inicia el escáner de cámara</li>
                        <li>Coloca el carnet frente a la cámara</li>
                        <li>Espera el sonido de confirmación</li>
                        <li>La asistencia se registrará automáticamente</li>
                      </ol>
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader>
                    <CardTitle>Últimos Registros</CardTitle>
                    <CardDescription>Alumnos escaneados recientemente</CardDescription>
                  </CardHeader>
                  <CardContent>
                    {attendanceRecords.filter(r => r.method === 'SCANNER').length === 0 ? (
                      <div className="text-center py-8 text-muted-foreground">
                        <Scan className="h-12 w-12 mx-auto mb-4 opacity-50" />
                        <p>Aún no hay registros por escáner</p>
                      </div>
                    ) : (
                      <div className="space-y-3">
                        {attendanceRecords
                          .filter(r => r.method === 'SCANNER')
                          .slice(-5)
                          .reverse()
                          .map(record => (
                            <div key={record.id} className="flex items-center justify-between p-3 rounded-lg border">
                              <div className="flex items-center gap-3">
                                <Avatar className="h-10 w-10">
                                  <AvatarFallback className="text-xs bg-primary/10 text-primary">
                                    {record.studentName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                                  </AvatarFallback>
                                </Avatar>
                                <div>
                                  <p className="font-medium text-sm">{record.studentName}</p>
                                  <p className="text-xs text-muted-foreground">{record.grade} {record.section} - {record.checkInTime}</p>
                                </div>
                              </div>
                              <Badge className={statusConfig[record.status].className}>
                                {statusConfig[record.status].label}
                              </Badge>
                            </div>
                          ))}
                      </div>
                    )}
                  </CardContent>
                </Card>
              </div>
            </TabsContent>

            {/* Manual Tab */}
            <TabsContent value="manual">
              <Card>
                <CardHeader>
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                      <CardTitle>Registro Manual de Asistencia</CardTitle>
                      <CardDescription>Selecciona la sección y registra la asistencia manualmente</CardDescription>
                    </div>
                    <div className="flex gap-2">
                      <Select value={selectedSection} onValueChange={setSelectedSection}>
                        <SelectTrigger className="w-32">
                          <SelectValue placeholder="Sección" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="3A">3° A</SelectItem>
                          <SelectItem value="3B">3° B</SelectItem>
                          <SelectItem value="4A">4° A</SelectItem>
                          <SelectItem value="4B">4° B</SelectItem>
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
                          <TableHead>Alumno</TableHead>
                          <TableHead>Código</TableHead>
                          <TableHead>Estado</TableHead>
                          <TableHead className="text-right">Acciones</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {mockStudents.map((student) => {
                          const record = attendanceRecords.find(r => r.studentCode === student.code);
                          return (
                            <TableRow key={student.id}>
                              <TableCell>
                                <div className="flex items-center gap-3">
                                  <Avatar className="h-8 w-8">
                                    <AvatarFallback className="text-xs">
                                      {student.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
                                    </AvatarFallback>
                                  </Avatar>
                                  <span className="font-medium">{student.name}</span>
                                </div>
                              </TableCell>
                              <TableCell className="font-mono text-sm">{student.code}</TableCell>
                              <TableCell>
                                {record ? (
                                  <Badge className={statusConfig[record.status].className}>
                                    {statusConfig[record.status].label}
                                  </Badge>
                                ) : (
                                  <Badge variant="outline">Sin registrar</Badge>
                                )}
                              </TableCell>
                              <TableCell className="text-right">
                                {!record ? (
                                  <div className="flex justify-end gap-1">
                                    <Button
                                      size="sm"
                                      variant="outline"
                                      className="text-green-600 hover:text-green-700"
                                      onClick={() => handleManualAttendance(student, 'PRESENT')}
                                    >
                                      <CheckCircle2 className="h-4 w-4" />
                                    </Button>
                                    <Button
                                      size="sm"
                                      variant="outline"
                                      className="text-amber-600 hover:text-amber-700"
                                      onClick={() => handleManualAttendance(student, 'LATE')}
                                    >
                                      <Clock className="h-4 w-4" />
                                    </Button>
                                    <Button
                                      size="sm"
                                      variant="outline"
                                      className="text-blue-600 hover:text-blue-700"
                                      onClick={() => handleManualAttendance(student, 'EXCUSED')}
                                    >
                                      <Calendar className="h-4 w-4" />
                                    </Button>
                                  </div>
                                ) : (
                                  <Select
                                    value={record.status}
                                    onValueChange={(value) => handleChangeStatus(record.id, value as any)}
                                  >
                                    <SelectTrigger className="w-32 h-8">
                                      <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent>
                                      <SelectItem value="PRESENT">Presente</SelectItem>
                                      <SelectItem value="LATE">Tardanza</SelectItem>
                                      <SelectItem value="ABSENT">Ausente</SelectItem>
                                      <SelectItem value="EXCUSED">Justificado</SelectItem>
                                    </SelectContent>
                                  </Select>
                                )}
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

            {/* Records Tab */}
            <TabsContent value="records">
              <Card>
                <CardHeader>
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                      <CardTitle>Registro del Día</CardTitle>
                      <CardDescription>Todos los registros de asistencia de hoy</CardDescription>
                    </div>
                    <div className="flex gap-2">
                      <Select value={selectedSection} onValueChange={setSelectedSection}>
                        <SelectTrigger className="w-32">
                          <SelectValue placeholder="Sección" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="all">Todas</SelectItem>
                          <SelectItem value="3A">3° A</SelectItem>
                          <SelectItem value="3B">3° B</SelectItem>
                          <SelectItem value="4A">4° A</SelectItem>
                        </SelectContent>
                      </Select>
                      <Input
                        type="date"
                        value={selectedDate}
                        className="w-40"
                        readOnly
                      />
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="overflow-x-auto">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Alumno</TableHead>
                          <TableHead>Grado/Sección</TableHead>
                          <TableHead>Estado</TableHead>
                          <TableHead>Hora</TableHead>
                          <TableHead>Método</TableHead>
                          <TableHead className="text-right">Acciones</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {attendanceRecords.map((record) => {
                          const StatusIcon = statusConfig[record.status].icon;
                          return (
                            <TableRow key={record.id}>
                              <TableCell>
                                <div className="flex items-center gap-3">
                                  <Avatar className="h-8 w-8">
                                    <AvatarFallback className="text-xs">
                                      {record.studentName.split(' ').map(n => n[0]).join('').slice(0, 2)}
                                    </AvatarFallback>
                                  </Avatar>
                                  <div>
                                    <span className="font-medium block">{record.studentName}</span>
                                    <span className="text-xs text-muted-foreground">{record.studentCode}</span>
                                  </div>
                                </div>
                              </TableCell>
                              <TableCell>{record.grade} {record.section}</TableCell>
                              <TableCell>
                                <Badge className={statusConfig[record.status].className}>
                                  <StatusIcon className="h-3 w-3 mr-1" />
                                  {statusConfig[record.status].label}
                                </Badge>
                              </TableCell>
                              <TableCell>{record.checkInTime || '-'}</TableCell>
                              <TableCell>
                                <Badge variant="outline">
                                  {record.method === 'SCANNER' ? 'Escáner' : 'Manual'}
                                </Badge>
                              </TableCell>
                              <TableCell className="text-right">
                                <Select
                                  value={record.status}
                                  onValueChange={(value) => handleChangeStatus(record.id, value as any)}
                                >
                                  <SelectTrigger className="w-32 h-8">
                                    <SelectValue />
                                  </SelectTrigger>
                                  <SelectContent>
                                    <SelectItem value="PRESENT">Presente</SelectItem>
                                    <SelectItem value="LATE">Tardanza</SelectItem>
                                    <SelectItem value="ABSENT">Ausente</SelectItem>
                                    <SelectItem value="EXCUSED">Justificado</SelectItem>
                                  </SelectContent>
                                </Select>
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
        </main>
      </div>
    </div>
  );
}
