// ============================================================================
// FECHAMENTO DE CAIXA - MAIN APPLICATION
// ============================================================================

// Check authentication on page load
document.addEventListener("DOMContentLoaded", function () {
  checkAuthentication();
  loadDashboardData();
  // Bind reset dev button if present
  const resetBtn = document.getElementById("reset-dev-btn");
  if (resetBtn) {
    resetBtn.addEventListener("click", openResetDevModal);
  }

  // Card "Inconsistências" → navega para Erros de Caixa
  const inconsistenciasCard = document.getElementById("inconsistencias-card");
  if (inconsistenciasCard) {
    const goErros = () => {
      if (checkAuthentication()) {
        showContent("erros");
        loadErrosDeCaixa();
      }
    };
    inconsistenciasCard.addEventListener("click", goErros);
    inconsistenciasCard.addEventListener("keypress", (e) => {
      if (e.key === "Enter") goErros();
    });
  }
});

// Authentication check
function checkAuthentication() {
  if (!API.auth.isAuthenticated()) {
    showLoginModal();
    return false;
  }

  // Hide login modal if authenticated
  hideLoginModal();

  const user = API.auth.getCurrentUser();
  if (user) {
    updateUserInfo(user);
  }

  return true;
}

// Update user info in header
function updateUserInfo(user) {
  const userNameElement = document.querySelector("header .text-sm.font-medium");
  const userRoleElement = document.querySelector("header .text-xs.bg-blue-100");
  const userAvatarElement = document.querySelector(
    "header .h-8.w-8.rounded-full span",
  );

  if (userNameElement && user.nome) userNameElement.textContent = user.nome;
  const perfilLabel =
    user.perfil || (Array.isArray(user.perfis) && user.perfis[0]) || "USER";
  if (userRoleElement)
    userRoleElement.textContent = String(perfilLabel).replace(/^ROLE_/i, "");
  if (userAvatarElement && user.nome)
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
    const fechamentosResponse = await API.fechamentos.listarFechamentos({
      size: 10,
      sort: "data,desc",
    });

    // Normaliza coleção
    const allItems = fechamentosResponse.content || fechamentosResponse || [];

    // Seleciona os "últimos" N itens (já em desc pelo backend)
    const latestDesc = allItems.slice(0, 5);

    // Para exibição na tabela: ordenar da menor data para a maior (asc)
    const latestAsc = [...latestDesc].sort(
      (a, b) => new Date(a.data) - new Date(b.data),
    );

    // Atualiza cards com o mesmo subconjunto exibido
    updateDashboardStats(latestDesc);
    updateFechamentosTable(latestAsc);
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
function updateDashboardStats(fechamentosSubset) {
  const fechamentos = fechamentosSubset || [];

  // Fechamentos de hoje (no subconjunto exibido)
  const today = new Date().toISOString().split("T")[0];
  const fechamentosHoje = fechamentos.filter((f) => f.data === today).length;

  // Soma de vendas (no subconjunto exibido)
  const totalVendas = fechamentos.reduce((sum, f) => sum + (f.vendas || 0), 0);

  // Contagem de inconsistências com base em totalCaixa (tolerância centavos)
  const EPS = 0.009; // tolerância para arredondamentos
  const inconsistencias = fechamentos.filter(
    (f) => Math.abs(f.totalCaixa || 0) > EPS,
  ).length;

  // Update UI
  document.querySelector(".grid .bg-white:nth-child(1) h3").textContent =
    fechamentosHoje;
  document.querySelector(".grid .bg-white:nth-child(2) h3").textContent =
    API.formatCurrency(totalVendas);
  const inconsistenciasEl = document.querySelector("#inconsistencias-card h3");
  if (inconsistenciasEl) inconsistenciasEl.textContent = inconsistencias;
}

// Update fechamentos table
function updateFechamentosTable(fechamentosData) {
  const fechamentos = fechamentosData.content || fechamentosData;
  const tbody = document.querySelector("#dashboard-content tbody");

  if (!tbody) return;

  tbody.innerHTML = "";

  fechamentos.slice(0, 5).forEach((fechamento) => {
    // Determina status a partir do totalCaixa
    const EPS = 0.009;
    const isCorreto = Math.abs(fechamento.totalCaixa || 0) <= EPS;
    const statusLabel = isCorreto ? "Caixa Correto" : "Inconsistente";
    const statusClass = isCorreto
      ? "bg-green-100 text-green-800"
      : "bg-red-100 text-red-800";
    const motivoTooltip = isCorreto
      ? "Diferença zerada"
      : fechamento.totalCaixa < 0
        ? `Faltando ${API.formatCurrency(Math.abs(fechamento.totalCaixa))}`
        : `Sobrando ${API.formatCurrency(Math.abs(fechamento.totalCaixa))}`;

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
        <span title="${motivoTooltip}" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}">
          ${statusLabel}
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

// Novo: Erros de Caixa
document.getElementById("erros-link")?.addEventListener("click", function (e) {
  e.preventDefault();
  if (checkAuthentication()) {
    showContent("erros");
    loadErrosDeCaixa();
  }
});

function showContent(content) {
  // Hide all content
  document.getElementById("dashboard-content").classList.add("hidden");
  document.getElementById("fechamento-content").classList.add("hidden");
  document.getElementById("relatorios-content").classList.add("hidden");
  const errosContent = document.getElementById("erros-content");
  if (errosContent) errosContent.classList.add("hidden");
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
    case "erros":
      title = "Erros de Caixa";
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

// ===============================
// Erros de Caixa - Tela dedicada
// ===============================
async function loadErrosDeCaixa() {
  if (!API.auth.isAuthenticated()) return;
  try {
    const resp = await API.fechamentos.listarFechamentos({
      size: 100,
      sort: "data,desc",
    });
    const all = resp.content || resp || [];
    const EPS = 0.009;
    const inconsistentes = all.filter((f) => Math.abs(f.totalCaixa || 0) > EPS);
    renderErrosTabela(inconsistentes);
    atualizarErrosResumo(inconsistentes);
    prepararErrosFiltros(inconsistentes);
  } catch (e) {
    console.error("Erro ao carregar Erros de Caixa:", e);
    API.showNotification("Erro ao carregar Erros de Caixa", "error");
  }
}

function prepararErrosFiltros(base) {
  const btnAplicar = document.getElementById("erros-aplicar-filtro");
  const btnLimpar = document.getElementById("erros-limpar-filtro");
  const btnCSV = document.getElementById("erros-exportar-csv");

  const apply = () => {
    const ini = document.getElementById("erros-data-inicio").value || null;
    const fim = document.getElementById("erros-data-fim").value || null;
    const resp = (document.getElementById("erros-responsavel").value || "")
      .trim()
      .toLowerCase();

    const filtered = base.filter((f) => {
      const d = new Date(f.data);
      const okIni = ini ? d >= new Date(ini) : true;
      const okFim = fim ? d <= new Date(fim) : true;
      const okResp = resp
        ? String(f.responsavel || "")
            .toLowerCase()
            .includes(resp)
        : true;
      return okIni && okFim && okResp;
    });

    renderErrosTabela(filtered);
    atualizarErrosResumo(filtered);
    lastErrosFiltered = filtered;
  };

  btnAplicar && btnAplicar.addEventListener("click", apply);
  btnLimpar &&
    btnLimpar.addEventListener("click", () => {
      document.getElementById("erros-data-inicio").value = "";
      document.getElementById("erros-data-fim").value = "";
      document.getElementById("erros-responsavel").value = "";
      renderErrosTabela(base);
      atualizarErrosResumo(base);
      lastErrosFiltered = base;
    });

  btnCSV &&
    btnCSV.addEventListener("click", () =>
      exportErrosCSV(lastErrosFiltered || base),
    );
  lastErrosFiltered = base;
}

function renderErrosTabela(items) {
  const tbody = document.getElementById("erros-tbody");
  if (!tbody) return;
  tbody.innerHTML = "";
  const EPS = 0.009;
  [...items]
    .sort((a, b) => new Date(b.data) - new Date(a.data))
    .forEach((f) => {
      const isCorreto = Math.abs(f.totalCaixa || 0) <= EPS;
      const statusLabel = isCorreto ? "Caixa Correto" : "Inconsistente";
      const statusClass = isCorreto
        ? "bg-green-100 text-green-800"
        : "bg-red-100 text-red-800";
      const motivo = isCorreto
        ? "Diferença zerada"
        : f.totalCaixa < 0
          ? `Faltando ${API.formatCurrency(Math.abs(f.totalCaixa))}`
          : `Sobrando ${API.formatCurrency(Math.abs(f.totalCaixa))}`;
      const tr = document.createElement("tr");
      tr.innerHTML = `
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${API.formatDate(f.data)}</td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${f.responsavel || "-"}</td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${API.formatCurrency(f.vendas || 0)}</td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${API.formatCurrency(f.totalCaixa || 0)}</td>
      <td class="px-6 py-4 whitespace-nowrap"><span title="${motivo}" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${statusClass}">${statusLabel}</span></td>
      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">${motivo}</td>`;
      tbody.appendChild(tr);
    });
}

function atualizarErrosResumo(items) {
  const total = items.length;
  const sobra = items
    .filter((i) => (i.totalCaixa || 0) > 0)
    .reduce((s, i) => s + (i.totalCaixa || 0), 0);
  const falta = items
    .filter((i) => (i.totalCaixa || 0) < 0)
    .reduce((s, i) => s + Math.abs(i.totalCaixa || 0), 0);
  const elCount = document.getElementById("erros-total-count");
  const elSobra = document.getElementById("erros-total-sobra");
  const elFalta = document.getElementById("erros-total-falta");
  if (elCount) elCount.textContent = String(total);
  if (elSobra) elSobra.textContent = API.formatCurrency(sobra);
  if (elFalta) elFalta.textContent = API.formatCurrency(falta);
}

function exportErrosCSV(items) {
  const headers = [
    "Data",
    "Responsavel",
    "Vendas",
    "TotalCaixa",
    "Status",
    "Motivo",
  ];
  const EPS = 0.009;
  const rows = items.map((f) => {
    const isCorreto = Math.abs(f.totalCaixa || 0) <= EPS;
    const status = isCorreto ? "Caixa Correto" : "Inconsistente";
    const motivo = isCorreto
      ? "Diferença zerada"
      : f.totalCaixa < 0
        ? `Faltando ${API.formatCurrency(Math.abs(f.totalCaixa))}`
        : `Sobrando ${API.formatCurrency(Math.abs(f.totalCaixa))}`;
    return [
      API.formatDate(f.data),
      f.responsavel || "-",
      (f.vendas || 0).toFixed(2),
      (f.totalCaixa || 0).toFixed(2),
      status,
      motivo,
    ];
  });
  const csv = [headers.join(";"), ...rows.map((r) => r.join(";"))].join("\n");
  const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "erros-de-caixa.csv";
  a.click();
  URL.revokeObjectURL(url);
}

let lastErrosFiltered = null;

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

// ===============================
// Reset DEV UI functions
// ===============================
function openResetDevModal() {
  const modal = document.getElementById("reset-dev-modal");
  const output = document.getElementById("reset-dev-output");
  if (output) output.textContent = "";
  if (modal) modal.classList.remove("hidden");
}

function closeResetDevModal() {
  const modal = document.getElementById("reset-dev-modal");
  if (modal) modal.classList.add("hidden");
}

async function runResetDev() {
  try {
    API.showLoading(true);
    const output = document.getElementById("reset-dev-output");
    if (output) {
      output.textContent = "Executando reset...\n";
    }

    const result = await API.test.resetDev();

    // Pretty print result
    if (output) {
      output.textContent = JSON.stringify(result, null, 2);
    }

    API.showNotification("Reset DEV executado com sucesso.", "success");

    // Se usuário foi recriado, atualiza UI de login
    showLoginModal();
  } catch (error) {
    console.error("Erro ao executar Reset DEV:", error);
    const output = document.getElementById("reset-dev-output");
    if (output) {
      output.textContent = `Erro: ${error.message || "Falha na execução"}`;
    }
    API.showNotification(
      error.message || "Falha ao executar Reset DEV",
      "error",
    );
  } finally {
    API.showLoading(false);
  }
}
