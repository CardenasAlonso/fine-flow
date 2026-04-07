'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  Bell,
  CheckCircle2,
  GraduationCap,
  Calendar,
  AlertTriangle,
  FileText,
  Users,
  Clock,
  Check,
  Trash2,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useAuth } from '@/lib/auth-context';

interface Notification {
  id: string;
  type: 'ATTENDANCE_ALERT' | 'SCORE_REGISTERED' | 'JUSTIFICATION_APPROVED' | 'REPORT_READY' | 'GENERAL' | 'PERIOD_CLOSING';
  title: string;
  body: string;
  isRead: boolean;
  createdAt: string;
}

const notificationsData: Notification[] = [
  {
    id: '1',
    type: 'SCORE_REGISTERED',
    title: 'Nueva calificación registrada',
    body: 'Se ha registrado tu nota en Matemáticas: 17/20 - Examen Parcial',
    isRead: false,
    createdAt: 'Hace 30 minutos',
  },
  {
    id: '2',
    type: 'ATTENDANCE_ALERT',
    title: 'Asistencia registrada',
    body: 'Tu asistencia de hoy ha sido marcada como Presente a las 07:45',
    isRead: false,
    createdAt: 'Hace 2 horas',
  },
  {
    id: '3',
    type: 'GENERAL',
    title: 'Reunión de padres programada',
    body: 'La reunión de padres se realizará el viernes 15 de abril a las 18:00 en el auditorio principal',
    isRead: false,
    createdAt: 'Hace 1 día',
  },
  {
    id: '4',
    type: 'PERIOD_CLOSING',
    title: 'Cierre de bimestre próximo',
    body: 'El 1er bimestre cierra el 30 de abril. Asegúrate de completar todas tus evaluaciones pendientes',
    isRead: true,
    createdAt: 'Hace 2 días',
  },
  {
    id: '5',
    type: 'SCORE_REGISTERED',
    title: 'Nota de Comunicación actualizada',
    body: 'Se ha registrado tu nota en Comunicación: 16/20 - Trabajo Práctico',
    isRead: true,
    createdAt: 'Hace 3 días',
  },
  {
    id: '6',
    type: 'REPORT_READY',
    title: 'Reporte disponible',
    body: 'Tu libreta de notas del período anterior está disponible para descarga',
    isRead: true,
    createdAt: 'Hace 5 días',
  },
  {
    id: '7',
    type: 'JUSTIFICATION_APPROVED',
    title: 'Justificación aprobada',
    body: 'Tu justificación por inasistencia del 15 de marzo ha sido aprobada',
    isRead: true,
    createdAt: 'Hace 1 semana',
  },
];

const typeConfig = {
  ATTENDANCE_ALERT: { icon: Calendar, color: 'text-purple-600 bg-purple-100 dark:bg-purple-950' },
  SCORE_REGISTERED: { icon: GraduationCap, color: 'text-blue-600 bg-blue-100 dark:bg-blue-950' },
  JUSTIFICATION_APPROVED: { icon: CheckCircle2, color: 'text-green-600 bg-green-100 dark:bg-green-950' },
  REPORT_READY: { icon: FileText, color: 'text-slate-600 bg-slate-100 dark:bg-slate-800' },
  GENERAL: { icon: Bell, color: 'text-orange-600 bg-orange-100 dark:bg-orange-950' },
  PERIOD_CLOSING: { icon: AlertTriangle, color: 'text-amber-600 bg-amber-100 dark:bg-amber-950' },
};

