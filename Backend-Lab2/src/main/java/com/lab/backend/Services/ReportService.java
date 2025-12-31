package com.lab.backend.Services;

import com.lab.backend.DTO.DailyWeight;
import com.lab.backend.Repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<DailyWeight> getDailyCollectedWeight() {
        return reportRepository.getDailyCollectedWeight();
    }
}
