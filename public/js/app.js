// ============================================================================
// FECHAMENTO DE CAIXA - MAIN APPLICATION
// ============================================================================

// Check authentication on page load
document.addEventListener("DOMContentLoaded", function () {
  checkAuthentication();
  loadDashboardData();
});

// Authentication check
function checkAuthentication() {
  if (!API.auth.isAuthenticated()) {
    showLoginModal();
    return false;
  }

  const user = API.auth.getCurrentUser();
  if (user) {
    updateUserInfo(user);
  }

  return true;
}

// Update user info in header
function updateUserInfo(user) {
  const userNameElement = document.querySelector(
    ".flex.items-center.space-x-2 span",
  );
  const userRoleElement = document.querySelector(".text-xs.bg-blue-100");
  const userAvatarElement = document.querySelector(
    ".h-8.w-8.rounded-full span",
  );

  if (userNameElement) userNameElement.textContent = user.nome;
  if (userRoleElement) userRoleElement.textContent = user.perfil;
  if (userAvatarElement)
    userAvatarElement.textContent = user.nome.charAt(0).toUpperCase();
}

// Show login modal
function showLoginModal() {
  const modal = document.getElementById("login-modal");
  if (modal) {
    modal.classList.remove("hidden");
  }
}

// Hide login modal
function hideLoginModal() {
  const modal = document.getElementById("login-modal");
  if (modal) {
    modal.classList.add("hidden");
  }
}

// Handle login form submission
async function handleLogin(event) {
  event.preventDefault();

  const email = document.getElementById("login-email").value;
  const senha = document.getElementById("login-senha").value;

  console.log("🔄 Tentando login com:", { email, senha: "***" });

  if (!email || !senha) {
    API.showNotification("Por favor, preencha todos os campos", "error");
    return;
  }

  try {
    API.showLoading(true);

    const response = await API.auth.login(email, senha);

    console.log("✅ Login realizado com sucesso:", response);

    API.showNotification(`Bem-vindo, ${response.nome}!`, "success");
    hideLoginModal();
    updateUserInfo(response);
    loadDashboardData();
  } catch (error) {
    console.error("❌ Erro no login:", error);
    API.showNotification(error.message || "Erro ao fazer login", "error");
  } finally {
    API.showLoading(false);
  }
}

// Handle logout
function handleLogout() {
  if (confirm("Deseja realmente sair do sistema?")) {
    API.auth.logout();
  }
}

// Load dashboard data from backend
async function loadDashboardData() {
  if (!API.auth.isAuthenticated()) return;

  try {
    const fechamentos = await API.fechamentos.listarFechamentos({
      size: 10,
      sort: "data,desc",
    });

    updateDashboardStats(fechamentos);
    updateFechamentosTable(fechamentos);
  } catch (error) {
    console.error("Error loading dashboard data:", error);
    if (error.isUnauthorized()) {
      API.auth.logout();
    } else {
      API.showNotification("Erro ao carregar dados do dashboard", "error");
    }
  }
}

// Update dashboard statistics
function updateDashboardStats(fechamentosData) {
  const fechamentos = fechamentosData.content || fechamentosData;

  // Count today's fechamentos
  const today = new Date().toISOString().split("T")[0];
  const fechamentosHoje = fechamentos.filter((f) => f.data === today).length;

  // Calculate total sales
  const totalVendas = fechamentos.reduce((sum, f) => sum + (f.vendas || 0), 0);

  // Count inconsistencies
  const inconsistencias = fechamentos.filter(
    (f) => f.status === "INCONSISTENTE",
  ).length;

  // Update UI
  document.querySelector(".grid .bg-white:nth-child(1) h3").textContent =
    fechamentosHoje;
  document.querySelector(".grid .bg-white:nth-child(2) h3").textContent =
    API.formatCurrency(totalVendas);
  document.querySelector(".grid .bg-white:nth-child(3) h3").textContent =
    inconsistencias;
}

