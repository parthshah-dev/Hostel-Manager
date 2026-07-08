import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Menu, User, Calendar, Bell } from 'lucide-react';

const Navbar = ({ toggleSidebar }) => {
  const { user } = useAuth();
  
  // Format current date
  const formattedDate = new Date().toLocaleDateString('en-US', {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  return (
    <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-6 md:px-8 sticky top-0 z-10">
      {/* Mobile Toggle & Greetings */}
      <div className="flex items-center gap-3">
        <button 
          onClick={toggleSidebar} 
          className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 hover:text-slate-700 md:hidden transition-colors"
        >
          <Menu className="w-5.5 h-5.5" />
        </button>
        <div className="hidden sm:block">
          <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider leading-none">Management Portal</p>
          <h2 className="text-sm font-bold text-slate-700 mt-0.5">Admin Workspace</h2>
        </div>
      </div>

      {/* Right side items */}
      <div className="flex items-center gap-4 md:gap-6">
        {/* Current Date Display */}
        <div className="hidden lg:flex items-center gap-2 text-slate-500 bg-slate-50 border border-slate-100 px-3.5 py-1.5 rounded-full text-xs font-medium">
          <Calendar className="w-4 h-4 text-blue-600" />
          <span>{formattedDate}</span>
        </div>

        {/* Action Button */}
        <button className="text-slate-400 hover:text-slate-600 relative p-1.5 rounded-lg hover:bg-slate-100 transition-colors">
          <Bell className="w-5 h-5" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-blue-600 rounded-full ring-2 ring-white"></span>
        </button>

        {/* Divider */}
        <div className="w-px h-6 bg-slate-200"></div>

        {/* User profile dropdown or display */}
        <div className="flex items-center gap-3">
          <div className="bg-blue-50 border border-blue-100 text-blue-700 w-9 h-9 rounded-xl flex items-center justify-center font-bold text-sm shadow-inner">
            {user?.email ? user.email.charAt(0).toUpperCase() : 'A'}
          </div>
          <div className="text-left hidden md:block">
            <p className="text-xs font-bold text-slate-800 leading-none">{user?.email?.split('@')[0]}</p>
            <span className="text-[9px] text-slate-400 font-bold uppercase tracking-wider">
              {user?.role?.replace('ROLE_', '') || 'Administrator'}
            </span>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
