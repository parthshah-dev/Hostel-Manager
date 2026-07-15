import React from 'react';

const Loader = ({ 
  type = 'spinner', 
  rows = 3, 
  className = '' 
}) => {
  if (type === 'full') {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-white/70 backdrop-blur-xs">
        <div className="flex flex-col items-center gap-3">
          <div className="w-10 h-10 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-semibold text-slate-500">Processing request...</p>
        </div>
      </div>
    );
  }

  if (type === 'skeleton') {
    return (
      <div className={`space-y-3 animate-pulse ${className}`}>
        {Array.from({ length: rows }).map((_, index) => (
          <div key={index} className="h-4 bg-slate-200 rounded-md w-full" />
        ))}
      </div>
    );
  }

  return (
    <div className={`flex justify-center items-center py-8 ${className}`}>
      <div className="w-8 h-8 border-3 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
    </div>
  );
};

export default Loader;
