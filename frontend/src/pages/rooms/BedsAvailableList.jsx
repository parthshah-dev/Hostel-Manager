import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Bed, Search, UserPlus } from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Loader from '../../components/common/Loader';
import SearchBar from '../../components/common/SearchBar';
import EmptyState from '../../components/common/EmptyState';
import Pagination from '../../components/common/Pagination';

const BedsAvailableList = () => {
  const navigate = useNavigate();
  const [bedsAvailableList, setBedsAvailableList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  const fetchBedsAvailable = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get('/api/rooms/beds-available');
      setBedsAvailableList(response.data);
    } catch (error) {
      console.error(error);
      toast.error('Failed to retrieve beds availability.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBedsAvailable();
  }, []);

  // Search filter
  const filteredBeds = bedsAvailableList.filter((item) =>
    item.roomNumber.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Pagination calculation
  const totalPages = Math.ceil(filteredBeds.length / itemsPerPage);
  const paginatedBeds = filteredBeds.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-black text-slate-800 tracking-tight">Beds Available</h1>
        <p className="text-xs text-slate-400 mt-1">
          Monitor rooms with remaining vacancies and assign new tenants directly.
        </p>
      </div>

      {/* Search Bar */}
      <Card bodyClassName="py-4">
        <SearchBar
          value={searchQuery}
          onChange={(val) => {
            setSearchQuery(val);
            setCurrentPage(1);
          }}
          placeholder="Search by room number..."
        />
      </Card>

      {/* List / Table */}
      {loading ? (
        <Loader type="spinner" className="py-20" />
      ) : paginatedBeds.length === 0 ? (
        <EmptyState
          title="All rooms are full"
          description="There are currently no vacant beds available in any room."
          icon={Bed}
        />
      ) : (
        <Card bodyClassName="p-0" className="overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-4">Room Number</th>
                  <th className="px-6 py-4">Beds Available</th>
                  <th className="px-6 py-4">Monthly Rent</th>
                  <th className="px-6 py-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {paginatedBeds.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-6 py-4 font-bold text-slate-800 text-sm">
                      Room {item.roomNumber}
                    </td>
                    <td className="px-6 py-4">
                      <span
                        className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold ${
                          item.bedsAvailable >= 2
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-100'
                            : 'bg-amber-50 text-amber-700 border border-amber-100'
                        }`}
                      >
                        <span
                          className={`w-1.5 h-1.5 rounded-full ${
                            item.bedsAvailable >= 2 ? 'bg-emerald-600' : 'bg-amber-500'
                          }`}
                        />
                        {item.bedsAvailable} {item.bedsAvailable === 1 ? 'bed' : 'beds'} available
                      </span>
                    </td>
                    <td className="px-6 py-4 font-black text-slate-800 text-sm">
                      ₹{item.monthlyRent?.toLocaleString('en-IN')}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <Button
                        variant="secondary"
                        size="xs"
                        icon={UserPlus}
                        onClick={() => navigate(`/tenants/add?roomId=${item.id}`)}
                      >
                        Assign Tenant
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {totalPages > 1 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          )}
        </Card>
      )}
    </div>
  );
};

export default BedsAvailableList;
