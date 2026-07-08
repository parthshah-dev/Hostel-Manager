import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldAlert, ArrowLeft } from 'lucide-react';
import Button from '../../components/common/Button';

const Unauthorized = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-6 text-center">
      <div className="bg-red-50 border border-red-100 p-4 rounded-full text-red-600 mb-6">
        <ShieldAlert className="w-12 h-12" />
      </div>
      <h1 className="text-4xl font-black text-slate-800 tracking-tight">403</h1>
      <h2 className="text-lg font-bold text-slate-700 mt-2">Access Denied</h2>
      <p className="text-xs text-slate-400 max-w-sm mt-2 mb-8 leading-normal">
        You do not have the required permissions to view this resource. Please contact management if you believe this is an error.
      </p>
      <Button
        variant="outline"
        icon={ArrowLeft}
        onClick={() => navigate('/login')}
      >
        Sign In as Admin
      </Button>
    </div>
  );
};

export default Unauthorized;
