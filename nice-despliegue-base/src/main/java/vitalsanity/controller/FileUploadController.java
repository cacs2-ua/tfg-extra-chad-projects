package vitalsanity.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitalsanity.authentication.ManagerUserSession;
import vitalsanity.model.MedicalReport;
import vitalsanity.model.User;
import vitalsanity.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UserService userService;

    @Autowired
    private MedicalReportService medicalReportService;

    private Long getUsuarioLogeadoId() {
        return managerUserSession.usuarioLogeado();
    }

    // Mostrar la página de subida (listar solo los archivos del usuario)
    @GetMapping("/upload")
    public String listUploadedFiles(Model model) {
        Long userId = getUsuarioLogeadoId();
        User user = userService.findById(userId);

        // Obtenemos los MedicalReports del usuario
        List<MedicalReport> reports = medicalReportService.findByUser(user);

        // Generamos URL pre-firmada para cada reporte
        List<ReportDTO> dtos = reports.stream().map(report -> {
            // Generar la URL pre-firmada (validez 1 hora por ejemplo)
            String presignedUrl = s3Service.generatePresignedUrl(
                    userId,
                    report.getS3Key(),
                    Duration.ofMinutes(30)
            );
            return new ReportDTO(
                    report.getName(),
                    presignedUrl,
                    report.getFileType(),
                    report.getSize()
            );
        }).collect(Collectors.toList());

        model.addAttribute("reports", dtos);
        return "upload";
    }

    // Manejar la subida de varios archivos
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files, RedirectAttributes redirectAttributes) {
        Long userId = getUsuarioLogeadoId();
        User user = userService.findById(userId);

        StringBuilder status = new StringBuilder();

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try {
                // 1) Subir a S3 con prefijo "informes/user-<userId>/..."
                s3Service.uploadFile(userId, fileName, file);

                // 2) Registrar en la tabla medical_reports
                String s3Key = "informes/user-" + userId + "/" + fileName;
                MedicalReport mr = new MedicalReport();
                mr.setName(fileName);
                mr.setS3Key(s3Key);
                mr.setFileType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
                mr.setSize(file.getSize());
                mr.setUploaded_at(new Timestamp(Instant.now().toEpochMilli()));
                mr.setUser(user);

                boolean existsReport = medicalReportService.existsMedicalReportByS3Key(s3Key);

                if (!existsReport) {
                    medicalReportService.createMedicalReport(mr);
                    status.append("Archivo ").append(fileName).append(" subido exitosamente.<br>");
                }

                else  {
                    status.append("La s3Key del archivo que acabas de subir con nombre: ").append(fileName).append(" ya existía en la base de datos. El archivo no se ha subido.<br>");
                }

            } catch (IOException e) {
                status.append("Error al subir ").append(fileName)
                        .append(": ").append(e.getMessage()).append("<br>");
            }
        }
        // Añadir el mensaje de status como atributo flash
        redirectAttributes.addFlashAttribute("status", status.toString());
        return "redirect:/upload";
    }


    // DTO interno para mostrar info en la plantilla
    static class ReportDTO {
        private String name;
        private String presignedUrl;
        private String fileType;
        private Long size;

        public ReportDTO(String name, String presignedUrl, String fileType, Long size) {
            this.name = name;
            this.presignedUrl = presignedUrl;
            this.fileType = fileType;
            this.size = size;
        }

        // getters
        public String getName() { return name; }
        public String getPresignedUrl() { return presignedUrl; }
        public String getFileType() { return fileType; }
        public Long getSize() { return size; }
    }
}
