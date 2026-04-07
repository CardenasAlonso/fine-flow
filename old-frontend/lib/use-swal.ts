'use client';

import { toast } from 'sonner';
import { create } from 'zustand';

interface AlertState {
  isOpen: boolean;
  title: string;
  text: string;
  icon: 'success' | 'error' | 'warning' | 'info' | 'question';
  showCancelButton: boolean;
  confirmButtonText: string;
  cancelButtonText: string;
  onConfirm: (() => void) | null;
  onCancel: (() => void) | null;
  openAlert: (options: AlertOptions) => Promise<{ isConfirmed: boolean }>;
  closeAlert: (confirmed: boolean) => void;
}

interface AlertOptions {
  title?: string;
  text?: string;
  html?: string;
  icon?: 'success' | 'error' | 'warning' | 'info' | 'question';
  timer?: number;
  showConfirmButton?: boolean;
  showCancelButton?: boolean;
  confirmButtonText?: string;
  cancelButtonText?: string;
  confirmButtonColor?: string;
  cancelButtonColor?: string;
  position?: string;
  toast?: boolean;
}

let resolvePromise: ((value: { isConfirmed: boolean }) => void) | null = null;

export const useAlertStore = create<AlertState>((set) => ({
  isOpen: false,
  title: '',
  text: '',
  icon: 'info',
  showCancelButton: false,
  confirmButtonText: 'OK',
  cancelButtonText: 'Cancelar',
  onConfirm: null,
  onCancel: null,
  openAlert: (options) => {
    return new Promise((resolve) => {
      resolvePromise = resolve;
      set({
        isOpen: true,
        title: options.title || '',
        text: options.text || options.html || '',
        icon: options.icon || 'info',
        showCancelButton: options.showCancelButton || false,
        confirmButtonText: options.confirmButtonText || 'OK',
        cancelButtonText: options.cancelButtonText || 'Cancelar',
      });

      // Auto-close timer
      if (options.timer) {
        setTimeout(() => {
          set({ isOpen: false });
          if (resolvePromise) {
            resolvePromise({ isConfirmed: false });
            resolvePromise = null;
          }
        }, options.timer);
      }
    });
  },
  closeAlert: (confirmed) => {
    set({ isOpen: false });
    if (resolvePromise) {
      resolvePromise({ isConfirmed: confirmed });
      resolvePromise = null;
    }
  },
}));

// SweetAlert2-like API using sonner for toasts and zustand for modals
export function useSwal() {
  const alertStore = useAlertStore();

  const fire = async (options: AlertOptions): Promise<{ isConfirmed: boolean; value?: string }> => {
    // Use toast for quick notifications
    if (options.toast || (options.timer && options.timer <= 3000 && !options.showCancelButton)) {
      const toastOptions = {
        duration: options.timer || 3000,
      };

      switch (options.icon) {
        case 'success':
          toast.success(options.title || '', {
            description: options.text || options.html,
            ...toastOptions,
          });
          break;
        case 'error':
          toast.error(options.title || '', {
            description: options.text || options.html,
            ...toastOptions,
          });
          break;
        case 'warning':
          toast.warning(options.title || '', {
            description: options.text || options.html,
            ...toastOptions,
          });
          break;
        case 'info':
        case 'question':
        default:
          toast.info(options.title || '', {
            description: options.text || options.html,
            ...toastOptions,
          });
          break;
      }
      return { isConfirmed: true };
    }

    // Use AlertDialog for confirmations
    return alertStore.openAlert(options);
  };

  const showLoading = () => {
    toast.loading('Cargando...');
  };

  return {
    fire,
    showLoading,
    isReady: true,
    Swal: { fire, showLoading },
  };
}
