import React from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sidebar } from './Sidebar';

export const Layout = () => {
  const { isAuthenticated, isGuest, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-darkBg flex flex-col justify-center items-center gap-4">
        {/* Loading Spinner */}
        <div className="relative h-12 w-12">
          <div className="absolute inset-0 rounded-full border-4 border-slate-800" />
          <div className="absolute inset-0 rounded-full border-4 border-indigo-500 border-t-transparent animate-spin" />
        </div>
        <p className="text-slate-400 font-semibold text-sm tracking-wide animate-pulse">Initializing Security Session...</p>
      </div>
    );
  }

  // If not authenticated and not a guest, bounce them back to the login page
  if (!isAuthenticated && !isGuest) {
    return <Navigate to="/" replace />;
  }

  return (
    <div className="min-h-screen bg-darkBg text-slate-100 flex flex-col lg:flex-row relative">
      {/* Background Radial Glow effects */}
      <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] bg-indigo-900/10 rounded-full blur-[120px] pointer-events-none animate-blob" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[50%] h-[50%] bg-cyan-900/10 rounded-full blur-[120px] pointer-events-none animate-blob animation-delay-4000" />

      {/* Main Sidebar */}
      <Sidebar />

      {/* Primary Page Canvas */}
      <main className="flex-1 p-4 lg:p-8 overflow-y-auto max-w-7xl mx-auto w-full z-10 relative">
        <Outlet />
      </main>
    </div>
  );
};
