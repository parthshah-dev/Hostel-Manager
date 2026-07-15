import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { Plus, Edit3, Trash2, AlertTriangle, Search, SlidersHorizontal, ArrowUpRight, ClipboardList, Inbox, MessageSquarePlus } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';
import SearchBar from '../../components/common/SearchBar';
import StatusBadge from '../../components/common/StatusBadge';
import EmptyState from '../../components/common/EmptyState';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import Pagination from '../../components/common/Pagination';
import Modal from '../../components/common/Modal';
import Input from '../../components/common/Input';
import Select from '../../components/common/Select';

const ComplaintList = () => {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tenants, setTenants] = useState([]);
  const [statistics, setStatistics] = useState(null);


  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');


  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;


  const [showAddModal, setShowAddModal] = useState(false);
  const [adding, setAdding] = useState(false);


  const [selectedComplaint, setSelectedComplaint] = useState(null);
  const [updating, setUpdating] = useState(false);


  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const {
    register: registerAdd,
    handleSubmit: handleSubmitAdd,
    formState: { errors: addErrors },
    reset: resetAdd
  } = useForm({
    defaultValues: {
      tenantId: '',
      complaintCategory: 'OTHER',
      description: ''
    }
  });

  const {
    register: registerUpdate,
    handleSubmit: handleSubmitUpdate,
    setValue: setUpdateValue,
    formState: { errors: updateErrors },
    reset: resetUpdate
  } = useForm({
    defaultValues: {
      status: 'OPEN',
      resolutionRemarks: ''
    }
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [complaintsRes, statsRes, tenantsRes] = await Promise.all([
        axiosInstance.get('/api/complaints'),
        axiosInstance.get('/api/complaints/statistics'),
        axiosInstance.get('/api/tenants/active')
      ]);
      setComplaints(complaintsRes.data);
      setStatistics(statsRes.data);
      setTenants(tenantsRes.data);
    } catch (error) {
      console.error(error);
      toast.error('Failed to load complaints data.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);


  useEffect(() => {
    if (selectedComplaint) {
      setUpdateValue('status', selectedComplaint.status);
      setUpdateValue('resolutionRemarks', selectedComplaint.resolutionRemarks || '');
    }
  }, [selectedComplaint, setUpdateValue]);


  const onAddSubmit = async (data) => {
    try {
      setAdding(true);
      const payload = {
        tenantId: parseInt(data.tenantId, 10),
        complaintCategory: data.complaintCategory,
        description: data.description
      };
      const response = await axiosInstance.post('/api/complaints', payload);
      toast.success(response.data.message || 'Complaint submitted successfully.');
      setShowAddModal(false);
      resetAdd();
      fetchData();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to submit complaint.');
    } finally {
      setAdding(false);
    }
  };


  const onUpdateSubmit = async (data) => {
    if (!selectedComplaint) return;
    try {
      setUpdating(true);
      const payload = {
        status: data.status,
        resolutionRemarks: data.resolutionRemarks
      };
      const response = await axiosInstance.put(`/api/complaints/${selectedComplaint.id}`, payload);
      toast.success(response.data.message || 'Complaint updated successfully.');
      setSelectedComplaint(null);
      resetUpdate();
      fetchData();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to update complaint.');
    } finally {
      setUpdating(false);
    }
  };


  const handleDelete = async () => {
    if (!deleteId) return;
    try {
      setDeleting(true);
      const response = await axiosInstance.delete(`/api/complaints/${deleteId}`);
      toast.success(response.data.message || 'Complaint deleted successfully.');
      setDeleteId(null);
      fetchData();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to delete complaint.');
    } finally {
      setDeleting(false);
    }
  };


  const filteredComplaints = complaints.filter((c) => {
    const matchesSearch = 
      c.tenantName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesStatus = statusFilter === '' || c.status === statusFilter;
    const matchesCategory = categoryFilter === '' || c.complaintCategory === categoryFilter;

    return matchesSearch && matchesStatus && matchesCategory;
  });


  const totalPages = Math.ceil(filteredComplaints.length / itemsPerPage);
  const paginatedComplaints = filteredComplaints.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const categories = [
    { value: 'ELECTRICITY', label: 'Electricity' },
    { value: 'PLUMBING', label: 'Plumbing' },
    { value: 'CLEANING', label: 'Cleaning' },
    { value: 'INTERNET', label: 'Internet / Wi-Fi' },
    { value: 'FURNITURE', label: 'Furniture' },
    { value: 'SECURITY', label: 'Security' },
    { value: 'OTHER', label: 'Other' }
  ];

  const tenantOptions = tenants.map((t) => ({
    value: t.id,
    label: `${t.fullName} (Room ${t.roomNumber})`
  }));

  return (
    <div className="space-y-6">

      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-800 tracking-tight">Complaint Board</h1>
          <p className="text-xs text-slate-400 mt-1">Review tenant grievances, update statuses, and log resolutions.</p>
        </div>
        <Button
          variant="primary"
          icon={Plus}
          onClick={() => setShowAddModal(true)}
        >
          File Complaint
        </Button>
      </div>


      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-xs">
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Total Complaints</p>
          <p className="text-2xl font-black text-slate-800 tracking-tight mt-1">{statistics?.totalComplaints || 0}</p>
        </div>
        <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-xs">
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider text-red-500">Open Issues</p>
          <p className="text-2xl font-black text-red-600 tracking-tight mt-1">{statistics?.open || 0}</p>
        </div>
        <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-xs">
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider text-blue-500">In Progress</p>
          <p className="text-2xl font-black text-blue-600 tracking-tight mt-1">{statistics?.inProgress || 0}</p>
        </div>
        <div className="bg-white border border-slate-200 rounded-xl p-4 shadow-xs">
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider text-green-500">Resolved</p>
          <p className="text-2xl font-black text-green-600 tracking-tight mt-1">{statistics?.resolved || 0}</p>
        </div>
      </div>


      <Card bodyClassName="py-4">
        <SearchBar
          value={searchQuery}
          onChange={(val) => {
            setSearchQuery(val);
            setCurrentPage(1);
          }}
          placeholder="Search complaints by tenant or description..."
        >
          <div className="flex flex-wrap items-center gap-2">
            <select
              value={categoryFilter}
              onChange={(e) => {
                setCategoryFilter(e.target.value);
                setCurrentPage(1);
              }}
              className="px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-600 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            >
              <option value="">All Categories</option>
              {categories.map((cat) => (
                <option key={cat.value} value={cat.value}>{cat.label}</option>
              ))}
            </select>
            <select
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setCurrentPage(1);
              }}
              className="px-3.5 py-2.5 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-600 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            >
              <option value="">All Statuses</option>
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
            </select>
          </div>
        </SearchBar>
      </Card>


      {loading ? (
        <Loader type="spinner" className="py-20" />
      ) : paginatedComplaints.length === 0 ? (
        <EmptyState
          title="No complaints logged"
          description="Everything is smooth! There are no complaints reported."
          actionLabel="File Complaint"
          onAction={() => setShowAddModal(true)}
          icon={AlertTriangle}
        />
      ) : (
        <Card bodyClassName="p-0" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-4">Tenant</th>
                  <th className="px-6 py-4">Category</th>
                  <th className="px-6 py-4">Description</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4">Remarks</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {paginatedComplaints.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-6 py-4 font-bold text-slate-800 text-sm">{item.tenantName}</td>
                    <td className="px-6 py-4 font-semibold text-slate-500 text-[10px] uppercase tracking-wider">
                      {item.complaintCategory}
                    </td>
                    <td className="px-6 py-4 text-slate-600 max-w-xs truncate" title={item.description}>
                      {item.description}
                    </td>
                    <td className="px-6 py-4">
                      <StatusBadge status={item.status} />
                    </td>
                    <td className="px-6 py-4 text-slate-400 italic max-w-xs truncate" title={item.resolutionRemarks}>
                      {item.resolutionRemarks || 'No remarks recorded'}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button
                          onClick={() => setSelectedComplaint(item)}
                          className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-blue-600 transition-colors"
                          title="Update Status / Action"
                        >
                          <Edit3 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => setDeleteId(item.id)}
                          className="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-red-600 transition-colors"
                          title="Delete Complaint"
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


      <Modal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        title="File New Tenant Complaint"
        size="md"
      >
        <form onSubmit={handleSubmitAdd(onAddSubmit)} className="space-y-4">
          <Select
            label="Reporting Tenant"
            name="tenantId"
            options={tenantOptions}
            required
            placeholder={tenants.length === 0 ? 'No active tenants available' : 'Select tenant'}
            error={addErrors.tenantId?.message}
            {...registerAdd('tenantId', {
              required: 'Selecting a tenant is required'
            })}
          />

          <Select
            label="Complaint Category"
            name="complaintCategory"
            options={categories}
            required
            error={addErrors.complaintCategory?.message}
            {...registerAdd('complaintCategory', {
              required: 'Category is required'
            })}
          />

          <div className="flex flex-col gap-1.5 w-full">
            <label className="text-xs font-bold text-slate-700">Complaint Description *</label>
            <textarea
              name="description"
              placeholder="e.g. Wi-Fi connection is dropping frequently in room A-101..."
              className={`px-3.5 py-2.5 bg-white border rounded-lg text-sm transition-all focus:border-blue-500 focus:ring-2 focus:ring-blue-500/10 outline-none placeholder:text-slate-400 min-h-[100px] ${
                addErrors.description ? 'border-red-300' : 'border-slate-300'
              }`}
              {...registerAdd('description', {
                required: 'Description is required',
                minLength: { value: 10, message: 'Description must be at least 10 characters long' },
                maxLength: { value: 1000, message: 'Description must not exceed 1000 characters' }
              })}
            />
            {addErrors.description && <span className="text-xs font-semibold text-red-500">{addErrors.description.message}</span>}
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setShowAddModal(false)}
              disabled={adding}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="sm"
              disabled={adding}
            >
              {adding ? 'Submitting...' : 'Submit Complaint'}
            </Button>
          </div>
        </form>
      </Modal>


      <Modal
        isOpen={selectedComplaint !== null}
        onClose={() => setSelectedComplaint(null)}
        title={`Process Complaint - ${selectedComplaint?.tenantName}`}
        size="md"
      >
        <form onSubmit={handleSubmitUpdate(onUpdateSubmit)} className="space-y-4">
          <div className="bg-slate-50 border border-slate-200 rounded-lg p-4 space-y-2 text-xs text-slate-600">
            <p><b>Category:</b> {selectedComplaint?.complaintCategory}</p>
            <p className="leading-relaxed"><b>Issue:</b> {selectedComplaint?.description}</p>
          </div>

          <Select
            label="Update Status"
            name="status"
            options={[
              { value: 'OPEN', label: 'Open' },
              { value: 'IN_PROGRESS', label: 'In Progress' },
              { value: 'RESOLVED', label: 'Resolved' }
            ]}
            required
            error={updateErrors.status?.message}
            {...registerUpdate('status', {
              required: 'Status selection is required'
            })}
          />

          <div className="flex flex-col gap-1.5 w-full">
            <label className="text-xs font-bold text-slate-700">Resolution Remarks</label>
            <textarea
              name="resolutionRemarks"
              placeholder="e.g. WiFi router restarted, verified connection is stable."
              className="px-3.5 py-2.5 bg-white border border-slate-300 rounded-lg text-sm transition-all focus:border-blue-500 focus:ring-2 focus:ring-blue-500/10 outline-none placeholder:text-slate-400 min-h-[100px]"
              {...registerUpdate('resolutionRemarks', {
                maxLength: { value: 500, message: 'Remarks must not exceed 500 characters' }
              })}
            />
            {updateErrors.resolutionRemarks && (
              <span className="text-xs font-semibold text-red-500">{updateErrors.resolutionRemarks.message}</span>
            )}
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setSelectedComplaint(null)}
              disabled={updating}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="sm"
              disabled={updating}
            >
              {updating ? 'Saving...' : 'Update Complaint'}
            </Button>
          </div>
        </form>
      </Modal>


      <ConfirmDialog
        isOpen={deleteId !== null}
        onClose={() => setDeleteId(null)}
        onConfirm={handleDelete}
        title="Delete Complaint"
        message="Are you sure you want to delete this complaint? This record will be permanently removed from logs."
        confirmLabel="Delete"
        loading={deleting}
      />
    </div>
  );
};

export default ComplaintList;
