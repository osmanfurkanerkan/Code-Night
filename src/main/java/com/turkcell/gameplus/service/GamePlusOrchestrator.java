package com.turkcell.gameplus.service;

import com.turkcell.gameplus.model.QuestAward;
import com.turkcell.gameplus.model.User;
import com.turkcell.gameplus.model.UserState;
import com.turkcell.gameplus.repository.UserRepository;
import com.turkcell.gameplus.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamePlusOrchestrator {

    private final UserRepository userRepository;
    private final MetricsService metricsService;
    private final QuestEngine questEngine;
    private final PointsLedgerService pointsLedgerService;
    private final BadgeService badgeService;
    private final NotificationService notificationService;
    private final LeaderboardService leaderboardService;

    @Transactional
    public void processDate(LocalDate asOfDate) {
        log.info("Processing date: {}", asOfDate);

        // Get all users
        List<User> users = userRepository.findAll();
        log.info("Processing {} users", users.size());

        // Process each user
        for (User user : users) {
            try {
                processUser(user.getUserId(), asOfDate);
            } catch (Exception e) {
                log.error("Error processing user: {}", user.getUserId(), e);
                // Continue with next user
            }
        }

        // Generate leaderboard for all users
        leaderboardService.generateLeaderboard(asOfDate);

        log.info("Completed processing date: {}", asOfDate);
    }

    @Transactional
    public void processUser(String userId, LocalDate asOfDate) {
        log.debug("Processing user: {} for date: {}", userId, asOfDate);

        // 1. Calculate UserState
        UserState userState = metricsService.calculateUserState(userId, asOfDate);
        log.debug("UserState calculated for user: {}", userId);

        // 2. Evaluate Quests
        QuestAward questAward = questEngine.evaluateQuests(userId, asOfDate, userState);

        if (questAward != null) {
            // 3. Add points to ledger
            pointsLedgerService.addPoints(userId, questAward.getRewardPoints(),
                    AppConstants.QUALIFIER_QUEST_REWARD, questAward.getAwardId());
            log.debug("Added {} points to user: {} from quest: {}", questAward.getRewardPoints(),
                    userId, questAward.getSelectedQuestId());

            // 4. Send notification
            String message = String.format(
                    "Tebrikler! '%s' görevini tamamladınız ve %d puan kazandınız!",
                    questAward.getSelectedQuest() != null
                            ? questAward.getSelectedQuest().getQuestName()
                            : AppConstants.DEFAULT_QUEST_NAME,
                    questAward.getRewardPoints());
            notificationService.sendNotification(userId, message, asOfDate);
        }

        // 5. Evaluate Badges
        badgeService.evaluateBadges(userId, asOfDate);
        log.debug("Badges evaluated for user: {}", userId);
    }
}

