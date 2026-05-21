import React from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Navigate } from 'react-router-dom';
import { Scale, Sparkles, ArrowRight } from 'lucide-react';

export const Login = () => {
  const { loginWithGoogle, loginAsGuest, isAuthenticated, isGuest, isLoading } = useAuth();
  const navigate = useNavigate();

  // If already authenticated or guest, redirect to dashboard
  if (!isLoading && (isAuthenticated || isGuest)) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleGuest = () => {
    loginAsGuest();
    navigate('/dashboard');
  };

  return (
    <div className="min-h-screen bg-darkBg text-slate-100 flex flex-col justify-center items-center p-4 relative overflow-hidden">
      {/* Background Animated Blobs */}
      <div className="absolute top-[10%] left-[10%] w-72 h-72 bg-indigo-600/10 rounded-full blur-[100px] pointer-events-none animate-blob" />
      <div className="absolute bottom-[10%] right-[10%] w-80 h-80 bg-violet-600/10 rounded-full blur-[100px] pointer-events-none animate-blob animation-delay-2000" />
      <div className="absolute top-[40%] right-[20%] w-64 h-64 bg-cyan-600/10 rounded-full blur-[100px] pointer-events-none animate-blob animation-delay-4000" />

      {/* Main Glass Login Card */}
      <div className="w-full max-w-md glass-panel p-8 rounded-3xl relative z-10 shadow-glass border border-slate-800 flex flex-col items-center">
        {/* Logo Shield */}
        <div className="h-16 w-16 bg-gradient-to-br from-indigo-500 via-violet-500 to-cyan-500 rounded-2xl flex items-center justify-center shadow-glow-indigo mb-6 animate-pulse">
          <Scale className="h-9 w-9 text-white" />
        </div>

        {/* Brand Headings */}
        <h1 className="text-3xl font-extrabold tracking-tight text-center bg-gradient-to-r from-indigo-400 via-violet-400 to-cyan-400 bg-clip-text text-transparent mb-2">
          Precision Metrics
        </h1>
        <p className="text-slate-400 text-sm font-medium text-center mb-8 max-w-[280px]">
          Convert, Compare, and Calculate quantities with ultimate precision.
        </p>

        {/* Integration Buttons */}
        <div className="w-full space-y-4">
          {/* Sign In with Google */}
          <button
            onClick={loginWithGoogle}
            className="w-full py-4 px-6 rounded-xl bg-white text-slate-900 font-bold hover:bg-slate-100 hover:shadow-glow-indigo transition-all duration-300 flex items-center justify-center gap-3 border border-slate-200"
          >
            {/* Google Icon */}
            <svg className="h-5 w-5" viewBox="0 0 24 24" width="24" height="24" xmlns="http://www.w3.org/2000/svg">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" fill="#FBBC05"/>
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" fill="#EA4335"/>
            </svg>
            <span>Continue with Google</span>
          </button>

          {/* Continue as Guest */}
          <button
            onClick={handleGuest}
            className="w-full py-4 px-6 rounded-xl bg-slate-900/50 hover:bg-slate-900 border border-slate-800 hover:border-slate-700 font-bold hover:shadow-glass-hover transition-all duration-300 flex items-center justify-center gap-2 group text-indigo-300"
          >
            <Sparkles className="h-4 w-4 text-indigo-400 group-hover:scale-110 transition-transform" />
            <span>Explore as Guest</span>
            <ArrowRight className="h-4 w-4 group-hover:translate-x-1 transition-transform ml-1" />
          </button>
        </div>

        {/* Footer info */}
        <div className="mt-8 text-center">
          <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">
            Powered by Spring Boot & React
          </p>
        </div>
      </div>
    </div>
  );
};
