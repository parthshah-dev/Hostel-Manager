import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { CreditCard, PlusCircle, Search, ClipboardList, Calendar, Coins, CheckCircle, Mail, Printer } from 'lucide-react';
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
import Select from '../../components/common/Select';
import Input from '../../components/common/Input';

const RentList = () => {
  const navigate = useNavigate();
  const [rents, setRents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalPendingAmount, setTotalPendingAmount] = useState(0);

  // Search & Filter state
  const [searchQuery, setSearchQuery] = useState('');

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  // Generate Rent dialog state
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [generating, setGenerating] = useState(false);

  // Pay Rent modal state
  const [selectedRent, setSelectedRent] = useState(null);
  const [paying, setPaying] = useState(false);

  // Print Invoice Modal state
  const [invoiceRent, setInvoiceRent] = useState(null);

  const {
    register: registerGenerate,
    handleSubmit: handleSubmitGenerate,
    formState: { errors: generateErrors },
    reset: resetGenerate
  } = useForm({
    defaultValues: {
      rentMonth: new Date().toISOString().slice(0, 7) // Default to current YYYY-MM
    }
  });

  const {
    register: registerPayment,
    handleSubmit: handleSubmitPayment,
    formState: { errors: paymentErrors },
    reset: resetPayment
  } = useForm({
    defaultValues: {
      paymentMode: 'CASH',
      remarks: ''
    }
  });

  const fetchData = async () => {
    try {
      setLoading(true);
      const [pendingRes, amountRes] = await Promise.all([
        axiosInstance.get('/api/rents/pending'),
        axiosInstance.get('/api/rents/pending-amount')
      ]);
      setRents(pendingRes.data);
      setTotalPendingAmount(amountRes.data.pendingAmount || 0);
    } catch (error) {
      console.error(error);
      toast.error('Failed to load pending rent lists.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // Handle Rent Generation
  const onGenerateSubmit = async (data) => {
    try {
      setGenerating(true);
      const response = await axiosInstance.post('/api/rents/generate', {
        rentMonth: data.rentMonth
      });
      toast.success(response.data.message || 'Rent generated successfully for all active tenants.');
      setShowGenerateModal(false);
      resetGenerate();
      fetchData();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to generate monthly rents.');
    } finally {
      setGenerating(false);
    }
  };

  // Handle Recording Rent Payment
  const onPaymentSubmit = async (data) => {
    if (!selectedRent) return;
    try {
      setPaying(true);
      const response = await axiosInstance.put(`/api/rents/${selectedRent.id}/pay`, {
        paymentMode: data.paymentMode,
        remarks: data.remarks
      });
      
      // Success toast displays payment message and confirmation email notice
      toast.success((t) => (
        <span>
          <b>{response.data.message || 'Rent recorded as Paid.'}</b>
          <br />
          <span className="text-xs text-slate-400">Confirmation email sent to tenant successfully.</span>
        </span>
      ), { duration: 5000 });

      setSelectedRent(null);
      resetPayment();
      fetchData();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || 'Failed to record payment.');
    } finally {
      setPaying(false);
    }
  };

  // Filter & Search
  const filteredRents = rents.filter((r) => {
    return (
      r.tenantName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.roomNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      r.rentMonth.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  // Pagination
  const totalPages = Math.ceil(filteredRents.length / itemsPerPage);
  const paginatedRents = filteredRents.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-black text-slate-800 tracking-tight">Rent Management</h1>
          <p className="text-xs text-slate-400 mt-1">Generate invoices, track pending rentals, and check payments.</p>
        </div>
        <div className="flex items-center gap-2.5">
          <Button
            variant="outline"
            icon={ClipboardList}
            onClick={() => navigate('/rents/history')}
          >
            Rent History
          </Button>
          <Button
            variant="primary"
            icon={PlusCircle}
            onClick={() => setShowGenerateModal(true)}
          >
            Generate Rent
          </Button>
        </div>
      </div>

      {/* Info Stats Banner */}
      <div className="bg-gradient-to-r from-blue-600 to-indigo-600 rounded-xl p-5 text-white flex items-center justify-between shadow-md shadow-blue-600/10">
        <div className="space-y-1">
          <span className="text-xs font-bold text-blue-100 uppercase tracking-wider">Outstanding Balance</span>
          <h2 className="text-3xl font-black tracking-tight">₹{totalPendingAmount.toLocaleString('en-IN')}</h2>
          <p className="text-[10px] text-blue-100">Across all outstanding unpaid bills</p>
        </div>
        <div className="bg-white/10 p-3 rounded-xl border border-white/10 text-white">
          <Coins className="w-8 h-8" />
        </div>
      </div>

      {/* Search Bar */}
      <Card bodyClassName="py-4">
        <SearchBar
          value={searchQuery}
          onChange={(val) => {
            setSearchQuery(val);
            setCurrentPage(1);
          }}
          placeholder="Search pending bills by tenant name, room number, or month..."
        />
      </Card>

      {/* Pending Rents Table */}
      {loading ? (
        <Loader type="spinner" className="py-20" />
      ) : paginatedRents.length === 0 ? (
        <EmptyState
          title="No pending payments"
          description="Hurray! All rents for active tenants have been paid."
          actionLabel="Generate Rent"
          onAction={() => setShowGenerateModal(true)}
          icon={CheckCircle}
        />
      ) : (
        <Card bodyClassName="p-0" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-4">Tenant</th>
                  <th className="px-6 py-4">Room</th>
                  <th className="px-6 py-4">Rent Month</th>
                  <th className="px-6 py-4">Due Amount</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {paginatedRents.map((rent) => (
                  <tr key={rent.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-6 py-4 font-bold text-slate-800 text-sm">{rent.tenantName}</td>
                    <td className="px-6 py-4 text-slate-500 font-bold">Room {rent.roomNumber}</td>
                    <td className="px-6 py-4 text-slate-500">
                      <span className="flex items-center gap-1"><Calendar className="w-3.5 h-3.5 text-slate-400" />{rent.rentMonth}</span>
                    </td>
                    <td className="px-6 py-4 font-black text-red-600">₹{rent.dueAmount?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-4">
                      <StatusBadge status={rent.paymentStatus} />
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => setSelectedRent(rent)}
                        >
                          Mark Paid
                        </Button>
                        <button
                          onClick={() => setInvoiceRent(rent)}
                          className="p-1.5 border border-slate-200 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-700 transition-colors"
                          title="View Invoice"
                        >
                          <Printer className="w-4 h-4" />
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

      {/* Generate Monthly Rent Modal */}
      <Modal
        isOpen={showGenerateModal}
        onClose={() => setShowGenerateModal(false)}
        title="Generate Monthly Rent"
        size="sm"
      >
        <form onSubmit={handleSubmitGenerate(onGenerateSubmit)} className="space-y-4">
          <Input
            label="Rent Billing Month (YYYY-MM)"
            name="rentMonth"
            type="month"
            required
            error={generateErrors.rentMonth?.message}
            {...registerGenerate('rentMonth', {
              required: 'Billing month is required'
            })}
          />
          <p className="text-[11px] text-slate-400 leading-normal">
            This will create pending rent records for the specified month for all currently active tenants based on their room monthly rent settings.
          </p>
          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setShowGenerateModal(false)}
              disabled={generating}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="sm"
              disabled={generating}
            >
              {generating ? 'Generating...' : 'Generate Rents'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Pay Rent Modal */}
      <Modal
        isOpen={selectedRent !== null}
        onClose={() => setSelectedRent(null)}
        title={`Record Payment - ${selectedRent?.tenantName}`}
        size="sm"
      >
        <form onSubmit={handleSubmitPayment(onPaymentSubmit)} className="space-y-4">
          <div className="bg-slate-50 border border-slate-200 rounded-lg p-3.5 space-y-1.5 text-xs text-slate-600">
            <p><b>Room Number:</b> {selectedRent?.roomNumber}</p>
            <p><b>Rent Month:</b> {selectedRent?.rentMonth}</p>
            <p className="text-sm font-bold text-slate-800"><b>Amount Due:</b> ₹{selectedRent?.dueAmount?.toLocaleString('en-IN')}</p>
          </div>

          <Select
            label="Payment Mode"
            name="paymentMode"
            options={[
              { value: 'CASH', label: 'Cash' },
              { value: 'UPI', label: 'UPI (GPay / PhonePe)' },
              { value: 'BANK', label: 'Bank Transfer' }
            ]}
            required
            error={paymentErrors.paymentMode?.message}
            {...registerPayment('paymentMode', {
              required: 'Payment mode is required'
            })}
          />

          <Input
            label="Remarks (Optional)"
            name="remarks"
            placeholder="e.g. Received online transfer"
            error={paymentErrors.remarks?.message}
            {...registerPayment('remarks', {
              maxLength: { value: 255, message: 'Remarks must not exceed 255 characters' }
            })}
          />

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => setSelectedRent(null)}
              disabled={paying}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="primary"
              size="sm"
              disabled={paying}
            >
              {paying ? 'Processing...' : 'Confirm Payment'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Print Invoice Modal */}
      <Modal
        isOpen={invoiceRent !== null}
        onClose={() => setInvoiceRent(null)}
        title="Rent Bill Invoice"
        size="md"
      >
        {invoiceRent && (
          <div className="space-y-6">
            {/* Invoice Printable Content */}
            <div id="invoice-print" className="p-6 bg-white border border-slate-200 rounded-xl space-y-6 text-slate-600 text-xs">
              <div className="flex justify-between items-start">
                <div>
                  <h2 className="text-lg font-black text-slate-800 uppercase tracking-tight">HostelEase PG</h2>
                  <p className="text-[10px] text-slate-400">Premium Accommodation Billing</p>
                </div>
                <div className="text-right">
                  <span className="inline-flex px-2 py-0.5 rounded-full text-[10px] font-bold border border-red-200 bg-red-50 text-red-700">UNPAID DUE</span>
                  <p className="text-[10px] text-slate-400 mt-1.5">Invoice Ref: INV-{invoiceRent.id}</p>
                </div>
              </div>

              <hr className="border-slate-100" />

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Billed To:</p>
                  <p className="text-slate-800 font-bold text-sm mt-0.5">{invoiceRent.tenantName}</p>
                  <p className="text-slate-500 mt-0.5">Room {invoiceRent.roomNumber}</p>
                </div>
                <div className="text-right">
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Billing Period:</p>
                  <p className="text-slate-800 font-bold mt-0.5">{invoiceRent.rentMonth}</p>
                </div>
              </div>

              {/* Items Table */}
              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold">
                    <th className="py-2.5 px-3">Description</th>
                    <th className="py-2.5 px-3 text-right">Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr className="border-b border-slate-100">
                    <td className="py-3 px-3 font-semibold">Accommodation Monthly Rental</td>
                    <td className="py-3 px-3 text-right font-bold text-slate-800">₹{invoiceRent.monthlyRent?.toLocaleString('en-IN')}</td>
                  </tr>
                  <tr className="border-b border-slate-100 bg-slate-50/20">
                    <td className="py-2.5 px-3 text-slate-400">Total Payments Recorded</td>
                    <td className="py-2.5 px-3 text-right text-green-600">-₹{invoiceRent.amountPaid?.toLocaleString('en-IN')}</td>
                  </tr>
                  <tr className="font-bold text-slate-800">
                    <td className="py-3 px-3 text-right">Total Outstanding Balance</td>
                    <td className="py-3 px-3 text-right text-red-600 text-sm">₹{invoiceRent.dueAmount?.toLocaleString('en-IN')}</td>
                  </tr>
                </tbody>
              </table>

              <div className="bg-slate-50 border border-slate-200 rounded-lg p-3 text-[10px] text-slate-400 leading-normal text-center">
                This is a computer-generated summary invoice representing pending pg accommodations.
              </div>
            </div>

            <div className="flex items-center justify-end gap-2.5">
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setInvoiceRent(null)}
              >
                Close
              </Button>
              <Button
                variant="primary"
                size="sm"
                icon={Printer}
                onClick={() => window.print()}
              >
                Print Invoice
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RentList;
