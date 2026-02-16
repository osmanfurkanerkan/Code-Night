package com.turkcell.gameplus.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkcell.gameplus.dto.UserDetailDto;
import com.turkcell.gameplus.dto.UserDto;
import com.turkcell.gameplus.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserDto toUserDto(User user, Integer totalPoints) {
        return new UserDto(user.getUserId(), user.getName(), user.getCity(), user.getSegment(),
                totalPoints);
    }

    public UserDetailDto toUserDetailDto(User user, Integer totalPoints, UserState latestState,
            List<QuestAward> questAwards, List<BadgeAward> badges,
            List<Notification> notifications) {
        UserDetailDto detailDto = new UserDetailDto();
        detailDto.setUserId(user.getUserId());
        detailDto.setName(user.getName());
        detailDto.setCity(user.getCity());
        detailDto.setSegment(user.getSegment());
        detailDto.setTotalPoints(totalPoints);

        if (latestState != null) {
            detailDto.setUserState(toUserStateDto(latestState));
        }

        detailDto.setQuestAwards(
                questAwards.stream().map(this::toQuestAwardDto).collect(Collectors.toList()));

        detailDto
                .setBadges(badges.stream().map(this::toBadgeAwardDto).collect(Collectors.toList()));

        detailDto.setNotifications(
                notifications.stream().map(this::toNotificationDto).collect(Collectors.toList()));

        return detailDto;
    }

    private UserDetailDto.UserStateDto toUserStateDto(UserState state) {
        return new UserDetailDto.UserStateDto(state.getAsOfDate(), state.getLoginCountToday(),
                state.getPlayMinutesToday(), state.getPvpWinsToday(), state.getCoopMinutesToday(),
                state.getTopupTryToday(), state.getPlayMinutes7d(), state.getTopupTry7d(),
                state.getLogins7d(), state.getLoginStreakDays());
    }

    private UserDetailDto.QuestAwardDto toQuestAwardDto(QuestAward award) {
        List<String> triggered = parseJsonList(award.getTriggeredQuests());
        List<String> suppressed = parseJsonList(award.getSuppressedQuests());
        return new UserDetailDto.QuestAwardDto(award.getAwardId(), award.getAsOfDate(),
                award.getSelectedQuest() != null ? award.getSelectedQuest().getQuestName() : "",
                award.getRewardPoints(), triggered, suppressed);
    }

    private UserDetailDto.BadgeAwardDto toBadgeAwardDto(BadgeAward badge) {
        return new UserDetailDto.BadgeAwardDto(badge.getBadgeId(),
                badge.getBadge() != null ? badge.getBadge().getBadgeName() : "",
                badge.getBadge() != null ? badge.getBadge().getLevel() : 0);
    }

    private UserDetailDto.NotificationDto toNotificationDto(Notification notif) {
        return new UserDetailDto.NotificationDto(notif.getNotificationId(), notif.getChannel(),
                notif.getMessage(), notif.getSentAt() != null ? notif.getSentAt().toString() : "");
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Error parsing JSON list: {}", json, e);
            return List.of();
        }
    }
}
