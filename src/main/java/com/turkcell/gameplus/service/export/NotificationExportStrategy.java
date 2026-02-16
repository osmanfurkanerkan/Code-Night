package com.turkcell.gameplus.service.export;

import com.turkcell.gameplus.model.Notification;
import com.turkcell.gameplus.repository.NotificationRepository;
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
public class NotificationExportStrategy implements ExportStrategy {

    private final NotificationRepository notificationRepository;
    private final CsvUtil csvUtil;

    @Override
    public void export(String outputBasePath) throws IOException {
        String filePath = outputBasePath + "notifications.csv";
        List<Notification> notifications = notificationRepository.findAll();
        List<String[]> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Notification notification : notifications) {
            String[] row = {notification.getNotificationId(), notification.getUserId(),
                    notification.getChannel(), notification.getMessage(),
                    notification.getSentAt() != null ? notification.getSentAt().format(formatter)
                            : ""};
            data.add(row);
        }

        String[] header = {"notification_id", "user_id", "channel", "message", "sent_at"};
        csvUtil.writeCsv(filePath, data, header);
        log.info("Exported {} notifications", data.size());
    }
}
