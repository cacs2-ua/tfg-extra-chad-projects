package vitalsanity.dto;

import java.util.Objects;

public class ReportDTO {

    Long id;
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

    // getters y setters

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

    public String getPresignedUrl() {
        return presignedUrl;
    }

    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportDTO)) return false;
        ReportDTO reportDTO = (ReportDTO) o;
        return Objects.equals(id, reportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
