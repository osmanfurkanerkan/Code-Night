package com.turkcell.gameplus.service.strategy;

import com.turkcell.gameplus.model.UserState;
import com.turkcell.gameplus.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class NumericConditionStrategy implements ConditionStrategy {

    private static final Map<String, String> FIELD_MAPPING =
            Map.of(AppConstants.FIELD_LOGIN_COUNT_TODAY, "loginCountToday",
                    AppConstants.FIELD_PLAY_MINUTES_TODAY, "playMinutesToday",
                    AppConstants.FIELD_PVP_WINS_TODAY, "pvpWinsToday",
                    AppConstants.FIELD_COOP_MINUTES_TODAY, "coopMinutesToday",
                    AppConstants.FIELD_TOPUP_TRY_TODAY, "topupTryToday",
                    AppConstants.FIELD_PLAY_MINUTES_7D, "playMinutes7d",
                    AppConstants.FIELD_TOPUP_TRY_7D, "topupTry7d", AppConstants.FIELD_LOGINS_7D,
                    "logins7d", AppConstants.FIELD_LOGIN_STREAK_DAYS, "loginStreakDays");

    @Override
    public boolean supports(String fieldName) {
        return FIELD_MAPPING.containsKey(fieldName);
    }

    @Override
    public boolean evaluate(String fieldName, String operator, String valueStr,
            UserState userState) {
        try {
            String actualFieldName = FIELD_MAPPING.get(fieldName);
            if (actualFieldName == null)
                return false;

            Field field = UserState.class.getDeclaredField(actualFieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(userState);

            if (fieldValue == null)
                return false;

            double actualValue;
            if (fieldValue instanceof Integer) {
                actualValue = ((Integer) fieldValue).doubleValue();
            } else if (fieldValue instanceof Double) {
                actualValue = (Double) fieldValue;
            } else {
                return false;
            }

            double threshold = Double.parseDouble(valueStr);

            return switch (operator) {
                case AppConstants.OP_GREATER_THAN_OR_EQUAL -> actualValue >= threshold;
                case AppConstants.OP_LESS_THAN_OR_EQUAL -> actualValue <= threshold;
                case AppConstants.OP_EQUALS -> Math.abs(actualValue - threshold) < 0.0001;
                case AppConstants.OP_GREATER_THAN -> actualValue > threshold;
                case AppConstants.OP_LESS_THAN -> actualValue < threshold;
                default -> false;
            };
        } catch (Exception e) {
            log.error("Error evaluating numeric condition for field: {}", fieldName, e);
            return false;
        }
    }
}
