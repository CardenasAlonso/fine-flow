'use client';

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { useAlertStore } from '@/lib/use-swal';
import { CheckCircle2, XCircle, AlertTriangle, Info, HelpCircle } from 'lucide-react';

const iconMap = {
  success: CheckCircle2,
  error: XCircle,
  warning: AlertTriangle,
  info: Info,
  question: HelpCircle,
};

const iconColorMap = {
  success: 'text-green-500',
  error: 'text-red-500',
  warning: 'text-amber-500',
  info: 'text-blue-500',
  question: 'text-primary',
};

export function AlertProvider() {
  const { isOpen, title, text, icon, showCancelButton, confirmButtonText, cancelButtonText, closeAlert } = useAlertStore();
  
  const IconComponent = iconMap[icon] || Info;
  const iconColor = iconColorMap[icon] || 'text-primary';

  return (
    <AlertDialog open={isOpen} onOpenChange={(open) => !open && closeAlert(false)}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <div className="flex items-center gap-3">
            <div className={`p-2 rounded-full bg-muted ${iconColor}`}>
              <IconComponent className="h-6 w-6" />
            </div>
            <AlertDialogTitle>{title}</AlertDialogTitle>
          </div>
          {text && (
            <AlertDialogDescription className="pt-2">
              <span dangerouslySetInnerHTML={{ __html: text }} />
            </AlertDialogDescription>
          )}
        </AlertDialogHeader>
        <AlertDialogFooter>
          {showCancelButton && (
            <AlertDialogCancel onClick={() => closeAlert(false)}>
              {cancelButtonText}
            </AlertDialogCancel>
          )}
          <AlertDialogAction onClick={() => closeAlert(true)}>
            {confirmButtonText}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
