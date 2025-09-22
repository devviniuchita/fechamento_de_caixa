/**
 * API Service Layer for Fechamento de Caixa System
 * Handles amunication between frontend and Spring Boot backend
 */

// Base API Configuration
const API_CONFIG = {
  baseURL: window.location.origin,
  endpoints: {
    auth: {
      login: "/auth/login",
      register: "/auth/registrar",
    },
    fechamentos: {
      base: "/api/fechamentos",
      create: "/api/fechamentos",
      list: "/api/fechamentos",
      validate: (id) => `/api/fechamentos/${id}/validar`,
      daily: "/api/fechamentos/diario",
    },
    usuarios: {
      base: "/usuarios",
      list: "/usuarios",
    },
    test: {
      public: "/test/public",
      user: "/test/user",
      admin: "/test/admin",
      resetDev: "/test/reset-dev",
    },
  },
};

// Token Management
class TokenManager {
  static TOKEN_KEY = "fechamento_caixa_token";
  static USER_KEY = "fechamento_caixa_user";

  static getToken() {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  static setToken(token) {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  static removeToken() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  static getUser() {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  static setUser(user) {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  static isAuthenticated() {
    return !!this.getToken();
  }
}

// Base API Client
class BaseAPI {
  constructor() {
    this.baseURL = API_CONFIG.baseURL;
  }

  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    const token = TokenManager.getToken();

    const defaultHeaders = {
      "Content-Type": "application/json",
      Accept: "application/json",
    };

    // optionally skip auth header (e.g., login)
    const skipAuth = options.skipAuth === true;
    if (token && !skipAuth) {
      defaultHeaders["Authorization"] = `Bearer ${token}`;
    }

    const config = {
      method: "GET",
      headers: { ...defaultHeaders, ...options.headers },
      ...options,
    };

    if (config.body && typeof config.body === "object") {
      config.body = JSON.stringify(config.body);
    }

    try {
      console.log(
        `🔄 API Request: ${config.method} ${url}`,
        config.body ? JSON.parse(config.body) : "",
      );

      const response = await fetch(url, config);
      const data = await response.json();

      if (!response.ok) {
        console.error(`❌ API Error: ${response.status}`, data);
        throw new APIError(
          data.message || "Erro na requisição",
          response.status,
          data,
        );
      }

      console.log(`✅ API Success: ${config.method} ${url}`, data);
      return data;
    } catch (error) {
      if (error instanceof APIError) {
        throw error;
      }

      console.error(`❌ Network Error: ${config.method} ${url}`, error);
      throw new APIError("Erro de conexão com o servidor", 0, error);
    }
  }

  async get(endpoint, params = {}) {
    const queryString = new URLSearchParams(params).toString();
    const url = queryString ? `${endpoint}?${queryString}` : endpoint;
    return this.request(url);
  }

  async post(endpoint, data = {}) {
    return this.request(endpoint, {
      method: "POST",
      body: data,
    });
  }

  async put(endpoint, data = {}) {
    return this.request(endpoint, {
      method: "PUT",
      body: data,
    });
  }

  async delete(endpoint) {
    return this.request(endpoint, {
      method: "DELETE",
    });
  }
}

// Custom API Error
class APIError extends Error {
  constructor(message, status, data) {
    super(message);
    this.name = "APIError";
    this.status = status;
    this.data = data;
  }

  isUnauthorized() {
    return this.status === 401;
  }

  isForbidden() {
    return this.status === 403;
  }

  isNotFound() {
    return this.status === 404;
  }

  isValidationError() {
    return this.status === 400;
  }
}

// Authentication API
class AuthAPI extends BaseAPI {
  async login(email, senha) {
    try {
      const response = await this.request(API_CONFIG.endpoints.auth.login, {
        method: "POST",
        body: { email, senha },
        skipAuth: true,
      });

      if (response.token) {
        TokenManager.setToken(response.token);
        const perfis = response.perfis || response.roles || [];
        const perfilLabel =
          Array.isArray(perfis) && perfis.length
            ? perfis[0].replace(/^ROLE_/i, "")
            : "USER";
        TokenManager.setUser({
          id: response.id,
          nome: response.nome,
          email: response.email,
          perfil: perfilLabel,
          perfis,
        });
      }

      return response;
    } catch (error) {
      if (error.isUnauthorized()) {
        throw new APIError("Email ou senha inválidos", 401, error.data);
      }
      throw error;
    }
  }

  async register(userData) {
    return this.post(API_CONFIG.endpoints.auth.register, userData);
  }

  logout() {
    TokenManager.removeToken();
    window.location.reload();
  }

  getCurrentUser() {
    return TokenManager.getUser();
  }

  isAuthenticated() {
    return TokenManager.isAuthenticated();
  }
}

// Fechamento API
class FechamentoAPI extends BaseAPI {
  /**
   * Transform frontend form data to backend DTO format
   */
  transformFormData() {
    // Get basic fields
    const data = document.getElementById("data").value;
    const responsavel = document.getElementById("responsavel").value;
    const caixaInicial =
      parseFloat(document.getElementById("caixa-inicial").value) || 0;
    const vendas = parseFloat(document.getElementById("vendas").value) || 0;
    const trocoInserido =
      parseFloat(document.getElementById("troco-inserido").value) || 0;

    // Get payment methods
    const formasPagamento = {
      dinheiro: parseFloat(document.getElementById("dinheiro").value) || 0,
      pix: parseFloat(document.getElementById("pix").value) || 0,
      deposito: parseFloat(document.getElementById("deposito").value) || 0,
      vale: parseFloat(document.getElementById("vale").value) || 0,
      sangria: parseFloat(document.getElementById("sangria").value) || 0,
      voucher: parseFloat(document.getElementById("voucher").value) || 0,
      debito: {
        visa: parseFloat(document.getElementById("visa-debito").value) || 0,
        master: parseFloat(document.getElementById("master-debito").value) || 0,
        elo: parseFloat(document.getElementById("elo-debito").value) || 0,
      },
      credito: {
        visa: parseFloat(document.getElementById("visa-credito").value) || 0,
        master:
          parseFloat(document.getElementById("master-credito").value) || 0,
        elo: parseFloat(document.getElementById("elo-credito").value) || 0,
      },
    };

    // Get expenses
    const despesas = [];
    document.querySelectorAll('[id^="despesa-desc-"]').forEach((descEl) => {
      const id = descEl.id.split("-")[2];
      const valorEl = document.getElementById(`despesa-valor-${id}`);

      if (descEl.value && valorEl.value) {
        despesas.push({
          descricao: descEl.value,
          valor: parseFloat(valorEl.value) || 0,
        });
      }
    });

    return {
      data,
      responsavel,
      caixaInicial,
      vendas,
      trocoInserido,
      formasPagamento,
      despesas,
      observacoes: "", // Can be added later
      comprovantes: [], // File uploads can be added later
    };
  }

  async criarFechamento() {
    const fechamentoData = this.transformFormData();
    return this.post(API_CONFIG.endpoints.fechamentos.create, fechamentoData);
  }

  async listarFechamentos(params = {}) {
    return this.get(API_CONFIG.endpoints.fechamentos.list, params);
  }

  async buscarFechamento(id) {
    return this.get(`${API_CONFIG.endpoints.fechamentos.base}/${id}`);
  }

  async validarFechamento(id) {
    return this.post(API_CONFIG.endpoints.fechamentos.validate(id));
  }

  async criarFechamentoDiario() {
    const fechamentoData = this.transformFormData();
    return this.post(API_CONFIG.endpoints.fechamentos.daily, fechamentoData);
  }
}

// Usuario API
class UsuarioAPI extends BaseAPI {
  async listarUsuarios() {
    return this.get(API_CONFIG.endpoints.usuarios.list);
  }

  async buscarUsuario(id) {
    return this.get(`${API_CONFIG.endpoints.usuarios.base}/${id}`);
  }
}

// Test API
class TestAPI extends BaseAPI {
  async testPublic() {
    return this.get(API_CONFIG.endpoints.test.public);
  }

  async testUser() {
    return this.get(API_CONFIG.endpoints.test.user);
  }

  async testAdmin() {
    return this.get(API_CONFIG.endpoints.test.admin);
  }

  async resetDev() {
    return this.post(API_CONFIG.endpoints.test.resetDev, {});
  }
}

// Main API Service - Singleton pattern
class APIService {
  constructor() {
    this.auth = new AuthAPI();
    this.fechamentos = new FechamentoAPI();
    this.usuarios = new UsuarioAPI();
    this.test = new TestAPI();
  }

  // Utility methods
  formatCurrency(value) {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(value);
  }

  formatDate(date) {
    return new Date(date).toLocaleDateString("pt-BR");
  }

  showNotification(message, type = "info") {
    // Create notification element
    const notification = document.createElement("div");
    notification.className = `fixed top-4 right-4 z-50 p-4 rounded-md shadow-lg max-w-sm ${
      type === "success"
        ? "bg-green-500 text-white"
        : type === "error"
          ? "bg-red-500 text-white"
          : type === "warning"
            ? "bg-yellow-500 text-black"
            : "bg-blue-500 text-white"
    }`;

    notification.innerHTML = `
      <div class="flex items-center justify-between">
        <span>${message}</span>
        <button onclick="this.parentElement.parentElement.remove()" class="ml-2 text-lg">&times;</button>
      </div>
    `;

    document.body.appendChild(notification);

    // Auto remove after 5 seconds
    setTimeout(() => {
      if (notification.parentElement) {
        notification.remove();
      }
    }, 5000);
  }

  showLoading(show = true) {
    let loader = document.getElementById("api-loader");

    if (show && !loader) {
      loader = document.createElement("div");
      loader.id = "api-loader";
      loader.className =
        "fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50";
      loader.innerHTML = `
        <div class="bg-white p-6 rounded-lg shadow-lg">
          <div class="flex items-center space-x-3">
            <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
            <span>Processando...</span>
          </div>
        </div>
      `;
      document.body.appendChild(loader);
    } else if (!show && loader) {
      loader.remove();
    }
  }
}

// Export singleton instance
const API = new APIService();

// Export classes for advanced usage
window.APIService = APIService;
window.APIError = APIError;
window.TokenManager = TokenManager;
window.API = API;

console.log("✅ API Service initialized successfully");
