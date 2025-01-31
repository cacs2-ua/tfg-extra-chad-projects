package vitalsanity.controller;

import vitalsanity.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

@Controller
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    // Mostrar la página de subida
    @GetMapping("/upload")
    public String listUploadedFiles(Model model) {
        List<S3Object> files = s3Service.listFiles();
        model.addAttribute("files", files);
        return "upload";
    }

    // Manejar la subida de varios archivos
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files, Model model) {
        StringBuilder status = new StringBuilder();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                s3Service.uploadFile(fileName, file);
                status.append("Archivo ").append(fileName).append(" subido exitosamente.<br>");
            } catch (IOException e) {
                status.append("Error al subir ").append(fileName)
                        .append(": ").append(e.getMessage()).append("<br>");
            }
        }
        model.addAttribute("status", status.toString());
        model.addAttribute("files", s3Service.listFiles());
        return "upload";
    }
}
