import React, { forwardRef } from 'react';

const Input = forwardRef(({ 
  label, 
  name, 
  type = 'text', 
  placeholder = '', 
  error = '', 
  required = false,
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
      <input
        ref={ref}
        id={name}
        name={name}
        type={type}
        placeholder={placeholder}
        className={`px-3.5 py-2.5 bg-white border rounded-lg text-sm transition-all duration-200 outline-none focus:ring-2 placeholder:text-slate-400 ${
          error 
            ? 'border-red-300 focus:border-red-500 focus:ring-red-200 focus:ring-offset-0' 
            : 'border-slate-300 focus:border-blue-500 focus:ring-blue-100'
        }`}
        {...props}
      />
      {error && <span className="text-xs font-semibold text-red-500">{error}</span>}
    </div>
  );
});

Input.displayName = 'Input';

export default Input;
