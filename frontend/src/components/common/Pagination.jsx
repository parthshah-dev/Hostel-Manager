import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const Pagination = ({ 
  currentPage = 1, 
  totalPages = 1, 
  onPageChange,
  className = '' 
}) => {
  if (totalPages <= 1) return null;

  return (
    <div className={`flex items-center justify-between px-4 py-3 bg-white border border-slate-200 border-t-0 rounded-b-xl ${className}`}>

      <div className="hidden sm:block">
        <p className="text-xs text-slate-500">
          Page <span className="font-semibold text-slate-800">{currentPage}</span> of{' '}
          <span className="font-semibold text-slate-800">{totalPages}</span>
        </p>
      </div>


      <div className="flex justify-between sm:justify-end gap-2 w-full sm:w-auto">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="inline-flex items-center gap-1 px-3 py-1.5 border border-slate-200 text-xs font-semibold rounded-lg text-slate-600 bg-white hover:bg-slate-50 transition-colors disabled:opacity-40 disabled:pointer-events-none"
        >
          <ChevronLeft className="w-4 h-4" />
          <span>Previous</span>
        </button>
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="inline-flex items-center gap-1 px-3 py-1.5 border border-slate-200 text-xs font-semibold rounded-lg text-slate-600 bg-white hover:bg-slate-50 transition-colors disabled:opacity-40 disabled:pointer-events-none"
        >
          <span>Next</span>
          <ChevronRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};

export default Pagination;
