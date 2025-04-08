/**
 * Este JS maneja la logica de:
 * 1) Llamar al backend para generar el PDF en Base64.
 * 2) Invocar a la firma trifasica con "sign(...)" de autoscript.js.
 * 3) Enviar el PDF firmado al backend y mostrar enlace de descarga.
 * 4) Realizar la cofirma de un PDF previamente firmado.
 */

// Variable global para recordar el ID (uuid) del PDF firmado
let globalSignedId = null;

// URL del servicio de firma trifasica
const TRIFASICA_SERVER_URL = "https://192.168.147.218/vital-sanity/afirma-server-triphase-signer";

/**
 * FIRMAR (primera firma del documento) utilizando firma trifasica.
 */
function onClickFirmar() {
    // 1) Recogemos datos del formulario
    const form = document.getElementById("formData");
    const formData = new FormData(form);

    // Llamamos por AJAX a /signer/generate-pdf para obtener un PDF en Base64
    fetch("/vital-sanity/signer/generate-pdf", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(pdfBase64 => {
            // 2) Invocamos la firma trifasica con AutoFirma:
            AutoScript.sign(
                pdfBase64,                                // dataB64
                "SHA512withRSA",                          // algorithm
                "PAdEStri",                               // format (se usa PAdEStri para firma trifasica)
                "serverUrl=" + TRIFASICA_SERVER_URL,        // params: indicamos la URL del servicio trifasico
                function(signedPdfBase64, signerCert, extraInfo) {
                    // EXITO: subimos el PDF firmado al servidor
                    uploadSignedPdf(signedPdfBase64);
                },
                function(errorType, errorMessage) {
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

            // Mostramos el boton de COFIRMAR (ahora que ya existe un PDF firmado)
            document.getElementById("btnCofirmar").style.display = "inline";
        })
        .catch(err => {
            alert("Error subiendo PDF firmado: " + err);
        });
}

/**
 * COFIRMAR: Realiza la cofirma de un PDF previamente firmado utilizando firma trifasica.
 * 1) Descargamos en Base64 el PDF previamente firmado.
 * 2) Llamamos a cosign(...).
 * 3) Subimos el resultado (cofirmado).
 */
function onClickCofirmar() {
    if (!globalSignedId) {
        alert("No se ha firmado aun ningun PDF para cofirmar.");
        return;
    }

    // 1) Descargamos en Base64 el PDF previamente firmado
    fetch("/vital-sanity/signer/download-base64/" + globalSignedId)
        .then(response => response.text())
        .then(signedPdfBase64 => {
            // 2) Invocamos la cofirma trifasica
            AutoScript.cosign(
                signedPdfBase64,                          // firma ya existente en Base64
                "SHA512withRSA",                          // algoritmo
                "PAdEStri",                               // format (se usa PAdEStri para firma trifasica)
                "serverUrl=" + TRIFASICA_SERVER_URL,        // params: indicamos la URL del servicio trifasico
                function(cosignedPdfBase64, signerCert, extraInfo) {
                    // EXITO: subimos la cofirma al servidor
                    uploadCosignedPdf(cosignedPdfBase64);
                },
                function(errorType, errorMessage) {
                    alert("ERROR en cofirma: " + errorType + " - " + errorMessage);
                }
            );
        })
        .catch(err => {
            alert("Error al descargar PDF firmado en base64: " + err);
        });
}

/**
 * Subir PDF cofirmado al servidor y mostrar enlace de descarga.
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
            // Mostramos el enlace de descarga del PDF cofirmado
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
 * - Por defecto, AutoScript usara Socket si el navegador lo soporta.
 * - Si desearamos forzar el uso de servicios, hariamos:
 *      setForceWSMode(true);
 *      setServlets("/vital-sanity/storage/StorageService","/vital-sanity/retriever/RetrieveService");
 */

// Al cargar la pagina, inicializamos la app de autofirma:
window.addEventListener("load", () => {
    // Si se detecta un dispositivo movil (Android o iOS), forzamos el uso de servicios intermedios
    // para garantizar la compatibilidad, y configuramos las URL de los servicios Storage y Retrieve.
    if (AutoScript.isAndroid() || AutoScript.isIOS()) {
        AutoScript.setForceWSMode(true);
        AutoScript.setServlets(
            "https://192.168.147.218/vital-sanity/afirma-signature-storage/StorageService",
            "https://192.168.147.218/vital-sanity/afirma-signature-retriever/RetrieveService"
        );
    }
    // Cargamos la app de autofirma
    AutoScript.cargarAppAfirma();
});
