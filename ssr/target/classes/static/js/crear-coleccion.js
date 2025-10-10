async function obtenerLinksFuentesCsv(event) {
  event.preventDefault()
  //agregar animaciones de espera
  let submitButton = document.querySelector("#submit-button")
  submitButton.disabled = true
  const fuentes = document.querySelectorAll('.fuente-item');
  let contenedorFuentes
  for (const fuente of fuentes) {
    const tipo = fuente.querySelector('select').value;
    const input = fuente.querySelector('.fuente-input');
    contenedorFuentes = input.parentElement

    if (tipo === "ESTATICA" && input.files !== null && input.files.length > 0) {
            console.log("ESTO")
      const formData = new FormData();
      formData.append("file", input.files[0]);

      try {
        const resp = await fetch("http://localhost:4080/api/fuentes", {
          method: "POST",
          body: formData
        });

        const data = await resp.json();
        console.log(data)
        let newInput = document.createElement('input')
        newInput.type = "text"
        newInput.value = "http://localhost:4080/api/fuentes/" + data.id;
        newInput.name = input.name; // mantiene el binding
        console.log(newInput)
        contenedorFuentes.replaceChild(newInput, input)
      } catch (err) {
        alert("Error guardando archivo csv: " + err.message);
        return;
      }
    }
  }
        event.target.submit();
}



function cambiarInput(select) {
  const contenedor = select.parentElement;
  const oldInput = contenedor.querySelector('.fuente-input');
  let newInput = oldInput;
  if (select.value === "ESTATICA") {
    newInput.type = "file"
    newInput.accept = ".csv";
    newInput.onchange= () => verificarArchivoCsv(newInput);
    newInput.required = true
  } else {
    newInput.type = "text";
    newInput.placeholder = "URL";
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
                            <option value="PROXY_API">Proxy api</option>
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

async function verificarArchivoCsv(inputFile) {

    const errorMsg = document.getElementById("errorMsg");

    errorMsg.style.display = "none";

    if (inputFile.files.length === 0) return;

    const formData = new FormData();
    formData.append("file", inputFile.files[0]);

    try {
        // Consulta API externa para validar si es CSV
        const resp = await fetch("http://localhost:4080/api/fuentes/validar-csv", {
            method: "POST",
            body: formData
        });
        const result = await resp.json();

        if (!result.esCsv) {
            errorMsg.innerText = "El archivo no es un CSV válido.";
            errorMsg.style.display = "inline";
            inputFile.classList.add("is-invalid");
        } else {
        if (result.registros >= 10000){
                        inputFile.classList.add("is-valid");
                        } else {
                        errorMsg.innerText = "Tiene menos de 10000";
                                                errorMsg.style.display = "inline";
                                                inputFile.classList.add("is-invalid");
                        }
        }
    } catch (e) {
        errorMsg.innerText = "Error consultando API externa.";
        errorMsg.style.display = "inline";
    }
}
