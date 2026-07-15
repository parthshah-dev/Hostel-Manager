import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import Footer from './Footer';

const MainLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex">

      <Sidebar isOpen={sidebarOpen} toggleSidebar={toggleSidebar} />


      <div className="flex-1 flex flex-col md:pl-64 min-h-screen">

        <Navbar toggleSidebar={toggleSidebar} />


        <main className="flex-1 p-6 md:p-8">
          <div className="max-w-7xl mx-auto">
            <Outlet />
          </div>
        </main>


        <Footer />
      </div>
    </div>
  );
};

export default MainLayout;
