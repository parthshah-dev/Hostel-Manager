import React from 'react';

const StatusBadge = ({ status }) => {
  if (!status) return null;

  const normalized = status.toUpperCase();

  const config = {

    AVAILABLE: { bg: 'bg-green-50 text-green-700 border-green-200', label: 'Available' },
    OCCUPIED: { bg: 'bg-amber-50 text-amber-700 border-amber-200', label: 'Occupied' },
    ACTIVE: { bg: 'bg-green-50 text-green-700 border-green-200', label: 'Active' },
    INACTIVE: { bg: 'bg-slate-50 text-slate-500 border-slate-200', label: 'Inactive' },


    PAID: { bg: 'bg-green-50 text-green-700 border-green-200', label: 'Paid' },
    PENDING: { bg: 'bg-red-50 text-red-700 border-red-200', label: 'Pending' },


    OPEN: { bg: 'bg-red-50 text-red-700 border-red-200', label: 'Open' },
    IN_PROGRESS: { bg: 'bg-blue-50 text-blue-700 border-blue-200', label: 'In Progress' },
    RESOLVED: { bg: 'bg-green-50 text-green-700 border-green-200', label: 'Resolved' },
  };

  const item = config[normalized] || { bg: 'bg-slate-50 text-slate-600 border-slate-200', label: status };

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${item.bg}`}>
      {item.label}
    </span>
  );
};

export default StatusBadge;
