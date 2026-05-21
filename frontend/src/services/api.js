import axios from 'axios';

// Configure backend URL explicitly for absolute robustness
const BACKEND_URL = 'http://localhost:8080';

const API = axios.create({
  baseURL: BACKEND_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to automatically inject the Bearer JWT token if stored
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Quantity Measurement API endpoints
export const quantityService = {
  // Convert quantity
  convert: async (value, unit, measurementType, targetUnit) => {
    const response = await API.post('/api/v1/quantities/convert', {
      thisQuantityDTO: { value, unit, measurementType },
      thatQuantityDTO: { value: 0.0, unit: targetUnit, measurementType }
    });
    return response.data;
  },

  // Compare two quantities
  compare: async (value1, unit1, value2, unit2, measurementType) => {
    const response = await API.post('/api/v1/quantities/compare', {
      thisQuantityDTO: { value: value1, unit: unit1, measurementType },
      thatQuantityDTO: { value: value2, unit: unit2, measurementType }
    });
    return response.data;
  },

  // Add two quantities
  add: async (value1, unit1, value2, unit2, measurementType, targetUnit = null) => {
    const response = await API.post('/api/v1/quantities/add', {
      thisQuantityDTO: { value: value1, unit: unit1, measurementType },
      thatQuantityDTO: { value: value2, unit: unit2, measurementType },
      targetUnit
    });
    return response.data;
  },

  // Subtract two quantities
  subtract: async (value1, unit1, value2, unit2, measurementType, targetUnit = null) => {
    const response = await API.post('/api/v1/quantities/subtract', {
      thisQuantityDTO: { value: value1, unit: unit1, measurementType },
      thatQuantityDTO: { value: value2, unit: unit2, measurementType },
      targetUnit
    });
    return response.data;
  },

  // Divide two quantities
  divide: async (value1, unit1, value2, unit2, measurementType) => {
    const response = await API.post('/api/v1/quantities/divide', {
      thisQuantityDTO: { value: value1, unit: unit1, measurementType },
      thatQuantityDTO: { value: value2, unit: unit2, measurementType }
    });
    return response.data;
  },

  // Get operation history
  getHistoryByOperation: async (operation) => {
    const response = await API.get(`/api/v1/quantities/history/operation/${operation.toUpperCase()}`);
    return response.data;
  },

  // Get history by type/category
  getHistoryByType: async (type) => {
    const response = await API.get(`/api/v1/quantities/history/type/${type.toUpperCase()}`);
    return response.data;
  },

  // Get errored history
  getErrorHistory: async () => {
    const response = await API.get('/api/v1/quantities/history/errored');
    return response.data;
  },

  // Get count by operation
  getCountByOperation: async (operation) => {
    const response = await API.get(`/api/v1/quantities/count/${operation.toUpperCase()}`);
    return response.data;
  }
};

// Auth API endpoints
export const authService = {
  // Get redirect login URL for Google
  getLoginUrl: () => {
    return `${BACKEND_URL}/oauth2/authorization/google`;
  },

  // Validate the current token
  validateToken: async () => {
    const response = await API.get('/api/auth/validate');
    return response.data;
  },

  // Get current user information
  getCurrentUser: async () => {
    const response = await API.get('/api/auth/user');
    return response.data;
  }
};

export default API;
