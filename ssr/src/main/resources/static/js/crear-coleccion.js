// =============================
// CONFIG
// =============================
//const BASE_URL_ESTATICA = "https://fuente-estatica.up.railway.app/api/fuentes"
const BASE_URL_ESTATICA = "http://localhost:4080/api/fuentes"
// =============================
// HELPERS
// =============================

// Hace POST con archivo a un endpoint dado y retorna JSON
async function postFile(url, file) {
  const formData = new FormData();
  formData.append("file", file);

  const resp = await fetch(url, { method: "POST", body: formData });

  if (!resp.ok) {
    throw new Error("Error en respuesta del servidor (" + resp.status + ")");
  }

  return resp.json();
}


// =============================
// VALIDACIÓN DEL CSV
// =============================
async function verificarArchivoCsv(inputFile) {
  const errorMsg = document.getElementById("errorMsg");

  // reset
  errorMsg.style.display = "none";
  inputFile.classList.remove("is-invalid", "is-valid");

  if (inputFile.files.length === 0) return;

  try {
    const result = await postFile(`${BASE_URL_ESTATICA}/validar-csv`, inputFile.files[0]);

    if (!result.esCsv) {
      errorMsg.innerText = "El archivo no es un CSV válido.";
      errorMsg.style.display = "inline";
      inputFile.classList.add("is-invalid");
    } else {
      inputFile.classList.add("is-valid");
    }

  } catch (e) {
    errorMsg.innerText = "Error consultando API de validación.";
    errorMsg.style.display = "inline";
    inputFile.classList.add("is-invalid");
  }
}

function mostrarErrorGlobal(mensaje) {
    const contenedorError = document.getElementById("mensajeErrorGlobal");
    contenedorError.innerText = mensaje;
    contenedorError.style.display = "block";

    // Hacemos scroll hacia el error para asegurar que el usuario lo vea
    contenedorError.scrollIntoView({ behavior: "smooth", block: "center" });
}

async function procesarSubmit(event) {
  event.preventDefault();

  const form = event.target;
  const submitButton = document.querySelector("#submit-button");
  const contenedorError = document.getElementById("mensajeErrorGlobal");

  // 1. Limpiar errores previos al intentar enviar de nuevo
  contenedorError.style.display = "none";
  contenedorError.innerText = "";

  // --- VALIDACIÓN: BLOQUEAR SI HAY ERRORES (is-invalid) ---
  if (form.querySelector('.is-invalid')) {
    mostrarErrorGlobal("No se puede enviar el formulario. Por favor, corrija los campos marcados en rojo (archivos inválidos).");
    return;
  }

  submitButton.disabled = true;
  const fuentes = document.querySelectorAll('.fuente-item');

  try {
    for (const fuente of fuentes) {
      const tipo = fuente.querySelector('select').value;
      const input = fuente.querySelector('.fuente-input');

      // Solo manejar fuentes estáticas con archivo seleccionado
      if (tipo === "ESTATICA" && input.files?.length > 0) {

        // Subir archivo al módulo Fuente Estática
        const data = await postFile(BASE_URL_ESTATICA, input.files[0]);

        // Reemplazar input file por input text con el ID
        const contenedor = input.parentElement;
        const newInput = document.createElement('input');
        newInput.type = "text";
        newInput.name = input.name;
        newInput.value = `${data.id}`;

        // Ocultar el input con el ID
        newInput.hidden = true;

        contenedor.replaceChild(newInput, input);
      }
    }

    // 1. Ocultamos el formulario visualmente
    form.style.display = 'none';

    // 2. Creamos el spinner
    const spinnerDiv = document.createElement('div');
    spinnerDiv.className = 'd-flex flex-column align-items-center justify-content-center mt-5';
    spinnerDiv.innerHTML = `
        <div class='spinner-border' role='status'><span class='visually-hidden'>Loading...</span></div>
        <h4 class="mt-3">Subiendo colección, aguarde unos instantes…</h4>
    `;

    // 3. Agregamos el spinner
    form.parentElement.appendChild(spinnerDiv);

    // 4. Enviamos el formulario
    form.submit();

  } catch (err) {
    mostrarErrorGlobal("Ocurrió un error procesando el archivo: " + (err.message || "Error desconocido"));

    submitButton.disabled = false;

    form.style.display = 'block';

    // Remover el spinner si quedó ahí
    const spinner = document.querySelector('.spinner-border')?.parentElement;
    if(spinner) spinner.remove();
  }
}

function cambiarInput(select) {
  const contenedor = select.parentElement;
  const oldInput = contenedor.querySelector('.fuente-input');
  let newInput = oldInput; // Aquí defines la variable que debes usar

  if(select.value === "ESTATICA"){
     newInput.type = "file";
         newInput.accept = ".csv";
         newInput.onchange = () => verificarArchivoCsv(newInput);
         newInput.required = true;
         newInput.hidden = false;
         newInput.required = true;
  }
  else {
    newInput.hidden = true;       // Esto estaba bien
    newInput.required = false;
  }

  contenedor.replaceChild(newInput, oldInput);
}


