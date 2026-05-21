import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  Scale, 
  History, 
  BarChart3, 
  LogOut, 
  Menu, 
  X, 
  Sparkles,
  UserCheck
} from 'lucide-react';

export const Sidebar = () => {
  const { user, isGuest, logout } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const navItems = [
    { to: '/dashboard', label: 'Interactive Hub', icon: Scale },
    { to: '/history', label: 'Audit History', icon: History },
    { to: '/analytics', label: 'Usage Stats', icon: BarChart3 },
  ];

  return (
    <>
      {/* Mobile Toggle Bar */}
      <div className="lg:hidden w-full bg-slate-900/90 backdrop-blur-md border-b border-slate-800 px-4 py-3 flex justify-between items-center sticky top-0 z-40">
        <div className="flex items-center gap-2">
          <div className="p-2 bg-indigo-600 rounded-lg shadow-glow-indigo">
            <Scale className="h-5 w-5 text-white" />
          </div>
          <span className="font-bold text-lg tracking-tight bg-gradient-to-r from-indigo-400 via-violet-400 to-cyan-400 bg-clip-text text-transparent">
            Quantities
          </span>
        </div>
        <button 
          onClick={() => setIsOpen(!isOpen)}
          className="p-2 text-slate-400 hover:text-white transition-colors focus:outline-none"
        >
          {isOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
        </button>
      </div>

      {/* Desktop & Mobile Sidebar Menu */}
      <aside className={`
        fixed inset-y-0 left-0 z-30 w-72 bg-slate-950/70 backdrop-blur-xl border-r border-slate-900 flex flex-col justify-between p-6 transform transition-transform duration-300 ease-in-out lg:translate-x-0
        ${isOpen ? 'translate-x-0' : '-translate-x-full'}
        lg:sticky lg:h-screen
      `}>
        {/* Top Branding Section */}
        <div className="space-y-8">
          <div className="flex items-center gap-3 px-2">
            <div className="p-2.5 bg-indigo-600 rounded-xl shadow-glow-indigo transition-transform hover:scale-105">
              <Scale className="h-6 w-6 text-white" />
            </div>
            <div>
              <span className="font-extrabold text-xl tracking-tight bg-gradient-to-r from-indigo-400 via-violet-400 to-cyan-400 bg-clip-text text-transparent">
                Quantities
              </span>
              <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">PRO</p>
            </div>
          </div>

          {/* Navigation Links */}
          <nav className="space-y-1">
            {navItems.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                onClick={() => setIsOpen(false)}
                className={({ isActive }) => `
                  flex items-center gap-3 px-4 py-3.5 rounded-xl font-medium text-sm transition-all duration-200 group
                  ${isActive 
                    ? 'bg-gradient-to-r from-indigo-600/30 to-violet-600/10 text-indigo-200 border-l-4 border-indigo-500 shadow-sm shadow-indigo-500/5' 
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/50 border-l-4 border-transparent'}
                `}
              >
                {({ isActive }) => (
                  <>
                    <item.icon className={`h-5 w-5 transition-colors group-hover:scale-105 duration-200 ${isActive ? 'text-indigo-400' : 'text-slate-400 group-hover:text-slate-300'}`} />
                    <span>{item.label}</span>
                  </>
                )}
              </NavLink>
            ))}
          </nav>
        </div>

        {/* User Card at bottom */}
        <div className="space-y-4 pt-4 border-t border-slate-900">
          {/* Guest or User display */}
          <div className="p-3.5 rounded-xl bg-slate-900/40 border border-slate-900 flex items-center gap-3">
            {isGuest ? (
              <>
                <div className="h-10 w-10 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center">
                  <UserCheck className="h-5 w-5 text-indigo-400" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-slate-200 truncate">Guest Session</p>
                  <span className="inline-flex items-center gap-1 text-[10px] font-bold text-slate-500 uppercase tracking-wide">
                    <Sparkles className="h-3 w-3 text-amber-500" /> Sandbox
                  </span>
                </div>
              </>
            ) : (
              <>
                {user?.picture ? (
                  <img 
                    src={user.picture} 
                    alt={user.name} 
                    className="h-10 w-10 rounded-full border border-indigo-500/50 shadow-sm"
                    referrerPolicy="no-referrer"
                  />
                ) : (
                  <div className="h-10 w-10 rounded-full bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center font-bold text-white shadow-sm">
                    {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
                  </div>
                )}
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-slate-200 truncate">{user?.name || 'User'}</p>
                  <p className="text-xs text-slate-400 truncate">{user?.email || 'Authenticated'}</p>
                </div>
              </>
            )}
          </div>

          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-4 py-3 rounded-xl border border-rose-500/20 text-rose-400 font-semibold text-sm bg-rose-500/5 hover:bg-rose-500/10 hover:border-rose-500/30 transition-all duration-200 shadow-sm"
          >
            <LogOut className="h-4 w-4" />
            <span>End Session</span>
          </button>
        </div>
      </aside>

      {/* Dimmed backdrop when mobile drawer is open */}
      {isOpen && (
        <div 
          onClick={() => setIsOpen(false)}
          className="lg:hidden fixed inset-0 bg-black/60 z-20 backdrop-blur-sm"
        />
      )}
    </>
  );
};
