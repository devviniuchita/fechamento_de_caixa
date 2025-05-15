import ApiService from "./api.service.js";
import {
  isAuthenticated,
  setAuthToken,
  setUser,
  removeAuthToken,
  removeUser,
  getUser,
  hasRole,
} from "./config.js";

// Check authentication on page load
document.addEventListener("DOMContentLoaded", async () => {
  if (!isAuthenticated()) {
    showLoginForm();
    return;
  }

  try {
    const user = await ApiService.getCurrentUser();
    setUser(user);
    initializeApp();
  } catch (error) {
    console.error("Failed to get current user:", error);
    showLoginForm();
  }
});

// Login form handling
function showLoginForm() {
  const mainContent = document.getElementById("main-content");
  mainContent.innerHTML = `
        <div class="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
            <div class="sm:mx-auto sm:w-full sm:max-w-sm">
                <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
                    Login
                </h2>
            </div>

            <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
                <form id="login-form" class="space-y-6">
                    <div>
                        <label for="username" class="block text-sm font-medium leading-6 text-gray-900">
                            Usuário
                        </label>
                        <div class="mt-2">
                            <input id="username" name="username" type="text" required
                                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6">
                        </div>
                    </div>

                    <div>
                        <label for="password" class="block text-sm font-medium leading-6 text-gray-900">
                            Senha
                        </label>
                        <div class="mt-2">
                            <input id="password" name="password" type="password" required
                                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6">
                        </div>
                    </div>

                    <div>
                        <button type="submit"
                            class="flex w-full justify-center rounded-md bg-blue-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600">
                            Entrar
                        </button>
                    </div>
                </form>
            </div>
        </div>
    `;

  document
    .getElementById("login-form")
    .addEventListener("submit", async (e) => {
      e.preventDefault();
      const username = document.getElementById("username").value;
      const password = document.getElementById("password").value;

      try {
        const response = await ApiService.login(username, password);
        setAuthToken(response.token);
        setUser({
          username: response.username,
          role: response.role,
        });
        initializeApp();
      } catch (error) {
        console.error("Login failed:", error);
        alert("Falha no login. Verifique suas credenciais.");
      }
    });
}

function initializeApp() {
  // Show/hide elements based on user role
  const user = getUser();
  document.querySelectorAll("[data-role]").forEach((element) => {
    const requiredRole = element.dataset.role;
    if (!hasRole(requiredRole)) {
      element.style.display = "none";
    }
  });

  // Initialize navigation
  initializeNavigation();

  // Show dashboard by default
  showContent("dashboard");

  // Initialize event listeners
  initializeEventListeners();
}

// Navigation
function initializeNavigation() {
  document
    .getElementById("dashboard-link")
    .addEventListener("click", function (e) {
      e.preventDefault();
      showContent("dashboard");
    });

  document
    .getElementById("fechamento-link")
    .addEventListener("click", function (e) {
      e.preventDefault();
      showContent("fechamento");
    });

  document
    .getElementById("relatorios-link")
    .addEventListener("click", function (e) {
      e.preventDefault();
      showContent("relatorios");
    });

  document
    .getElementById("comprovantes-link")
    .addEventListener("click", function (e) {
      e.preventDefault();
      showContent("comprovantes");
    });

  document
    .getElementById("usuarios-link")
    .addEventListener("click", function (e) {
      e.preventDefault();
      showContent("usuarios");
    });

  // Toggle sidebar
  document
    .getElementById("toggle-sidebar")
    .addEventListener("click", function () {
      document.querySelector(".sidebar").classList.toggle("collapsed");
      const icon = this.querySelector("i");
      if (icon.classList.contains("fa-chevron-left")) {
        icon.classList.remove("fa-chevron-left");
        icon.classList.add("fa-chevron-right");
      } else {
        icon.classList.remove("fa-chevron-right");
        icon.classList.add("fa-chevron-left");
      }
    });
}

function showContent(content) {
  // Hide all content
  document.getElementById("dashboard-content").classList.add("hidden");
  document.getElementById("fechamento-content").classList.add("hidden");
  document.getElementById("relatorios-content").classList.add("hidden");
  document.getElementById("comprovantes-content").classList.add("hidden");
  document.getElementById("usuarios-content").classList.add("hidden");

  // Remove active class from all links
  document.getElementById("dashboard-link").classList.remove("active");
  document.getElementById("fechamento-link").classList.remove("active");
  document.getElementById("relatorios-link").classList.remove("active");
  document.getElementById("comprovantes-link").classList.remove("active");
  document.getElementById("usuarios-link").classList.remove("active");

  // Show selected content
  document.getElementById(content + "-content").classList.remove("hidden");
  document.getElementById(content + "-link").classList.add("active");

  // Update page title
  let title = "";
  switch (content) {
    case "dashboard":
      title = "Dashboard";
      loadDashboardData();
      break;
    case "fechamento":
      title = "Fechamento de Caixa";
      break;
    case "relatorios":
      title = "Relatórios";
      break;
    case "comprovantes":
      title = "Comprovantes";
      break;
    case "usuarios":
      title = "Usuários";
      break;
  }
  document.getElementById("page-title").textContent = title;
}