function agregarFuente() {
  const container = document.getElementById('fuentes-container');
  const nuevaFuente = document.createElement('div');
  const id = container.querySelectorAll('.fuente-item').length;
  nuevaFuente.innerHTML = `
<div class="fuente-item d-flex flex-row mb-4 p-2 align-items-center justify-content-start gap-3">
                        <select class="form-select" aria-label="seleccionar-fuente"
                        name="fuentes[${id}].tipoFuente" onchange="cambiarInput(this)"
                        >
                            <option selected>Tipo de fuente</option>
                            <option value="ESTATICA">Estatica</option>
                            <option value="DINAMICA">Dinamica</option>
                            <option value="PROXY_METAMAPA">Proxy api</option>
                        </select>
                        <input class="form-control fuente-input" type="text" placeholder="URL"
                        name="fuentes[${id}].url">
                        <button type="button" class="btn btn-danger" onclick="eliminarFuente(this)">Eliminar</button>
                    </div>
    `;

  container.appendChild(nuevaFuente);

  // Animación de entrada
  setTimeout(() => {
    nuevaFuente.style.opacity = '1';
    nuevaFuente.style.transform = 'translateY(0)';
  }, 10);
}


function eliminarFuente(boton) {
  const fuente = boton.closest('.fuente-item');
  // Animación de salida
  fuente.style.transition = 'all 0.3s ease';
  fuente.style.opacity = '0';
  fuente.style.transform = 'translateX(-100%)';

  setTimeout(() => {
    fuente.remove();
    reindexarFuentes()
  }, 200);
}

function reindexarFuentes() {
  const items = document.querySelectorAll('#fuentes-container .fuente-item');
  items.forEach((item, index) => {
    const select = item.querySelector("select");
    const input = item.querySelector(".fuente-input");

    if (select) select.name = `fuentes[${index}].tipoFuente`;
    if (input) input.name = `fuentes[${index}].url`;
  });
}

const filtrosUsados = new Set();
const listaCriterios = document.getElementById("lista-filtros");
const selectTipoFiltro = document.getElementById("tipoFiltro");

function agregarFiltro() {
  const tipoCriterio = selectTipoFiltro.value;
  if (!tipoCriterio) return;

  if (filtrosUsados.has(tipoCriterio)) {
    alert("Ya se agregó un filtro de este tipo.");
    return;
  }

  filtrosUsados.add(tipoCriterio);
  const index = listaCriterios.children.length;

  const div = document.createElement("div");
  div.classList.add("filtro-item", "border", "p-3", "mb-3", "rounded");
  div.dataset.tipo = tipoCriterio;

  let contenido = `
    <input type="hidden" name="criterios[${index}].tipoCriterio" id="criterios${index}.tipoCriterio" value="${tipoCriterio}">
  `;

  switch (tipoCriterio) {
    case "FILTRO_CATEGORIA":
    case "FILTRO_PROVINCIA":
    case "FILTRO_DEPARTAMENTO":
    case "FILTRO_MUNICIPIO":
      contenido += `
        <label class="form-label">${tipoCriterio.replace("FILTRO_", "").toLowerCase()}:</label>
        <input type="text" class="form-control"
               name="criterios[${index}].valor" id="criterios${index}.valor"
               placeholder="Ingrese valor" required>
      `;
      break;

    case "FILTRO_FUENTE":
      contenido += `
        <label class="form-label">Tipo de fuente:</label>
        <select class="form-select"
                name="criterios[${index}].tipoFuente" id="criterios${index}.tipoFuente" required>
          <option value="">Seleccione tipo de fuente</option>
          <option value="ESTATICA">Estática</option>
          <option value="DINAMICA">Dinámica</option>
          <option value="PROXY_METAMAPA">Proxy API</option>
        </select>
      `;
      break;

    case "FILTRO_FECHA_ACONTECIMIENTO":
    case "FILTRO_FECHA_REPORTE":
      contenido += `
        <label class="form-label">
          ${tipoCriterio === "FILTRO_FECHA_ACONTECIMIENTO" ? "Fecha de acontecimiento" : "Fecha de reporte"}:
        </label>
        <div class="d-flex gap-2 align-items-center">
          <input type="date" class="form-control"
                 name="criterios[${index}].fechaInicio" id="criterios${index}.fechaInicio" required>
          <span>a</span>
          <input type="date" class="form-control"
                 name="criterios[${index}].fechaFin" id="criterios${index}.fechaFin" required>
        </div>
      `;
      break;
  }

  contenido += `
    <button type="button" class="btn btn-danger mt-2" onclick="eliminarFiltro(this, '${tipoCriterio}')">Eliminar</button>
  `;

  div.innerHTML = contenido;
  listaCriterios.appendChild(div);
  selectTipoFiltro.value = "";
}

function eliminarFiltro(btn) {
   const tipoFiltro = btn.dataset.tipoCriterio;
      console.log(tipoFiltro)
  console.log("filtro recibido: " + tipoFiltro)

  filtrosUsados.delete(tipoFiltro);
  btn.closest(".filtro-item").remove();

  // Reindexar campos para mantener compatibilidad con el mapeo de Spring
  Array.from(listaCriterios.children).forEach((div, i) => {
    div.querySelectorAll("input, select").forEach(el => {
      if (el.name) {
        el.name = el.name.replace(/criterios\[\d+\]/, `criterios[${i}]`);
      }
      if (el.id) {
        el.id = el.id.replace(/criterios\d+/, `criterios${i}`);
      }
    });
  });
}

//leer criterios de fuente
document.addEventListener("DOMContentLoaded", () => {
    const criteriosExistentes = document.querySelectorAll("#lista-filtros .filtro-item");

    criteriosExistentes.forEach(div => {
        const tipo = div.querySelector("select")?.value
                  || div.dataset.tipo
                  || div.getAttribute("data-tipo-criterio");

        if (tipo) {
            filtrosUsados.add(tipo);
            console.log("Filtro precargado:", tipo);
        }
    });
});