// Update fechamentos table
function updateFechamentosTable(fechamentosData) {
  const fechamentos = fechamentosData.content || fechamentosData;
  const tbody = document.querySelector("#dashboard-content tbody");

  if (!tbody) return;

  tbody.innerHTML = "";

  fechamentos.slice(0, 5).forEach((fechamento) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
        ${API.formatDate(fechamento.data)}
      </td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
        ${fechamento.responsavel}
      </td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
        ${API.formatCurrency(fechamento.vendas)}
      </td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
        ${API.formatCurrency(fechamento.totalCaixa)}
      </td>
      <td class="px-6 py-4 whitespace-nowrap">
        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
          fechamento.status === "OK"
            ? "bg-green-100 text-green-800"
            : "bg-red-100 text-red-800"
        }">
          ${fechamento.status === "OK" ? "OK" : "Inconsistente"}
        </span>
      </td>
    `;
    tbody.appendChild(row);
  });
}

// Navigation
document
  .getElementById("dashboard-link")
  .addEventListener("click", function (e) {
    e.preventDefault();
    if (checkAuthentication()) {
      showContent("dashboard");
      loadDashboardData();
    }
  });

document
  .getElementById("fechamento-link")
  .addEventListener("click", function (e) {
    e.preventDefault();
    if (checkAuthentication()) {
      showContent("fechamento");
    }
  });

document
  .getElementById("relatorios-link")
  .addEventListener("click", function (e) {
    e.preventDefault();
    if (checkAuthentication()) {
      showContent("relatorios");
    }
  });

document
  .getElementById("comprovantes-link")
  .addEventListener("click", function (e) {
    e.preventDefault();
    if (checkAuthentication()) {
      showContent("comprovantes");
    }
  });

document
  .getElementById("usuarios-link")
  .addEventListener("click", function (e) {
    e.preventDefault();
    if (checkAuthentication()) {
      showContent("usuarios");
      loadUsuarios();
    }
  });

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

// Format currency
function formatCurrency(value) {
  return (
    "R$ " +
    value
      .toFixed(2)
      .replace(".", ",")
      .replace(/(\d)(?=(\d{3})+\,)/g, "$1.")
  );
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

  // Calculate totals
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

  // Check consistency
  verificarConsistencia(totalCaixa);
}

// Check consistency
function verificarConsistencia(totalCaixa) {
  const inconsistenciaMsg = document.getElementById("inconsistencia-message");
  const totalCaixaElement = document.getElementById("total-caixa");

  // Remove color classes
  totalCaixaElement.classList.remove(
    "text-red-600",
    "text-green-600",
    "text-blue-600",
  );

  if (Math.abs(totalCaixa) > 0.01) {
    inconsistenciaMsg.classList.remove("hidden");

    if (totalCaixa < 0) {
      inconsistenciaMsg.innerHTML = `<i class="fas fa-exclamation-triangle mr-1"></i> Está faltando ${formatCurrency(Math.abs(totalCaixa))} no caixa!`;
      totalCaixaElement.classList.add("text-red-600");
    } else {
      inconsistenciaMsg.innerHTML = `<i class="fas fa-exclamation-triangle mr-1"></i> <span class="text-blue-600">Está sobrando ${formatCurrency(totalCaixa)} no caixa!</span>`;
      totalCaixaElement.classList.add("text-blue-600");
    }
  } else {
    inconsistenciaMsg.classList.add("hidden");
    totalCaixaElement.classList.add("text-green-600");
  }
}

// Handle fechamento submission
async function finalizarFechamento() {
  if (!API.auth.isAuthenticated()) {
    showLoginModal();
    return;
  }

  try {
    API.showLoading(true);

    const response = await API.fechamentos.criarFechamento();

    API.showNotification("Fechamento realizado com sucesso!", "success");
    loadDashboardData();

    console.log("Fechamento criado:", response);
  } catch (error) {
    console.error("Error creating fechamento:", error);

    if (error.isUnauthorized()) {
      API.auth.logout();
    } else if (error.isValidationError()) {
      API.showNotification(
        "Dados inválidos. Verifique os campos preenchidos.",
        "error",
      );
    } else {
      API.showNotification(
        error.message || "Erro ao finalizar fechamento",
        "error",
      );
    }
  } finally {
    API.showLoading(false);
  }
}

// Load usuarios data
async function loadUsuarios() {
  if (!API.auth.isAuthenticated()) return;

  try {
    const usuarios = await API.usuarios.listarUsuarios();
    updateUsuariosTable(usuarios);
  } catch (error) {
    console.error("Error loading usuarios:", error);
    if (error.isUnauthorized()) {
      API.auth.logout();
    } else {
      API.showNotification("Erro ao carregar usuários", "error");
    }
  }
}

// Update usuarios table
function updateUsuariosTable(usuarios) {
  // Implementation depends on your usuarios table structure
  console.log("Usuários loaded:", usuarios);
}

// Add despesa
document
  .getElementById("adicionar-despesa")
  .addEventListener("click", function () {
    const despesasContainer = document.getElementById("despesas-container");
    const newId = despesasContainer.children.length + 1;
    const newDespesa = document.createElement("div");
    newDespesa.className = "grid grid-cols-1 md:grid-cols-3 gap-4 mb-4";
    newDespesa.innerHTML = `
    <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="despesa-desc-${newId}">Descrição</label>
        <input type="text" id="despesa-desc-${newId}" class="input-highlight w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
    </div>
    <div>
        <label class="block text-gray-700 text-sm font-bold mb-2" for="despesa-valor-${newId}">Valor</label>
        <div class="relative">
            <span class="absolute left-3 top-2">R$</span>
            <input type="number" step="0.01" id="despesa-valor-${newId}" class="input-highlight w-full pl-8 pr-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>
    </div>
    <div class="flex items-end">
        <button class="bg-red-500 hover:bg-red-600 text-white px-3 py-2 rounded-md text-sm" onclick="removerDespesa(${newId})">
            <i class="fas fa-trash"></i> Remover
        </button>
    </div>
  `;
    despesasContainer.appendChild(newDespesa);

    // Add event listeners to new inputs
    document
      .getElementById(`despesa-desc-${newId}`)
      .addEventListener("input", calculateTotals);
    document
      .getElementById(`despesa-valor-${newId}`)
      .addEventListener("input", calculateTotals);

    calculateTotals();
  });

// Remove despesa
function removerDespesa(id) {
  const despesaElement = document.getElementById("despesa-desc-" + id)
    .parentElement.parentElement;
  despesaElement.remove();
  calculateTotals();
}

// Calculate totals on input change
document.querySelectorAll("input").forEach((input) => {
  input.addEventListener("input", calculateTotals);
});

// Set default date to today
document.getElementById("data").valueAsDate = new Date();

// Initial calculation
calculateTotals();

console.log("✅ App.js loaded successfully");
