// Variable global para recordar el ID (uuid) del PDF firmado
let globalSignedId = null;

function onClickFirmar() {
    const form = document.getElementById("formData");
    const formData = new FormData(form);

    fetch("/vital-sanity/signer/generate-pdf", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(pdfBase64 => {
            AutoScript.sign(
                pdfBase64,
                "SHA512withRSA",
                "PAdES",
                null,
                function (signedPdfBase64, signerCert, extraInfo) {
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

function uploadSignedPdf(signedPdfBase64) {
    const formData = new FormData();
    formData.append("signedPdfBase64", signedPdfBase64);

    fetch("/vital-sanity/signer/save-signed", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(uuid => {
            globalSignedId = uuid;

            const resultadoDiv = document.getElementById("resultadoFirma");
            const link = document.createElement("a");
            link.href = "/vital-sanity/signer/download/" + uuid;
            link.target = "_blank";
            link.innerText = "Descargar PDF FIRMADO";
            resultadoDiv.innerHTML = "";
            resultadoDiv.appendChild(link);

            document.getElementById("btnCofirmar").style.display = "inline";
        })
        .catch(err => {
            alert("Error subiendo PDF firmado: " + err);
        });
}

function onClickCofirmar() {
    if (!globalSignedId) {
        alert("No se ha firmado aún ningún PDF para cofirmar.");
        return;
    }

    fetch("/vital-sanity/signer/download-base64/" + globalSignedId)
        .then(response => response.text())
        .then(signedPdfBase64 => {
            AutoScript.cosign(
                signedPdfBase64,
                "SHA512withRSA",
                "PAdES",
                null,
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

function uploadCosignedPdf(cosignedPdfBase64) {
    const formData = new FormData();
    formData.append("cosignedPdfBase64", cosignedPdfBase64);

    fetch("/vital-sanity/signer/save-cosigned", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(uuid => {
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

window.addEventListener("load", () => {
    AutoScript.cargarAppAfirma();
});
