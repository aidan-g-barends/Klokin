package com.klokin.backend.controller;

import com.klokin.backend.dto.TimeEventDto;
import com.klokin.backend.service.TimeEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TimeEventController {

    private final TimeEventService service;

    public TimeEventController(TimeEventService service) {
        this.service = service;
    }

    // The phone calls this whenever it has a connection, with everything it saved offline.
    // Always 200 OK: the per-event results say what was saved, skipped or rejected.
    @PostMapping("/sync/events")
    public TimeEventDto.SyncResponse sync(@RequestBody TimeEventDto.SyncRequest request) {
        return service.sync(request);
    }

    // Boss dashboard: the latest clock-ins, lunches and site visits across the whole company
    @GetMapping("/companies/{companyId}/feed")
    public List<TimeEventDto.FeedItem> feed(@PathVariable Long companyId,
                                            @RequestParam(defaultValue = "50") int limit) {
        return service.getRecentForCompany(companyId, limit).stream().map(TimeEventDto.FeedItem::from).toList();
    }
}
