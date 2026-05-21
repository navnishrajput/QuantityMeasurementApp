import React, { useState, useEffect } from 'react';
import { quantityService } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
  History as HistoryIcon,
  Filter,
  CheckCircle,
  XCircle,
  RefreshCw,
  Trash2,
  AlertOctagon,
  Sparkles,
  Info
} from 'lucide-react';

export const History = () => {
  const { isGuest, loginWithGoogle } = useAuth();
  
  // States
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  // Filtering states
  const [categoryFilter, setCategoryFilter] = useState('ALL'); // 'ALL', 'LENGTH', 'VOLUME', 'WEIGHT', 'TEMPERATURE'
  const [operationFilter, setOperationFilter] = useState('ALL'); // 'ALL', 'CONVERT', 'COMPARE', 'ADD', 'SUBTRACT', 'DIVIDE'
  const [showOnlyErrors, setShowOnlyErrors] = useState(false);

  // Fetch logs
  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      let data = [];
      if (showOnlyErrors) {
        data = await quantityService.getErrorHistory();
      } else if (categoryFilter !== 'ALL') {
        data = await quantityService.getHistoryByType(categoryFilter);
      } else if (operationFilter !== 'ALL') {
        data = await quantityService.getHistoryByOperation(operationFilter);
      } else {
        // Fetch all - spring boot history doesn't have an 'all' endpoint but we can combine or default to Type: LENGTH as sample
        // Wait, let's pull LENGTH history by default or fetch by type and merge.
        // Let's try combining histories of all types to show a comprehensive dashboard list!
        const [length, volume, weight, temp] = await Promise.all([
          quantityService.getHistoryByType('LENGTH').catch(() => []),
          quantityService.getHistoryByType('VOLUME').catch(() => []),
          quantityService.getHistoryByType('WEIGHT').catch(() => []),
          quantityService.getHistoryByType('TEMPERATURE').catch(() => [])
        ]);
        
        data = [...length, ...volume, ...weight, ...temp];
      }

      // Sort by creation time descending or ID descending
      data.sort((a, b) => b.id - a.id);
      setLogs(data);
    } catch (err) {
      console.error(err);
      setError("Failed to fetch calculation history from server.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!isGuest) {
      fetchLogs();
    } else {
      setLoading(false);
    }
  }, [categoryFilter, operationFilter, showOnlyErrors, isGuest]);

  const handleResetFilters = () => {
    setCategoryFilter('ALL');
    setOperationFilter('ALL');
    setShowOnlyErrors(false);
  };

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Title section */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent flex items-center gap-3">
            <HistoryIcon className="h-8 w-8 text-indigo-400" />
            <span>Audit History Logs</span>
          </h2>
          <p className="text-slate-400 text-sm mt-1">Review audit trails of calculations and metric comparisons.</p>
        </div>

        {!isGuest && (
          <button
            onClick={fetchLogs}
            disabled={loading}
            className="self-start md:self-auto py-2.5 px-4 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-300 font-semibold text-xs transition-all duration-200 flex items-center gap-2"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} />
            <span>Refresh Audit Logs</span>
          </button>
        )}
      </div>

      {/* Guest Mode restricted block */}
      {isGuest ? (
        <div className="glass-panel p-8 lg:p-12 rounded-3xl border border-indigo-500/20 text-center space-y-6 max-w-lg mx-auto shadow-glass-hover">
          <div className="h-16 w-16 rounded-2xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center mx-auto text-indigo-400">
            <AlertOctagon className="h-8 w-8" />
          </div>
          <div className="space-y-2">
            <h3 className="text-xl font-bold text-slate-200">History Restricted</h3>
            <p className="text-slate-400 text-sm leading-relaxed">
              Calculation history logging is a premium feature reserved for registered users. 
              Sign in via Google to unlock persistency and view your history logs.
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
          {/* Filters Bar card */}
          <div className="glass-panel p-5 rounded-2xl border border-slate-900/60 flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="flex flex-wrap items-center gap-4">
              {/* Category Filter dropdown */}
              <div className="space-y-1">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Category</label>
                <select
                  value={categoryFilter}
                  disabled={operationFilter !== 'ALL' || showOnlyErrors}
                  onChange={(e) => setCategoryFilter(e.target.value)}
                  className="py-2.5 px-4 rounded-xl glass-input font-semibold text-xs cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  <option value="ALL">ALL CATEGORIES</option>
                  <option value="LENGTH">LENGTH</option>
                  <option value="VOLUME">VOLUME</option>
                  <option value="WEIGHT">WEIGHT</option>
                  <option value="TEMPERATURE">TEMPERATURE</option>
                </select>
              </div>

              {/* Operation Filter dropdown */}
              <div className="space-y-1">
                <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Operation</label>
                <select
                  value={operationFilter}
                  disabled={categoryFilter !== 'ALL' || showOnlyErrors}
                  onChange={(e) => setOperationFilter(e.target.value)}
                  className="py-2.5 px-4 rounded-xl glass-input font-semibold text-xs cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  <option value="ALL">ALL OPERATIONS</option>
                  <option value="CONVERT">CONVERT</option>
                  <option value="COMPARE">COMPARE</option>
                  <option value="ADD">ADD</option>
                  <option value="SUBTRACT">SUBTRACT</option>
                  <option value="DIVIDE">DIVIDE</option>
                </select>
              </div>

              {/* Show only errors toggle button */}
              <div className="space-y-1 self-end md:self-auto">
                <button
                  onClick={() => {
                    setShowOnlyErrors(!showOnlyErrors);
                    setCategoryFilter('ALL');
                    setOperationFilter('ALL');
                  }}
                  className={`
                    py-2.5 px-4 rounded-xl border text-xs font-semibold transition-all duration-200
                    ${showOnlyErrors 
                      ? 'border-rose-500/40 bg-rose-500/10 text-rose-300 shadow-glow-rose' 
                      : 'border-slate-800 bg-slate-900/30 text-slate-400 hover:text-slate-200'}
                  `}
                >
                  Show Only Errors
                </button>
              </div>
            </div>

            {/* Clear filters action */}
            {(categoryFilter !== 'ALL' || operationFilter !== 'ALL' || showOnlyErrors) && (
              <button
                onClick={handleResetFilters}
                className="self-end md:self-auto text-indigo-400 hover:text-indigo-300 font-bold text-xs flex items-center gap-1 hover:underline decoration-indigo-400"
              >
                <Trash2 className="h-4 w-4" />
                <span>Reset Filters</span>
              </button>
            )}
          </div>

          {/* Loader status */}
          {loading ? (
            <div className="space-y-4">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="h-16 w-full bg-slate-900/20 border border-slate-900/40 rounded-2xl animate-pulse" />
              ))}
            </div>
          ) : error ? (
            <div className="p-8 text-center glass-panel rounded-3xl border border-rose-500/20 bg-rose-500/5">
              <AlertOctagon className="h-12 w-12 text-rose-500 mx-auto mb-3" />
              <p className="text-slate-300 font-medium">{error}</p>
            </div>
          ) : logs.length === 0 ? (
            <div className="p-16 text-center glass-panel rounded-3xl border border-slate-900/60 space-y-3">
              <div className="h-12 w-12 bg-slate-900 border border-slate-800 rounded-2xl flex items-center justify-center mx-auto text-slate-600">
                <Filter className="h-6 w-6" />
              </div>
              <h4 className="font-bold text-slate-400 text-sm">No Audit Logs Found</h4>
              <p className="text-slate-500 text-xs max-w-xs mx-auto">No records match the current filter selection coordinates. Try broadening your criteria.</p>
            </div>
          ) : (
            /* Audit Log Grid */
            <div className="glass-panel rounded-3xl border border-slate-900/60 shadow-glass overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-slate-900 bg-slate-900/10">
                      <th className="py-4 px-6 text-xs font-bold text-slate-400 uppercase tracking-wider">ID</th>
                      <th className="py-4 px-6 text-xs font-bold text-slate-400 uppercase tracking-wider">Operation</th>
                      <th className="py-4 px-6 text-xs font-bold text-slate-400 uppercase tracking-wider">Category</th>
                      <th className="py-4 px-6 text-xs font-bold text-slate-400 uppercase tracking-wider">Calculation Details</th>
                      <th className="py-4 px-6 text-xs font-bold text-slate-400 uppercase tracking-wider">Status</th>
                      <th className="py-4 px-6 text-xs font-bold text-slate-400 uppercase tracking-wider">Created At</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-900/40">
                    {logs.map((log) => (
                      <tr key={log.id} className="hover:bg-slate-900/20 transition-colors">
                        {/* ID */}
                        <td className="py-4 px-6 text-xs font-semibold text-slate-500">#{log.id}</td>
                        
                        {/* Operation Badge */}
                        <td className="py-4 px-6">
                          <span className={`
                            inline-flex items-center px-2.5 py-1 rounded-lg text-[10px] font-bold uppercase tracking-wider
                            ${log.operation === 'CONVERT' && 'bg-blue-500/10 text-blue-400 border border-blue-500/20'}
                            ${log.operation === 'COMPARE' && 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20'}
                            ${(log.operation === 'ADD' || log.operation === 'SUBTRACT' || log.operation === 'DIVIDE') && 'bg-purple-500/10 text-purple-400 border border-purple-500/20'}
                          `}>
                            {log.operation}
                          </span>
                        </td>

                        {/* Category Badge */}
                        <td className="py-4 px-6">
                          <span className="text-xs font-bold text-slate-300">
                            {log.measurementType}
                          </span>
                        </td>

                        {/* Calculation Summary details */}
                        <td className="py-4 px-6">
                          {log.isError ? (
                            <span className="text-xs font-medium text-rose-400 truncate max-w-[260px] block">
                              {log.errorMessage || 'Invalid calculation'}
                            </span>
                          ) : (
                            <div className="text-xs font-medium text-slate-300">
                              {log.operation === 'CONVERT' && (
                                <p>{log.inputValue1} {log.inputUnit1} &rarr; <span className="font-bold text-slate-200">{log.resultValue % 1 === 0 ? log.resultValue : log.resultValue.toFixed(4)}</span> {log.resultUnit}</p>
                              )}
                              {log.operation === 'COMPARE' && (
                                <p>Compare {log.inputValue1} {log.inputUnit1} vs {log.inputValue2} {log.inputUnit2} &rarr; <span className="font-bold text-slate-200">{log.inputValue1 === log.resultValue ? 'Equal' : log.inputValue1 > log.resultValue ? 'Greater' : 'Lesser'}</span></p>
                              )}
                              {(log.operation === 'ADD' || log.operation === 'SUBTRACT' || log.operation === 'DIVIDE') && (
                                <p>{log.inputValue1} {log.inputUnit1} {log.operation === 'ADD' ? '+' : log.operation === 'SUBTRACT' ? '-' : '/'} {log.inputValue2} {log.inputUnit2} = <span className="font-bold text-slate-200">{log.resultValue % 1 === 0 ? log.resultValue : log.resultValue.toFixed(4)}</span> {log.resultUnit}</p>
                              )}
                            </div>
                          )}
                        </td>

                        {/* Status Check badge */}
                        <td className="py-4 px-6">
                          {log.isError ? (
                            <span className="inline-flex items-center gap-1 text-xs font-semibold text-rose-400">
                              <XCircle className="h-4 w-4 text-rose-500" />
                              <span>Errored</span>
                            </span>
                          ) : (
                            <span className="inline-flex items-center gap-1 text-xs font-semibold text-emerald-400">
                              <CheckCircle className="h-4 w-4 text-emerald-500" />
                              <span>Success</span>
                            </span>
                          )}
                        </td>

                        {/* Timestamp */}
                        <td className="py-4 px-6 text-xs font-medium text-slate-500">
                          {log.createdAt ? new Date(log.createdAt).toLocaleString() : 'N/A'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
