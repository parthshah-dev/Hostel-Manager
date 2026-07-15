import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { ArrowLeft, Search, Calendar, SlidersHorizontal, Printer, Inbox } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';
import SearchBar from '../../components/common/SearchBar';
import StatusBadge from '../../components/common/StatusBadge';
import EmptyState from '../../components/common/EmptyState';
import Pagination from '../../components/common/Pagination';
import Modal from '../../components/common/Modal';

const RentHistory = () => {
  const navigate = useNavigate();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);


  const [searchQuery, setSearchQuery] = useState('');
  const [monthFilter, setMonthFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');


  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;


  const [receiptRent, setReceiptRent] = useState(null);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get('/api/rents/history');
      setHistory(response.data);
    } catch (error) {
      console.error(error);
      toast.error('Failed to load rent history.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);


  const filteredHistory = history.filter((item) => {
    const matchesSearch = 
      item.tenantName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.roomNumber?.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesMonth = monthFilter === '' || item.rentMonth === monthFilter;
    const matchesStatus = statusFilter === '' || item.paymentStatus === statusFilter;

    return matchesSearch && matchesMonth && matchesStatus;
  });


  const totalPages = Math.ceil(filteredHistory.length / itemsPerPage);
  const paginatedHistory = filteredHistory.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  return (
    <div className="space-y-6">

      <div className="flex items-center gap-3">
        <button
          onClick={() => navigate('/rents')}
          className="p-2 bg-white hover:bg-slate-50 border border-slate-200 rounded-lg text-slate-500 hover:text-slate-700 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
        </button>
        <div>
          <h1 className="text-xl font-bold text-slate-800 tracking-tight">Rent Payment History</h1>
          <p className="text-xs text-slate-400 mt-0.5">View and print receipts of all processed tenant rent statements.</p>
        </div>
      </div>


      <Card bodyClassName="py-4">
        <SearchBar
          value={searchQuery}
          onChange={(val) => {
            setSearchQuery(val);
            setCurrentPage(1);
          }}
          placeholder="Search by tenant name or room number..."
        >
          <div className="flex flex-wrap items-center gap-2">
            <div className="flex items-center gap-1.5">
              <Calendar className="w-4 h-4 text-slate-400" />
              <input
                type="month"
                value={monthFilter}
                onChange={(e) => {
                  setMonthFilter(e.target.value);
                  setCurrentPage(1);
                }}
                className="px-3 py-2 bg-white border border-slate-200 rounded-lg text-xs font-semibold text-slate-600 outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              />
            </div>
            <div className="flex items-center gap-1.5">
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
                <option value="PAID">Paid</option>
                <option value="PENDING">Pending</option>
              </select>
            </div>
          </div>
        </SearchBar>
      </Card>


      {loading ? (
        <Loader type="spinner" className="py-20" />
      ) : paginatedHistory.length === 0 ? (
        <EmptyState
          title="No history records found"
          description="There are no rent transaction histories matching your filters."
          icon={Inbox}
        />
      ) : (
        <Card bodyClassName="p-0" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-4">Tenant</th>
                  <th className="px-6 py-4">Room</th>
                  <th className="px-6 py-4">Billing Month</th>
                  <th className="px-6 py-4">Monthly Rent</th>
                  <th className="px-6 py-4">Paid</th>
                  <th className="px-6 py-4">Due</th>
                  <th className="px-6 py-4">Status</th>
                  <th className="px-6 py-4">Payment Info</th>
                  <th className="px-6 py-4 text-right">Receipt</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {paginatedHistory.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-6 py-4 font-bold text-slate-800 text-sm">{item.tenantName}</td>
                    <td className="px-6 py-4 text-slate-500 font-bold">Room {item.roomNumber}</td>
                    <td className="px-6 py-4 text-slate-500">{item.rentMonth}</td>
                    <td className="px-6 py-4 text-slate-500">₹{item.monthlyRent?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-4 font-bold text-green-600">₹{item.amountPaid?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-4 text-red-600">₹{item.dueAmount?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-4">
                      <StatusBadge status={item.paymentStatus} />
                    </td>
                    <td className="px-6 py-4 text-slate-500">
                      {item.paymentStatus === 'PAID' ? (
                        <div>
                          <p className="font-semibold text-slate-700 uppercase tracking-wider text-[9px]">{item.paymentMode}</p>
                          <span className="text-[10px] text-slate-400">{item.paymentDate}</span>
                        </div>
                      ) : (
                        <span className="text-slate-400 italic">Not available</span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-right">
                      {item.paymentStatus === 'PAID' ? (
                        <button
                          onClick={() => setReceiptRent(item)}
                          className="p-1.5 border border-slate-200 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-700 transition-colors"
                          title="View receipt"
                        >
                          <Printer className="w-4 h-4" />
                        </button>
                      ) : (
                        <span className="text-slate-400 italic text-[10px]">Pending payment</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={(p) => setCurrentPage(p)}
          />
        </Card>
      )}


      <Modal
        isOpen={receiptRent !== null}
        onClose={() => setReceiptRent(null)}
        title="Rent Payment Receipt"
        size="md"
      >
        {receiptRent && (
          <div className="space-y-6">
            <div id="receipt-print" className="p-6 bg-white border border-slate-200 rounded-xl space-y-6 text-slate-600 text-xs">
              <div className="flex justify-between items-start">
                <div>
                  <h2 className="text-lg font-black text-slate-800 uppercase tracking-tight text-blue-600">HostelEase</h2>
                  <p className="text-[10px] text-slate-400 font-semibold uppercase tracking-wider">Official Payment Receipt</p>
                </div>
                <div className="text-right">
                  <span className="inline-flex px-2.5 py-0.5 rounded-full text-[10px] font-bold border border-green-200 bg-green-50 text-green-700">PAID FULL</span>
                  <p className="text-[10px] text-slate-400 mt-1.5">Receipt No: RCPT-{receiptRent.id}</p>
                </div>
              </div>

              <hr className="border-slate-100" />

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Received From:</p>
                  <p className="text-slate-800 font-bold text-sm mt-0.5">{receiptRent.tenantName}</p>
                  <p className="text-slate-500">Room {receiptRent.roomNumber}</p>
                </div>
                <div className="text-right">
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Date of Payment:</p>
                  <p className="text-slate-800 font-semibold mt-0.5">{receiptRent.paymentDate}</p>
                  <p className="text-slate-500 uppercase font-bold text-[9px] mt-0.5">Mode: {receiptRent.paymentMode}</p>
                </div>
              </div>

              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold">
                    <th className="py-2.5 px-3">Transaction Description</th>
                    <th className="py-2.5 px-3 text-right">Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr className="border-b border-slate-100">
                    <td className="py-3 px-3 font-semibold text-slate-700">Rent Payment for {receiptRent.rentMonth}</td>
                    <td className="py-3 px-3 text-right font-black text-slate-800">₹{receiptRent.monthlyRent?.toLocaleString('en-IN')}</td>
                  </tr>
                  <tr className="border-b border-slate-100 bg-slate-50/20">
                    <td className="py-2.5 px-3 text-slate-400">Total Paid Amount</td>
                    <td className="py-2.5 px-3 text-right text-green-600 font-bold">₹{receiptRent.amountPaid?.toLocaleString('en-IN')}</td>
                  </tr>
                  <tr className="font-bold text-slate-800">
                    <td className="py-3 px-3 text-right">Outstanding Due Balance</td>
                    <td className="py-3 px-3 text-right text-slate-400">₹0.00</td>
                  </tr>
                </tbody>
              </table>

              {receiptRent.remarks && (
                <div className="bg-slate-50 border border-slate-200 rounded-lg p-3 text-xs text-slate-500">
                  <b>Remarks:</b> {receiptRent.remarks}
                </div>
              )}

              <div className="text-center text-[10px] text-slate-400 leading-normal">
                Thank you for your payment! If you have any questions, contact management.
              </div>
            </div>

            <div className="flex items-center justify-end gap-2.5">
              <Button variant="secondary" size="sm" onClick={() => setReceiptRent(null)}>
                Close
              </Button>
              <Button
                variant="primary"
                size="sm"
                icon={Printer}
                onClick={() => window.print()}
              >
                Print Receipt
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default RentHistory;
