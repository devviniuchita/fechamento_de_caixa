// Navigation
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

// Calculate totals - Versão refatorada com lógica da planilha
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

  // Calculate total caixa (Fórmula da planilha: =SUM(C15+C29+C40-C8))
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

  // Verificar consistência (deve resultar em 0)
  verificarConsistencia(totalCaixa);

  // Update print version
  updatePrintVersion();
}

// Verificar consistência dos valores - Versão simplificada
function verificarConsistencia(totalCaixa) {
  const inconsistenciaMsg = document.getElementById("inconsistencia-message");
  const totalCaixaElement = document.getElementById("total-caixa");
  const printInconsistenciaMsg = document.getElementById(
    "print-inconsistencia-message"
  );

  // Remove todas as classes de cor primeiro
  totalCaixaElement.classList.remove(
    "text-red-600",
    "text-green-600",
    "text-blue-600"
  );

  // Se o total do caixa não for zero, há inconsistência
  if (Math.abs(totalCaixa) > 0.01) {
    // Margem de 1 centavo
    inconsistenciaMsg.classList.remove("hidden");

    if (totalCaixa < 0) {
      // Valor negativo (vermelho)
      inconsistenciaMsg.innerHTML = `<i class="fas fa-exclamation-triangle mr-1"></i> Está faltando ${formatCurrency(
        totalCaixa
      )} no caixa!`;
      totalCaixaElement.classList.add("text-red-600");
      printInconsistenciaMsg.textContent = `Diferença negativa no fechamento: ${formatCurrency(
        totalCaixa
      )}`;
    } else {
      // Valor positivo (azul)
      inconsistenciaMsg.innerHTML = `<i class="fas fa-exclamation-triangle mr-1"></i> <span class="text-blue-600">Está sobrando ${formatCurrency(
        totalCaixa
      )} no caixa!</span>`;
      totalCaixaElement.classList.add("text-blue-600");
      printInconsistenciaMsg.classList.add("text-blue-600"); // Adiciona a classe azul
      printInconsistenciaMsg.textContent = `Diferença positiva no fechamento: ${formatCurrency(
        totalCaixa
      )}`;
    }

    printInconsistenciaMsg.classList.remove("hidden");
  } else {
    // Valor zero (verde)
    inconsistenciaMsg.classList.add("hidden");
    totalCaixaElement.classList.add("text-green-600");
    printInconsistenciaMsg.classList.add("hidden");
  }
}

// Update print version
function updatePrintVersion() {
  // Basic info
  const dataInput = document.getElementById("data");
  const date = new Date(dataInput.value);
  document.getElementById("print-data").textContent =
    date.toLocaleDateString("pt-BR");
  document.getElementById("print-responsavel").textContent =
    document.getElementById("responsavel").value;

  // Values
  document.getElementById("print-caixa-inicial").textContent = (
    parseFloat(document.getElementById("caixa-inicial").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-vendas").textContent = (
    parseFloat(document.getElementById("vendas").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-troco-inserido").textContent = (
    parseFloat(document.getElementById("troco-inserido").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-total-entradas").textContent = (
    parseFloat(
      document
        .getElementById("total-entradas")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");

  document.getElementById("print-dinheiro").textContent = (
    parseFloat(document.getElementById("dinheiro").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-pix").textContent = (
    parseFloat(document.getElementById("pix").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-deposito").textContent = (
    parseFloat(document.getElementById("deposito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-sangria").textContent = (
    parseFloat(document.getElementById("sangria").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-vale").textContent = (
    parseFloat(document.getElementById("vale").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-total-ativos").textContent = (
    parseFloat(
      document
        .getElementById("total-ativos")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");

  document.getElementById("print-visa-debito").textContent = (
    parseFloat(document.getElementById("visa-debito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-master-debito").textContent = (
    parseFloat(document.getElementById("master-debito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-elo-debito").textContent = (
    parseFloat(document.getElementById("elo-debito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-total-debito").textContent = (
    parseFloat(
      document
        .getElementById("total-debito")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");

  document.getElementById("print-visa-credito").textContent = (
    parseFloat(document.getElementById("visa-credito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-master-credito").textContent = (
    parseFloat(document.getElementById("master-credito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-elo-credito").textContent = (
    parseFloat(document.getElementById("elo-credito").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-total-credito").textContent = (
    parseFloat(
      document
        .getElementById("total-credito")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");

  document.getElementById("print-voucher").textContent = (
    parseFloat(document.getElementById("voucher").value) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-total-cartoes").textContent = (
    parseFloat(
      document
        .getElementById("total-cartoes")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");

  document.getElementById("print-total-despesas").textContent = (
    parseFloat(
      document
        .getElementById("total-despesas")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");
  document.getElementById("print-total-caixa").textContent = (
    parseFloat(
      document
        .getElementById("total-caixa")
        .textContent.replace("R$ ", "")
        .replace(".", "")
        .replace(",", ".")
    ) || 0
  )
    .toFixed(2)
    .replace(".", ",");

  // Update despesas in print version
  const printDespesasContainer = document.getElementById("print-despesas");
  printDespesasContainer.innerHTML = "";

  document
    .querySelectorAll('[id^="despesa-desc-"]')
    .forEach((descEl, index) => {
      const id = descEl.id.split("-")[2];
      const valorEl = document.getElementById(`despesa-valor-${id}`);

      const row = document.createElement("tr");
      row.innerHTML = `
        <td class="border border-gray-300 px-4 py-2">${
          descEl.value || "Despesa"
        }</td>
        <td class="border border-gray-300 px-4 py-2">${(
          parseFloat(valorEl.value) || 0
        )
          .toFixed(2)
          .replace(".", ",")}</td>
      `;
      printDespesasContainer.appendChild(row);
    });

  // Add total despesas row
  const totalRow = document.createElement("tr");
  totalRow.className = "font-bold";
  totalRow.innerHTML = `
      <td class="border border-gray-300 px-4 py-2">Total Despesas</td>
      <td class="border border-gray-300 px-4 py-2">${(
        parseFloat(
          document
            .getElementById("total-despesas")
            .textContent.replace("R$ ", "")
            .replace(".", "")
            .replace(",", ".")
        ) || 0
      )
        .toFixed(2)
        .replace(".", ",")}</td>
    `;
  printDespesasContainer.appendChild(totalRow);

  // Update inconsistency message in print version
  const printInconsistenciaMsg = document.getElementById(
    "print-inconsistencia-message"
  );
  const inconsistenciaMsg = document.getElementById("inconsistencia-message");

  if (!inconsistenciaMsg.classList.contains("hidden")) {
    printInconsistenciaMsg.textContent = inconsistenciaMsg.textContent;
    printInconsistenciaMsg.classList.remove("hidden");
  } else {
    printInconsistenciaMsg.textContent = "";
    printInconsistenciaMsg.classList.add("hidden");
  }
}

// Remove despesa
function removerDespesa(id) {
  const despesaElement = document.getElementById("despesa-desc-" + id)
    .parentElement.parentElement;
  despesaElement.remove();
  calculateTotals();
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

// Calculate totals on input change
document.querySelectorAll("input").forEach((input) => {
  input.addEventListener("input", calculateTotals);
});

// Set default date to today
document.getElementById("data").valueAsDate = new Date();

// Initial calculation
calculateTotals();