export default function NotificacionesPage() {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState(notificationsData);

  const unreadCount = notifications.filter(n => !n.isRead).length;

  const markAsRead = (id: string) => {
    setNotifications(prev => 
      prev.map(n => n.id === id ? { ...n, isRead: true } : n)
    );
  };

  const markAllAsRead = () => {
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
  };

  const deleteNotification = (id: string) => {
    setNotifications(prev => prev.filter(n => n.id !== id));
  };

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-4xl mx-auto">
          {/* Page Header */}
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
            <div>
              <h1 className="text-3xl font-bold text-foreground">Notificaciones</h1>
              <p className="text-muted-foreground mt-1">
                Mantente al día con las novedades de tu institución
              </p>
            </div>
            {unreadCount > 0 && (
              <Button variant="outline" onClick={markAllAsRead} className="gap-2">
                <Check size={16} />
                Marcar todas como leídas
              </Button>
            )}
          </div>

          {/* Stats */}
          <div className="grid gap-4 md:grid-cols-3 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Bell className="h-5 w-5 text-primary" />
                  <div className="text-2xl font-bold">{notifications.length}</div>
                </div>
                <p className="text-xs text-muted-foreground">Total</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <Clock className="h-5 w-5 text-blue-600" />
                  <div className="text-2xl font-bold text-blue-600">{unreadCount}</div>
                </div>
                <p className="text-xs text-muted-foreground">Sin leer</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center gap-2">
                  <CheckCircle2 className="h-5 w-5 text-green-600" />
                  <div className="text-2xl font-bold text-green-600">{notifications.length - unreadCount}</div>
                </div>
                <p className="text-xs text-muted-foreground">Leídas</p>
              </CardContent>
            </Card>
          </div>

          {/* Notifications List */}
          <Card>
            <CardHeader>
              <Tabs defaultValue="all" className="w-full">
                <TabsList>
                  <TabsTrigger value="all">Todas</TabsTrigger>
                  <TabsTrigger value="unread">
                    Sin leer
                    {unreadCount > 0 && (
                      <Badge className="ml-2 h-5 w-5 p-0 flex items-center justify-center" variant="destructive">
                        {unreadCount}
                      </Badge>
                    )}
                  </TabsTrigger>
                </TabsList>

                <TabsContent value="all" className="mt-4">
                  <div className="space-y-2">
                    {notifications.map((notification) => {
                      const config = typeConfig[notification.type];
                      const Icon = config.icon;
                      return (
                        <div
                          key={notification.id}
                          className={cn(
                            'flex gap-4 p-4 rounded-lg border transition-colors',
                            !notification.isRead ? 'bg-primary/5 border-primary/20' : 'bg-background'
                          )}
                        >
                          <div className={cn('p-2 rounded-full h-fit', config.color)}>
                            <Icon className="h-4 w-4" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-start justify-between gap-2">
                              <div>
                                <p className={cn('font-medium', !notification.isRead && 'text-foreground')}>
                                  {notification.title}
                                </p>
                                <p className="text-sm text-muted-foreground mt-1">
                                  {notification.body}
                                </p>
                              </div>
                              {!notification.isRead && (
                                <div className="h-2 w-2 rounded-full bg-primary flex-shrink-0 mt-2" />
                              )}
                            </div>
                            <div className="flex items-center justify-between mt-3">
                              <span className="text-xs text-muted-foreground">{notification.createdAt}</span>
                              <div className="flex gap-1">
                                {!notification.isRead && (
                                  <Button
                                    variant="ghost"
                                    size="sm"
                                    className="h-7 text-xs"
                                    onClick={() => markAsRead(notification.id)}
                                  >
                                    <Check className="h-3 w-3 mr-1" />
                                    Marcar leída
                                  </Button>
                                )}
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="h-7 text-xs text-destructive hover:text-destructive"
                                  onClick={() => deleteNotification(notification.id)}
                                >
                                  <Trash2 className="h-3 w-3" />
                                </Button>
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </TabsContent>

                <TabsContent value="unread" className="mt-4">
                  <div className="space-y-2">
                    {notifications.filter(n => !n.isRead).length === 0 ? (
                      <div className="text-center py-8 text-muted-foreground">
                        <CheckCircle2 className="h-12 w-12 mx-auto mb-3 opacity-50" />
                        <p>No tienes notificaciones sin leer</p>
                      </div>
                    ) : (
                      notifications.filter(n => !n.isRead).map((notification) => {
                        const config = typeConfig[notification.type];
                        const Icon = config.icon;
                        return (
                          <div
                            key={notification.id}
                            className="flex gap-4 p-4 rounded-lg border bg-primary/5 border-primary/20"
                          >
                            <div className={cn('p-2 rounded-full h-fit', config.color)}>
                              <Icon className="h-4 w-4" />
                            </div>
                            <div className="flex-1 min-w-0">
                              <div className="flex items-start justify-between gap-2">
                                <div>
                                  <p className="font-medium">{notification.title}</p>
                                  <p className="text-sm text-muted-foreground mt-1">{notification.body}</p>
                                </div>
                                <div className="h-2 w-2 rounded-full bg-primary flex-shrink-0 mt-2" />
                              </div>
                              <div className="flex items-center justify-between mt-3">
                                <span className="text-xs text-muted-foreground">{notification.createdAt}</span>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="h-7 text-xs"
                                  onClick={() => markAsRead(notification.id)}
                                >
                                  <Check className="h-3 w-3 mr-1" />
                                  Marcar leída
                                </Button>
                              </div>
                            </div>
                          </div>
                        );
                      })
                    )}
                  </div>
                </TabsContent>
              </Tabs>
            </CardHeader>
          </Card>
        </div>
      </main>
    </div>
  );
}
