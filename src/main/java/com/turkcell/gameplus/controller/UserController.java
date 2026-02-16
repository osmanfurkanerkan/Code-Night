package com.turkcell.gameplus.controller;

import com.turkcell.gameplus.dto.UserDetailDto;
import com.turkcell.gameplus.dto.UserDto;
import com.turkcell.gameplus.mapper.UserMapper;
import com.turkcell.gameplus.model.*;
import com.turkcell.gameplus.repository.*;
import com.turkcell.gameplus.util.AppConstants;
import com.turkcell.gameplus.service.BadgeService;
import com.turkcell.gameplus.service.NotificationService;
import com.turkcell.gameplus.service.PointsLedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

        private final UserRepository userRepository;
        private final PointsLedgerService pointsLedgerService;
        private final UserStateRepository userStateRepository;
        private final QuestAwardRepository questAwardRepository;
        private final BadgeService badgeService;
        private final NotificationService notificationService;
        private final UserMapper userMapper;

        @GetMapping
        public ResponseEntity<List<UserDto>> getAllUsers() {
                List<User> users = userRepository.findAll();
                List<UserDto> userDtos = users.stream().map(user -> {
                        Integer totalPoints = pointsLedgerService.getTotalPoints(user.getUserId());
                        return userMapper.toUserDto(user, totalPoints);
                }).collect(Collectors.toList());
                return ResponseEntity.ok(userDtos);
        }

        @GetMapping("/{userId}")
        public ResponseEntity<UserDetailDto> getUserDetail(@PathVariable String userId) {
                User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(
                                AppConstants.ERR_USER_NOT_FOUND + userId));

                Integer totalPoints = pointsLedgerService.getTotalPoints(userId);

                // Get latest user state
                UserState latestState = userStateRepository.findAll().stream()
                                .filter(state -> state.getUserId().equals(userId))
                                .max((s1, s2) -> s1.getAsOfDate().compareTo(s2.getAsOfDate()))
                                .orElse(null);

                // Get quest awards
                List<QuestAward> questAwards =
                                questAwardRepository.findByUserIdOrderByAsOfDateDesc(userId);

                // Get badges
                List<BadgeAward> badges = badgeService.getUserBadges(userId);

                // Get notifications
                List<Notification> notifications = notificationService.getUserNotifications(userId);

                // Build DTO using Mapper
                UserDetailDto detailDto = userMapper.toUserDetailDto(user, totalPoints, latestState,
                                questAwards, badges, notifications);

                return ResponseEntity.ok(detailDto);
        }
}
