import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/layout/ProtectedRoute';
import MainLayout from './components/layout/MainLayout';


import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import Activate from './pages/auth/Activate';
import Dashboard from './pages/dashboard/Dashboard';
import RoomList from './pages/rooms/RoomList';
import RoomForm from './pages/rooms/RoomForm';
import BedsAvailableList from './pages/rooms/BedsAvailableList';
import TenantList from './pages/tenants/TenantList';
import TenantForm from './pages/tenants/TenantForm';
import RentList from './pages/rents/RentList';
import RentHistory from './pages/rents/RentHistory';
import ComplaintList from './pages/complaints/ComplaintList';
import ChangePassword from './pages/profile/ChangePassword';


import NotFound from './pages/error/NotFound';
import Unauthorized from './pages/error/Unauthorized';

const App = () => {
  return (
    <AuthProvider>
      <BrowserRouter>

        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: '#ffffff',
              color: '#334155',
              border: '1px solid #e2e8f0',
              borderRadius: '12px',
              fontSize: '13px',
              fontWeight: '500',
              padding: '12px 16px',
            },
            success: {
              iconTheme: {
                primary: '#2563eb',
                secondary: '#ffffff',
              },
            },
          }}
        />

        <Routes>

          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/activate" element={<Activate />} />
          <Route path="/unauthorized" element={<Unauthorized />} />


          <Route
            path="/"
            element={
              <ProtectedRoute>
                <MainLayout />
              </ProtectedRoute>
            }
          >

            <Route index element={<Navigate to="/dashboard" replace />} />
            
            <Route path="dashboard" element={<Dashboard />} />
            

            <Route path="rooms" element={<RoomList />} />
            <Route path="rooms/add" element={<RoomForm />} />
            <Route path="rooms/edit/:id" element={<RoomForm />} />
            <Route path="beds-available" element={<BedsAvailableList />} />
            

            <Route path="tenants" element={<TenantList />} />
            <Route path="tenants/add" element={<TenantForm />} />
            <Route path="tenants/edit/:id" element={<TenantForm />} />


            <Route path="rents" element={<RentList />} />
            <Route path="rents/history" element={<RentHistory />} />


            <Route path="complaints" element={<ComplaintList />} />


            <Route path="change-password" element={<ChangePassword />} />
          </Route>


          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
