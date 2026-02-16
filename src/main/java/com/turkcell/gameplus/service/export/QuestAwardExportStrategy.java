package com.turkcell.gameplus.service.export;

import com.turkcell.gameplus.model.QuestAward;
import com.turkcell.gameplus.repository.QuestAwardRepository;
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
public class QuestAwardExportStrategy implements ExportStrategy {

    private final QuestAwardRepository questAwardRepository;
    private final CsvUtil csvUtil;

    @Override
    public void export(String outputBasePath) throws IOException {
        String filePath = outputBasePath + "quest_awards.csv";
        List<QuestAward> awards = questAwardRepository.findAll();
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (QuestAward award : awards) {
            String[] row = {award.getAwardId(), award.getUserId(),
                    award.getAsOfDate().format(dateFormatter), award.getTriggeredQuests(),
                    award.getSelectedQuestId(), String.valueOf(award.getRewardPoints()),
                    award.getSuppressedQuests(),
                    award.getTimestamp() != null ? award.getTimestamp().format(timestampFormatter)
                            : ""};
            data.add(row);
        }

        String[] header = {"award_id", "user_id", "as_of_date", "triggered_quests",
                "selected_quest", "reward_points", "suppressed_quests", "timestamp"};
        csvUtil.writeCsv(filePath, data, header);
        log.info("Exported {} quest awards", data.size());
    }
}
