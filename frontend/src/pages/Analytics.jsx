import React, { useState, useEffect } from 'react';
import { quantityService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
  BarChart3,
  RefreshCw,
  Scale,
  Plus,
  Minus,
  Percent,
  Sparkles,
  AlertOctagon
} from 'lucide-react';

export const Analytics = () => {
  const { isGuest, loginWithGoogle } = useAuth();
  
  // States
  const [stats, setStats] = useState({
    CONVERT: 0,
    COMPARE: 0,
    ADD: 0,
    SUBTRACT: 0,
    DIVIDE: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchStats = async () => {
    setLoading(true);
    setError(null);
    try {
      const [convertCount, compareCount, addCount, subCount, divCount] = await Promise.all([
        quantityService.getCountByOperation('CONVERT').catch(() => 0),
        quantityService.getCountByOperation('COMPARE').catch(() => 0),
        quantityService.getCountByOperation('ADD').catch(() => 0),
        quantityService.getCountByOperation('SUBTRACT').catch(() => 0),
        quantityService.getCountByOperation('DIVIDE').catch(() => 0)
      ]);

      setStats({
        CONVERT: convertCount,
        COMPARE: compareCount,
        ADD: addCount,
        SUBTRACT: subCount,
        DIVIDE: divCount
      });
    } catch (err) {
      console.error(err);
      setError("Failed to retrieve operational statistics from backend.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!isGuest) {
      fetchStats();
    } else {
      setLoading(false);
    }
  }, [isGuest]);

  // Total operations helper
  const totalOperations = Object.values(stats).reduce((a, b) => a + b, 0);

  const metrics = [
    {
      title: 'Conversions Done',
      count: stats.CONVERT,
      icon: RefreshCw,
      color: 'from-blue-500/10 via-indigo-500/5 to-transparent border-blue-500/20 text-blue-400'
    },
    {
      title: 'Comparisons Complete',
      count: stats.COMPARE,
      icon: Scale,
      color: 'from-cyan-500/10 via-teal-500/5 to-transparent border-cyan-500/20 text-cyan-400'
    },
    {
      title: 'Additions Made',
      count: stats.ADD,
      icon: Plus,
      color: 'from-emerald-500/10 via-teal-500/5 to-transparent border-emerald-500/20 text-emerald-400'
    },
    {
      title: 'Subtractions Made',
      count: stats.SUBTRACT,
      icon: Minus,
      color: 'from-rose-500/10 via-orange-500/5 to-transparent border-rose-500/20 text-rose-400'
    },
    {
      title: 'Divisions Computed',
      count: stats.DIVIDE,
      icon: Percent,
      color: 'from-purple-500/10 via-pink-500/5 to-transparent border-purple-500/20 text-purple-400'
    }
  ];

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Title */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent flex items-center gap-3">
            <BarChart3 className="h-8 w-8 text-indigo-400" />
            <span>Operational Statistics</span>
          </h2>
          <p className="text-slate-400 text-sm mt-1">Real-time metrics auditing your computation workloads.</p>
        </div>

        {!isGuest && (
          <button
            onClick={fetchStats}
            disabled={loading}
            className="self-start md:self-auto py-2.5 px-4 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-300 font-semibold text-xs transition-all duration-200 flex items-center gap-2"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} />
            <span>Refresh Stats</span>
          </button>
        )}
      </div>

      {/* Guest wall block */}
      {isGuest ? (
        <div className="glass-panel p-8 lg:p-12 rounded-3xl border border-indigo-500/20 text-center space-y-6 max-w-lg mx-auto shadow-glass-hover">
          <div className="h-16 w-16 rounded-2xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center mx-auto text-indigo-400">
            <AlertOctagon className="h-8 w-8" />
          </div>
          <div className="space-y-2">
            <h3 className="text-xl font-bold text-slate-200">Analytics Restricted</h3>
            <p className="text-slate-400 text-sm leading-relaxed">
              Operational usage reports and analytical metrics are premium features. 
              Sign in via Google to log transactions and access the analytics control board.
            </p>
          </div>
          <button
            onClick={loginWithGoogle}
            className="py-3.5 px-6 rounded-xl bg-gradient-to-r from-indigo-600 via-purple-600 to-cyan-600 hover:shadow-glow-indigo text-white font-bold text-sm transition-all duration-300 flex items-center justify-center gap-2 mx-auto"
          >
            <Sparkles className="h-4 w-4" />
            <span>Sign In with Google</span>
          </button>
        </div>
      ) : (
        <>
          {/* Loader */}
          {loading ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="h-32 w-full bg-slate-900/20 border border-slate-900/40 rounded-3xl animate-pulse" />
              ))}
            </div>
          ) : error ? (
            <div className="p-8 text-center glass-panel rounded-3xl border border-rose-500/20 bg-rose-500/5">
              <AlertOctagon className="h-12 w-12 text-rose-500 mx-auto mb-3" />
              <p className="text-slate-300 font-medium">{error}</p>
            </div>
          ) : (
            <div className="space-y-8">
              {/* Total calculations highlight card */}
              <div className="glass-panel p-6 lg:p-8 rounded-3xl border border-slate-900/60 shadow-glass relative overflow-hidden flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div className="absolute inset-0 bg-gradient-to-r from-indigo-500/5 via-cyan-500/5 to-transparent pointer-events-none" />
                <div className="space-y-1 relative z-10">
                  <span className="text-xs font-bold text-indigo-400 uppercase tracking-widest">Aggregate Payload</span>
                  <h3 className="text-2xl font-black text-slate-100">Cumulative Computations</h3>
                  <p className="text-slate-400 text-xs max-w-md mt-1 leading-relaxed">
                    This represents the aggregate sum of conversion, comparison and arithmetic operations stored across your cluster database.
                  </p>
                </div>
                <div className="text-right relative z-10 self-start md:self-auto">
                  <h2 className="text-6xl font-black bg-gradient-to-r from-indigo-200 via-slate-100 to-cyan-200 bg-clip-text text-transparent">{totalOperations}</h2>
                  <p className="text-xs font-bold text-slate-500 uppercase tracking-wider mt-1">Calculations Processed</p>
                </div>
              </div>

              {/* Sub Operations stats grids */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {metrics.map((metric, i) => {
                  const Icon = metric.icon;
                  return (
                    <div
                      key={i}
                      className={`
                        glass-panel p-6 rounded-3xl border relative overflow-hidden bg-gradient-to-br transition-all duration-300 shadow-glass
                        ${metric.color}
                      `}
                    >
                      <div className="flex justify-between items-start mb-4">
                        <div className="p-2.5 rounded-2xl bg-slate-900/80">
                          <Icon className="h-5 w-5" />
                        </div>
                      </div>
                      <h4 className="text-sm font-bold text-slate-400">{metric.title}</h4>
                      <h2 className="text-4xl font-extrabold text-slate-100 mt-2 break-all">{metric.count}</h2>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
