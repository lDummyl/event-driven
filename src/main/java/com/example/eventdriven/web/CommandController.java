package com.example.eventdriven.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.eventdriven.core.EventRouter;
import com.example.eventdriven.core.EventStore;
import com.example.eventdriven.service.DonationService;
import com.example.eventdriven.service.UserPrimalDataService;
import com.example.eventdriven.state.UserStateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommandController {

    private final DonationService donations;
    private final UserPrimalDataService userPrimalData;
    private final EventRouter router;
    private final EventStore eventStore;
    private final UserStateService stateService;

    public CommandController(DonationService donations,
                             UserPrimalDataService userPrimalData,
                             EventRouter router,
                             EventStore eventStore,
                             UserStateService stateService) {
        this.donations = donations;
        this.userPrimalData = userPrimalData;
        this.router = router;
        this.eventStore = eventStore;
        this.stateService = stateService;
    }

    @PostMapping("/donate")
    public Map<String, Object> donate(@RequestBody Map<String, String> body) {
        return run(() -> donations.donate(Long.parseLong(body.getOrDefault("amount", "0"))));
    }

    @PostMapping("/username")
    public Map<String, Object> changeUsername(@RequestBody Map<String, String> body) {
        return run(() -> userPrimalData.changeUsername(body.getOrDefault("name", "")));
    }

    @PostMapping("/email")
    public Map<String, Object> changeEmail(@RequestBody Map<String, String> body) {
        return run(() -> userPrimalData.changeEmail(body.getOrDefault("email", "")));
    }

    @PostMapping("/rebuild")
    public Map<String, Object> rebuild() {
        return run(router::rebuild);
    }

    @PostMapping("/seed/username")
    public Map<String, Object> seedUsername(@RequestBody Map<String, String> body) {
        return run(() -> userPrimalData.seedLegacyUsername(body.getOrDefault("name", "12345")));
    }

    @PostMapping("/seed/email")
    public Map<String, Object> seedEmail(@RequestBody Map<String, String> body) {
        return run(() -> userPrimalData.seedLegacyEmail(body.getOrDefault("email", "boss@legacy.mail")));
    }

    @GetMapping("/state")
    public Map<String, Object> state() {
        return snapshot(true, null);
    }

    private Map<String, Object> run(Runnable action) {
        try {
            action.run();
            return snapshot(true, "OK");
        } catch (IllegalArgumentException e) {
            return snapshot(false, e.getMessage());
        }
    }

    private Map<String, Object> snapshot(boolean ok, String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ok", ok);
        response.put("message", message);
        response.put("state", stateService.get());
        List<String> log = eventStore.all().stream().map(Object::toString).toList();
        response.put("logSize", log.size());
        return response;
    }
}
