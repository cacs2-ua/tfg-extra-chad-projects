package vitalsanity.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class S3Service {

    private S3Client s3Client;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @PostConstruct
    private void init() {
        // Para DEV: las credenciales locales (AWS CLI / Variables de Entorno).
        // Para PROD: Roles IAM (si la app se ejecuta en EC2, ECS, etc.).
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // Subir archivo
    public void uploadFile(String fileName, MultipartFile file) throws IOException {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
    }

    // Descargar archivo
    public byte[] downloadFile(String fileName) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseBytes<GetObjectResponse> s3Object = s3Client.getObjectAsBytes(getRequest);
        return s3Object.asByteArray();
    }

    // Listar archivos
    public List<S3Object> listFiles() {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(listRequest);
        return result.contents();
    }

    // Eliminar archivo
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
