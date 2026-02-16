package com.turkcell.gameplus.service.export;

import com.turkcell.gameplus.model.BadgeAward;
import com.turkcell.gameplus.repository.BadgeAwardRepository;
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
public class BadgeAwardExportStrategy implements ExportStrategy {

    private final BadgeAwardRepository badgeAwardRepository;
    private final CsvUtil csvUtil;

    @Override
    public void export(String outputBasePath) throws IOException {
        String filePath = outputBasePath + "badge_awards.csv";
        List<BadgeAward> awards = badgeAwardRepository.findAll();
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (BadgeAward award : awards) {
            String[] row = {award.getUserId(), award.getBadgeId(),
                    award.getAwardedAt() != null ? award.getAwardedAt().format(formatter) : ""};
            data.add(row);
        }

        String[] header = {"user_id", "badge_id", "awarded_at"};
        csvUtil.writeCsv(filePath, data, header);
        log.info("Exported {} badge awards", data.size());
    }
}
