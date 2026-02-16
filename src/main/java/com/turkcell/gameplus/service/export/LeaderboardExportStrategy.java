package com.turkcell.gameplus.service.export;

import com.turkcell.gameplus.model.LeaderboardEntry;
import com.turkcell.gameplus.repository.LeaderboardEntryRepository;
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
public class LeaderboardExportStrategy implements ExportStrategy {

    private final LeaderboardEntryRepository leaderboardEntryRepository;
    private final CsvUtil csvUtil;

    @Override
    public void export(String outputBasePath) throws IOException {
        String filePath = outputBasePath + "leaderboard.csv";
        List<LeaderboardEntry> entries = leaderboardEntryRepository.findAll();
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (LeaderboardEntry entry : entries) {
            String[] row = {String.valueOf(entry.getRank()), entry.getUserId(),
                    String.valueOf(entry.getTotalPoints()), entry.getAsOfDate().format(formatter)};
            data.add(row);
        }

        String[] header = {"rank", "user_id", "total_points", "as_of_date"};
        csvUtil.writeCsv(filePath, data, header);
        log.info("Exported {} leaderboard entries", data.size());
    }
}
