package com.turkcell.gameplus.util;

public final class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    // Quest Related
    public static final String QUALIFIER_QUEST_REWARD = "QUEST_REWARD";
    public static final String DEFAULT_QUEST_NAME = "Quest";

    // User State Fields (for ConditionEvaluator)
    public static final String FIELD_LOGIN_COUNT_TODAY = "login_count_today";
    public static final String FIELD_PLAY_MINUTES_TODAY = "play_minutes_today";
    public static final String FIELD_PVP_WINS_TODAY = "pvp_wins_today";
    public static final String FIELD_COOP_MINUTES_TODAY = "coop_minutes_today";
    public static final String FIELD_TOPUP_TRY_TODAY = "topup_try_today";
    public static final String FIELD_PLAY_MINUTES_7D = "play_minutes_7d";
    public static final String FIELD_TOPUP_TRY_7D = "topup_try_7d";
    public static final String FIELD_LOGINS_7D = "logins_7d";
    public static final String FIELD_LOGIN_STREAK_DAYS = "login_streak_days";
    public static final String FIELD_TOTAL_POINTS = "total_points";

    // Operators
    public static final String OP_GREATER_THAN_OR_EQUAL = ">=";
    public static final String OP_LESS_THAN_OR_EQUAL = "<=";
    public static final String OP_EQUALS = "==";
    public static final String OP_GREATER_THAN = ">";
    public static final String OP_LESS_THAN = "<";

    // Award ID Prefix
    public static final String PREFIX_AWARD_ID = "A-";

    // Error Messages
    public static final String ERR_USER_NOT_FOUND = "User not found: ";
}
