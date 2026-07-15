import React from 'react';
import { AlertCircle } from 'lucide-react';
import Modal from './Modal';
import Button from './Button';

const ConfirmDialog = ({ 
  isOpen, 
  onClose, 
  onConfirm, 
  title = 'Are you sure?', 
  message = 'This action cannot be undone. Please confirm to proceed.',
  confirmLabel = 'Delete',
  cancelLabel = 'Cancel',
  variant = 'danger', 
  loading = false
}) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title} size="sm">
      <div className="flex flex-col gap-4">

        <div className="flex gap-3">
          <div className={`p-2 rounded-lg h-fit ${
            variant === 'danger' ? 'bg-red-50 text-red-600' : 'bg-blue-50 text-blue-600'
          }`}>
            <AlertCircle className="w-6 h-6" />
          </div>
          <div>
            <p className="text-sm text-slate-600 leading-normal">{message}</p>
          </div>
        </div>


        <div className="flex items-center justify-end gap-2.5 pt-2 border-t border-slate-100">
          <Button variant="secondary" size="sm" onClick={onClose} disabled={loading}>
            {cancelLabel}
          </Button>
          <Button 
            variant={variant} 
            size="sm" 
            onClick={onConfirm} 
            disabled={loading}
          >
            {loading ? 'Processing...' : confirmLabel}
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default ConfirmDialog;
