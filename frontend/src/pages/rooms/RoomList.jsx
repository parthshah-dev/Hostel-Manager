import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Plus, Edit2, Trash2, Home, Search, SlidersHorizontal, Eye } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';
import SearchBar from '../../components/common/SearchBar';
import StatusBadge from '../../components/common/StatusBadge';
import EmptyState from '../../components/common/EmptyState';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Pagination from '../../components/common/Pagination';

const RoomList = () => {
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');


  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;


  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get('/api/rooms');
      setRooms(response.data);
    } catch (error) {
      console.error(error);
      toast.error('Failed to retrieve rooms list.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      setDeleting(true);
      const response = await axiosInstance.delete(`/api/rooms/${deleteId}`);
      toast.success(response.data.message || 'Room deleted successfully.');
      setDeleteId(null);
      fetchRooms();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to delete room.');
    } finally {
      setDeleting(false);
    }
  };


  const filteredRooms = rooms.filter((room) => {
    const matchesSearch = room.roomNumber.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === '' || room.roomStatus === statusFilter;
    return matchesSearch && matchesStatus;
  });


  const totalPages = Math.ceil(filteredRooms.length / itemsPerPage);
  const paginatedRooms = filteredRooms.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div className="space-y-6">

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-800 tracking-tight">Room Management</h1>
          <p className="text-xs text-slate-400 mt-1">Add, update, and search for pg rooms, types, and rent prices.</p>
        </div>
        <Button
          variant="primary"
          icon={Plus}
          onClick={() => navigate('/rooms/add')}
        >
          Add Room
        </Button>
      </div>


      <Card bodyClassName="py-4">
        <SearchBar
          value={searchQuery}
          onChange={(val) => {
            setSearchQuery(val);
            setCurrentPage(1);
          }}
          placeholder="Search by room number..."
        >
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-slate-400" />
            <select
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setCurrentPage(1);
              }}
              className="px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-600 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            >
              <option value="">All Statuses</option>
              <option value="AVAILABLE">Available</option>
              <option value="OCCUPIED">Occupied</option>
            </select>
          </div>
        </SearchBar>
      </Card>


      {loading ? (
        <Loader type="spinner" className="py-20" />
      ) : paginatedRooms.length === 0 ? (
        <EmptyState
          title="No rooms found"
          description="Try modifying your search queries or create a new room."
          actionLabel="Add Room"
          onAction={() => navigate('/rooms/add')}
          icon={Home}
        />
      ) : (
        <Card bodyClassName="p-0" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-4">Room Number</th>
                  <th className="px-6 py-4">Room Type</th>
                  <th className="px-6 py-4">Capacity</th>
                  <th className="px-6 py-4">Monthly Rent</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {paginatedRooms.map((room) => (
                  <tr key={room.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-6 py-4 font-bold text-slate-800 text-sm">{room.roomNumber}</td>
                    <td className="px-6 py-4 text-slate-500 font-semibold">{room.roomType}</td>
                    <td className="px-6 py-4 text-slate-500">{room.capacity} beds</td>
                    <td className="px-6 py-4 font-black text-slate-800">₹{room.monthlyRent?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-4">
                      <StatusBadge status={room.roomStatus} />
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => navigate(`/rooms/edit/${room.id}`)}
                          className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-blue-600 transition-colors"
                          title="Edit Room"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setDeleteId(room.id)}
                          className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-red-600 transition-colors"
                          title="Delete Room"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </Card>
      )}


      <ConfirmDialog
        isOpen={deleteId !== null}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Room"
        message="Are you sure you want to delete this room? This action is permanent and cannot be undone."
        confirmLabel="Delete Room"
        loading={deleting}
      />
    </div>
  );
};

export default RoomList;
