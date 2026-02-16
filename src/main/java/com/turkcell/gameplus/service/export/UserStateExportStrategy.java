package com.turkcell.gameplus.service.export;

import com.turkcell.gameplus.model.UserState;
import com.turkcell.gameplus.repository.UserStateRepository;
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
public class UserStateExportStrategy implements ExportStrategy {

    private final UserStateRepository userStateRepository;
    private final CsvUtil csvUtil;

    @Override
    public void export(String outputBasePath) throws IOException {
        String filePath = outputBasePath + "user_state.csv";
        List<UserState> states = userStateRepository.findAll();
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (UserState state : states) {
            String[] row = {state.getUserId(), state.getAsOfDate().format(formatter),
                    String.valueOf(state.getLoginCountToday()),
                    String.valueOf(state.getPlayMinutesToday()),
                    String.valueOf(state.getPvpWinsToday()),
                    String.valueOf(state.getCoopMinutesToday()),
                    String.valueOf(state.getTopupTryToday()),
                    String.valueOf(state.getPlayMinutes7d()), String.valueOf(state.getTopupTry7d()),
                    String.valueOf(state.getLogins7d()),
                    String.valueOf(state.getLoginStreakDays())};
            data.add(row);
        }

        String[] header = {"user_id", "as_of_date", "login_count_today", "play_minutes_today",
                "pvp_wins_today", "coop_minutes_today", "topup_try_today", "play_minutes_7d",
                "topup_try_7d", "logins_7d", "login_streak_days"};
        csvUtil.writeCsv(filePath, data, header);
        log.info("Exported {} user states", data.size());
    }
}
