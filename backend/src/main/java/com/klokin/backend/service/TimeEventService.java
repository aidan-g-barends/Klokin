package com.klokin.backend.service;

import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.dto.TimeEventDto;

import java.util.List;

public interface TimeEventService {

    // Stores a batch of events from a phone. Safe to call again with the same events.
    TimeEventDto.SyncResponse sync(TimeEventDto.SyncRequest request);

    List<TimeEvent> getRecentForCompany(Long companyId, int limit);
}
