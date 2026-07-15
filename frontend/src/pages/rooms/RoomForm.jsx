import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { ArrowLeft, Save } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';

const RoomForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEdit = !!id;
  const [loading, setLoading] = useState(false);
  const [fetching, setFetching] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors }
  } = useForm({
    defaultValues: {
      roomNumber: '',
      roomType: '',
      capacity: 1,
      monthlyRent: '',
      roomStatus: 'AVAILABLE'
    }
  });

  useEffect(() => {
    if (isEdit) {
      const fetchRoomDetails = async () => {
        try {
          setFetching(true);
          const response = await axiosInstance.get(`/api/rooms/${id}`);
          const room = response.data;
          
          setValue('roomNumber', room.roomNumber);
          setValue('roomType', room.roomType);
          setValue('capacity', room.capacity);
          setValue('monthlyRent', room.monthlyRent);
          setValue('roomStatus', room.roomStatus);
        } catch (error) {
          console.error(error);
          toast.error('Failed to load room details.');
          navigate('/rooms');
        } finally {
          setFetching(false);
        }
      };

      fetchRoomDetails();
    }
  }, [id, isEdit, setValue, navigate]);

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      
      const payload = {
        roomNumber: data.roomNumber,
        roomType: data.roomType,
        capacity: parseInt(data.capacity, 10),
        monthlyRent: parseFloat(data.monthlyRent),
        roomStatus: data.roomStatus
      };

      if (isEdit) {
        const response = await axiosInstance.put(`/api/rooms/${id}`, payload);
        toast.success(response.data.message || 'Room updated successfully.');
      } else {
        const response = await axiosInstance.post('/api/rooms', payload);
        toast.success(response.data.message || 'Room added successfully.');
      }
      
      navigate('/rooms');
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to save room details.');
    } finally {
      setLoading(false);
    }
  };

  const roomTypes = [
    { value: 'SINGLE', label: 'Single Occupancy' },
    { value: 'DOUBLE', label: 'Double Occupancy' },
    { value: 'TRIPLE', label: 'Triple Occupancy' }
  ];

  const roomStatuses = [
    { value: 'AVAILABLE', label: 'Available' },
    { value: 'OCCUPIED', label: 'Occupied' }
  ];

  if (fetching) {
    return <Loader type="spinner" className="h-96" />;
  }

  return (
    <div className="space-y-6 max-w-2xl mx-auto">

      <div className="flex items-center gap-3">
        <button
          onClick={() => navigate('/rooms')}
          className="p-2 bg-white hover:bg-slate-50 border border-slate-200 rounded-lg text-slate-500 hover:text-slate-700 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>
        <div>
          <h1 className="text-xl font-bold text-slate-800 tracking-tight">
            {isEdit ? 'Edit Room' : 'Add New Room'}
          </h1>
          <p className="text-xs text-slate-400 mt-0.5">
            {isEdit ? 'Update details of the selected room' : 'Create a new room in the system'}
          </p>
        </div>
      </div>

      <Card>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">

          <Input
            label="Room Number"
            name="roomNumber"
            placeholder="e.g. A-101"
            required
            error={errors.roomNumber?.message}
            {...register('roomNumber', {
              required: 'Room number is required',
              maxLength: {
                value: 20,
                message: 'Room number must be at most 20 characters'
              }
            })}
          />


          <Select
            label="Room Type"
            name="roomType"
            options={roomTypes}
            required
            error={errors.roomType?.message}
            placeholder="Select type"
            {...register('roomType', {
              required: 'Room type is required'
            })}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">

            <Input
              label="Bed Capacity"
              name="capacity"
              type="number"
              min="1"
              required
              error={errors.capacity?.message}
              {...register('capacity', {
                required: 'Capacity is required',
                min: { value: 1, message: 'Capacity must be at least 1' }
              })}
            />


            <Input
              label="Monthly Rent (INR)"
              name="monthlyRent"
              type="number"
              step="0.01"
              required
              error={errors.monthlyRent?.message}
              placeholder="e.g. 5500"
              {...register('monthlyRent', {
                required: 'Monthly rent is required',
                min: { value: 0.01, message: 'Rent must be greater than 0' }
              })}
            />
          </div>


          <Select
            label="Room Availability Status"
            name="roomStatus"
            options={roomStatuses}
            required
            error={errors.roomStatus?.message}
            placeholder="Select status"
            {...register('roomStatus', {
              required: 'Room status is required'
            })}
          />


          <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
            <Button
              variant="secondary"
              onClick={() => navigate('/rooms')}
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
              {loading ? 'Saving...' : 'Save Room'}
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default RoomForm;
