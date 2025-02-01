package vitalsanity.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Service
public class S3Service {

    private S3Client s3Client;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.kms.key-id:}")
    private String kmsKeyId;

    @PostConstruct
    private void init() {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // Subir archivo a S3 con prefijo "informes/user-{userId}/..."
    public void uploadFile(Long userId, String fileName, MultipartFile file) throws IOException {
        String key = "informes/user-" + userId + "/" + fileName;

        // Se indica el cifrado SSE-KMS, pero sin espeit stcificar la clave (se usará la configuración predeterminada del bucket)
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
    }


    // Generar una URL pre-firmada para descargar el archivo
    public String generatePresignedUrl(Long userId, String s3Key, Duration duration) {
        // Creamos un S3Presigner (se autogestiona con DefaultCredentialsProvider)
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest =
                    presigner.presignGetObject(presignRequest);

            // Retorna la URL pre-firmada
            return presignedGetObjectRequest.url().toString();
        }
    }

    // Listar objetos de S3 (no se usa si obtenemos la info de la DB)
    public List<S3Object> listAllFilesInBucket() {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(listRequest);
        return result.contents();
    }
}
