package com.turkcell.gameplus.service.strategy;

import com.turkcell.gameplus.model.UserState;

public interface ConditionStrategy {
    boolean evaluate(String fieldName, String operator, String valueStr, UserState userState);

    boolean supports(String fieldName);
}
