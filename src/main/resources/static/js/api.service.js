import { ENDPOINTS, getAuthToken } from "./config.js";

class ApiService {
  static async request(endpoint, options = {}) {
    const token = getAuthToken();
    const headers = {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    };

    const config = {
      ...options,
      headers,
    };

    try {
      const response = await fetch(endpoint, config);
      if (!response.ok) {
        throw await response.json();
      }
      return response.json();
    } catch (error) {
      console.error("API Error:", error);
      throw error;
    }
  }

  static async login(username, password) {
    return this.request(ENDPOINTS.AUTH.LOGIN, {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });
  }

  static async register(userData) {
    return this.request(ENDPOINTS.AUTH.REGISTER, {
      method: "POST",
      body: JSON.stringify(userData),
    });
  }

  static async getCurrentUser() {
    return this.request(ENDPOINTS.AUTH.ME);
  }

  static async createCashClosing(data) {
    return this.request(ENDPOINTS.CASH_CLOSING.BASE, {
      method: "POST",
      body: JSON.stringify(data),
    });
  }

  static async updateCashClosing(id, data) {
    return this.request(ENDPOINTS.CASH_CLOSING.BY_ID(id), {
      method: "PUT",
      body: JSON.stringify(data),
    });
  }

  static async deleteCashClosing(id) {
    return this.request(ENDPOINTS.CASH_CLOSING.BY_ID(id), {
      method: "DELETE",
    });
  }

  static async getCashClosingById(id) {
    return this.request(ENDPOINTS.CASH_CLOSING.BY_ID(id));
  }

  static async getCashClosingsByDate(date) {
    return this.request(ENDPOINTS.CASH_CLOSING.BY_DATE(date));
  }

  static async getCashClosingsByDateRange(startDate, endDate) {
    return this.request(
      `${ENDPOINTS.CASH_CLOSING.BY_RANGE}?startDate=${startDate}&endDate=${endDate}`
    );
  }

  static async uploadReceipt(id, file) {
    const formData = new FormData();
    formData.append("file", file);

    return this.request(ENDPOINTS.CASH_CLOSING.UPLOAD_RECEIPT(id), {
      method: "POST",
      headers: {
        // Remove Content-Type to let browser set it with boundary
        "Content-Type": undefined,
      },
      body: formData,
    });
  }

  static async generateDailyReport(date) {
    return this.request(ENDPOINTS.CASH_CLOSING.REPORTS.DAILY(date), {
      headers: {
        Accept: "application/octet-stream",
      },
    });
  }

  static async generateWeeklyReport(date) {
    return this.request(ENDPOINTS.CASH_CLOSING.REPORTS.WEEKLY(date), {
      headers: {
        Accept: "application/octet-stream",
      },
    });
  }

  static async generateMonthlyReport(date) {
    return this.request(ENDPOINTS.CASH_CLOSING.REPORTS.MONTHLY(date), {
      headers: {
        Accept: "application/octet-stream",
      },
    });
  }

  static async backupToGoogleDrive(id) {
    return this.request(ENDPOINTS.CASH_CLOSING.BACKUP(id), {
      method: "POST",
    });
  }
}

export default ApiService;