async function loadDashboardData() {
  try {
    const today = new Date().toISOString().split("T")[0];
    const closings = await ApiService.getCashClosingsByDate(today);

    // Update dashboard statistics
    document.getElementById("total-fechamentos").textContent = closings.length;

    let totalVendas = 0;
    let inconsistencias = 0;

    closings.forEach((closing) => {
      totalVendas += closing.sales;
      if (closing.hasInconsistency) {
        inconsistencias++;
      }
    });

    document.getElementById("total-vendas").textContent =
      formatCurrency(totalVendas);
    document.getElementById("total-inconsistencias").textContent =
      inconsistencias;

    // Update recent closings table
    updateRecentClosingsTable(closings);
  } catch (error) {
    console.error("Failed to load dashboard data:", error);
    alert("Erro ao carregar dados do dashboard");
  }
}

function updateRecentClosingsTable(closings) {
  const tbody = document.querySelector("#recent-closings tbody");
  tbody.innerHTML = "";

  closings.forEach((closing) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                ${new Date(closing.date).toLocaleDateString()}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                ${closing.responsibleName}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                ${formatCurrency(closing.sales)}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                ${formatCurrency(closing.totalAssets)}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                  closing.hasInconsistency
                    ? "bg-red-100 text-red-800"
                    : "bg-green-100 text-green-800"
                }">
                    ${closing.hasInconsistency ? "Inconsistente" : "OK"}
                </span>
            </td>
        `;
    tbody.appendChild(tr);
  });
}

// Format currency
function formatCurrency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value);
}

// Calculate totals
function calculateTotals() {
  // Get values from inputs
  const caixaInicial =
    parseFloat(document.getElementById("caixa-inicial").value) || 0;
  const vendas = parseFloat(document.getElementById("vendas").value) || 0;
  const trocoInserido =
    parseFloat(document.getElementById("troco-inserido").value) || 0;
  const dinheiro = parseFloat(document.getElementById("dinheiro").value) || 0;
  const pix = parseFloat(document.getElementById("pix").value) || 0;
  const deposito = parseFloat(document.getElementById("deposito").value) || 0;
  const sangria = parseFloat(document.getElementById("sangria").value) || 0;
  const vale = parseFloat(document.getElementById("vale").value) || 0;
  const visaDebito =
    parseFloat(document.getElementById("visa-debito").value) || 0;
  const masterDebito =
    parseFloat(document.getElementById("master-debito").value) || 0;
  const eloDebito =
    parseFloat(document.getElementById("elo-debito").value) || 0;
  const visaCredito =
    parseFloat(document.getElementById("visa-credito").value) || 0;
  const masterCredito =
    parseFloat(document.getElementById("master-credito").value) || 0;
  const eloCredito =
    parseFloat(document.getElementById("elo-credito").value) || 0;
  const voucher = parseFloat(document.getElementById("voucher").value) || 0;

  // Calculate totals according to spreadsheet logic
  const totalEntradas = caixaInicial + vendas + trocoInserido;
  const totalAtivos = dinheiro + pix + deposito + vale + sangria;
  const totalDebito = visaDebito + masterDebito + eloDebito;
  const totalCredito = visaCredito + masterCredito + eloCredito;
  const totalCartoes = totalDebito + totalCredito + voucher;

  // Calculate total despesas
  let totalDespesas = 0;
  const despesaElements = document.querySelectorAll('[id^="despesa-valor-"]');
  despesaElements.forEach((el) => {
    totalDespesas += parseFloat(el.value) || 0;
  });

  // Calculate total caixa
  const totalCaixa = totalAtivos + totalCartoes + totalDespesas - totalEntradas;

  // Update display
  document.getElementById("total-entradas").textContent =
    formatCurrency(totalEntradas);
  document.getElementById("total-ativos").textContent =
    formatCurrency(totalAtivos);
  document.getElementById("total-debito").textContent =
    formatCurrency(totalDebito);
  document.getElementById("total-credito").textContent =
    formatCurrency(totalCredito);
  document.getElementById("total-cartoes").textContent =
    formatCurrency(totalCartoes);
  document.getElementById("total-despesas").textContent =
    formatCurrency(totalDespesas);
  document.getElementById("total-caixa").textContent =
    formatCurrency(totalCaixa);

  // Verificar consistência
  verificarConsistencia(totalCaixa);

  // Update print version
  updatePrintVersion();

  return {
    caixaInicial,
    vendas,
    trocoInserido,
    totalEntradas,
    totalAtivos,
    totalCartoes,
    totalDespesas,
    totalCaixa,
    paymentMethods: {
      dinheiro,
      pix,
      deposito,
      sangria,
      vale,
      visaDebito,
      masterDebito,
      eloDebito,
      visaCredito,
      masterCredito,
      eloCredito,
      voucher,
    },
  };
}

function verificarConsistencia(totalCaixa) {
  const inconsistenciaMsg = document.getElementById("inconsistencia-message");
  const totalCaixaElement = document.getElementById("total-caixa");

  // Remove todas as classes de cor primeiro
  totalCaixaElement.classList.remove("text-red-600", "text-green-600");

  // Se o total do caixa não for zero, há inconsistência
  if (Math.abs(totalCaixa) > 0.01) {
    inconsistenciaMsg.classList.remove("hidden");
    if (totalCaixa < 0) {
      inconsistenciaMsg.innerHTML = `<i class="fas fa-exclamation-triangle mr-1"></i> Faltando ${formatCurrency(
        Math.abs(totalCaixa)
      )}`;
      totalCaixaElement.classList.add("text-red-600");
    } else {
      inconsistenciaMsg.innerHTML = `<i class="fas fa-exclamation-triangle mr-1"></i> Sobrando ${formatCurrency(
        totalCaixa
      )}`;
      totalCaixaElement.classList.add("text-red-600");
    }
  } else {
    inconsistenciaMsg.classList.add("hidden");
    totalCaixaElement.classList.add("text-green-600");
  }
}

function updatePrintVersion() {
  document.getElementById("print-date").textContent =
    new Date().toLocaleDateString();
  document.getElementById("print-time").textContent =
    new Date().toLocaleTimeString();

  const totalCaixa = document.getElementById("total-caixa").textContent;
  const hasInconsistency = !document
    .getElementById("inconsistencia-message")
    .classList.contains("hidden");

  document.getElementById("print-status").textContent = hasInconsistency
    ? "Inconsistente"
    : "OK";
  document.getElementById("print-status").className = hasInconsistency
    ? "text-red-600"
    : "text-green-600";
}

// Initialize event listeners
function initializeEventListeners() {
  // Add input event listeners for all numeric inputs
  document.querySelectorAll('input[type="number"]').forEach((input) => {
    input.addEventListener("input", calculateTotals);
  });

  // Add event listener for receipt upload
  document
    .querySelector('input[type="file"]')
    .addEventListener("change", async (e) => {
      const file = e.target.files[0];
      if (!file) return;

      try {
        const cashClosingId = getCurrentCashClosingId(); // Implement this function
        await ApiService.uploadReceipt(cashClosingId, file);
        alert("Comprovante enviado com sucesso!");
      } catch (error) {
        console.error("Failed to upload receipt:", error);
        alert("Erro ao enviar comprovante");
      }
    });

  // Add event listener for closing submission
  document
    .getElementById("submit-closing")
    .addEventListener("click", async () => {
      const data = calculateTotals();
      try {
        await ApiService.createCashClosing(data);
        alert("Fechamento salvo com sucesso!");
        showContent("dashboard");
      } catch (error) {
        console.error("Failed to save closing:", error);
        alert("Erro ao salvar fechamento");
      }
    });

  // Add event listener for report generation
  document.querySelectorAll('[id$="-report-btn"]').forEach((button) => {
    button.addEventListener("click", async () => {
      const type = button.id.split("-")[0]; // daily, weekly, or monthly
      const date = document.getElementById(`${type}-date`).value;

      try {
        let response;
        switch (type) {
          case "daily":
            response = await ApiService.generateDailyReport(date);
            break;
          case "weekly":
            response = await ApiService.generateWeeklyReport(date);
            break;
          case "monthly":
            response = await ApiService.generateMonthlyReport(date);
            break;
        }

        // Create blob and download
        const blob = new Blob([response], {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${type}-report-${date}.xlsx`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
      } catch (error) {
        console.error("Failed to generate report:", error);
        alert("Erro ao gerar relatório");
      }
    });
  });
}

// Logout function
window.logout = function () {
  removeAuthToken();
  removeUser();
  showLoginForm();
};
