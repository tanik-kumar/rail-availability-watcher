package com.example.railwatcher.notification;

import com.example.railwatcher.common.model.AlertCandidate;
import com.example.railwatcher.persistence.entity.WatchJobEntity;
import java.util.List;

public interface NotificationService {

    void dispatch(WatchJobEntity watchJob, List<AlertCandidate> alerts);
}
