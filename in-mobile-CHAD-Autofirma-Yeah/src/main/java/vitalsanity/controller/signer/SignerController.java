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

    // Repositorio en memoria para guardar PDFs (firmados o cofirmados)
    private final Map<String, byte[]> signedRepository = new ConcurrentHashMap<>();

    public SignerController(SignPdfService signPdfService) {
        this.signPdfService = signPdfService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        // Retorna la vista Thymeleaf "form-signer.html"
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

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) {

        byte[] data = signedRepository.get(id);
        if (data == null) {
            throw new RuntimeException("No se encontró la firma con id=" + id);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "documento-firmado.pdf");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // =========================================================
    // NUEVO: Descargar PDF firmado en Base64 para cofirmarlo
    // =========================================================
    @GetMapping("/download-base64/{id}")
    @ResponseBody
    public String downloadBase64(@PathVariable("id") String id) {
        byte[] data = signedRepository.get(id);
        if (data == null) {
            throw new RuntimeException("No se encontró la firma con id=" + id);
        }
        // Devolvemos el PDF en Base64 para usarlo en la cofirma
        return Base64.getEncoder().encodeToString(data);
    }

    // =========================================================
    // NUEVO: Guardar el PDF cofirmado
    // =========================================================
    @PostMapping("/save-cosigned")
    @ResponseBody
    public String saveCosignedPdf(@RequestParam String cosignedPdfBase64) {
        byte[] cosignedPdf = Base64.getDecoder().decode(cosignedPdfBase64);
        String uuid = UUID.randomUUID().toString();
        signedRepository.put(uuid, cosignedPdf);
        return uuid;
    }

    // =========================================================
    // NUEVO: Descargar el PDF cofirmado
    // =========================================================
    @GetMapping("/download-cosigned/{id}")
    public ResponseEntity<byte[]> downloadCosigned(@PathVariable("id") String id) {

        byte[] data = signedRepository.get(id);
        if (data == null) {
            throw new RuntimeException("No se encontró la cofirma con id=" + id);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "documento-cofirmado.pdf");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
