package vitalsanity.controller;

import vitalsanity.authentication.ManagerUserSession;
import vitalsanity.model.User;
import vitalsanity.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Object;
import vitalsanity.service.UserService;

import java.util.List;

@RestController
public class DownloadController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UserService userService;

    private Long getUsuarioLogeadoId() {
        return managerUserSession.usuarioLogeado();
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        Long userId = getUsuarioLogeadoId();
        User user = userService.findById(userId);

        byte[] fileContent = s3Service.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }
}
