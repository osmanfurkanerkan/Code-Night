package com.turkcell.gameplus.util;

import com.turkcell.gameplus.model.UserState;
import com.turkcell.gameplus.service.strategy.ConditionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConditionEvaluator {

    private final List<ConditionStrategy> strategies;

    private static final Pattern CONDITION_PATTERN =
            Pattern.compile("([a-zA-Z_0-9]+)\\s*(>=|<=|==|>|<)\\s*(\\d+(?:\\.\\d+)?)");

    public boolean evaluate(String condition, UserState userState) {
        if (condition == null || condition.trim().isEmpty()) {
            return false;
        }

        try {
            Matcher matcher = CONDITION_PATTERN.matcher(condition.trim());
            if (!matcher.matches()) {
                log.warn("Condition pattern does not match: {}", condition);
                return false;
            }

            String fieldName = matcher.group(1);
            String operator = matcher.group(2);
            String valueStr = matcher.group(3);

            // Find matching strategy
            for (ConditionStrategy strategy : strategies) {
                if (strategy.supports(fieldName)) {
                    return strategy.evaluate(fieldName, operator, valueStr, userState);
                }
            }

            log.warn("No strategy found for field: {}", fieldName);
            return false;

        } catch (Exception e) {
            log.error("Error evaluating condition: {}", condition, e);
            return false;
        }
    }

    public boolean evaluateTotalPointsCondition(String condition, int totalPoints) {
        if (condition == null || condition.trim().isEmpty()) {
            return false;
        }

        try {
            Matcher matcher = CONDITION_PATTERN.matcher(condition.trim());
            if (!matcher.matches()) {
                return false;
            }

            String fieldName = matcher.group(1);
            if (!AppConstants.FIELD_TOTAL_POINTS.equals(fieldName)) {
                return false;
            }

            String operator = matcher.group(2);
            double threshold = Double.parseDouble(matcher.group(3));

            return evaluateOperator(totalPoints, operator, threshold);

        } catch (Exception e) {
            log.error("Error evaluating total_points condition: {}", condition, e);
            return false;
        }
    }

    private boolean evaluateOperator(double actualValue, String operator, double threshold) {
        switch (operator) {
            case AppConstants.OP_GREATER_THAN_OR_EQUAL:
                return actualValue >= threshold;
            case AppConstants.OP_LESS_THAN_OR_EQUAL:
                return actualValue <= threshold;
            case AppConstants.OP_EQUALS:
                return Math.abs(actualValue - threshold) < 0.0001; // Float comparison
            case AppConstants.OP_GREATER_THAN:
                return actualValue > threshold;
            case AppConstants.OP_LESS_THAN:
                return actualValue < threshold;
            default:
                log.warn("Unsupported operator: {}", operator);
                return false;
        }
    }
}
