package com.help.tap.repository;

import com.help.tap.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {

    List<AccessLog> findByWearable_IdOrderByAccessedAtDesc(Integer wearableId);

    List<AccessLog> findByWearable_User_IdOrderByAccessedAtDesc(Integer userId);

    List<AccessLog> findByWearable_IdAndAccessLevel(Integer wearableId, AccessLog.AccessLevel accessLevel);

    List<AccessLog> findByWearable_User_IdAndAccessedAtBetween(
            Integer userId, LocalDateTime from, LocalDateTime to);

    int countByWearable_Id(Integer wearableId);
}
