package vitalsanity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vitalsanity.model.MedicalReport;
import vitalsanity.model.User;

import java.util.List;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {

    List<MedicalReport> findByUser(User user);

    boolean existsByName(String name);

    boolean existsByS3Key(String s3Key);
}
