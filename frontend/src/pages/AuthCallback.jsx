import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Scale } from 'lucide-react';

export const AuthCallback = () => {
  const [searchParams] = useSearchParams();
  const { handleAuthSuccess } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get('token');
    const email = searchParams.get('email');
    const name = searchParams.get('name');
    const picture = searchParams.get('picture');

    if (token) {
      // Complete authentications handshake
      handleAuthSuccess(token, email || '', name || '', picture || '');
      
      // Delay navigation slightly for feedback
      const timer = setTimeout(() => {
        navigate('/dashboard', { replace: true });
      }, 1500);

      return () => clearTimeout(timer);
    } else {
      console.error("Token missing in OAuth callback redirect.");
      navigate('/', { replace: true });
    }
  }, [searchParams, handleAuthSuccess, navigate]);

  return (
    <div className="min-h-screen bg-darkBg text-slate-100 flex flex-col justify-center items-center gap-6 relative">
      {/* Background blur glows */}
      <div className="absolute top-[30%] left-[30%] w-72 h-72 bg-indigo-500/10 rounded-full blur-[100px] pointer-events-none animate-pulse" />

      {/* Handshake UI Card */}
      <div className="flex flex-col items-center gap-4 text-center z-10">
        <div className="p-4 bg-indigo-600 rounded-3xl shadow-glow-indigo animate-bounce">
          <Scale className="h-10 w-10 text-white" />
        </div>
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-slate-100">Syncing Profile Details...</h2>
          <p className="text-slate-400 text-sm mt-1">Establishing secure, encrypted connection to Antigravity AI.</p>
        </div>

        {/* Loading Spinner */}
        <div className="relative h-8 w-8 mt-4">
          <div className="absolute inset-0 rounded-full border-4 border-slate-800" />
          <div className="absolute inset-0 rounded-full border-4 border-indigo-500 border-t-transparent animate-spin" />
        </div>
      </div>
    </div>
  );
};
