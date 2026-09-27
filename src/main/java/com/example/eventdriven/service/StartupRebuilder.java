package com.example.eventdriven.service;

import com.example.eventdriven.core.EventRouter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * On every startup the state is rebuilt from the eternal log, so the H2 projection is always
 * disposable and reproducible.
 */
@Component
public class StartupRebuilder implements ApplicationRunner {

    private final EventRouter router;

    public StartupRebuilder(EventRouter router) {
        this.router = router;
    }

    @Override
    public void run(ApplicationArguments args) {
        router.rebuild();
    }
}
