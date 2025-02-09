/**
 * Este JS maneja la lógica de:
 * 1) Llamar al backend para generar el PDF en Base64.
 * 2) Invocar a la firma con "sign(...)" de autoscript.js.
 * 3) Enviar el PDF firmado al backend y mostrar enlace de descarga.
 * 4) NUEVO: Realizar cofirma de un PDF previamente firmado.
 */

// Variable global para recordar el ID (uuid) del PDF firmado
let globalSignedId = null;

/**
 * FIRMAR (primera firma del documento)
 */
function onClickFirmar() {
    // 1) Recogemos datos del formulario
    const form = document.getElementById("formData");
    const formData = new FormData(form);

    // Llamamos por AJAX a /signer/generate-pdf para obtener un PDF base64
    fetch("/vital-sanity/signer/generate-pdf", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(pdfBase64 => {
            // 2) Invocamos la firma con AutoFirma (sign):
            AutoScript.sign(
                pdfBase64,                  // dataB64
                "SHA512withRSA",            // algorithm
                "PAdES",                    // format
                null,                       // params (simple demo)
                function (signedPdfBase64, signerCert, extraInfo) {
                    // EXITO: subimos el PDF firmado al servidor
                    uploadSignedPdf(signedPdfBase64);
                },
                function (errorType, errorMessage) {
                    alert("ERROR en firma: " + errorType + " - " + errorMessage);
                }
            );
        })
        .catch(err => {
            alert("Error generando el PDF: " + err);
        });
}

/**
 * Subimos el PDF firmado (Base64) al servidor y mostramos enlace descarga.
 */
function uploadSignedPdf(signedPdfBase64) {
    const formData = new FormData();
    formData.append("signedPdfBase64", signedPdfBase64);

    fetch("/vital-sanity/signer/save-signed", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(uuid => {
            // Guardamos el ID del PDF firmado (para cofirma posterior)
            globalSignedId = uuid;

            // Mostramos el enlace de descarga
            const resultadoDiv = document.getElementById("resultadoFirma");
            const link = document.createElement("a");
            link.href = "/vital-sanity/signer/download/" + uuid;
            link.target = "_blank";
            link.innerText = "Descargar PDF FIRMADO";
            resultadoDiv.innerHTML = "";
            resultadoDiv.appendChild(link);

            // NUEVO: Mostramos el botón de COFIRMAR (ahora que ya existe un PDF firmado)
            document.getElementById("btnCofirmar").style.display = "inline";
        })
        .catch(err => {
            alert("Error subiendo PDF firmado: " + err);
        });
}

/**
 * NUEVO: COFIRMAR
 * 1) Descargamos el PDF firmado en Base64.
 * 2) Llamamos a cosign(...).
 * 3) Subimos resultado (cofirmado).
 */
function onClickCofirmar() {
    if (!globalSignedId) {
        alert("No se ha firmado aún ningún PDF para cofirmar.");
        return;
    }

    // 1) Descargamos en Base64 el PDF previamente firmado
    fetch("/vital-sanity/signer/download-base64/" + globalSignedId)
        .then(response => response.text())
        .then(signedPdfBase64 => {
            // 2) Invocamos cofirma
            AutoScript.cosign(
                signedPdfBase64,          // firma ya existente en base64
                "SHA512withRSA",          // algoritmo
                "PAdES",                  // formato (PAdES)
                null,                     // params
                function (cosignedPdfBase64, signerCert, extraInfo) {
                    // EXITO: subimos la cofirma al servidor
                    uploadCosignedPdf(cosignedPdfBase64);
                },
                function (errorType, errorMessage) {
                    alert("ERROR en cofirma: " + errorType + " - " + errorMessage);
                }
            );
        })
        .catch(err => {
            alert("Error al descargar PDF firmado en base64: " + err);
        });
}

/**
 * NUEVO: Subir PDF cofirmado al servidor y mostrar enlace de descarga.
 */
function uploadCosignedPdf(cosignedPdfBase64) {
    const formData = new FormData();
    formData.append("cosignedPdfBase64", cosignedPdfBase64);

    fetch("/vital-sanity/signer/save-cosigned", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(uuid => {
            // Mostramos el enlace de descarga del PDF COFIRMADO
            const resultadoCofirmaDiv = document.getElementById("resultadoCofirma");
            const link = document.createElement("a");
            link.href = "/vital-sanity/signer/download-cosigned/" + uuid;
            link.target = "_blank";
            link.innerText = "Descargar PDF COFIRMADO";
            resultadoCofirmaDiv.innerHTML = "";
            resultadoCofirmaDiv.appendChild(link);
        })
        .catch(err => {
            alert("Error subiendo PDF cofirmado: " + err);
        });
}

/**
 * IMPORTANTE (recordatorio de los servicios Storage/Retriever):
 * - Por defecto, AutoScript usará Socket si el navegador lo soporta.
 * - Si deseáramos forzar el uso de servicios, haríamos:
 *      setForceWSMode(true);
 *      setServlets("/vital-sanity/storage/StorageService","/vital-sanity/retriever/RetrieveService");
 */

// Al cargar la página, inicializamos la app de @firma:
window.addEventListener("load", () => {
    // Cargamos la app de autofirma
    AutoScript.cargarAppAfirma();
});
