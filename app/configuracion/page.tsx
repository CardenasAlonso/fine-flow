'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { 
  Building2, 
  Calendar, 
  Bell, 
  Shield, 
  Palette,
  Save,
  RefreshCw
} from 'lucide-react';
import { useAuth } from '@/lib/auth-context';

export default function ConfiguracionPage() {
  const { user } = useAuth();
  const [isSaving, setIsSaving] = useState(false);

  const handleSave = async () => {
    setIsSaving(true);
    await new Promise(resolve => setTimeout(resolve, 1000));
    setIsSaving(false);
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <div className="min-h-screen bg-background">
      <Sidebar />
      <Header />

      <main className="ml-64 pt-20 p-6 transition-all duration-300">
        <div className="max-w-4xl mx-auto">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-foreground">Configuración</h1>
            <p className="text-muted-foreground mt-1">
              Administra la configuración del sistema
            </p>
          </div>

          <Tabs defaultValue="general" className="space-y-6">
            <TabsList className="grid w-full grid-cols-4 lg:w-auto lg:inline-flex">
              <TabsTrigger value="general" className="gap-2">
                <Building2 className="h-4 w-4" />
                <span className="hidden sm:inline">General</span>
              </TabsTrigger>
              <TabsTrigger value="academic" className="gap-2">
                <Calendar className="h-4 w-4" />
                <span className="hidden sm:inline">Académico</span>
              </TabsTrigger>
              <TabsTrigger value="notifications" className="gap-2">
                <Bell className="h-4 w-4" />
                <span className="hidden sm:inline">Notificaciones</span>
              </TabsTrigger>
              <TabsTrigger value="appearance" className="gap-2">
                <Palette className="h-4 w-4" />
                <span className="hidden sm:inline">Apariencia</span>
              </TabsTrigger>
            </TabsList>

            {/* General Settings */}
            <TabsContent value="general">
              <Card>
                <CardHeader>
                  <CardTitle>Información de la Institución</CardTitle>
                  <CardDescription>
                    Datos generales de la institución educativa
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-2">
                      <Label htmlFor="schoolName">Nombre del Colegio</Label>
                      <Input 
                        id="schoolName" 
                        defaultValue="I.E. N° 20188" 
                        disabled={!isAdmin}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="modular">Código Modular</Label>
                      <Input 
                        id="modular" 
                        defaultValue="0285098" 
                        disabled={!isAdmin}
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="address">Dirección</Label>
                    <Input 
                      id="address" 
                      defaultValue="Av. Principal 123, San Vicente de Cañete" 
                      disabled={!isAdmin}
                    />
                  </div>
                  <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-2">
                      <Label htmlFor="phone">Teléfono</Label>
                      <Input 
                        id="phone" 
                        defaultValue="(01) 234-5678" 
                        disabled={!isAdmin}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="email">Correo Institucional</Label>
                      <Input 
                        id="email" 
                        defaultValue="contacto@ie20188.edu.pe" 
                        disabled={!isAdmin}
                      />
                    </div>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="director">Director(a)</Label>
                    <Input 
                      id="director" 
                      defaultValue="Mg. Patricia Rodríguez Vargas" 
                      disabled={!isAdmin}
                    />
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            {/* Academic Settings */}
            <TabsContent value="academic">
              <Card>
                <CardHeader>
                  <CardTitle>Configuración Académica</CardTitle>
                  <CardDescription>
                    Parámetros del año escolar y evaluación
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-2">
                      <Label>Año Académico Activo</Label>
                      <Select defaultValue="2025" disabled={!isAdmin}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="2025">2025</SelectItem>
                          <SelectItem value="2024">2024</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="space-y-2">
                      <Label>Período Actual</Label>
                      <Select defaultValue="bim1" disabled={!isAdmin}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="bim1">1er Bimestre</SelectItem>
                          <SelectItem value="bim2">2do Bimestre</SelectItem>
                          <SelectItem value="bim3">3er Bimestre</SelectItem>
                          <SelectItem value="bim4">4to Bimestre</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>

                  <Separator />

                  <div className="space-y-4">
                    <h4 className="font-medium">Sistema de Calificaciones</h4>
                    <div className="grid gap-4 md:grid-cols-2">
                      <div className="space-y-2">
                        <Label>Escala de Notas</Label>
                        <Select defaultValue="vigesimal" disabled={!isAdmin}>
                          <SelectTrigger>
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="vigesimal">Vigesimal (0-20)</SelectItem>
                            <SelectItem value="literal">Literal (AD, A, B, C)</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                      <div className="space-y-2">
                        <Label>Nota Mínima Aprobatoria</Label>
                        <Input 
                          type="number" 
                          defaultValue="11" 
                          min="0" 
                          max="20"
                          disabled={!isAdmin}
                        />
                      </div>
                    </div>
                  </div>

                  <Separator />

                  <div className="space-y-4">
                    <h4 className="font-medium">Asistencia</h4>
                    <div className="grid gap-4 md:grid-cols-2">
                      <div className="space-y-2">
                        <Label>Hora de Entrada</Label>
                        <Input type="time" defaultValue="07:45" disabled={!isAdmin} />
                      </div>
                      <div className="space-y-2">
                        <Label>Tolerancia (minutos)</Label>
                        <Input type="number" defaultValue="15" disabled={!isAdmin} />
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            {/* Notification Settings */}
            <TabsContent value="notifications">
              <Card>
                <CardHeader>
                  <CardTitle>Preferencias de Notificaciones</CardTitle>
                  <CardDescription>
                    Configura cómo y cuándo recibir notificaciones
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium">Notificaciones por Email</p>
                        <p className="text-sm text-muted-foreground">
                          Recibe resúmenes diarios por correo
                        </p>
                      </div>
                      <Switch defaultChecked />
                    </div>
                    <Separator />
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium">Alertas de Asistencia</p>
                        <p className="text-sm text-muted-foreground">
                          Notificar inasistencias y tardanzas
                        </p>
                      </div>
                      <Switch defaultChecked />
                    </div>
                    <Separator />
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium">Nuevas Calificaciones</p>
                        <p className="text-sm text-muted-foreground">
                          Notificar cuando se registren notas
                        </p>
                      </div>
                      <Switch defaultChecked />
                    </div>
                    <Separator />
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium">Avisos del Sistema</p>
                        <p className="text-sm text-muted-foreground">
                          Actualizaciones y mantenimiento
                        </p>
                      </div>
                      <Switch defaultChecked />
                    </div>
                    <Separator />
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-medium">Recordatorios de Evaluaciones</p>
                        <p className="text-sm text-muted-foreground">
                          Alertas antes de exámenes programados
                        </p>
                      </div>
                      <Switch defaultChecked />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            {/* Appearance Settings */}
            <TabsContent value="appearance">
              <Card>
                <CardHeader>
                  <CardTitle>Apariencia</CardTitle>
                  <CardDescription>
                    Personaliza la apariencia del sistema
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                  <div className="space-y-2">
                    <Label>Tema</Label>
                    <Select defaultValue="system">
                      <SelectTrigger className="w-48">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="light">Claro</SelectItem>
                        <SelectItem value="dark">Oscuro</SelectItem>
                        <SelectItem value="system">Sistema</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <Separator />

                  <div className="space-y-2">
                    <Label>Idioma</Label>
                    <Select defaultValue="es">
                      <SelectTrigger className="w-48">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="es">Español</SelectItem>
                        <SelectItem value="en">English</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <Separator />

                  <div className="flex items-center justify-between">
                    <div>
                      <p className="font-medium">Sidebar Compacto</p>
                      <p className="text-sm text-muted-foreground">
                        Mostrar solo iconos en la barra lateral
                      </p>
                    </div>
                    <Switch />
                  </div>
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>

          {/* Save Button */}
          <div className="flex justify-end gap-2 mt-6">
            <Button variant="outline" className="gap-2">
              <RefreshCw className="h-4 w-4" />
              Restaurar
            </Button>
            <Button onClick={handleSave} disabled={isSaving} className="gap-2">
              <Save className="h-4 w-4" />
              {isSaving ? 'Guardando...' : 'Guardar Cambios'}
            </Button>
          </div>
        </div>
      </main>
    </div>
  );
}
