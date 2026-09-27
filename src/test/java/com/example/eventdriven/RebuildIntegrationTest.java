package com.example.eventdriven;

import com.example.eventdriven.core.EventRouter;
import com.example.eventdriven.log.EventLog;
import com.example.eventdriven.service.CommandService;
import com.example.eventdriven.state.UserStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "app.log.path=target/test-log/events.ndjson",
        "app.log.wipe-on-start=true",
        "app.rating.points-per-100=200"
})
class RebuildIntegrationTest {

    @Autowired
    EventLog eventLog;
    @Autowired
    EventRouter router;
    @Autowired
    UserStateService stateService;
    @Autowired
    CommandService commands;

    @BeforeEach
    void clean() {
        eventLog.clear();
        router.rebuild();
    }

    @Test
    void frozenUsernameIsAcceptedOnRebuildEvenThoughCurrentRuleRejectsIt() {
        commands.seedLegacyUsername("12345");
        assertThatThrownBy(() -> commands.changeUsername("12345"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(stateService.get().getUsername()).isNotEqualTo("12345");

        router.rebuild();

        assertThat(stateService.get().getUsername()).isEqualTo("12345");
    }

    @Test
    void emailIsRestoredFromItsRecordedResponseByCorrelation() {
        commands.seedLegacyEmail("boss@legacy.mail");
        router.rebuild();
        assertThat(stateService.get().getEmail()).isEqualTo("boss@legacy.mail");
    }

    @Test
    void onlineEmailChangeRejectsNewlyBannedDomain() {
        assertThatThrownBy(() -> commands.changeEmail("me@legacy.mail"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void donationIsRecalculatedOnRebuild() {
        commands.donate(100);
        assertThat(stateService.get().getPoints()).isEqualTo(200);

        router.rebuild();

        assertThat(stateService.get().getPoints()).isEqualTo(200);
    }
}
