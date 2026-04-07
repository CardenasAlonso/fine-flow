'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import { useAuth } from '@/lib/auth-context';
import { demoUsers } from '@/lib/demo-users';
import type { UserRole } from '@/lib/types';
import { 
  GraduationCap, 
  Eye, 
  EyeOff, 
  LogIn,
  Shield,
  Users,
  BookOpen,
  User,
  UserCheck
} from 'lucide-react';

const roleIcons: Record<UserRole, React.ReactNode> = {
  ADMIN: <Shield className="h-4 w-4" />,
  COORDINATOR: <Users className="h-4 w-4" />,
  TEACHER: <BookOpen className="h-4 w-4" />,
  STUDENT: <GraduationCap className="h-4 w-4" />,
  GUARDIAN: <UserCheck className="h-4 w-4" />,
};

const roleColors: Record<UserRole, string> = {
  ADMIN: 'bg-red-100 text-red-800 dark:bg-red-950 dark:text-red-300',
  COORDINATOR: 'bg-blue-100 text-blue-800 dark:bg-blue-950 dark:text-blue-300',
  TEACHER: 'bg-green-100 text-green-800 dark:bg-green-950 dark:text-green-300',
  STUDENT: 'bg-purple-100 text-purple-800 dark:bg-purple-950 dark:text-purple-300',
  GUARDIAN: 'bg-orange-100 text-orange-800 dark:bg-orange-950 dark:text-orange-300',
};

const roleDescriptions: Record<UserRole, string> = {
  ADMIN: 'Acceso completo al sistema',
  COORDINATOR: 'Gestión académica y supervisión',
  TEACHER: 'Asistencia y calificaciones',
  STUDENT: 'Consulta de notas y horarios',
  GUARDIAN: 'Seguimiento del alumno',
};

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    
    // Simulate login delay
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // For demo, find user by email or use admin
    const user = demoUsers.find(u => u.email.toLowerCase() === email.toLowerCase()) || demoUsers[0];
    login(user);
    router.push('/');
    
    setIsLoading(false);
  };

  const handleDemoLogin = async (userId: string) => {
    setIsLoading(true);
    await new Promise(resolve => setTimeout(resolve, 300));
    
    const user = demoUsers.find(u => u.id === userId);
    if (user) {
      login(user);
      router.push('/');
    }
    setIsLoading(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/5 via-background to-accent/5 flex items-center justify-center p-4">
      <div className="w-full max-w-4xl">
        {/* Logo and Title */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-3 mb-4">
            <div className="w-12 h-12 bg-primary rounded-lg flex items-center justify-center">
              <GraduationCap className="h-7 w-7 text-primary-foreground" />
            </div>
            <span className="text-3xl font-bold text-foreground">Fine Flow</span>
          </div>
          <p className="text-muted-foreground">Sistema de Gestión Educativa</p>
        </div>

        <Tabs defaultValue="demo" className="w-full">
          <TabsList className="grid w-full max-w-md mx-auto grid-cols-2 mb-6">
            <TabsTrigger value="demo">Demo Rápido</TabsTrigger>
            <TabsTrigger value="login">Iniciar Sesión</TabsTrigger>
          </TabsList>

          {/* Demo Quick Access */}
          <TabsContent value="demo">
            <Card>
              <CardHeader className="text-center">
                <CardTitle>Acceso Demo</CardTitle>
                <CardDescription>
                  Selecciona un rol para explorar el sistema con datos de ejemplo
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                  {demoUsers.map((user) => (
                    <button
                      key={user.id}
                      onClick={() => handleDemoLogin(user.id)}
                      disabled={isLoading}
                      className="flex flex-col items-center gap-3 p-4 rounded-lg border bg-card hover:bg-accent/50 transition-colors text-left disabled:opacity-50"
                    >
                      <Avatar className="h-16 w-16">
                        <AvatarImage src={user.avatar_url || undefined} />
                        <AvatarFallback className="text-lg">
                          {user.first_name[0]}{user.last_name[0]}
                        </AvatarFallback>
                      </Avatar>
                      <div className="text-center">
                        <p className="font-medium">{user.first_name} {user.last_name}</p>
                        <p className="text-xs text-muted-foreground">{user.email}</p>
                      </div>
                      <Badge className={roleColors[user.role]}>
                        <span className="mr-1">{roleIcons[user.role]}</span>
                        {user.role}
                      </Badge>
                      <p className="text-xs text-muted-foreground text-center">
                        {roleDescriptions[user.role]}
                      </p>
                    </button>
                  ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Traditional Login */}
          <TabsContent value="login">
            <Card className="max-w-md mx-auto">
              <CardHeader className="text-center">
                <CardTitle>Iniciar Sesión</CardTitle>
                <CardDescription>
                  Ingresa tus credenciales para acceder al sistema
                </CardDescription>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleLogin} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="email">Correo Electrónico</Label>
                    <Input
                      id="email"
                      type="email"
                      placeholder="usuario@ie20188.edu.pe"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="password">Contraseña</Label>
                    <div className="relative">
                      <Input
                        id="password"
                        type={showPassword ? 'text' : 'password'}
                        placeholder="••••••••"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                      />
                      <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                      >
                        {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                      </button>
                    </div>
                  </div>
                  <Button type="submit" className="w-full gap-2" disabled={isLoading}>
                    <LogIn className="h-4 w-4" />
                    {isLoading ? 'Ingresando...' : 'Ingresar'}
                  </Button>
                </form>

                <Separator className="my-6" />

                <div className="text-center text-sm text-muted-foreground">
                  <p>Para la demo, puedes usar cualquier credencial.</p>
                  <p className="mt-1">O utiliza la pestaña &quot;Demo Rápido&quot; para acceso inmediato.</p>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

        {/* Footer */}
        <p className="text-center text-sm text-muted-foreground mt-8">
          I.E. N° 20188 - San Vicente de Cañete
        </p>
      </div>
    </div>
  );
}
