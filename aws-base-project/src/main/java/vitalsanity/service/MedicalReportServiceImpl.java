package vitalsanity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vitalsanity.model.MedicalReport;
import vitalsanity.model.User;
import vitalsanity.repository.MedicalReportRepository;

import java.util.List;

@Service
public class MedicalReportServiceImpl implements MedicalReportService {

    @Autowired
    private MedicalReportRepository medicalReportRepository;

    @Override
    public MedicalReport createMedicalReport(MedicalReport report) {
        return medicalReportRepository.save(report);
    }

    @Override
    public List<MedicalReport> findByUser(User user) {
        return medicalReportRepository.findByUser(user);
    }

    @Override
    public boolean existsMedicalReportByName(String name) {
        return medicalReportRepository.existsByName(name);
    }
}
