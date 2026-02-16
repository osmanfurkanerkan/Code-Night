package com.turkcell.gameplus.service.export;

import com.turkcell.gameplus.model.PointsLedgerEntry;
import com.turkcell.gameplus.repository.PointsLedgerEntryRepository;
import com.turkcell.gameplus.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointsLedgerExportStrategy implements ExportStrategy {

    private final PointsLedgerEntryRepository pointsLedgerEntryRepository;
    private final CsvUtil csvUtil;

    @Override
    public void export(String outputBasePath) throws IOException {
        String filePath = outputBasePath + "points_ledger.csv";
        List<PointsLedgerEntry> entries = pointsLedgerEntryRepository.findAll();
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (PointsLedgerEntry entry : entries) {
            String[] row = {entry.getLedgerId(), entry.getUserId(),
                    String.valueOf(entry.getPointsDelta()), entry.getSource(), entry.getSourceRef(),
                    entry.getCreatedAt() != null ? entry.getCreatedAt().format(formatter) : ""};
            data.add(row);
        }

        String[] header =
                {"ledger_id", "user_id", "points_delta", "source", "source_ref", "created_at"};
        csvUtil.writeCsv(filePath, data, header);
        log.info("Exported {} ledger entries", data.size());
    }
}
