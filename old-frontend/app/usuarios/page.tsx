'use client';

import { useState } from 'react';
import { Sidebar } from '@/components/sidebar';
import { Header } from '@/components/header';
import { useAuth } from '@/lib/auth-context';
import { useSwal } from '@/lib/use-swal';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
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
import { Label } from '@/components/ui/label';
import { Plus, Search, Edit, Trash2, Shield, UserCog, Key, MoreVertical } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

const mockUsers = [
  { id: 1, name: 'Carlos Mendoza', email: 'carlos.mendoza@fineflow.edu.pe', role: 'ADMIN', status: 'ACTIVE', lastLogin: '2025-03-28 08:45' },
  { id: 2, name: 'María García', email: 'maria.garcia@fineflow.edu.pe', role: 'COORDINATOR', status: 'ACTIVE', lastLogin: '2025-03-28 07:30' },
  { id: 3, name: 'José Pérez', email: 'jose.perez@fineflow.edu.pe', role: 'TEACHER', status: 'ACTIVE', lastLogin: '2025-03-27 16:20' },
  { id: 4, name: 'Ana López', email: 'ana.lopez@fineflow.edu.pe', role: 'TEACHER', status: 'ACTIVE', lastLogin: '2025-03-28 09:00' },
  { id: 5, name: 'Pedro Quispe', email: 'pedro.quispe@fineflow.edu.pe', role: 'TEACHER', status: 'INACTIVE', lastLogin: '2025-02-15 11:30' },
  { id: 6, name: 'Luis Chávez', email: 'luis.chavez@fineflow.edu.pe', role: 'STUDENT', status: 'ACTIVE', lastLogin: '2025-03-28 07:50' },
  { id: 7, name: 'Rosa Huamán', email: 'rosa.huaman@fineflow.edu.pe', role: 'GUARDIAN', status: 'ACTIVE', lastLogin: '2025-03-26 19:15' },
];

const roleLabels: Record<string, string> = {
  ADMIN: 'Administrador',
  COORDINATOR: 'Coordinador',
  TEACHER: 'Docente',
  STUDENT: 'Alumno',
  GUARDIAN: 'Apoderado',
};

const roleColors: Record<string, string> = {
  ADMIN: 'bg-red-500/10 text-red-600',
  COORDINATOR: 'bg-purple-500/10 text-purple-600',
  TEACHER: 'bg-blue-500/10 text-blue-600',
  STUDENT: 'bg-green-500/10 text-green-600',
  GUARDIAN: 'bg-amber-500/10 text-amber-600',
};

