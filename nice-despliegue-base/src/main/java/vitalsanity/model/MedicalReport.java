package vitalsanity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "medical_reports")
public class MedicalReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "s3_key")
    private String s3Key;

    @NotNull
    @Column(name = "file_type")
    private String fileType;

    @NotNull
    @Column(name = "size")
    private Long size;

    @NotNull
    @Column(name = "uploaded_at")
    private Timestamp uploaded_at;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // getters y setters

    public MedicalReport() {}

    public MedicalReport(String name, String s3Key, String fileType, Long size, Timestamp uploaded_at) {
        this.name = name;
        this.s3Key = s3Key;
        this.fileType = fileType;
        this.size = size;
        this.uploaded_at = uploaded_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Timestamp getUploaded_at() {
        return uploaded_at;
    }

    public void setUploaded_at(Timestamp uploaded_at) {
        this.uploaded_at = uploaded_at;
    }

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        if (this.user == user || user == null) {
            return;
        }

        // Si ya tiene un comercio, lo desvincula de la lista de usuarios de ese comercio
        if (this.user != null) {
            this.user.getMedicalReports().remove(this);
        }

        // Asigna el nuevo comercio
        this.user = user;

        if (!user.getMedicalReports().contains(this)) {
            user.addMedicalReport(this);
        }
    }


}
