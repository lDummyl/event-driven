package com.example.eventdriven;

import com.example.eventdriven.core.EventRouter;
import com.example.eventdriven.core.EventStore;
import com.example.eventdriven.core.Mode;
import com.example.eventdriven.service.DonationService;
import com.example.eventdriven.service.UserPrimalDataService;
import com.example.eventdriven.state.UserState;
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
    EventStore eventStore;
    @Autowired
    EventRouter router;
    @Autowired
    UserStateService stateService;
    @Autowired
    DonationService donations;
    @Autowired
    UserPrimalDataService userPrimalData;

    @BeforeEach
    void clean() {
        eventStore.clear();
        router.rebuild();
    }

    @Test
    void frozenUsernameIsAcceptedOnRebuildEvenThoughCurrentRuleRejectsIt() {
        userPrimalData.seedLegacyUsername("12345");
        assertThatThrownBy(() -> userPrimalData.changeUsername("12345"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(stateService.get().getUsername()).isNotEqualTo("12345");

        router.rebuild();

        assertThat(stateService.get().getUsername()).isEqualTo("12345");
    }

    @Test
    void emailIsRestoredFromItsRecordedResponseByCorrelation() {
        userPrimalData.seedLegacyEmail("boss@legacy.mail");
        router.rebuild();
        assertThat(stateService.get().getEmail()).isEqualTo("boss@legacy.mail");
    }

    @Test
    void onlineEmailChangeRejectsNewlyBannedDomain() {
        assertThatThrownBy(() -> userPrimalData.changeEmail("me@legacy.mail"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void donationIsRecalculatedOnRebuild() {
        donations.donate(100);
        assertThat(stateService.get().getPoints()).isEqualTo(200);

        router.rebuild();

        assertThat(stateService.get().getPoints()).isEqualTo(200);
    }

    @Test
    void eventsCarryAggregateIdAndSchemaVersion() {
        donations.donate(100);
        var event = eventStore.all().get(0);
        assertThat(event.aggregateId()).isEqualTo(UserState.AGGREGATE_ID);
        assertThat(event.metadata().schemaVersion()).isEqualTo(1);
    }

    @Test
    void byAggregateFiltersEvents() {
        donations.donate(100);
        userPrimalData.seedLegacyUsername("12345");

        assertThat(eventStore.byAggregate(UserState.AGGREGATE_ID)).hasSize(2);
        assertThat(eventStore.byAggregate("user:999")).isEmpty();
    }

    @Test
    void modeIsOnlineOutsideRebuild() {
        assertThat(router.currentMode()).isEqualTo(Mode.ONLINE);
        router.rebuild();
        assertThat(router.currentMode()).isEqualTo(Mode.ONLINE);
    }
}
