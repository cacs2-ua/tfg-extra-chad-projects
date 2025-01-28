package vitalsanity.controller.signer;

import java.util.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vitalsanity.service.SignPdfService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/signer")
public class SignerController {

    private final SignPdfService signPdfService;

    // Repositorio en memoria para guardar PDFs firmados,
    // SOLO para ejemplo. En producción, guárdalos en BBDD o storage adecuado.
    private final Map<String, byte[]> signedRepository = new ConcurrentHashMap<>();

    public SignerController(SignPdfService signPdfService) {
        this.signPdfService = signPdfService;
    }

    /**
     * Muestra el formulario principal con Thymeleaf.
     */
    @GetMapping("/form")
    public String showForm(Model model) {
        // Simplemente retorna la vista Thymeleaf "form-signer.html"
        return "signer/form-signer";
    }

    /**
     * Genera el PDF en base a los datos y lo devuelve codificado en Base64 (para luego firmarlo en el navegador).
     */
    @PostMapping("/generate-pdf")
    @ResponseBody
    public String generatePdf(@RequestParam String nombre,
                              @RequestParam String email,
                              @RequestParam String observaciones) {

        // 1) Crear el PDF
        byte[] pdfBytes = signPdfService.generatePdf(nombre, email, observaciones);

        // 2) Retornar en Base64
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    /**
     * Recibe el PDF ya firmado en Base64, lo almacena temporalmente y devuelve un ID para su futura descarga.
     */
    @PostMapping("/save-signed")
    @ResponseBody
    public String saveSignedPdf(@RequestParam String signedPdfBase64) {
        // Decodificar
        byte[] signedPdf = Base64.getDecoder().decode(signedPdfBase64);

        // Guardar en un Map en memoria con un ID aleatorio
        String uuid = UUID.randomUUID().toString();
        signedRepository.put(uuid, signedPdf);

        // Devolvemos el ID, para que el front sepa cómo descargarlo
        return uuid;
    }

    /**
     * Permite descargar el PDF firmado, dada la clave devuelta al guardar.
     */
    @GetMapping("/download/{id}")
    @ResponseBody
    public byte[] download(@PathVariable("id") String id) {
        byte[] data = signedRepository.get(id);
        if (data == null) {
            throw new RuntimeException("No se encontró la firma con id=" + id);
        }
        // Devolvemos binario. Para forzar descarga, setear cabeceras
        // en vez de @ResponseBody, por simplicidad de ejemplo lo devolvemos crudo.
        return data;
    }
}
