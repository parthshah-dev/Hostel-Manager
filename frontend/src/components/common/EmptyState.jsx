import React from 'react';
import { Inbox } from 'lucide-react';
import Button from './Button';

const EmptyState = ({ 
  title = 'No data found', 
  description = 'There are no items to display at the moment.', 
  actionLabel, 
  onAction,
  icon: Icon = Inbox
}) => {
  return (
    <div className="flex flex-col items-center justify-center text-center py-12 px-4 border border-dashed border-slate-200 rounded-xl bg-slate-50/50">
      <div className="bg-slate-100 p-4 rounded-full text-slate-400 mb-4">
        <Icon className="w-8 h-8" />
      </div>
      <h3 className="text-sm font-bold text-slate-800 tracking-tight">{title}</h3>
      <p className="text-xs text-slate-400 max-w-xs mt-1 mb-6 leading-normal">{description}</p>
      {actionLabel && onAction && (
        <Button variant="primary" size="sm" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
};

export default EmptyState;
