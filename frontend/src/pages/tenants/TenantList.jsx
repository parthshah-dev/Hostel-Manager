import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Plus, Edit2, Trash2, Users, Search, Mail, Phone, Calendar, ClipboardList } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';
import SearchBar from '../../components/common/SearchBar';
import StatusBadge from '../../components/common/StatusBadge';
import EmptyState from '../../components/common/EmptyState';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Pagination from '../../components/common/Pagination';

const TenantList = () => {
  const navigate = useNavigate();
  const [tenants, setTenants] = useState([]);
  const [loading, setLoading] = useState(true);


  const [searchQuery, setSearchQuery] = useState('');


  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;


  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const fetchTenants = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get('/api/tenants');
      setTenants(response.data);
    } catch (error) {
      console.error(error);
      toast.error('Failed to retrieve tenants list.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTenants();
  }, []);

  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      setDeleting(true);
      const response = await axiosInstance.delete(`/api/tenants/${deleteId}`);
      toast.success(response.data.message || 'Tenant deleted successfully.');
      setDeleteId(null);
      fetchTenants();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to delete tenant.');
    } finally {
      setDeleting(false);
    }
  };


  const filteredTenants = tenants.filter((tenant) => {
    return (
      tenant.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      tenant.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      tenant.roomNumber?.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });


  const totalPages = Math.ceil(filteredTenants.length / itemsPerPage);
  const paginatedTenants = filteredTenants.slice(
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
          <h1 className="text-2xl font-black text-slate-800 tracking-tight">Tenant Management</h1>
          <p className="text-xs text-slate-400 mt-1">Add, update, and search for active and inactive hostel tenants.</p>
        </div>
        <Button
          variant="primary"
          icon={Plus}
          onClick={() => navigate('/tenants/add')}
        >
          Add Tenant
        </Button>
      </div>


      <Card bodyClassName="py-4">
        <SearchBar
          value={searchQuery}
          onChange={(val) => {
            setSearchQuery(val);
            setCurrentPage(1);
          }}
          placeholder="Search by name, email or room number..."
        />
      </Card>


      {loading ? (
        <Loader type="spinner" className="py-20" />
      ) : paginatedTenants.length === 0 ? (
        <EmptyState
          title="No tenants found"
          description="Try modifying your search queries or register a new tenant."
          actionLabel="Add Tenant"
          onAction={() => navigate('/tenants/add')}
          icon={Users}
        />
      ) : (
        <Card bodyClassName="p-0" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-4">Name</th>
                  <th className="px-6 py-4">Contact</th>
                  <th className="px-6 py-4">Room</th>
                  <th className="px-6 py-4">Aadhaar</th>
                  <th className="px-6 py-4">Check-in Date</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {paginatedTenants.map((tenant) => (
                  <tr key={tenant.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-6 py-4">
                      <p className="font-bold text-slate-800 text-sm leading-tight">{tenant.fullName}</p>
                      <span className="text-[10px] text-slate-400 font-semibold">{tenant.email}</span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex flex-col gap-0.5 text-slate-500">
                        <span className="flex items-center gap-1"><Phone className="w-3.5 h-3.5 text-slate-400" />{tenant.phoneNumber}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 font-bold text-blue-600">
                      {tenant.roomNumber ? `Room ${tenant.roomNumber}` : 'Unassigned'}
                    </td>
                    <td className="px-6 py-4 text-slate-500">{tenant.aadhaarNumber}</td>
                    <td className="px-6 py-4 text-slate-500">
                      <span className="flex items-center gap-1"><Calendar className="w-3.5 h-3.5 text-slate-400" />{tenant.checkInDate}</span>
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge status={tenant.active ? 'ACTIVE' : 'INACTIVE'} />
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => navigate(`/tenants/edit/${tenant.id}`)}
                          className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-blue-600 transition-colors"
                          title="Edit Profile"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setDeleteId(tenant.id)}
                          className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-red-600 transition-colors"
                          title="Checkout/Delete Tenant"
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
        title="Check-Out / Delete Tenant"
        message="Are you sure you want to delete this tenant profile? This action will check-out the tenant, vacant the bed, and delete records permanently."
        confirmLabel="Check-Out Tenant"
        loading={deleting}
      />
    </div>
  );
};

export default TenantList;
