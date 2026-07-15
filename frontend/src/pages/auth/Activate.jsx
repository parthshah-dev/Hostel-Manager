import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { CheckCircle, XCircle, Loader2, Building } from 'lucide-react';
import Button from '../../components/common/Button';

const Activate = () => {
  const { activate } = useAuth();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const [status, setStatus] = useState('loading'); 
  const [message, setMessage] = useState('Activating your account, please wait...');

  useEffect(() => {
    const performActivation = async () => {
      if (!token) {
        setStatus('error');
        setMessage('Activation token is missing. Please check your link.');
        return;
      }

      const result = await activate(token);
      if (result.success) {
        setStatus('success');
        setMessage(result.message || 'Your account has been successfully activated!');
      } else {
        setStatus('error');
        setMessage(result.message || 'Activation failed. The token may be invalid or expired.');
      }
    };

    performActivation();
  }, [token, activate]);

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <div className="bg-blue-600 p-3.5 rounded-2xl text-white shadow-lg shadow-blue-600/10">
            <Building className="w-8 h-8" />
          </div>
        </div>
        <h2 className="mt-6 text-center text-2xl font-bold tracking-tight text-slate-800">
          Account Activation
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 border border-slate-200 sm:rounded-2xl sm:px-10 shadow-xs text-center space-y-6">
          
          {status === 'loading' && (
            <div className="flex flex-col items-center gap-3">
              <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
              <p className="text-sm font-semibold text-slate-500">{message}</p>
            </div>
          )}

          {status === 'success' && (
            <div className="flex flex-col items-center gap-3">
              <CheckCircle className="w-12 h-12 text-green-600" />
              <p className="text-sm font-semibold text-slate-700">{message}</p>
              <p className="text-xs text-slate-400">You can now proceed to log in with your credentials.</p>
              <Button
                variant="primary"
                className="mt-2 w-full py-2"
                onClick={() => navigate('/login')}
              >
                Go to Login
              </Button>
            </div>
          )}

          {status === 'error' && (
            <div className="flex flex-col items-center gap-3">
              <XCircle className="w-12 h-12 text-red-600" />
              <p className="text-sm font-semibold text-slate-700">{message}</p>
              <Link to="/login" className="mt-2 text-xs font-bold text-blue-600 hover:text-blue-700 block">
                Return to Login
              </Link>
            </div>
          )}

        </div>
      </div>
    </div>
  );
};

export default Activate;
