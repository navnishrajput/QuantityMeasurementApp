import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('auth_token'));
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isGuest, setIsGuest] = useState(() => {
    return localStorage.getItem('is_guest') === 'true';
  });
  const [isLoading, setIsLoading] = useState(true);

  const handleLogout = useCallback(() => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_name');
    localStorage.removeItem('user_picture');
    localStorage.removeItem('is_guest');
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
    setIsGuest(false);
  }, []);

  // Validate existing token on boot
  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        try {
          const validation = await authService.validateToken();
          if (validation.valid) {
            // Token is valid, sync user details
            setUser({
              email: validation.email || localStorage.getItem('user_email') || '',
              name: validation.name || localStorage.getItem('user_name') || '',
              picture: localStorage.getItem('user_picture') || ''
            });
            setIsAuthenticated(true);
            setIsGuest(false);
          } else {
            // Invalid token
            handleLogout();
          }
        } catch (error) {
          console.error("Token verification failed:", error);
          // If server is down or error occurs, preserve locally stored details but set authenticated to false
          setIsAuthenticated(false);
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, [token, handleLogout]);

  const handleLoginRedirect = useCallback(() => {
    window.location.href = authService.getLoginUrl();
  }, []);

  const handleGuestLogin = useCallback(() => {
    localStorage.setItem('is_guest', 'true');
    localStorage.removeItem('auth_token');
    setIsGuest(true);
    setIsAuthenticated(false);
    setUser(null);
  }, []);

  const handleAuthSuccess = useCallback((newToken, email, name, picture) => {
    localStorage.setItem('auth_token', newToken);
    localStorage.setItem('user_email', email);
    localStorage.setItem('user_name', name);
    localStorage.setItem('user_picture', picture);
    localStorage.removeItem('is_guest');
    
    setToken(newToken);
    setUser({ email, name, picture });
    setIsAuthenticated(true);
    setIsGuest(false);
  }, []);

  return (
    <AuthContext.Provider value={{
      user,
      token,
      isAuthenticated,
      isGuest,
      isLoading,
      loginWithGoogle: handleLoginRedirect,
      logout: handleLogout,
      loginAsGuest: handleGuestLogin,
      handleAuthSuccess
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
