package com.example.eventdriven.web;

import com.example.eventdriven.config.LogProperties;
import com.example.eventdriven.config.RatingProperties;
import com.example.eventdriven.core.EventStore;
import com.example.eventdriven.state.UserStateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final UserStateService stateService;
    private final EventStore eventStore;
    private final RatingProperties rating;
    private final LogProperties logProperties;

    public PageController(UserStateService stateService,
                          EventStore eventStore,
                          RatingProperties rating,
                          LogProperties logProperties) {
        this.stateService = stateService;
        this.eventStore = eventStore;
        this.rating = rating;
        this.logProperties = logProperties;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("state", stateService.get());
        model.addAttribute("events", eventStore.all().stream().map(event -> new EventView(
                event.getClass().getSimpleName(),
                event.kind().name(),
                event.correlationId().toString(),
                event.occurredAt().toString(),
                event.toString())).toList());
        model.addAttribute("pointsPer100", rating.getPointsPer100());
        model.addAttribute("logPath", logProperties.getPath());
        model.addAttribute("wipeOnStart", logProperties.isWipeOnStart());
        return "index";
    }
}
