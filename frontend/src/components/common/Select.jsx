import React, { forwardRef } from 'react';

const Select = forwardRef(({ 
  label, 
  name, 
  options = [], 
  error = '', 
  required = false,
  placeholder = 'Select an option',
  className = '',
  ...props 
}, ref) => {
  return (
    <div className={`flex flex-col gap-1.5 w-full ${className}`}>
      {label && (
        <label htmlFor={name} className="text-xs font-bold text-slate-700 select-none">
          {label} {required && <span className="text-red-500">*</span>}
        </label>
      )}
      <div className="relative">
        <select
          ref={ref}
          id={name}
          name={name}
          className={`w-full px-3.5 py-2.5 bg-white border rounded-lg text-sm transition-all duration-200 outline-none appearance-none focus:ring-2 ${
            error 
              ? 'border-red-300 focus:border-red-500 focus:ring-red-200' 
              : 'border-slate-300 focus:border-blue-500 focus:ring-blue-100'
          }`}
          {...props}
        >
          {placeholder && <option value="">{placeholder}</option>}
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-slate-500">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
          </svg>
        </div>
      </div>
      {error && <span className="text-xs font-semibold text-red-500">{error}</span>}
    </div>
  );
});

Select.displayName = 'Select';

export default Select;
