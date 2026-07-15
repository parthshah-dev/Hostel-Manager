import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { Key, Eye, EyeOff, Save, ShieldAlert } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import Card from '../../components/common/Card';
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';

const ChangePassword = () => {
  const { changePassword } = useAuth();
  const [loading, setLoading] = useState(false);
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const {
    register,
    handleSubmit,
    watch,
    reset,
    formState: { errors }
  } = useForm({
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  });

  const newPasswordVal = watch('newPassword');

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      const result = await changePassword(data.currentPassword, data.newPassword);
      if (result.success) {
        toast.success(result.message || 'Password changed successfully!');
        reset();
      } else {
        toast.error(result.message || 'Failed to change password. Please check your current password.');
      }
    } catch (error) {
      console.error(error);
      toast.error('An error occurred. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 max-w-xl mx-auto">

      <div>
        <h1 className="text-2xl font-black text-slate-800 tracking-tight">Security Settings</h1>
        <p className="text-xs text-slate-400 mt-1">Manage and update your administrator credentials.</p>
      </div>

      <Card>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div className="flex gap-3 bg-blue-50 border border-blue-200 p-4 rounded-xl text-blue-700 text-xs leading-normal">
            <ShieldAlert className="w-5 h-5 flex-shrink-0" />
            <div>
              <p className="font-bold">Password Requirements</p>
              <ul className="list-disc pl-4 mt-1 space-y-0.5 font-medium">
                <li>Must be at least 8 characters long</li>
                <li>Ensure it is different from your previous password</li>
                <li>Avoid using simple sequences or dictionary words</li>
              </ul>
            </div>
          </div>


          <div className="relative">
            <Input
              label="Current Password"
              name="currentPassword"
              type={showCurrent ? 'text' : 'password'}
              placeholder="••••••••"
              required
              error={errors.currentPassword?.message}
              {...register('currentPassword', {
                required: 'Current password is required'
              })}
            />
            <button
              type="button"
              className="absolute right-3.5 top-9 text-slate-400 hover:text-slate-600"
              onClick={() => setShowCurrent(!showCurrent)}
            >
              {showCurrent ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>


          <div className="relative">
            <Input
              label="New Password"
              name="newPassword"
              type={showNew ? 'text' : 'password'}
              placeholder="••••••••"
              required
              error={errors.newPassword?.message}
              {...register('newPassword', {
                required: 'New password is required',
                minLength: { value: 8, message: 'Password must be at least 8 characters long' }
              })}
            />
            <button
              type="button"
              className="absolute right-3.5 top-9 text-slate-400 hover:text-slate-600"
              onClick={() => setShowNew(!showNew)}
            >
              {showNew ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>


          <div className="relative">
            <Input
              label="Confirm New Password"
              name="confirmPassword"
              type={showConfirm ? 'text' : 'password'}
              placeholder="••••••••"
              required
              error={errors.confirmPassword?.message}
              {...register('confirmPassword', {
                required: 'Please confirm your new password',
                validate: (value) => value === newPasswordVal || 'Passwords do not match'
              })}
            />
            <button
              type="button"
              className="absolute right-3.5 top-9 text-slate-400 hover:text-slate-600"
              onClick={() => setShowConfirm(!showConfirm)}
            >
              {showConfirm ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </button>
          </div>


          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button
              type="submit"
              variant="primary"
              icon={Save}
              disabled={loading}
            >
              {loading ? 'Updating...' : 'Change Password'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default ChangePassword;
