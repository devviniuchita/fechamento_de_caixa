const API_BASE_URL = "/api";

const ENDPOINTS = {
  AUTH: {
    LOGIN: `${API_BASE_URL}/auth/login`,
    REGISTER: `${API_BASE_URL}/auth/register`,
    ME: `${API_BASE_URL}/auth/me`,
  },
  CASH_CLOSING: {
    BASE: `${API_BASE_URL}/cash-closings`,
    BY_DATE: (date) => `${API_BASE_URL}/cash-closings/date/${date}`,
    BY_RANGE: `${API_BASE_URL}/cash-closings/range`,
    BY_ID: (id) => `${API_BASE_URL}/cash-closings/${id}`,
    UPLOAD_RECEIPT: (id) => `${API_BASE_URL}/cash-closings/${id}/receipt`,
    REPORTS: {
      DAILY: (date) => `${API_BASE_URL}/cash-closings/reports/daily/${date}`,
      WEEKLY: (date) => `${API_BASE_URL}/cash-closings/reports/weekly/${date}`,
      MONTHLY: (date) =>
        `${API_BASE_URL}/cash-closings/reports/monthly/${date}`,
    },
    BACKUP: (id) => `${API_BASE_URL}/cash-closings/${id}/backup`,
  },
};

const TOKEN_KEY = "auth_token";
const USER_KEY = "user_data";

function getAuthToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function setAuthToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

function removeAuthToken() {
  localStorage.removeItem(TOKEN_KEY);
}

function getUser() {
  const userData = localStorage.getItem(USER_KEY);
  return userData ? JSON.parse(userData) : null;
}

function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

function removeUser() {
  localStorage.removeItem(USER_KEY);
}

function isAuthenticated() {
  return !!getAuthToken();
}

function hasRole(role) {
  const user = getUser();
  return user && user.role === role;
}

function isAdmin() {
  return hasRole("ADMIN");
}

function isGerente() {
  return hasRole("GERENTE");
}

function isCaixa() {
  return hasRole("CAIXA");
}

// Export as module
export {
  API_BASE_URL,
  ENDPOINTS,
  getAuthToken,
  setAuthToken,
  removeAuthToken,
  getUser,
  setUser,
  removeUser,
  isAuthenticated,
  hasRole,
  isAdmin,
  isGerente,
  isCaixa,
};
