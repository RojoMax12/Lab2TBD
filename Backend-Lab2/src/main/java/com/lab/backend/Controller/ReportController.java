package com.lab.backend.Controller;

import com.lab.backend.DTO.DailyWeight;
import com.lab.backend.Services.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily-weight")
    public List<DailyWeight> getDailyWeight() {
        return reportService.getDailyCollectedWeight();
    }
}
