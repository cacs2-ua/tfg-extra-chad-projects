package vitalsanity.controller.signer;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vitalsanity.service.SignPdfService;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/signer")
public class SignerController {

    private final SignPdfService signPdfService;

    // Repositorio en memoria para guardar PDFs firmados
    private final Map<String, byte[]> signedRepository = new ConcurrentHashMap<>();

    public SignerController(SignPdfService signPdfService) {
        this.signPdfService = signPdfService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        // Simplemente retorna la vista Thymeleaf "form-signer.html"
        return "signer/form-signer";
    }

    @PostMapping("/generate-pdf")
    @ResponseBody
    public String generatePdf(@RequestParam String nombre,
                              @RequestParam String email,
                              @RequestParam String observaciones) {

        byte[] pdfBytes = signPdfService.generatePdf(nombre, email, observaciones);
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    @PostMapping("/save-signed")
    @ResponseBody
    public String saveSignedPdf(@RequestParam String signedPdfBase64) {
        byte[] signedPdf = Base64.getDecoder().decode(signedPdfBase64);
        String uuid = UUID.randomUUID().toString();
        signedRepository.put(uuid, signedPdf);
        return uuid;
    }

    /**
     * Permite descargar el PDF firmado, dada la clave devuelta al guardar.
     * Devuelve un ResponseEntity con las cabeceras correctas para forzar la descarga.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) {

        byte[] data = signedRepository.get(id);
        if (data == null) {
            throw new RuntimeException("No se encontró la firma con id=" + id);
        }

        // Cabeceras HTTP para forzar la descarga y definir tipo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Cabecera para forzar "attachment" y proponer nombre de fichero
        headers.setContentDispositionFormData("attachment", "documento-firmado.pdf");

        // Devolvemos el PDF con las cabeceras
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
