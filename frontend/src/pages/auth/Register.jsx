import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext';
import { Building, Eye, EyeOff, User, Mail, Lock } from 'lucide-react';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';

const Register = () => {
  const { signup } = useAuth();
  const navigate = useNavigate();
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm({
    defaultValues: {
      fullName: '',
      email: '',
      password: ''
    }
  });

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      const result = await signup(data.fullName, data.email, data.password);
      if (result.success) {
        toast.success(result.message || 'Account registered! Please check your email for activation.');
        navigate('/login');
      } else {
        toast.error(result.message);
      }
    } catch (error) {
      console.error(error);
      toast.error('An error occurred during registration.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <div className="bg-blue-600 p-3.5 rounded-2xl text-white shadow-lg shadow-blue-600/10">
            <Building className="w-8 h-8" />
          </div>
        </div>
        <h2 className="mt-6 text-center text-2xl font-bold tracking-tight text-slate-800">
          Create Admin Account
        </h2>
        <p className="mt-1.5 text-center text-xs text-slate-400">
          Register to manage PG rooms, tenants, rents, and complaints
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 border border-slate-200 sm:rounded-2xl sm:px-10 shadow-xs">
          <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>


            <div>
              <Input
                label="Full Name"
                name="fullName"
                type="text"
                placeholder="e.g. John Doe"
                required
                error={errors.fullName?.message}
                {...register('fullName', {
                  required: 'Full name is required',
                  maxLength: {
                    value: 100,
                    message: 'Full name must not exceed 100 characters'
                  }
                })}
              />
            </div>


            <div>
              <Input
                label="Admin Email Address"
                name="email"
                type="email"
                placeholder="admin@example.com"
                required
                error={errors.email?.message}
                {...register('email', {
                  required: 'Email address is required',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Invalid email address'
                  }
                })}
              />
            </div>


            <div className="relative">
              <Input
                label="Password"
                name="password"
                type={showPassword ? 'text' : 'password'}
                placeholder="••••••••"
                required
                error={errors.password?.message}
                {...register('password', {
                  required: 'Password is required',
                  minLength: {
                    value: 8,
                    message: 'Password must be at least 8 characters long'
                  }
                })}
              />
              <button
                type="button"
                className="absolute right-3.5 top-9 text-slate-400 hover:text-slate-600"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>


            <div>
              <Button
                type="submit"
                variant="primary"
                className="w-full py-2.5"
                disabled={loading}
              >
                {loading ? 'Creating Account...' : 'Register'}
              </Button>
            </div>
          </form>


          <div className="mt-6 text-center text-xs text-slate-400">
            Already have an account?{' '}
            <Link to="/login" className="font-bold text-blue-600 hover:text-blue-700">
              Sign In
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
