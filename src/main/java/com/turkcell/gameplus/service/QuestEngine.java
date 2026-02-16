package com.turkcell.gameplus.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkcell.gameplus.model.Quest;
import com.turkcell.gameplus.model.QuestAward;
import com.turkcell.gameplus.model.User;
import com.turkcell.gameplus.model.UserState;
import com.turkcell.gameplus.repository.QuestAwardRepository;
import com.turkcell.gameplus.repository.QuestRepository;
import com.turkcell.gameplus.repository.UserRepository;
import com.turkcell.gameplus.util.AppConstants;
import com.turkcell.gameplus.util.ConditionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestEngine {

    private final QuestRepository questRepository;
    private final QuestAwardRepository questAwardRepository;
    private final UserRepository userRepository;
    private final ConditionEvaluator conditionEvaluator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public QuestAward evaluateQuests(String userId, LocalDate asOfDate, UserState userState) {
        log.debug("Evaluating quests for user: {} as of date: {}", userId, asOfDate);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Check if user already has an award for this date
        List<QuestAward> existingAwards =
                questAwardRepository.findByUserIdAndAsOfDate(userId, asOfDate);
        if (!existingAwards.isEmpty()) {
            log.info("User {} already has {} quest award(s) for date {}. Skipping evaluation.",
                    userId, existingAwards.size(), asOfDate);
            return null;
        }

        // Load active quests
        List<Quest> activeQuests = questRepository.findByIsActiveTrue();
        log.debug("Found {} active quests", activeQuests.size());

        // Find triggered quests
        List<Quest> triggeredQuests = new ArrayList<>();
        for (Quest quest : activeQuests) {
            if (conditionEvaluator.evaluate(quest.getCondition(), userState)) {
                triggeredQuests.add(quest);
                log.debug("Quest triggered: {} (priority: {})", quest.getQuestName(),
                        quest.getPriority());
            }
        }

        if (triggeredQuests.isEmpty()) {
            log.debug("No quests triggered for user: {}", userId);
            return null;
        }

        // Resolve conflicts: select quest with highest priority (lowest priority number)
        Quest selectedQuest =
                triggeredQuests.stream().min(Comparator.comparing(Quest::getPriority)).orElse(null);

        if (selectedQuest == null) {
            log.warn("No quest selected despite triggered quests found");
            return null;
        }

        // Get suppressed quests (all triggered except selected)
        List<Quest> suppressedQuests = triggeredQuests.stream()
                .filter(q -> !q.getQuestId().equals(selectedQuest.getQuestId()))
                .collect(Collectors.toList());

        // Create QuestAward
        QuestAward award = new QuestAward();
        award.setAwardId(AppConstants.PREFIX_AWARD_ID + UUID.randomUUID().toString());
        award.setUser(user);
        award.setUserId(userId);
        award.setAsOfDate(asOfDate);
        award.setSelectedQuest(selectedQuest);
        award.setSelectedQuestId(selectedQuest.getQuestId());
        award.setRewardPoints(selectedQuest.getRewardPoints());

        // Store triggered and suppressed quests as JSON strings
        try {
            List<String> triggeredQuestIds =
                    triggeredQuests.stream().map(Quest::getQuestId).collect(Collectors.toList());
            award.setTriggeredQuests(objectMapper.writeValueAsString(triggeredQuestIds));

            if (!suppressedQuests.isEmpty()) {
                List<String> suppressedQuestIds = suppressedQuests.stream().map(Quest::getQuestId)
                        .collect(Collectors.toList());
                award.setSuppressedQuests(objectMapper.writeValueAsString(suppressedQuestIds));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing quest lists", e);
        }

        QuestAward savedAward = questAwardRepository.save(award);
        log.info("Quest award created: {} for user: {} with quest: {} ({} points)",
                savedAward.getAwardId(), userId, selectedQuest.getQuestName(),
                selectedQuest.getRewardPoints());

        return savedAward;
    }
}

