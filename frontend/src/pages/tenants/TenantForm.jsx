import React, { useEffect, useState } from 'react';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { ArrowLeft, Save } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';

const TenantForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const isEdit = !!id;
  
  const searchParams = new URLSearchParams(location.search);
  const preSelectedRoomId = searchParams.get('roomId');
  
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(false);
  const [rooms, setRooms] = useState([]);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors }
  } = useForm({
    defaultValues: {
      fullName: '',
      email: '',
      phoneNumber: '',
      aadhaarNumber: '',
      checkInDate: new Date().toISOString().split('T')[0],
      securityDeposit: '',
      roomId: ''
    }
  });

  useEffect(() => {
    if (preSelectedRoomId && !isEdit) {
      setValue('roomId', preSelectedRoomId);
    }
  }, [preSelectedRoomId, isEdit, setValue]);

  // Fetch all available rooms for assignment
  useEffect(() => {
    const loadRooms = async () => {
      try {
        const response = await axiosInstance.get('/api/rooms');
        // Let's list rooms that are available, or include the current tenant's room if editing
        setRooms(response.data);
      } catch (error) {
        console.error(error);
        toast.error('Failed to load rooms list.');
      }
    };
    loadRooms();
  }, []);

  // Load tenant details if editing
  useEffect(() => {
    if (isEdit) {
      const fetchTenantDetails = async () => {
        try {
          setFetching(true);
          const response = await axiosInstance.get(`/api/tenants/${id}`);
          const tenant = response.data;
          
          setValue('fullName', tenant.fullName);
          setValue('email', tenant.email);
          setValue('phoneNumber', tenant.phoneNumber);
          setValue('aadhaarNumber', tenant.aadhaarNumber);
          setValue('checkInDate', tenant.checkInDate);
          setValue('securityDeposit', tenant.securityDeposit);
          setValue('roomId', tenant.roomId);
        } catch (error) {
          console.error(error);
          toast.error('Failed to load tenant details.');
          navigate('/tenants');
        } finally {
          setFetching(false);
        }
      };

      fetchTenantDetails();
    }
  }, [id, isEdit, setValue, navigate]);

  const onSubmit = async (data) => {
    try {
      setLoading(true);

      const payload = {
        fullName: data.fullName,
        email: data.email,
        phoneNumber: data.phoneNumber,
        aadhaarNumber: data.aadhaarNumber,
        checkInDate: data.checkInDate,
        securityDeposit: parseFloat(data.securityDeposit),
        roomId: parseInt(data.roomId, 10)
      };

      if (isEdit) {
        const response = await axiosInstance.put(`/api/tenants/${id}`, payload);
        toast.success(response.data.message || 'Tenant updated successfully.');
      } else {
        const response = await axiosInstance.post('/api/tenants', payload);
        toast.success(response.data.message || 'Tenant registered successfully.');
      }

      navigate('/tenants');
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to save tenant details.');
    } finally {
      setLoading(false);
    }
  };

  const roomOptions = rooms.map((r) => ({
    value: r.id,
    label: `Room ${r.roomNumber} (${r.roomType} - ₹${r.monthlyRent}/mo) - ${r.roomStatus}`
  }));

  if (fetching) {
    return <Loader type="spinner" className="h-96" />;
  }

  return (
    <div className="space-y-6 max-w-2xl mx-auto">
      {/* Header */}
      <div className="flex items-center gap-3">
        <button
          onClick={() => navigate('/tenants')}
          className="p-2 bg-white hover:bg-slate-50 border border-slate-200 rounded-lg text-slate-500 hover:text-slate-700 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>
        <div>
          <h1 className="text-xl font-bold text-slate-800 tracking-tight">
            {isEdit ? 'Edit Tenant Profile' : 'Register New Tenant'}
          </h1>
          <p className="text-xs text-slate-400 mt-0.5">
            {isEdit ? 'Modify profiles of existing PG occupants' : 'Check-in a new tenant and assign a room'}
          </p>
        </div>
      </div>

      <Card>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          {/* Full Name */}
          <Input
            label="Full Name"
            name="fullName"
            placeholder="e.g. Parth Shah"
            required
            error={errors.fullName?.message}
            {...register('fullName', {
              required: 'Full name is required',
              maxLength: { value: 100, message: 'Name must be at most 100 characters' }
            })}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Email */}
            <Input
              label="Email Address"
              name="email"
              type="email"
              placeholder="parth@example.com"
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

            {/* Phone Number */}
            <Input
              label="Phone Number"
              name="phoneNumber"
              placeholder="e.g. 9876543210"
              required
              error={errors.phoneNumber?.message}
              {...register('phoneNumber', {
                required: 'Phone number is required',
                pattern: {
                  value: /^\d{10}$/,
                  message: 'Phone number must be exactly 10 digits'
                }
              })}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Aadhaar Number */}
            <Input
              label="Aadhaar Card Number"
              name="aadhaarNumber"
              placeholder="e.g. 123456789012"
              required
              error={errors.aadhaarNumber?.message}
              {...register('aadhaarNumber', {
                required: 'Aadhaar number is required',
                pattern: {
                  value: /^\d{12}$/,
                  message: 'Aadhaar number must be exactly 12 digits'
                }
              })}
            />

            {/* Check-in Date */}
            <Input
              label="Check-In Date"
              name="checkInDate"
              type="date"
              required
              error={errors.checkInDate?.message}
              {...register('checkInDate', {
                required: 'Check-in date is required'
              })}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Security Deposit */}
            <Input
              label="Security Deposit Paid (INR)"
              name="securityDeposit"
              type="number"
              step="0.01"
              placeholder="e.g. 10000"
              required
              error={errors.securityDeposit?.message}
              {...register('securityDeposit', {
                required: 'Security deposit is required',
                min: { value: 0, message: 'Deposit cannot be negative' }
              })}
            />

            {/* Room ID Assignment */}
            <Select
              label="Assign Room"
              name="roomId"
              options={roomOptions}
              required
              placeholder={rooms.length === 0 ? 'No rooms available' : 'Select a Room'}
              error={errors.roomId?.message}
              {...register('roomId', {
                required: 'Room assignment is required'
              })}
            />
          </div>

          {/* Action Buttons */}
          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button
              variant="secondary"
              onClick={() => navigate('/tenants')}
              disabled={loading}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              icon={Save}
              disabled={loading}
            >
              {loading ? 'Saving...' : 'Register Tenant'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default TenantForm;
