package vitalsanity.controller;

import org.springframework.http.HttpStatus;
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

import java.time.Duration;
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
    public ResponseEntity<Void> downloadFile(@PathVariable String fileName) {
        Long userId = getUsuarioLogeadoId();
        User user = userService.findById(userId);

        String s3Key = "informes/user-" + user.getId() + "/" + fileName;
        String presignedUrl = s3Service.generatePresignedUrl(userId, s3Key, Duration.ofHours(1));
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }
}
