package vitalsanity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vitalsanity.model.FormData;
import vitalsanity.service.SignatureService;

import java.util.Base64;

@Controller
@RequestMapping("/sign")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

    /**
     * Muestra el formulario HTML.
     */
    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("formData", new FormData());
        return "sign-form"; // Plantilla Thymeleaf
    }

    /**
     * Procesa el formulario, genera un PDF en memoria y redirige a la página
     * donde se presenta el botón de firma.
     */
    @PostMapping("/submit")
    public String submitForm(@ModelAttribute FormData formData, Model model) {
        try {
            byte[] pdfBytes = signatureService.generatePdf(formData);
            // Guardamos en memoria (SignatureService) y pasamos a la vista
            model.addAttribute("pdfBase64", Base64.getEncoder().encodeToString(pdfBytes));
            model.addAttribute("fileName", "documento-sin-firma.pdf");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error generando el PDF: " + e.getMessage());
        }
        return "sign-result"; // Plantilla con el botón "FIRMAR"
    }

    /**
     * Callback que invoca AutoFirma con el PDF ya firmado en Base64.
     * Se asume que la aplicación local de AutoFirma envía "result=" con el PDF firmado.
     */
    @GetMapping("/callback")
    public String signCallback(
            @RequestParam(name = "result", required = false) String base64SignedPdf,
            Model model) {

        if (base64SignedPdf == null || base64SignedPdf.isEmpty()) {
            model.addAttribute("error", "No se recibió ningún PDF firmado.");
            return "sign-result";
        }

        try {
            byte[] signedPdf = Base64.getDecoder().decode(base64SignedPdf);
            signatureService.setLastSignedPdf(signedPdf);

            model.addAttribute("message", "¡Documento firmado correctamente!");
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el PDF firmado: " + e.getMessage());
            e.printStackTrace();
        }

        return "sign-result";
    }

    /**
     * Devuelve el PDF ya firmado para descargar.
     */
    @GetMapping("/download")
    @ResponseBody
    public byte[] downloadSignedPdf(@RequestParam(name = "filename", defaultValue = "documento-firmado.pdf") String fileName) {
        // Avisamos al navegador del tipo de contenido
        // y del nombre sugerido del archivo.
        // Se puede setear en la cabecera, pero de modo sencillo devolvemos los bytes.
        return signatureService.getLastSignedPdf();
    }
}
