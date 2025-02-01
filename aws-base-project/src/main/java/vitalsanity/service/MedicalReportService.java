package vitalsanity.service;

import vitalsanity.model.MedicalReport;
import vitalsanity.model.User;

import java.util.List;

public interface MedicalReportService {
    MedicalReport createMedicalReport(MedicalReport report);
    List<MedicalReport> findByUser(User user);
}
