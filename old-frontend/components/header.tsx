'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Bell, Search, ChevronDown, LogOut, User, Settings, RefreshCw, Building, CalendarCheck } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { useAuth } from '@/lib/auth-context';
import type { UserRole } from '@/lib/types';
import { cn } from '@/lib/utils';

const roleLabels: Record<UserRole, string> = {
  ADMIN: 'Administrador',
  COORDINATOR: 'Coordinador',
  TEACHER: 'Docente',
  STUDENT: 'Alumno',
  GUARDIAN: 'Apoderado',
};

const roleColors: Record<UserRole, string> = {
  ADMIN: 'bg-red-500/10 text-red-600',
  COORDINATOR: 'bg-blue-500/10 text-blue-600',
  TEACHER: 'bg-green-500/10 text-green-600',
  STUDENT: 'bg-amber-500/10 text-amber-600',
  GUARDIAN: 'bg-purple-500/10 text-purple-600',
};

interface HeaderProps {
  sidebarCollapsed?: boolean;
}

export function Header({ sidebarCollapsed }: HeaderProps) {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState('');
  const { user, school, subscription, currentPeriod, currentSchoolYear, setRole, logout, hasFeature } = useAuth();

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  const allRoles: UserRole[] = ['ADMIN', 'COORDINATOR', 'TEACHER', 'STUDENT', 'GUARDIAN'];

  // Get plan badge color
  const planBadgeColor = {
    FREE: 'bg-gray-500/10 text-gray-600',
    BASIC: 'bg-blue-500/10 text-blue-600',
    STANDARD: 'bg-green-500/10 text-green-600',
    PREMIUM: 'bg-amber-500/10 text-amber-600',
    ENTERPRISE: 'bg-purple-500/10 text-purple-600',
  }[subscription?.plan_name ?? 'FREE'];

  return (
    <header 
      className={cn(
        'fixed top-0 right-0 z-30 bg-background border-b border-border h-16 flex items-center justify-between px-6 transition-all duration-300',
        sidebarCollapsed ? 'left-20' : 'left-64'
      )}
    >
      <div className="flex items-center gap-4">
        {/* Current Period Display */}
        <div className="flex items-center gap-2">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" className="gap-2">
                <CalendarCheck size={16} className="text-muted-foreground" />
                {currentPeriod?.name ?? 'Sin periodo'} - {currentSchoolYear?.calendar_year ?? new Date().getFullYear()}
                <ChevronDown size={16} />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="start" className="w-56">
              <DropdownMenuLabel>Periodo Academico Actual</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <div className="px-2 py-1.5 text-sm">
                <p className="font-medium">{currentSchoolYear?.name}</p>
                <p className="text-xs text-muted-foreground mt-1">
                  {currentPeriod?.period_type === 'BIMESTER' && 'Bimestre'}
                  {currentPeriod?.period_type === 'TRIMESTER' && 'Trimestre'}
                  {currentPeriod?.period_type === 'SEMESTER' && 'Semestre'}
                  {' '}{currentPeriod?.name}
                </p>
                <p className="text-xs text-muted-foreground">
                  {currentPeriod?.start_date && new Date(currentPeriod.start_date).toLocaleDateString('es-PE')}
                  {' - '}
                  {currentPeriod?.end_date && new Date(currentPeriod.end_date).toLocaleDateString('es-PE')}
                </p>
              </div>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        {/* Role Badge */}
        {user && (
          <Badge 
            variant="secondary" 
            className={cn('hidden sm:inline-flex', roleColors[user.role])}
          >
            {roleLabels[user.role]}
          </Badge>
        )}

        {/* Plan Badge - Admin only */}
        {user?.role === 'ADMIN' && subscription && (
          <Badge 
            variant="secondary" 
            className={cn('hidden md:inline-flex', planBadgeColor)}
          >
            {subscription.plan_name}
            {subscription.is_trial && ' (Trial)'}
          </Badge>
        )}
      </div>

      <div className="flex items-center gap-4">
        {/* Search */}
        <div className="relative w-64 hidden lg:block">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Buscar alumno, profesor..."
            className="pl-8 bg-muted"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        {/* Role Switcher - For Demo */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm" className="gap-2 hidden md:flex">
              <RefreshCw size={14} />
              Cambiar Rol
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>Cambiar vista de rol</DropdownMenuLabel>
            <DropdownMenuSeparator />
            {allRoles.map((role) => (
              <DropdownMenuItem 
                key={role} 
                onClick={() => setRole(role)}
                className={cn(user?.role === role && 'bg-muted')}
              >
                <span className={cn('w-2 h-2 rounded-full mr-2', user?.role === role ? 'bg-primary' : 'bg-muted-foreground/30')} />
                {roleLabels[role]}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Notifications */}
        <Button 
          variant="ghost" 
          size="icon" 
          className="relative"
          onClick={() => router.push('/notificaciones')}
        >
          <Bell size={20} />
          <Badge className="absolute -top-1 -right-1 h-5 w-5 p-0 flex items-center justify-center bg-destructive text-destructive-foreground">
            3
          </Badge>
        </Button>

        {/* User Menu */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <button className="flex items-center gap-2 hover:opacity-80 transition-opacity">
              <Avatar className="h-8 w-8">
                <AvatarImage src={user?.avatar_url ?? undefined} />
                <AvatarFallback>
                  {user?.first_name?.charAt(0)}{user?.last_name?.charAt(0)}
                </AvatarFallback>
              </Avatar>
              <div className="hidden sm:block text-left">
                <p className="text-sm font-medium">{user?.first_name} {user?.last_name}</p>
                <p className="text-xs text-muted-foreground">{user?.email}</p>
              </div>
              <ChevronDown size={16} className="hidden sm:block" />
            </button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel>Mi Cuenta</DropdownMenuLabel>
            {school && (
              <>
                <DropdownMenuSeparator />
                <div className="px-2 py-1.5 text-xs text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Building size={12} />
                    {school.name}
                  </div>
                  <div className="mt-0.5">
                    Tipo: {school.institution_type === 'K12' ? 'Colegio' : school.institution_type === 'INSTITUTE' ? 'Instituto' : 'Universidad'}
                  </div>
                </div>
              </>
            )}
            <DropdownMenuSeparator />
            <DropdownMenuItem>
              <User className="mr-2 h-4 w-4" />
              Perfil
            </DropdownMenuItem>
            <DropdownMenuItem>
              <Settings className="mr-2 h-4 w-4" />
              Configuracion
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem className="text-destructive" onClick={handleLogout}>
              <LogOut className="mr-2 h-4 w-4" />
              Cerrar Sesion
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
