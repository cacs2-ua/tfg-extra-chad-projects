/**
 * Este JS maneja la lógica de:
 * 1) Llamar al backend para generar el PDF en Base64.
 * 2) Invocar a la firma con "sign(...)" de autoscript.js.
 * 3) Enviar el PDF firmado al backend y mostrar enlace de descarga.
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
            // 2) Invocamos la firma con AutoFirma:
            //    - dataB64 = el PDF en Base64
            //    - algorithm = "SHA512withRSA"
            //    - format = "PAdES"
            //    - params = { "extraParams" de @firma si deseas. Ejemplo: "mode=implicit\n" }
            //    - successCallback y errorCallback
            AutoScript.sign(
                pdfBase64,                  // dataB64
                "SHA512withRSA",            // algorithm
                "PAdES",                    // format
                null,                       // params (simple demo)
                function (signedPdfBase64, signerCert, extraInfo) {
                    // EXITO: subimos el PDF firmado al servidor y creamos enlace de descarga
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
 * Subimos el PDF firmado (Base64) al servidor para guardarlo y luego descargarlo.
 */
function uploadSignedPdf(signedPdfBase64) {

    // Preparamos body formData
    const formData = new FormData();
    formData.append("signedPdfBase64", signedPdfBase64);

    fetch("/vital-sanity/signer/save-signed", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(uuid => {
            // Con 'uuid' mostramos un link a /signer/download/{uuid}
            const resultadoDiv = document.getElementById("resultadoFirma");
            const link = document.createElement("a");
            link.href = "/vital-sanity/signer/download/" + uuid;
            link.target = "_blank";
            link.innerText = "Descargar PDF FIRMADO";
            resultadoDiv.innerHTML = "";
            resultadoDiv.appendChild(link);
        })
        .catch(err => {
            alert("Error subiendo PDF firmado: " + err);
        });

}

/**
 * IMPORTANTE:
 * - Si deseas compatibilidad con IE 10 o anterior, Safari 10, o móviles,
 *   debes indicar la URL de storage/retriever para la comunicación por servidor:
 *      setServlets("https://tuservidor.com/storage/StorageService",
 *                  "https://tuservidor.com/retriever/RetrieveService");
 *
 * - Por ejemplo (después de cargarAppAfirma()):
 *
 *   setServlets(
 *     window.location.origin + "/storage/StorageService",
 *     window.location.origin + "/retriever/RetrieveService"
 *   );
 *
 * - Y si quisieras forzar ALWAYS el WSMode (no usar sockets):
 *      setForceWSMode(true);
 */

// Al cargar la página, inicializamos la app de @firma:
window.addEventListener("load", () => {
    // Cargamos la app de autofirma
    AutoScript.cargarAppAfirma();

    // Si has desplegado los servicios Storage/Retriever:
    // setServlets(window.location.origin + "/storage/StorageService",
    //             window.location.origin + "/retriever/RetrieveService");

    // Por defecto, usará socket si el navegador lo soporta, o WSMode si no.
});