export default function UsuariosPage() {
  const { user } = useAuth();
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedRole, setSelectedRole] = useState('all');
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [users, setUsers] = useState(mockUsers);
  const { Swal } = useSwal();

  const filteredUsers = users.filter(u => {
    const matchesSearch = u.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      u.email.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesRole = selectedRole === 'all' || u.role === selectedRole;
    return matchesSearch && matchesRole;
  });

  const handleDelete = async (userId: number, userName: string) => {
    if (!Swal) return;
    
    const result = await Swal.fire({
      title: '¿Eliminar usuario?',
      text: `¿Estás seguro de eliminar al usuario "${userName}"? Esta acción no se puede deshacer.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      setUsers(users.filter(u => u.id !== userId));
      Swal.fire({
        title: '¡Eliminado!',
        text: 'El usuario ha sido eliminado del sistema.',
        icon: 'success',
        timer: 2000,
        showConfirmButton: false
      });
    }
  };

  const handleResetPassword = async (userName: string) => {
    if (!Swal) return;

    const result = await Swal.fire({
      title: 'Restablecer Contraseña',
      text: `¿Deseas restablecer la contraseña de "${userName}"? Se enviará un enlace al correo del usuario.`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Sí, restablecer',
      cancelButtonText: 'Cancelar'
    });

    if (result.isConfirmed) {
      Swal.fire({
        title: '¡Enlace enviado!',
        text: 'Se ha enviado un enlace de restablecimiento al correo del usuario.',
        icon: 'success',
        timer: 2000,
        showConfirmButton: false
      });
    }
  };

  const handleAddUser = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!Swal) return;

    const formData = new FormData(e.currentTarget);
    const newUser = {
      id: users.length + 1,
      name: formData.get('name') as string,
      email: formData.get('email') as string,
      role: formData.get('role') as string,
      status: 'ACTIVE',
      lastLogin: '-',
    };

    setUsers([...users, newUser]);
    setIsDialogOpen(false);
    
    await Swal.fire({
      title: '¡Usuario creado!',
      text: `El usuario "${newUser.name}" ha sido registrado exitosamente. Se enviará un correo con las credenciales.`,
      icon: 'success',
      timer: 3000,
      showConfirmButton: false
    });
  };

  if (!user || user.role !== 'ADMIN') {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Card className="p-6">
          <p className="text-muted-foreground">No tienes permisos para acceder a esta página.</p>
        </Card>
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
              <h1 className="text-2xl font-bold text-foreground">Gestión de Usuarios</h1>
              <p className="text-muted-foreground">Administra los usuarios y permisos del sistema</p>
            </div>
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="gap-2">
                  <Plus size={20} />
                  Nuevo Usuario
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-md">
                <DialogHeader>
                  <DialogTitle>Registrar Nuevo Usuario</DialogTitle>
                </DialogHeader>
                <form onSubmit={handleAddUser} className="space-y-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="name">Nombre Completo</Label>
                    <Input id="name" name="name" placeholder="Ej: Juan Pérez" required />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="email">Correo Electrónico</Label>
                    <Input id="email" name="email" type="email" placeholder="correo@fineflow.edu.pe" required />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="role">Rol del Usuario</Label>
                    <Select name="role" required>
                      <SelectTrigger>
                        <SelectValue placeholder="Seleccionar rol" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="ADMIN">Administrador</SelectItem>
                        <SelectItem value="COORDINATOR">Coordinador</SelectItem>
                        <SelectItem value="TEACHER">Docente</SelectItem>
                        <SelectItem value="STUDENT">Alumno</SelectItem>
                        <SelectItem value="GUARDIAN">Apoderado</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="flex justify-end gap-2 pt-4">
                    <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                      Cancelar
                    </Button>
                    <Button type="submit">Crear Usuario</Button>
                  </div>
                </form>
              </DialogContent>
            </Dialog>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 mb-6">
            <Card>
              <CardContent className="p-4">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <UserCog className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold">{users.length}</p>
                    <p className="text-sm text-muted-foreground">Total Usuarios</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            {['ADMIN', 'COORDINATOR', 'TEACHER', 'STUDENT'].map(role => (
              <Card key={role}>
                <CardContent className="p-4">
                  <div className="flex items-center gap-3">
                    <div className={`p-2 rounded-lg ${roleColors[role].replace('text-', 'bg-').replace('600', '500/10')}`}>
                      <Shield className={`h-5 w-5 ${roleColors[role].split(' ')[1]}`} />
                    </div>
                    <div>
                      <p className="text-2xl font-bold">{users.filter(u => u.role === role).length}</p>
                      <p className="text-sm text-muted-foreground">{roleLabels[role]}s</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Filters */}
          <Card className="mb-6">
            <CardContent className="p-4">
              <div className="flex flex-col sm:flex-row gap-4">
                <div className="relative flex-1">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Buscar por nombre o correo..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10"
                  />
                </div>
                <Select value={selectedRole} onValueChange={setSelectedRole}>
                  <SelectTrigger className="w-full sm:w-48">
                    <SelectValue placeholder="Filtrar por rol" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">Todos los roles</SelectItem>
                    <SelectItem value="ADMIN">Administrador</SelectItem>
                    <SelectItem value="COORDINATOR">Coordinador</SelectItem>
                    <SelectItem value="TEACHER">Docente</SelectItem>
                    <SelectItem value="STUDENT">Alumno</SelectItem>
                    <SelectItem value="GUARDIAN">Apoderado</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
          </Card>

          {/* Table */}
          <Card>
            <CardHeader>
              <CardTitle>Lista de Usuarios</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Usuario</TableHead>
                      <TableHead>Correo</TableHead>
                      <TableHead>Rol</TableHead>
                      <TableHead>Estado</TableHead>
                      <TableHead>Último Acceso</TableHead>
                      <TableHead className="text-right">Acciones</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredUsers.map((u) => (
                      <TableRow key={u.id}>
                        <TableCell>
                          <div className="flex items-center gap-3">
                            <Avatar className="h-8 w-8">
                              <AvatarFallback className="text-xs bg-primary/10 text-primary">
                                {u.name.split(' ').map(n => n[0]).join('')}
                              </AvatarFallback>
                            </Avatar>
                            <span className="font-medium">{u.name}</span>
                          </div>
                        </TableCell>
                        <TableCell className="text-muted-foreground">{u.email}</TableCell>
                        <TableCell>
                          <Badge className={roleColors[u.role]}>
                            {roleLabels[u.role]}
                          </Badge>
                        </TableCell>
                        <TableCell>
                          <Badge className={u.status === 'ACTIVE' ? 'bg-green-500/10 text-green-600' : 'bg-red-500/10 text-red-600'}>
                            {u.status === 'ACTIVE' ? 'Activo' : 'Inactivo'}
                          </Badge>
                        </TableCell>
                        <TableCell className="text-muted-foreground text-sm">{u.lastLogin}</TableCell>
                        <TableCell className="text-right">
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" size="icon">
                                <MoreVertical className="h-4 w-4" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                              <DropdownMenuItem className="gap-2">
                                <Edit className="h-4 w-4" />
                                Editar Usuario
                              </DropdownMenuItem>
                              <DropdownMenuItem className="gap-2" onClick={() => handleResetPassword(u.name)}>
                                <Key className="h-4 w-4" />
                                Restablecer Contraseña
                              </DropdownMenuItem>
                              <DropdownMenuItem className="gap-2 text-destructive" onClick={() => handleDelete(u.id, u.name)}>
                                <Trash2 className="h-4 w-4" />
                                Eliminar Usuario
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </main>
      </div>
    </div>
  );
}
