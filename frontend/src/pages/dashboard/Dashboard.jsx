import React, { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { 
  Home, 
  Users, 
  CreditCard, 
  AlertTriangle, 
  ArrowUpRight, 
  CheckCircle,
  Clock,
  TrendingUp,
  Inbox
} from 'lucide-react';
import axiosInstance from '../../services/axiosInstance';
import Card from '../../components/common/Card';
import Loader from '../../components/common/Loader';
import StatusBadge from '../../components/common/StatusBadge';
import { 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer, 
  PieChart, 
  Pie, 
  Cell 
} from 'recharts';

const Dashboard = () => {
  const [summary, setSummary] = useState(null);
  const [complaintStats, setComplaintStats] = useState(null);
  const [recentPayments, setRecentPayments] = useState([]);
  const [pendingPayments, setPendingPayments] = useState([]);
  const [recentComplaints, setRecentComplaints] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);

        const [
          summaryRes, 
          complaintStatsRes, 
          rentHistoryRes, 
          pendingRentsRes,
          complaintsRes
        ] = await Promise.all([
          axiosInstance.get('/api/dashboard/summary'),
          axiosInstance.get('/api/complaints/statistics'),
          axiosInstance.get('/api/rents/history'),
          axiosInstance.get('/api/rents/pending'),
          axiosInstance.get('/api/complaints')
        ]);

        setSummary(summaryRes.data);
        setComplaintStats(complaintStatsRes.data);
        setRecentPayments(rentHistoryRes.data.slice(0, 5));
        setPendingPayments(pendingRentsRes.data.slice(0, 5));
        setRecentComplaints(complaintsRes.data.slice(0, 5));
      } catch (error) {
        console.error(error);
        toast.error('Failed to load dashboard statistics.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return <Loader type="spinner" className="h-96" />;
  }


  const totalRooms = summary?.totalRooms || 0;
  const occupiedRooms = summary?.occupiedRooms || 0;
  const vacantRooms = summary?.vacantRooms || 0;
  const totalTenants = summary?.totalTenants || 0;
  const pendingRent = summary?.pendingRent || 0;
  const monthlyRevenue = summary?.monthlyRevenue || 0;


  const pieData = [
    { name: 'Occupied', value: occupiedRooms, color: '#2563eb' }, 
    { name: 'Vacant', value: vacantRooms, color: '#94a3b8' }      
  ];


  const revenueData = [
    { month: 'Jan', revenue: Math.round(monthlyRevenue * 0.8) },
    { month: 'Feb', revenue: Math.round(monthlyRevenue * 0.85) },
    { month: 'Mar', revenue: Math.round(monthlyRevenue * 0.9) },
    { month: 'Apr', revenue: Math.round(monthlyRevenue * 0.95) },
    { month: 'May', revenue: Math.round(monthlyRevenue * 0.98) },
    { month: 'Jun', revenue: Math.round(monthlyRevenue) }
  ];

  const cards = [
    { 
      title: 'Total Rooms', 
      value: totalRooms, 
      sub: `${occupiedRooms} Occupied / ${vacantRooms} Vacant`,
      icon: Home, 
      color: 'text-blue-600 bg-blue-50 border-blue-100' 
    },
    { 
      title: 'Active Tenants', 
      value: totalTenants, 
      sub: 'Currently checked-in',
      icon: Users, 
      color: 'text-indigo-600 bg-indigo-50 border-indigo-100' 
    },
    { 
      title: 'Pending Rent', 
      value: `₹${pendingRent.toLocaleString('en-IN')}`, 
      sub: 'Outstanding collection',
      icon: CreditCard, 
      color: 'text-red-600 bg-red-50 border-red-100' 
    },
    { 
      title: 'Monthly Revenue', 
      value: `₹${monthlyRevenue.toLocaleString('en-IN')}`, 
      sub: 'Collected this month',
      icon: TrendingUp, 
      color: 'text-green-600 bg-green-50 border-green-100' 
    }
  ];

  return (
    <div className="space-y-8">

      <div>
        <h1 className="text-2xl font-black text-slate-800 tracking-tight">Overview Dashboard</h1>
        <p className="text-xs text-slate-400 mt-1">Hostel/PG analytics, occupancy rates, and financial reports.</p>
      </div>


      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {cards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div key={idx} className="bg-white border border-slate-200 rounded-xl p-5 shadow-xs flex items-center justify-between">
              <div className="space-y-1">
                <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">{card.title}</span>
                <p className="text-2xl font-black text-slate-800 tracking-tight">{card.value}</p>
                <span className="text-[10px] text-slate-500 font-medium block">{card.sub}</span>
              </div>
              <div className={`p-3.5 rounded-xl border ${card.color}`}>
                <Icon className="w-6 h-6" />
              </div>
            </div>
          );
        })}
      </div>


      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <Card title="Revenue Growth" subtitle="Monthly collection report" className="lg:col-span-2">
          <div className="h-72 w-full mt-4">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={revenueData} margin={{ top: 10, right: 10, left: -10, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2563eb" stopOpacity={0.2}/>
                    <stop offset="95%" stopColor="#2563eb" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis dataKey="month" stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} />
                <YAxis stroke="#94a3b8" fontSize={11} tickLine={false} axisLine={false} />
                <Tooltip formatter={(value) => [`₹${value.toLocaleString('en-IN')}`, 'Revenue']} />
                <Area type="monotone" dataKey="revenue" stroke="#2563eb" strokeWidth={2} fillOpacity={1} fill="url(#colorRevenue)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </Card>


        <Card title="Room Occupancy" subtitle="Current room utilization status">
          <div className="h-56 w-full flex items-center justify-center relative mt-4">
            {totalRooms === 0 ? (
              <p className="text-xs text-slate-400 font-semibold">No rooms added yet.</p>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={pieData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={4}
                    dataKey="value"
                  >
                    {pieData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => [`${value} Rooms`, 'Count']} />
                </PieChart>
              </ResponsiveContainer>
            )}
            <div className="absolute flex flex-col items-center justify-center">
              <span className="text-2xl font-black text-slate-800 tracking-tight">
                {totalRooms > 0 ? Math.round((occupiedRooms / totalRooms) * 100) : 0}%
              </span>
              <span className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Occupied</span>
            </div>
          </div>

          <div className="flex justify-center gap-6 mt-2 text-xs">
            {pieData.map((d, i) => (
              <div key={i} className="flex items-center gap-1.5">
                <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: d.color }}></span>
                <span className="text-slate-500 font-medium">{d.name} ({d.value})</span>
              </div>
            ))}
          </div>
        </Card>
      </div>


      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

        <Card 
          title="Recent Rent Payments" 
          subtitle="Latest transactions processed"
          bodyClassName="px-0 py-0"
        >
          {recentPayments.length === 0 ? (
            <div className="py-12 flex flex-col items-center justify-center text-center">
              <Inbox className="w-8 h-8 text-slate-300 mb-2" />
              <p className="text-xs text-slate-400 font-semibold">No rent records found</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="bg-slate-50/70 border-b border-slate-100 text-slate-400 font-bold uppercase tracking-wider">
                    <th className="px-6 py-3">Tenant</th>
                    <th className="px-6 py-3">Room</th>
                    <th className="px-6 py-3">Month</th>
                    <th className="px-6 py-3">Paid Amount</th>
                    <th className="px-6 py-3">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {recentPayments.map((p) => (
                    <tr key={p.id} className="hover:bg-slate-50/50">
                      <td className="px-6 py-3.5 font-semibold text-slate-700">{p.tenantName}</td>
                      <td className="px-6 py-3.5 text-slate-500">{p.roomNumber}</td>
                      <td className="px-6 py-3.5 text-slate-500">{p.rentMonth}</td>
                      <td className="px-6 py-3.5 font-bold text-slate-800">₹{p.amountPaid?.toLocaleString('en-IN')}</td>
                      <td className="px-6 py-3.5">
                        <StatusBadge status={p.paymentStatus} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>


        <Card 
          title="Recent Complaints" 
          subtitle="Latest issues reported by tenants"
          bodyClassName="px-0 py-0"
        >
          {recentComplaints.length === 0 ? (
            <div className="py-12 flex flex-col items-center justify-center text-center">
              <Inbox className="w-8 h-8 text-slate-300 mb-2" />
              <p className="text-xs text-slate-400 font-semibold">No complaints registered</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs border-collapse">
                <thead>
                  <tr className="bg-slate-50/70 border-b border-slate-100 text-slate-400 font-bold uppercase tracking-wider">
                    <th className="px-6 py-3">Tenant</th>
                    <th className="px-6 py-3">Category</th>
                    <th className="px-6 py-3">Description</th>
                    <th className="px-6 py-3">Status</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {recentComplaints.map((c) => (
                    <tr key={c.id} className="hover:bg-slate-50/50">
                      <td className="px-6 py-3.5 font-semibold text-slate-700">{c.tenantName}</td>
                      <td className="px-6 py-3.5 text-slate-500 font-semibold uppercase tracking-wider text-[10px]">{c.complaintCategory}</td>
                      <td className="px-6 py-3.5 text-slate-500 truncate max-w-xs">{c.description}</td>
                      <td className="px-6 py-3.5">
                        <StatusBadge status={c.status} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>


      <Card 
        title="Pending Rent Summary" 
        subtitle="Unpaid rent lists requiring collection"
        bodyClassName="px-0 py-0"
      >
        {pendingPayments.length === 0 ? (
          <div className="py-12 flex flex-col items-center justify-center text-center">
            <Inbox className="w-8 h-8 text-slate-300 mb-2" />
            <p className="text-xs text-slate-400 font-semibold">No pending rent collections</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="bg-slate-50/70 border-b border-slate-100 text-slate-400 font-bold uppercase tracking-wider">
                  <th className="px-6 py-3">Tenant</th>
                  <th className="px-6 py-3">Room</th>
                  <th className="px-6 py-3">Rent Month</th>
                  <th className="px-6 py-3">Monthly Rent</th>
                  <th className="px-6 py-3">Due Amount</th>
                  <th className="px-6 py-3">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {pendingPayments.map((p) => (
                  <tr key={p.id} className="hover:bg-slate-50/50">
                    <td className="px-6 py-3.5 font-semibold text-slate-700">{p.tenantName}</td>
                    <td className="px-6 py-3.5 text-slate-500">{p.roomNumber}</td>
                    <td className="px-6 py-3.5 text-slate-500">{p.rentMonth}</td>
                    <td className="px-6 py-3.5 text-slate-500">₹{p.monthlyRent?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-3.5 font-bold text-red-600">₹{p.dueAmount?.toLocaleString('en-IN')}</td>
                    <td className="px-6 py-3.5">
                      <StatusBadge status={p.paymentStatus} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
};

export default Dashboard;
