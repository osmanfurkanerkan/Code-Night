package com.turkcell.gameplus.service;

import com.turkcell.gameplus.service.export.ExportStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataExportService {

    private final List<ExportStrategy> exportStrategies;

    @Value("${csv.output.path}")
    private String outputPath;

    public void exportToCsv() throws IOException {
        log.info("Starting CSV export...");

        for (ExportStrategy strategy : exportStrategies) {
            try {
                strategy.export(outputPath);
            } catch (Exception e) {
                log.error("Error executing export strategy: {}",
                        strategy.getClass().getSimpleName(), e);
                // Decide whether to continue or abort. Logging and continuing is often safer for
                // batch jobs.
            }
        }

        log.info("CSV export completed.");
    }
}
