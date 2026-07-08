import React from 'react';
import { Search } from 'lucide-react';

const SearchBar = ({ 
  value = '', 
  onChange, 
  placeholder = 'Search...', 
  className = '',
  children
}) => {
  return (
    <div className={`flex flex-col sm:flex-row gap-3 w-full items-stretch sm:items-center ${className}`}>
      {/* Search Input */}
      <div className="relative flex-1">
        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
          <Search className="w-4 h-4" />
        </div>
        <input
          type="text"
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="w-full pl-10 pr-4 py-2 bg-white border border-slate-200 rounded-lg text-sm transition-all focus:border-blue-500 focus:ring-2 focus:ring-blue-500/10 outline-none placeholder:text-slate-400"
        />
      </div>

      {/* Extra Filters / Dropdowns */}
      {children && (
        <div className="flex flex-wrap items-center gap-2">
          {children}
        </div>
      )}
    </div>
  );
};

export default SearchBar;
