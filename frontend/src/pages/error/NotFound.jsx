import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Home, AlertCircle } from 'lucide-react';
import Button from '../../components/common/Button';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-6 text-center">
      <div className="bg-blue-50 border border-blue-100 p-4 rounded-full text-blue-600 mb-6">
        <AlertCircle className="w-12 h-12 animate-bounce" />
      </div>
      <h1 className="text-4xl font-black text-slate-800 tracking-tight">404</h1>
      <h2 className="text-lg font-bold text-slate-700 mt-2">Page Not Found</h2>
      <p className="text-xs text-slate-400 max-w-sm mt-2 mb-8 leading-normal">
        The page you are looking for does not exist or has been moved. Check the URL and try again.
      </p>
      <Button
        variant="primary"
        icon={Home}
        onClick={() => navigate('/dashboard')}
      >
        Back to Dashboard
      </Button>
    </div>
  );
};

export default NotFound;
